package resourcemanager;

import javafx.application.Application; // definir que es aplicacion levantada como javafx
import javafx.fxml.FXMLLoader; // poder entender y cargar archivos fxml
import javafx.scene.Parent; // pantallas hijas que se abren sobre la principal en vez de crear nuevas ventanas en el taskbar
import javafx.scene.Scene; // movernos entre pantallas
import javafx.stage.Stage; // escenario sobre el que ocurren las escenas
import javafx.scene.image.Image;

import java.util.Objects;


// heredar de aplicación (runnable class)
public class Launcher extends Application {
    @Override // sobrecargar metodo que levanta la aplicacion

    // parametro es el escenario principal
    // siempre hay que tirar excepcion cuando es codigo externo al de nosotros
    public void start(Stage escenarioPrincipal) throws Exception{

        java.lang.System.out.println(getClass().getResource("ui/login.fxml"));
        Parent raiz = FXMLLoader.load(getClass().getResource("ui/login.fxml")); // convertir codigo fxml obteniendolo de la clase del recurso especificado
        Image icono = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resourcemanager/images/logo.png")));
        escenarioPrincipal.getIcons().add(icono);
        escenarioPrincipal.setTitle("Sistema de Reservas"); // ponerle titulo a la ventana
        escenarioPrincipal.setScene(new Scene(raiz,600,400)); // nueva escena del raiz
        escenarioPrincipal.setResizable(false); // que no se pueda cambiar su tamaño
        escenarioPrincipal.show();
    }

    public static void main(String[] args)
    {
        launch(args); //arrancar
    }

}
