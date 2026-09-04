package resourcemanager.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import resourcemanager.data.LoadFromXML;
import resourcemanager.data.SaveToXML;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.User;
import resourcemanager.model.dto.ReservationDTO;
import resourcemanager.model.dto.GeneratedReservationDTO;
import resourcemanager.service.GeminiService;

import javax.management.InstanceNotFoundException;
import java.nio.file.FileSystemException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

public class ReservationLogic {
    private LoadFromXML loadFromXML=new LoadFromXML();
    private SaveToXML saveToXML=new SaveToXML();
    private CategoryLogic categoryLogic = new CategoryLogic();
    private ResourceLogic resourceLogic = new ResourceLogic();


    // obtiene el ID actual y lo avanza
    private String generateID() throws FileSystemException {
        try{
            // obtener lista de reservaciones
            ArrayList<Reservation> allReservations = loadFromXML.loadReservations();
            // obtener ultimo id
            String lastIdString = allReservations.getLast().getId();

            // convertirlo a int y sumarle uno
            int lastId = Integer.parseInt(lastIdString);

            return Integer.toString(lastId+1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileSystemException("No se ha podido obtener el ID autogenerado"); //lanzar hacia arriba de nuevo
        }
    }


    private Reservation createFromDTO(ReservationDTO dto) throws FileSystemException  {
        // crear reservación real desde la información enviada por DTO
        Reservation r = new Reservation(
                generateID(),
                dto.getDescription(),
                dto.getStartDate(),
                dto.getEndDate()
        );
        return r;
    }

    private Reservation assignResources(Reservation r, ObservableList<Category> observableList) throws Exception {
        // convertir de ObservableList (asi devuelve JavaFX las filas seleccionadas) a ArrayList
        ArrayList<Category> selectedCategories = new ArrayList<>(observableList);
        // crear lista donde se guardara un recurso para cada categoria
        ArrayList<Resource> selectedResources = new ArrayList<>();

        // recorrer todas las categorias, buscar el primer recurso
        for (Category c : selectedCategories) {
            try {
                Resource firstFound = resourceLogic.findFirstResourceFree(c);
                if (firstFound != null) {
                    selectedResources.add(firstFound);

                    // agregar recurso a la reserva
                    addResource(firstFound.getId(), r);
                } else {
                    // algo anda raro porque se supone que deberia haber recurso
                    throw new InstanceNotFoundException("No se encontró recurso para la categoría seleccionada: " + c.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e; // reenviar otra vez para la capa controller
            }
        }
        return r;
    }

    public Reservation createReservationForUser(ReservationDTO dto, ObservableList<Category> observableList, User user) throws Exception {
        // crear reservación real desde la información enviada por DTO
        Reservation newReservation = createFromDTO(dto);

        // popular recursos de la reserva con lo seleccionado por usuario
        assignResources(newReservation, observableList);

        // agregar reserva construida
        UserLogic userLogic = new UserLogic();
        userLogic.addReservation(newReservation, user);

        // guardar a XML
        try{
            // actualizar usuario porque ahora tiene reserva
            if(saveToXML.updateUser(user)){
                // si se pudo guardar entonces ahora guardamos la reserva en su propio archivo
                saveToXML.addReservation(newReservation);
            }
            else{
                throw new InstanceNotFoundException("No se pudo actualizar el usuario en el XML porque no se encontró");
            }
        } catch (Exception e) {
            throw e; //lanzar hacia arriba de nuevo
        }

        return newReservation;
    }


    // buscar reserva por id
    public Reservation findReservationById(String id) throws Exception {
        // encontrar primero todos los recursos disponibles, luego recorrerlos añadiendo categorías libres para
        // estar seguros de su disponibilidad de al menos 1
        ArrayList<Reservation> allReservations = loadFromXML.loadReservations();

        // recorrer todas las reservas
        for(Reservation r : allReservations){
            System.out.println(r.getId());
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    public void promptAI(String prompt, Consumer<GeneratedReservationDTO> onSuccess, Consumer<Exception> onError) {
        if(prompt == null || prompt.isBlank()) {
            onError.accept(new InvalidParameterException("Debe de describir la reserva"));
            return; // no se puede contestar una pregunta vacia
        }

        // obtener categorias disponibles, si es posible
        Thread hiloGemini = new Thread(()->{
            try{
                ArrayList<Category> categories = categoryLogic.findFreeCategories();

                if(categories.isEmpty()) throw new RuntimeException("No hay categorias con recursos libres");

                // convertir objetos de categorias a json para que la IA pueda ver cuales estan disponibles
                Gson gson = new GsonBuilder().create();
                JsonArray categoryJson = (JsonArray) new Gson().toJsonTree(categories,
                       new TypeToken<ArrayList<Category>>() {}.getType());

                // tirar un hilo para que se pueda seguir haciendo cosas mientras gemini ejecuta otra tarea (en paralelo)
                // funcion vacia con "()" porque la aplicacion no es dueña de lo que es Gemini, el proceso no es mio fuera
                // del contexto de la aplicación, igual cuando se accede a la base de datos

                GeminiService geminiService = new GeminiService();
                String jsonString = geminiService.requestJSON(prompt, categoryJson); // pedirle JSON a la ia para luego desempaquetarlo
                GeneratedReservationDTO parsed = parseAI(jsonString, categories); //Coloca la informacion traida de Gemini como un DTO que el sistema entiende

                // cruzar plataforma java con la de gemini
                Platform.runLater(()->onSuccess.accept(parsed));

                }catch(Exception e){
                    Platform.runLater(()->onError.accept(e));
                }

            });
        hiloGemini.setDaemon(true); // "Poseer" el flujo principal y cambiarlo al flujo anterior cuando termine
        hiloGemini.start(); // comenzarlo
    }

    private GeneratedReservationDTO parseAI(String jsonString, ArrayList<Category> availableCategories) throws Exception {
        if(jsonString == null){
            throw new InvalidParameterException("La IA no devolvió ninguna respuesta");
        }
        String gemini = stripMarkdownFences(jsonString);

        JsonNode nameNode;
        try {
            nameNode = new ObjectMapper().readTree(gemini);
        } catch (Exception e) {
            throw new InvalidParameterException("La IA no devolvió un JSON con el formato esperado");
        }

        //Se obtienen los datos del Json entregado por la IA y se verifican que sean null o el texto
        GeneratedReservationDTO dto = new GeneratedReservationDTO();
        dto.setDescription(textOrNull(nameNode,"description"));
        dto.setStartHour(intOrNull(nameNode,"startHour"));
        dto.setStartMinute(intOrNull(nameNode,"startMinute"));
        dto.setEndHour(intOrNull(nameNode,"endHour"));
        dto.setEndMinute(intOrNull(nameNode,"endMinute"));

        String dateText = textOrNull(nameNode,"date");
        if (dateText != null){
            try{
                dto.setDate(LocalDate.parse(dateText)); //Se parsea usando el LocalDate para poder guardarlo
            } catch (DateTimeParseException e){} //Formato invalido
        }

        //Se verifican las categorias enviadas por la IA para poder obtener las que existen en el sistema
        ArrayList<Category> matchedCategories = new ArrayList<>();
        JsonNode catNode = nameNode.get("categories");
        if (catNode != null && catNode.isArray()){
            for (JsonNode idNode : catNode){
                String catId = idNode.isNull() ? null : idNode.asText();
                if (catId == null || catId.isBlank() || catId.equalsIgnoreCase("null")) continue;
                for (Category c : availableCategories){
                    if (c.getId().equals(catId)) { matchedCategories.add(c); break;}
                }
            }
        }
        dto.setCategories(matchedCategories);

        //Verificación final de que exista de verdad los datos y no sean todos null
        if ((dto.getDescription() == null) && (dto.getDate() == null) && (dto.getStartHour() == null) && matchedCategories.isEmpty()) {
            throw new InvalidParameterException("La descripción no cubre los datos necesarios para armar una reserva. Intente ser más específico.");
        }

        return dto;
    }

    private String stripMarkdownFences(String text) {
        String t = text.trim();
        //Elimina etiquetas adicionales que envie la IA
        if (t.startsWith("```")) {
            t = t.replaceFirst("^```[a-zA-Z]*\\s*", "");
            if (t.endsWith("```")) {
                t = t.substring(0, t.length() - 3);
            }
        }
        return t.trim();
    }

    //Verificaciones de datos obtenidos del JSON
    private String textOrNull(JsonNode root, String field) {
        JsonNode node = root.get(field);
        return (node == null || node.isNull()) ? null : node.asText();
    }

    private Integer intOrNull(JsonNode root, String field) {
        JsonNode node = root.get(field);
        return (node == null || node.isNull()) ? null : node.asInt();
    }

    public boolean deleteReservation(String reservationId, User user) throws Exception {
        ArrayList<Reservation> allReservations = loadFromXML.loadReservations();

        for(Reservation r : allReservations){
            if(r.getId().equals(reservationId)){

                // eliminar reserva del usuario y tambien de todas las reservas (sino no queda ligado a nadie)
                UserLogic userLogic = new UserLogic();
                userLogic.removeReservation(r, user);
                allReservations.remove(r);

                // guardar usuario y lista de reservas actualizadas
                saveToXML.overwriteReservations(allReservations);
                saveToXML.updateUser(user);
                return true;
            }
        }

        return false;
    }

    public ArrayList<Reservation> filterByDate(ArrayList<Reservation> list, LocalDate start, LocalDate end){
        ArrayList<Reservation> newList = new ArrayList<>();
        if(list.isEmpty()) return null; // no se puede iterar porque no hay nada

        for(Reservation r: list){
            // si la fecha de la reserva esta en el rango especificado, agregar
            // atStartOfDay es para asignarle una hora, que en este caso es apenas empieza el dia para el inicio y al final cuando termina
            // se usan negaciones porque usar "isAfter" no incluye la primera hora 0:00
            if(!r.getStartDate().isBefore(start.atStartOfDay()) && !r.getEndDate().isAfter(end.atTime(23,59,59))){
                newList.add(r);
            }
        }

        if (newList.isEmpty()) return null;
        return newList;
    }

    public ArrayList<Resource> extractResources(ArrayList<Reservation> list) throws Exception{
        ArrayList<Resource> resources = new ArrayList<>();
        for(Reservation r : list){
            for(String s : r.getResourceIdList()){
                try{
                    ResourceLogic resourceLogic = new ResourceLogic();
                    Resource foundResource = resourceLogic.findResourceById(s);
                    resources.add(foundResource);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        if (resources.isEmpty()) return null;
        return resources;
    }

    public void addResource(String r, Reservation reservation) throws Exception {
        ArrayList<String> resourceIdList = reservation.getResourceIdList();

        // si no existe dentro de la lista de recursos y ademas sí existe el recurso
        if(!resourceIdList.contains(r) && resourceLogic.findResourceById(r)!=null){
            resourceIdList.add(r);
        }
    }
}
