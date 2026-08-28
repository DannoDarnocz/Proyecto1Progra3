package resourcemanager.logic;

import javafx.collections.ObservableList;
import resourcemanager.filehandler.DataFinder;
import resourcemanager.filehandler.FileLocations;
import resourcemanager.filehandler.LoadXML;
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
    public static void addResource(Reservation reservation, Resource r){
        ArrayList<Resource> resources = reservation.getResources();
        resources.add(r);
    }
    // obtiene el ID actual y lo avanza
    private static String generateID() throws FileSystemException {
        try{
            // obtener lista de reservaciones
            ArrayList<Reservation> allReservations = LoadXML.loadList(FileLocations.RESERVATIONS_PATH, Reservation.class);
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
                Resource firstFound = DataFinder.findFirstResourceFree(c);
                if (firstFound != null) {
                    System.out.println(firstFound);
                    selectedResources.add(firstFound);

                    // agregar recurso a la reserva
                    r.addResource(firstFound);
                } else {
                    // algo anda raro
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
        Reservation r = createFromDTO(dto);

        // popular recursos de la reserva con lo seleccionado por usuario
        assignResources(r, observableList);

        // agregar reserva construida
        user.addReservation(r);

        return r;
    }


    // buscar reserva por id
    public static Reservation findReservationById(String id) throws Exception {
        // encontrar primero todos los recursos disponibles, luego recorrerlos añadiendo categorías libres para
        // estar seguros de su disponibilidad de al menos 1
        ArrayList<Reservation> allReservations = LoadXML.loadList(FileLocations.RESERVATIONS_PATH,Reservation.class);

        // recorrer todas las reservas
        for(Reservation r : allReservations){
            if (r.getId().equals(id)) return r;
        }
        return null;
    }
}
