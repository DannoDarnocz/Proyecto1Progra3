package resourcemanager.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import resourcemanager.data.DataHandler;
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
import java.util.concurrent.atomic.AtomicReference;

public class ReservationLogic {


    // obtiene el ID actual y lo avanza
    private static String generateID() throws FileSystemException {
        try{
            // obtener lista de reservaciones
            ArrayList<Reservation> allReservations = LoadFromXML.loadReservations();
            // los ids de reservas eliminadas nunca se vuelven a asignar, porque eso complicaría las cosas innecesariamente
            return Integer.toString(allReservations.size()+1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileSystemException("No se ha podido obtener el ID autogenerado"); //lanzar hacia arriba de nuevo
        }
    }


    private static Reservation createFromDTO(ReservationDTO dto) throws FileSystemException  {
        // crear reservación real desde la información enviada por DTO
        Reservation r = new Reservation(
                generateID(),
                dto.getDescription(),
                dto.getStartDate(),
                dto.getEndDate()
        );
        return r;
    }

    private static Reservation assignResources(Reservation r, ObservableList<Category> observableList) throws Exception {
        // convertir de ObservableList (asi devuelve JavaFX las filas seleccionadas) a ArrayList
        ArrayList<Category> selectedCategories = new ArrayList<>(observableList);
        // crear lista donde se guardara un recurso para cada categoria
        ArrayList<Resource> selectedResources = new ArrayList<>();

        // recorrer todas las categorias, buscar el primer recurso
        for (Category c : selectedCategories) {
            try {
                Resource firstFound = DataHandler.findFirstResourceFree(c);
                if (firstFound != null) {
                    selectedResources.add(firstFound);

                    // agregar recurso a la reserva
                    r.addResource(firstFound.getId());
                } else {
                    // algo anda raro porque se supone que deberia haber recurso
                    throw new InstanceNotFoundException("No se encontró recurso para la categoría seleccionada.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e; // reenviar otra vez para la capa controller
            }
        }
        return r;
    }

    public static Reservation createReservationForUser(ReservationDTO dto, ObservableList<Category> observableList, User user) throws Exception {
        // crear reservación real desde la información enviada por DTO
        Reservation newReservation = createFromDTO(dto);

        // popular recursos de la reserva con lo seleccionado por usuario
        assignResources(newReservation, observableList);

        // agregar reserva construida
        user.addReservation(newReservation);

        // guardar a XML
        try{
            // actualizar usuario porque ahora tiene reserva
            if(SaveToXML.updateUser(user)){
                // si se pudo guardar entonces ahora guardamos la reserva en su propio archivo
                SaveToXML.addReservation(newReservation);
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
    public static Reservation findReservationById(String id) throws Exception {
        // encontrar primero todos los recursos disponibles, luego recorrerlos añadiendo categorías libres para
        // estar seguros de su disponibilidad de al menos 1
        ArrayList<Reservation> allReservations = LoadFromXML.loadReservations();

        // recorrer todas las reservas
        for(Reservation r : allReservations){
            System.out.println(r.getId());
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    public static GeneratedReservationDTO promptAI(String prompt) throws Exception {
        if(prompt.isEmpty()) return null; // no se puede contestar una pregunta vacia

        // algo que se construirá una vez termine el hilo
        AtomicReference<GeneratedReservationDTO> result = new AtomicReference<>();

        // obtener categorias disponibles, si es posible
        try{
            ArrayList<Category> categories = DataHandler.findFreeCategories();

            if(categories.isEmpty()) throw new RuntimeException("No hay categorias con recursos libres");

            System.out.print("AAAAAA"); //TODO BORRAR
            // convertir objetos de categorias a json para que la IA pueda ver cuales estan disponibles
            Gson gson = new GsonBuilder().create();
            JsonArray categoryJson = (JsonArray) new Gson().toJsonTree(categories,
                    new TypeToken<ArrayList<Category>>() {
                    }.getType());

            // tirar un hilo para que se pueda seguir haciendo cosas mientras gemini ejecuta otra tarea (en paralelo)
            Thread hiloGemini = new Thread(()->{
                // funcion vacia con "()" porque la aplicacion no es dueña de lo que es Gemini, el proceso no es mio fuera
                // del contexto de la aplicación, igual cuando se accede a la base de datos

                // poner try y catch porque no domino Gemini y puede caerse.
                try{
                    GeminiService geminiService = new GeminiService();

                    String jsonString = geminiService.requestJSON(prompt, categoryJson); // pedirle JSON a la ia para luego desempaquetarlo


                    // cruzar plataforma java con la de gemini
                    Platform.runLater(()->{
                        if(jsonString == null){
                            throw new InvalidParameterException("La descripción no cubre todos los datos necesarios para reservar");
                        }

                        // que pasa cuando responde
                        // leer el json
                        ObjectMapper mapper = new ObjectMapper();

                        JsonNode nameNode = null;
                        try {
                            nameNode = mapper.readTree("{\"name\": \"John\"}");
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println(jsonString);
                    });

                }catch(Exception e){
                    Platform.runLater(()->{
                        // manejo de error, no se puede lanzar de nuevo asi que se retorna null
                    });
                }

            });

            hiloGemini.setDaemon(true); // "Poseer" el flujo principal y cambiarlo al flujo anterior cuando termine
            hiloGemini.start(); // comenzarlo

            return new GeneratedReservationDTO();

        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }



    }

    public static boolean deleteReservation(String reservationId, User user) throws Exception {
        ArrayList<Reservation> allReservations = LoadFromXML.loadReservations();

        for(Reservation r : allReservations){
            if(r.getId().equals(reservationId)){

                // eliminar reserva del usuario y tambien de todas las reservas (sino no queda ligado a nadie)
                user.removeReservation(r);
                allReservations.remove(r);

                // guardar usuario y lista de reservas actualizadas
                SaveToXML.overwriteReservations(allReservations);
                SaveToXML.updateUser(user);
                return true;
            }
        }

        return false;
    }
}
