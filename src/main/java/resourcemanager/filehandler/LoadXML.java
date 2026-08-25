package resourcemanager.filehandler;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import resourcemanager.model.User;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LoadXML {
    private static final String USERS_PATH = "/resourcemanager/data/users.xml";

    private static final XmlMapper mapper = new XmlMapper();
    static {
        // no maneja LocalDate de una vez, se ocupa esta dependencia
        mapper.registerModule(new JavaTimeModule());
    }
    // loader para xml genéricos
    public static <T> T load(String resourcePath, Class<T> clazz) throws Exception {
        InputStream is =  LoadXML.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
        }
        return mapper.readValue(is, clazz);
    }

    // buscar el usuario que corresponda con el ID
    public static User findUserById(String id) throws Exception {
        // obtener inputstream del archivo en la ruta de usuario
        try (InputStream is = LoadXML.class.getResourceAsStream(USERS_PATH)) {
            // no hay nadota
            if (is == null) throw new FileNotFoundException("Archivo de usuarios inexistente");

            // preparar para escanear
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(is);

            // escanear mientras hayan usuarios
            while (reader.hasNext()) {
                int event = reader.next();
                // obtener usuario actual si se puede
                if (event == XMLStreamConstants.START_ELEMENT
                        && reader.getLocalName().equals("user")) {

                    User user = mapper.readValue(reader, User.class);
                    if (user.getId().equals(id)) {
                        return user;   // el usuario corresponde al id, devolderlo
                    }
                }
            }
        }
        return null; // reached end of file, nothing matched
    }
}