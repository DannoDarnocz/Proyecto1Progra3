package resourcemanager.ui;
import javafx.fxml.FXML; // poder entender fxml
import resourcemanager.data.DataPaths;

// importar solo los elementos de la UI que se ocupa porque cualquier cosa lo que se importe
// se compila de todas formas incluso aunque no se utiliza


public class MainController {
    @FXML
    private void initialize() {
        // todo: borrar
        try{
            System.out.println(DataPaths.getUsersFile().getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
