package hufs.ces.udp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GrimPanFXMLMain extends Application {

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Application by sjkim");

		GrimTalkClientDemo root = new GrimTalkClientDemo();
		root.parentStage = primaryStage;
		Scene scene = new Scene(root);

		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
