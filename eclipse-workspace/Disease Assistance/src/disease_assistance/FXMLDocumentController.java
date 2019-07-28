package disease_assistance;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import disease_assistance.database.DatabaseHandler;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

public class FXMLDocumentController implements Initializable {
	
	@FXML
	private JFXTextField title;
	@FXML
	private JFXTextField id;
	@FXML
	private JFXTextField author;
	@FXML
	private JFXTextField publisher;
	@FXML
	private JFXButton saveButton;
	@FXML
	private JFXButton cancelButton;
	
	DatabaseHandler databaseHandler;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		databaseHandler = new disease_assistance.database.DatabaseHandler();
	}
	@FXML
	private void addDisease(ActionEvent event) {
		String bookID = id.getText();
		String bookAuthor = author.getText();
		String bookName = title.getText();
		String bookPublisher = publisher.getText();
		
		if(bookID.isEmpty() || bookAuthor.isEmpty() || bookName.isEmpty() || bookPublisher.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("Please Enter in all fields");
			alert.showAndWait();
			return;
		}
		String qu = "INSERT INTO BOOK VALUES ("+
		"'" + bookID + "'," +
		"'" + bookName + "'," +
		"'" + bookAuthor + "'," +
		"'" + bookPublisher + "'," +
		"" + "true" + "" +
				")";
		System.out.println(qu);
		if(databaseHandler.execAction(qu)){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("Success");
			alert.showAndWait();
			
		}else // ERROR
			{
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText(null);
		alert.setContentText("Failed");
		alert.showAndWait();
		}
	}
	
	@FXML
	private void cancel(ActionEvent event) {
	}
}
