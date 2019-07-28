package disease_assistance;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DiseaseAssistant extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		try {
		Parent root = FXMLLoader.load(getClass().getResource("/disease_assistance/FXMLDocument.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("addDisease.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	} 
	public static void main(String[] args) {
		launch(args);
	}
}
