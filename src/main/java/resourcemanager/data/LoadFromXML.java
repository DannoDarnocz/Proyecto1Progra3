package resourcemanager.data;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.User;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;

public class LoadFromXML {
    static XmlMapper mapper = MapperSingleton.getInstance();

    // cargar lista generica de lo que lea en el archivo, donde cada item es indicado por itemTagName
    private static <T> ArrayList<T> loadList(String resourcePath, String itemTagName, Class<T> itemClass) throws Exception {
        ArrayList<T> results = new ArrayList<>();

        try (InputStream is = LoadFromXML.class.getResourceAsStream(resourcePath)) {
            if (is == null) return results;

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(is);

            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals(itemTagName)) {
                    T item = mapper.readValue(reader, itemClass);
                    results.add(item);
                }
            }
        }
        return results;
    }

    public static ArrayList<User> loadUsers() throws Exception {
        return loadList(DataPaths.USERS_PATH, "user", User.class);
    }

    public static ArrayList<Resource> loadResources() throws Exception {
        return loadList(DataPaths.RESOURCES_PATH, "resource", Resource.class);
    }

    public static ArrayList<Reservation> loadReservations() throws Exception {
        return loadList(DataPaths.RESERVATIONS_PATH, "reservation", Reservation.class);
    }

    public static ArrayList<Category> loadCategories() throws Exception {
        return loadList(DataPaths.CATEGORIES_PATH, "category", Category.class);
    }
}