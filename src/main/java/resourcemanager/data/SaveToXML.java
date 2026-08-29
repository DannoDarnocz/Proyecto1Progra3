package resourcemanager.data;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;

import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static resourcemanager.data.LoadFromXML.loadReservations;
import static resourcemanager.data.LoadFromXML.loadUsers;

public class SaveToXML {
    private static final XmlMapper mapper = new XmlMapper();

    static {
        // no maneja LocalDateTime de una vez, se ocupa esta dependencia
        mapper.registerModule(new JavaTimeModule());
    }

    /*static {
        mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> void save(File xmlFile, T object) throws Exception {
        mapper.writeValue(xmlFile, object);
    }

    public static <T> void saveList(File xmlFile, List<T> objects, String rootName) throws Exception {
        mapper.writer()
                .withRootName(rootName)
                .writeValue(xmlFile, objects);
    }*/

    public static void addReservation(Reservation r) throws Exception {
        // cargar todas las reservas que hay en sistema
        ArrayList<Reservation> allReservations = loadReservations();

        // asumiendo que está bien se agrega a la lista
        allReservations.add(r);


        // obtener archivo de reservas desde la ruta almacenada como constante
        File reservationsFile = DataPaths.getReservationsFile();

        // caerle encima al archivo entero con la lista actualizada
        mapper.writer()
                .withRootName("reservations")
                .writeValue(reservationsFile, allReservations);
    }

    public static boolean updateUser(User updatedUser) throws Exception {
        // cargar todos los usuarios del archivo XML que se sobreescribirá
        ArrayList<User> users = loadUsers();

        // obtener archivo de usuarios desde la ruta almacenada como constante
        File usersFile = DataPaths.getUsersFile();

        // recorrer lista de usuarios del xml hasta encontrar el que se quiere actualizar
        // como solo el id no cambia entonces se asume que mismo id es el mismo usuario y el resto de
        // atributos se cambian

        // se ocupa recorrer por indice por el users.set() que requiere indice
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);   // reemplazar el usuario viejo con el nuevo actualizado

                // caerle encima al archivo entero
                mapper.writer()
                        .withRootName("users")
                        .writeValue(usersFile, users);

                return true;
            }
        }
        return false; // no se encontró
    }

    /*public static <T> void saveList(File file, String rootTag, String itemTag, ArrayList<T> items) throws Exception {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

        try (Writer fileWriter = new FileWriter(file)) {
            XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(fileWriter);
            xmlWriter.writeStartDocument();
            xmlWriter.writeStartElement(rootTag);

            ObjectWriter itemWriter = mapper.writer().withRootName(itemTag);
            for (T item : items) {
                itemWriter.writeValue(xmlWriter, item);
            }

            xmlWriter.writeEndElement();
            xmlWriter.writeEndDocument();
            xmlWriter.flush();
            xmlWriter.close();
        }
    }*/
}