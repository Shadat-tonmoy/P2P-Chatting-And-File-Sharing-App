package application;
	
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;


public class Main extends Application {
	private Pane root;
	private Parent chatScreen;
	private Scene scene;
	private MainController mainController;
	@FXML private ImageView homeImage;
	@Override
	public void start(Stage primaryStage) {
		try {
			
			root = FXMLLoader.load(getClass().getResource("WelcomeScreen.fxml"));
			System.out.println(root.lookup("#anotherButton"));
			chatScreen = FXMLLoader.load(getClass().getResource("ChatScreen.fxml"));
			scene = new Scene(root,600,600);
			scene.getStylesheets().add(getClass().getResource("listStyle.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Welcome");
			primaryStage.setResizable(false);
			primaryStage.show();
			mainController = new MainController(this);
			mainController.setStage(primaryStage);
			
			
			
		} catch(Exception e) {
			System.out.println("There is a problem");
			e.printStackTrace();
		}
	}
	
	public void setRoot(Pane root)
	{
		this.root = root;
		scene.setRoot(root);
	}
	
	
	
	public Pane getRoot() {
		return root;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
