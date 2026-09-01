package resourcemanager.service;

import javafx.stage.Stage;
import resourcemanager.logic.PrintLogic;

import java.io.File;
import java.util.List;

public class PrintService {
    public static void openPdf(File file) throws Exception{
        PrintLogic.openPdf(file);
    }

    public static <T> File generatePdf(List<T> items, Class<T> classType, String fileName) throws Exception{
        return PrintLogic.generatePdf(items, classType, fileName);
    }
}
