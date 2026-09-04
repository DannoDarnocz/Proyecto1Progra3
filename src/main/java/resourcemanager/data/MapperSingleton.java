package resourcemanager.data;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// instancia singleton para el mapper configurado de tal forma que pueda manipular localdate y las escriba con un formato consistente en un solo string
public class MapperSingleton {
    private static final XmlMapper mapper = new XmlMapper();
    static {
        // no maneja LocalDate de una vez, se ocupa esta dependencia
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // escribir fechas como texto, no como arreglo de números porque sino se despapaya a la hora de guardarlo de nuevo
    }
    //singleton
    private MapperSingleton(){};

    public static XmlMapper getInstance(){return mapper;}
}
