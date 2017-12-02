package application;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.CustomColorDialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import message.*;

public class MainController extends Thread{
	
	private Main main;
	private Pane root;
	private FXMLLoader fxmlLoader;
	@FXML private Button submitButton,resetButton;
	@FXML private TextField ipAddrField,portField,clientPortField,userNameField;
	private String ipAddressToConnect,portToConnect,portToListen,userName;
	@FXML private Label portNoToListenEmptyMessage,portNoEmptyMessage,ipaddrEmptyMessage,userNameEmptyMessage,chatUserNameLabel;
	@FXML public  Label message,connectedToLabel,listeningAtLabel;
	@FXML private TextArea messageSendBox;
	private boolean isValid;
	private MessageReceiver messageReceiver;
	private MessageSender messageSender;
	private Thread conncetionChecker;
	@FXML private ImageView messageSendButton,fileAttachButton,saveMessageButton,loadMessageIcon;
	@FXML private ColorPicker themeChangeButton;
	private FileChooser fileChooser;
	private Stage stage;
	private File file;
	private ScrollPane scrollPane;
	private byte [] byteArray;
	private ArrayList<String> sentMessages,receivedMessages;
	public static ArrayList<String> allMessages;
	public MainController()
	{
		
	}
	public MainController(Main main) throws IOException
	{
		this.main = main;
		root = main.getRoot();
		initNodes();
		setOnClickListener();
		allMessages = new ArrayList<String>();
		conncetionChecker = new Thread(this,"Conncetion Checker");
	}
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+" started....");
		while(!messageReceiver.isConnected() || messageReceiver.getReceiverPort()!=-1 || messageSender.getSenderPort()!=-1)
		{
			System.out.println("not connected");
			try {
				Thread.sleep(1000);
				if(messageReceiver.isConnected() && messageReceiver.getReceiverPort()!=-1 && messageSender.getSenderPort()!=-1)
				{
					System.out.println("Receiving at :"+messageReceiver.getReceiverPort());
					System.out.println("Boom");
					showChatScreen();
					messageReceiver.setRoot(root);
					messageSender.setRoot(root);
					messageSender.setSender(userName);
					messageReceiver.setStage(stage);
					if(conncetionChecker.isAlive())
					{
						conncetionChecker.stop();
						System.out.println("Thread Closed");
						
					}
					else System.out.println("Thread not active");
					
				}
				System.out.println("TOtal running...."+Thread.activeCount());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void showChatScreen() throws IOException
	{
		fxmlLoader = new FXMLLoader(getClass().getResource("ChatScreen.fxml"));
		root = (Pane) fxmlLoader.load();
		if(root==null)
			System.out.println("NULL");
		System.out.println("gg"+this.root);
		System.out.println(main);
		if(main!=null)
			main.setRoot(this.root);
		initChatScreenNodes();
		
	}
	
	public void setOnClickListener()
	{
		submitButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				try {
					getValues();
					validateForm();
					if(isValid){
						System.out.println("Sending...");
						submitButton.setOpacity(0.7);
						submitButton.setText("Connecting...");
						messageReceiver = new MessageReceiver(Integer.parseInt(portToListen));
						messageReceiver.start();
						messageSender = new MessageSender(ipAddressToConnect,Integer.parseInt(portToConnect));
						messageSender.start();
						conncetionChecker.start();
					}
						
					

					//showChatScreen();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		

		resetButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				try {
					System.out.println("Reseting....");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	public void initNodes(){
		submitButton = (Button) root.lookup("#submitButton");
		resetButton = (Button) root.lookup("#resetButton");
		ipAddrField = (TextField) root.lookup("#ipAddrField");
		portField = (TextField) root.lookup("#portField");
		clientPortField = (TextField) root.lookup("#clientPortField");
		ipaddrEmptyMessage = (Label) root.lookup("#ipaddrEmptyMessage");
		portNoToListenEmptyMessage = (Label) root.lookup("#portNoToListenEmptyMessage");
		portNoEmptyMessage = (Label) root.lookup("#portNoEmptyMessage");
		message = (Label) root.lookup("#message");
		userNameField = (TextField) root.lookup("#userNameField");
		userNameEmptyMessage = (Label) root.lookup("#userNameEmptyMessage");
		System.out.println(portField);
		ipaddrEmptyMessage.setVisible(false);
		portNoEmptyMessage.setVisible(false);
		portNoToListenEmptyMessage.setVisible(false);
		userNameEmptyMessage.setVisible(false);
		
	}
	
	public void initChatScreenNodes(){
		
		listeningAtLabel = (Label) root.lookup("#listeningAtLabel");
		connectedToLabel = (Label) root.lookup("#connectedToLabel");
		messageSendButton = (ImageView) root.lookup("#messageSendButton");
		messageSendBox = (TextArea) root.lookup("#messageSendBox");
		connectedToLabel.setText("Sending Message at Port : "+messageSender.getSenderPort());
		listeningAtLabel.setText("Receiving Message at Port :"+messageReceiver.getReceiverPort());
		fileAttachButton = (ImageView) root.lookup("#fileAttachButton");
		themeChangeButton = (ColorPicker) root.lookup("#themeChangeButton");
		themeChangeButton.setValue(Color.RED);
		scrollPane = (ScrollPane) root.lookup("#messageList");
		scrollPane.setStyle("-fx-background:transparent;");
		saveMessageButton = (ImageView) root.lookup("#saveMessageButton");
		chatUserNameLabel = (Label) root.lookup("#chatUserNameLabel");
		loadMessageIcon = (ImageView) root.lookup("#loadMessageIcon");
		chatUserNameLabel.setText("Welcome "+userName);
		
		
		messageSendButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				String message = messageSendBox.getText().toString();
				try {
					if(message.length()>0)
					{
						messageSender.sendMessage(message);
					}
					if(file!=null)
					{
						messageSender.sendFile(byteArray,file);
						file = null;
					}
					messageSendBox.setText("");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		fileAttachButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				fileChooser = new FileChooser();
				file = fileChooser.showOpenDialog(stage);
				if(file!=null)
				{
					String fileName = file.getName();
					byteArray  = new byte [(int)file.length()];
			        
				}
			}
		});
		
		themeChangeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Color color = themeChangeButton.getValue();

				String hexColor = "#" + Integer.toHexString(color.hashCode()); 
				
				System.out.println(scrollPane);
				try {
					messageSender.sendBackgroundColor(hexColor);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		saveMessageButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				try {
					saveMessages(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		loadMessageIcon.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				for(String message : allMessages)
				{
					System.out.println(message);
				}
				try {
					FileReader fileReader = new FileReader("C:\\chatlog\\"+ipAddressToConnect+".txt");
					StringBuilder messages = new StringBuilder();
					while(fileReader.read()!=-1)
					{
						System.out.println((char)fileReader.read());
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			}
		});
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				try {
					saveMessages(true);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
		
		
	}
	
	
	
	public void getValues(){
		ipAddressToConnect = ipAddrField.getText().toString();
		portToConnect = portField.getText().toString();
		portToListen = clientPortField.getText().toString();
		userName = userNameField.getText().toString();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void validateForm(){
		isValid = true;
		if(ipAddressToConnect.isEmpty()){
			ipaddrEmptyMessage.setVisible(true);
			isValid = false;
		}
		else {
			ipaddrEmptyMessage.setVisible(false);
		}
		

		if(portToConnect.isEmpty()){
			portNoEmptyMessage.setVisible(true);
			isValid = false;
		}	
		else {
			portNoEmptyMessage.setVisible(false);
		}

		if(portToListen.isEmpty()){
			portNoToListenEmptyMessage.setVisible(true);
			isValid = false;
		}	
		else {
			portNoToListenEmptyMessage.setVisible(false);
		}
		
		if(userName.isEmpty()){
			userNameEmptyMessage.setVisible(true);
			isValid = false;
		}
		else 
		{
			userNameEmptyMessage.setVisible(false);
		}
		System.out.println(isValid);
	}

	public void updateLabel()
	{
		message.setText("Connected");
	}
	
	public void saveMessages(boolean isExiting) throws IOException, InterruptedException{
		sentMessages = messageSender.getMessageList();
		String messages = "";
		for(String message : allMessages)
		{
			messages+=message+"\n";
		}
		System.out.println(messages);
		byte[] byteArrayFromString = messages.getBytes(Charset.forName("UTF-8"));
		
		
		if(!isExiting)
		{
			FileChooser fileSaver = new FileChooser();
			//FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
			fileSaver.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("Text File",
	                        "*.txt"),
	                new FileChooser.ExtensionFilter("Document",
	                        "*.pdf", "*.docx")
	                );
	        File file = fileSaver.showSaveDialog(stage);
	        if(file!=null)
	        {
	        	System.out.println(file.getAbsolutePath());
	        	try {
					saveFile(file.getAbsolutePath(),byteArrayFromString,false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		}
		else 
		{
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirm to close");
			String s = "Are you sure to exit?";
			alert.setContentText(s);
			Optional<ButtonType> result = alert.showAndWait();
			if ((result.isPresent()) && (result.get() == ButtonType.OK))
			{
				saveFile("C:\\chatlog\\"+ipAddressToConnect+".txt",byteArrayFromString,true);
				//Thread.currentThread().stop();
				//messageSender.stop();
				//messageReceiver.stop();
				Platform.exit();
				System.exit(0);
				if(messageReceiver.isAlive())
					messageReceiver.join();
				if(messageSender.isAlive())
					messageSender.join();
				
				
			}			
		}
	}
	
	public void saveReceivedMessage()
	{
		
	}
	
	public void saveFile(String filePath,byte[] byteArray,boolean isExiting) throws IOException
	{

		System.out.println("From OutSide");
		for(byte b : byteArray)
		{
			System.out.print((char)b);
		}
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
	    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
	    bufferedOutputStream.write(byteArray, 0, byteArray.length);
	    bufferedOutputStream.flush();
	    bufferedOutputStream.close();
	    if(!isExiting)
	    {
	    	Label infoMessage = new Label("File is successfully saved as "+filePath);;
		    infoMessage.setStyle("-fx-font-size:15;-fx-padding:10;");
		    Label openLink = new Label("Click Here To Open");
		    openLink.setCursor(Cursor.HAND);
		    openLink.setStyle("-fx-font-size:15;-fx-padding:10;-fx-text-fill:#3498db;");
		    openLink.setOnMouseClicked(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					try {
						Runtime.getRuntime().exec("explorer.exe /select," + filePath);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			});
		    VBox vbox = new VBox(infoMessage,openLink);
		    Alert alert = new Alert(AlertType.INFORMATION);
		    alert.setTitle("Success");
		    alert.setHeaderText("Congratulation");
		    alert.getDialogPane().setContent(vbox);
		    alert.show();
	    }
	}
}
