package resourcemanager.logic;

import javafx.scene.control.Alert;
import resourcemanager.model.Category;
import resourcemanager.filehandler.DataFinder;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.dto.ReservationDTO;
import resourcemanager.ui.Utilities;

import java.util.ArrayList;

public class CreateObject {
    public static Reservation createReservation(ReservationDTO dto) {
        // esto valida que la reserva con los datos ingresados de la vista y leidos por el controlador sea valido

        ArrayList<Resource> selectedResources = new ArrayList<>();

        Reservation r = CreateFromDTO.createReservation(dto);
        ArrayList<Category> selectedItems = dto.getCategories();

        // recorrer todas las categorias, buscar el primer recurso
        for (Category c : selectedItems) {
            if (c == null) System.out.println("DFHJKHDJKHSK");

            try {

                Resource firstFound = DataFinder.findFirstResourceFree(c);
                if (firstFound != null) {
                    System.out.println(firstFound);
                    selectedResources.add(firstFound);

                    // agregar recurso a la reserva
                    r.addResource(firstFound);
                } else {
                    // algo anda raro
                    Utilities.showAlert("Error", "No se encontró recurso disponible para la categoría seleccionada", Alert.AlertType.ERROR);
                    System.out.println("No se encontro recurso para ctaegoria que en teoria estaba libre");
                }
            } catch (Exception e) {
                Utilities.showAlert("Error", "Ha ocurrido un error para obtener un recurso de categoria: " + c.getDescription(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
        return r;
    }
}
