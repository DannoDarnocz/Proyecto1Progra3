package resourcemanager.filehandler;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import resourcemanager.structure.UserList;

import java.io.File;
import java.util.List;

public class LoadXML {
    private static final XmlMapper mapper = new XmlMapper();

    // loader para xml genéricos
    public static <T> T load(File xmlFile, Class<T> clazz) throws Exception {
        return mapper.readValue(xmlFile, clazz);
    }

    // loader para lista en xml genérico
    public static <T> List<T> loadList(File xmlFile, Class<T> itemClass) throws Exception {
        JavaType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, itemClass);
        return mapper.readValue(xmlFile, listType);
    }
}