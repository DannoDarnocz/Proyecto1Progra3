package resourcemanager.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;

public class DataPaths {
    static final String USERS_PATH = "/resourcemanager/data/users.xml";
    static final String RESOURCES_PATH = "/resourcemanager/data/resources.xml";
    static final String RESERVATIONS_PATH = "/resourcemanager/data/reservations.xml";
    static final String CATEGORIES_PATH = "/resourcemanager/data/categories.xml";

    public static File getUsersFile() throws Exception {
        return pathToFile(USERS_PATH);
    }

    public static File getResourcesFile() throws Exception {
        return pathToFile(RESOURCES_PATH);
    }

    public static File getReservationsFile() throws Exception {
        return pathToFile(RESERVATIONS_PATH);
    }

    public static File getCategoriesFile() throws Exception {
        return pathToFile(CATEGORIES_PATH);
    }

    // esto se hace porque File no entiende el PATH como viene en la constante asi que se convierte a URL
    // ojo: esto solo sirve para fines de demostración del proyecto sin empaquetarlo en JAR porque Resources no está diseñado para eso
    // solo funciona si se ejecuta desde el IDE directamente. Eventualmente se debería de utilizar AppData o algún
    // otro directorio para guardarlo y mantenerlo actualizado desde ahi
    private static File pathToFile(String classpathPath) throws Exception {
        // agregarle faltante al path para que se pueda utilizar el mismo archivo de Resources
        return new File("src/main/resources"+classpathPath);
    }
}
