package resourcemanager.filehandler;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.util.List;

public class SaveXML {
    private static final XmlMapper mapper = new XmlMapper();

    static {
        mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> void save(File xmlFile, T object) throws Exception {
        mapper.writeValue(xmlFile, object);
    }

    public static <T> void saveList(File xmlFile, List<T> objects, String rootName) throws Exception {
        mapper.writer()
                .withRootName(rootName)
                .writeValue(xmlFile, objects);
    }
}