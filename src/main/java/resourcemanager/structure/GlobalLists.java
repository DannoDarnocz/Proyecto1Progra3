package resourcemanager.structure;
import resourcemanager.filehandler.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class GlobalLists {
    private static final String USERS_PATH = "/resourcemanager/data/users.xml";
    private static GlobalLists instance;
    public static UserList userList = new UserList();
    public static ResourceList resourceList = new ResourceList();
    public static ReservationList reservationList = new ReservationList();
    public static CategoryList categoryList = new CategoryList();

    private GlobalLists() {
        // singleton
    }

    public static GlobalLists getInstance() {
        if (instance == null) {
            instance = new GlobalLists();
        }
        return instance;
    }

    public void loadAll() throws Exception {
        userList = LoadXML.load(USERS_PATH, UserList.class);
    }

    public void saveUsers() throws Exception {
        File file = resolveUsersFile();
        SaveXML.saveList(file,userList,"users");
    }

    public void updatePassword(String userId, String newPassword) throws Exception {
        File file = resolveUsersFile();

        // 1. Parsear el XML a un árbol de nodos (DOM)
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        // 2. Buscar puntualmente el nodo <password> del usuario con ese id
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expresion = "/users/user[id='" + userId + "']/password";
        Node passwordNode = (Node) xpath.evaluate(expresion, doc, XPathConstants.NODE);

        if (passwordNode == null) {
            throw new IllegalStateException("No se encontró el usuario '" + userId + "' en users.xml");
        }

        // 3. Modificar solo ese nodo, nada más del árbol se toca
        passwordNode.setTextContent(newPassword);

        // 4. Volcar el árbol (ya modificado) de vuelta al archivo
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }

    private File resolveUsersFile() throws Exception {
        URL url = GlobalLists.class.getResource(USERS_PATH);
        if (url == null) {
            throw new FileNotFoundException("No se encontró " + USERS_PATH + " en el classpath");
        }
        return new File(url.toURI());
    }

    public UserList getUsers() { return userList; }
    public ResourceList getResources() { return resourceList; }
    public ReservationList getReservations() { return reservationList; }
    public CategoryList getCategories() { return categoryList; }
}
