package resourcemanager.filehandler;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import resourcemanager.structure.UserList;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class LoadXML {
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
}