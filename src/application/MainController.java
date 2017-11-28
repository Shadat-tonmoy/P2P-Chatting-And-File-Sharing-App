package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import message.*;

public class MainController extends Thread{
	
	private Main main;
	private Parent root;
	private FXMLLoader fxmlLoader;
	@FXML private Button submitButton,resetButton;
	@FXML private TextField ipAddrField,portField,clientPortField;
	private String ipAddressToConnect,portToConnect,portToListen;
	@FXML private Label portNoToListenEmptyMessage,portNoEmptyMessage,ipaddrEmptyMessage;
	@FXML public  Label message,connectedToLabel,listeningAtLabel;
	@FXML private TextArea messageSendBox;
	private boolean isValid;
	private MessageReceiver messageReceiver;
	private MessageSender messageSender;
	private Thread conncetionChecker;
	@FXML private ImageView messageSendButton,fileAttachButton;
	private FileChooser fileChooser;
	private Stage stage;
	private File file;
	private byte [] byteArray;
	public MainController()
	{
		
	}
	public MainController(Main main) throws IOException
	{
		this.main = main;
		root = main.getRoot();
		initNodes();
		setOnClickListener();
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
					messageReceiver.setStage(stage);
					if(conncetionChecker.isAlive())
					{
						conncetionChecker.stop();
						System.out.println("Thread Closed");
						
					}
					else System.out.println("Thread not active");
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		if(conncetionChecker.isAlive())
//		{
//			conncetionChecker.stop();
//			System.out.println("Thread Closed");
//			
//		}
//		else System.out.println("Thread not active");
		
	}
	
	public void showChatScreen() throws IOException
	{
		fxmlLoader = new FXMLLoader(getClass().getResource("ChatScreen.fxml"));
		root = (Parent) fxmlLoader.load();
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
		System.out.println(portField);
		ipaddrEmptyMessage.setVisible(false);
		portNoEmptyMessage.setVisible(false);
		portNoToListenEmptyMessage.setVisible(false);
		
	}
	
	public void initChatScreenNodes(){
		
		listeningAtLabel = (Label) root.lookup("#listeningAtLabel");
		connectedToLabel = (Label) root.lookup("#connectedToLabel");
		messageSendButton = (ImageView) root.lookup("#messageSendButton");
		messageSendBox = (TextArea) root.lookup("#messageSendBox");
		connectedToLabel.setText("Sending Message at Port : "+messageSender.getSenderPort());
		listeningAtLabel.setText("Receiving Message at Port :"+messageReceiver.getReceiverPort());
		fileAttachButton = (ImageView) root.lookup("#fileAttachButton");
		
		
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
			        //fis = new FileInputStream(myFile);
			        //bis = new BufferedInputStream(fis);
			        //bis.read(mybytearray,0,mybytearray.length);
//			          os = sock.getOutputStream();
//			          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
//			          os.write(mybytearray,0,mybytearray.length);
//			          os.flush();
					
				}
				
				
			}
		});
	}
	
	
	
	public void getValues(){
		ipAddressToConnect = ipAddrField.getText().toString();
		portToConnect = portField.getText().toString();
		portToListen = clientPortField.getText().toString();
		
		
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
		System.out.println(isValid);
	}

	public void updateLabel()
	{
		message.setText("Connected");
	}
}
