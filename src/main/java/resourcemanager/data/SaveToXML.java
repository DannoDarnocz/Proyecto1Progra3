package resourcemanager.data;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

import static resourcemanager.data.LoadFromXML.*;

public class SaveToXML {

    static XmlMapper mapper = MapperSingleton.getInstance();

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
        overwriteReservations(allReservations);
        /*mapper.writer()
                .withRootName("reservations")
                .writeValue(reservationsFile, allReservations);*/
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

                // caerle encima al archivo entero con toda la lista
                overwriteUsers(users);
                /*mapper.writer()
                        .withRootName("users")
                        .writeValue(usersFile, users);*/

                return true;
            }
        }
        return false; // no se encontró
    }

    public static boolean updateCategory(Category updatedCategory) throws Exception {
        // cargar todos las categorias del archivo XML que se sobreescribirá
        ArrayList<Category> categories = loadCategories();

        // obtener archivo de usuarios desde la ruta almacenada como constante
        File categoryFile = DataPaths.getCategoriesFile();

        // recorrer lista de categorias del xml hasta encontrar el que se quiere actualizar
        // como solo el id no cambia entonces se asume que mismo id es el mismo y el resto de
        // atributos se cambian

        // se ocupa recorrer por indice por el users.set() que requiere indice
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(updatedCategory.getId())) {
                categories.set(i, updatedCategory);   // reemplazar el usuario viejo con el nuevo actualizado

                // caerle encima al archivo entero con toda la lista
                overwriteCategories(categories);

                return true;
            }
        }
        return false; // no se encontró
    }

    public static void overwriteUsers(ArrayList<User> items) throws Exception {
        saveList(DataPaths.getUsersFile(),"users","user",items);
    }

    public static void overwriteReservations(ArrayList<Reservation> items) throws Exception {
        saveList(DataPaths.getReservationsFile(),"reservations","reservation",items);
    }

    public static void overwriteCategories(ArrayList<Category> items) throws Exception {
        saveList(DataPaths.getCategoriesFile(),"categories","category",items);
    }



    // guardar lista de forma genérica, rootTag es la lista raíz que requiere xml y itemTag es el tag para cada objeto
    // de la lista que se le pase
    public static <T> void saveList(File file, String rootTag, String itemTag, ArrayList<T> items) throws Exception {
        // como es genérico se utiliza factory
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

        // try para manejo de recursos, nada más
        try (Writer fileWriter = new FileWriter(file)) {
            XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(fileWriter);
            xmlStreamWriter.writeStartDocument();
            xmlStreamWriter.writeStartElement(rootTag);

            XmlFactory xmlFactory = (XmlFactory) mapper.getFactory();

            // toxmlgenerator es como intermediario entre objeto java y la escritura xml
            ToXmlGenerator generator = xmlFactory.createGenerator(xmlStreamWriter);

            ObjectWriter itemWriter = mapper.writer().withRootName(itemTag);

            // guardar todos los items de la lista
            for (T item : items) {
                generator.setNextName(new QName(itemTag)); // le dice al generador qué tag usar para el próximo objeto
                itemWriter.writeValue(generator, item);
            }

            // escribir y cerrar
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.flush();
            xmlStreamWriter.close();
        }
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