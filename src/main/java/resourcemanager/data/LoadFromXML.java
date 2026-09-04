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
import java.io.File;
import java.io.FileInputStream;

public class LoadFromXML {
    static XmlMapper mapper = MapperSingleton.getInstance();

    // cargar lista generica de lo que lea en el archivo, donde cada item es indicado por itemTagName
    private  <T> ArrayList<T> loadList(File file, String itemTagName, Class<T> itemClass) throws Exception {
        ArrayList<T> results = new ArrayList<>();

        //Cambia a file en vez de Path debido a que la versión generada de Maven no se actualiza en tiempo real con los cambios realizados en el XML
        if (!file.exists()) return results;

        try (InputStream is = new FileInputStream(file)) {
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

    public ArrayList<User> loadUsers() throws Exception {
        return loadList(DataPaths.getUsersFile(), "user", User.class);
    }

    public  ArrayList<Resource> loadResources() throws Exception {
        return loadList(DataPaths.getResourcesFile(), "resource", Resource.class);
    }

    public  ArrayList<Reservation> loadReservations() throws Exception {
        return loadList(DataPaths.getReservationsFile(), "reservation", Reservation.class);
    }

    public ArrayList<Category> loadCategories() throws Exception {
        return loadList(DataPaths.getCategoriesFile(), "category", Category.class);
    }
}