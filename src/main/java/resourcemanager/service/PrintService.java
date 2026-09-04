package resourcemanager.service;

import javafx.stage.Stage;
import resourcemanager.logic.PrintLogic;
import resourcemanager.logic.ResourceLogic;

import java.io.File;
import java.util.List;

public class PrintService {
    private PrintLogic printLogic = new PrintLogic();

    public void openPdf(File file) throws Exception{
        printLogic.openPdf(file);
    }

    public <T> File generatePdf(List<T> items, Class<T> classType, String fileName) throws Exception{
        return printLogic.generatePdf(items, classType, fileName);
    }
}
