package resourcemanager.structure;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import resourcemanager.model.User;

import java.util.ArrayList;

public class UserList extends ArrayList<User> {

    // para garantizar la correcta lectura de los xml
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "user")

    public User findById(String id) {
        // filtrar el stream de todos los usuarios por el que tenga id, sino retornar null
        return this.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
