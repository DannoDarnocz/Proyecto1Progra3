package resourcemanager.logic;

import javafx.collections.ObservableList;
import resourcemanager.data.DataHandler;
import resourcemanager.data.LoadFromXML;
import resourcemanager.data.SaveToXML;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.User;
import resourcemanager.model.dto.ReservationDTO;

import javax.management.InstanceNotFoundException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;

public class ReservationLogic {

    // TODO: DTO o no?
    //  agregar resource a una reserva

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
}
