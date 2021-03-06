package lab5.java.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class BaseWindow {
    public void init() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/lab5/fxmlFiles/baseWindow.fxml"));
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Base window");
        stage.show();
    }
}
