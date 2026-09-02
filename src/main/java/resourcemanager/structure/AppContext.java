package resourcemanager.structure;

import javafx.stage.Stage;

public class AppContext {
    private static Stage primaryStage;

    private AppContext() {}

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}