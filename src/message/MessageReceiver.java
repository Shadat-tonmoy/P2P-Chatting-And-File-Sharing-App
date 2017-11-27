package message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import application.MainController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MessageReceiver extends Thread {
	

	private ServerSocket receiverSocket;
	private ArrayList<String> messageList;
	private String finalMessage;
	private boolean connected;
	private Socket receiver;
	private Parent root;
	private Label message;
	static VBox vbox;
	@FXML private ScrollPane messageListView;
	
	public MessageReceiver(int port) throws IOException {
		receiverSocket = new ServerSocket(port);
		receiverSocket.setSoTimeout(1000000);
		messageList = new ArrayList<String>();
		finalMessage = "";
		connected = false;
		vbox = new VBox();
		vbox.setSpacing(10);
		vbox.setMaxWidth(600);
		vbox.setMinWidth(570);
		
		//messageListView = new ListView<>();

		
			
	}
	
	
	@Override
	public void run() {

		System.out.println("From Server : Waiting for client on port " + receiverSocket.getLocalPort() + "... with address "+receiverSocket.getLocalSocketAddress());
		receiver = null;
		try {
			
			DataInputStream in;
			DataOutputStream out;
			
			while((receiver = receiverSocket.accept())!=null)
			{
			
				//receiver = receiverSocket.accept();
				if(receiver!=null)
				{
					connected = true;
					System.out.println("Active..."+Thread.activeCount());
					//MainController.message.setText("Connected");
				}
				
				//System.out.println("Received from "+receiver);
				while(true){
					in = new DataInputStream(receiver.getInputStream());
					String receivedMessage = in.readUTF();
					finalMessage = receivedMessage;
					if(finalMessage.length()>50)
					{
						int len = finalMessage.length();
						String partA = "", partB = "";
						for(int i=0;i<len;i+=50)
						{
							if(i>0)
							{
								partA = finalMessage.substring(0, i);
								partB = finalMessage.substring(i+1, len);
								finalMessage = partA + "\n" + partB;
							}
						}
					}
					//messageList.add(receivedMessage);
					
//					finalMessage = "";
//					for(int i=0;i<messageList.size();i++)
//					{
//						finalMessage+=messageList.get(i)+"\n";
//					}
					//System.out.println(message);
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							
							Label messageLabel = new Label(finalMessage);
							//messageLabel.setPadding(new Insets(10,10,10,10));
							messageLabel.setFont(new Font(15));
							messageLabel.setStyle("-fx-background-color:#e67e22;-fx-padding:10;-fx-background-radius:8;");
							messageLabel.setTextFill(Color.WHITE);
							BorderPane borderPane = new BorderPane();
							borderPane.setLeft(messageLabel);
							vbox.getChildren().add(borderPane);
							messageListView.setContent(vbox);
							//ObservableList<String> messages = messageListView.getItems();
							//for(int i=0;i<messages.size();i++)
							//	System.out.println(messages.get(i));
							
							
							//message.setText(finalMessage);
							//System.out.println("Running......"+message.getText());
							
						}
					});
					
					System.out.println(finalMessage);	
				}
				
				//out = new DataOutputStream(receiver.getOutputStream());
				//out.writeUTF("'Server' Said : message received");
				//out.close();
				//out.writeUTF(output);
				//receiver.close();
				//messageList.add(receivedMessage);
				
			}
			System.out.println("Dropped......."+receiver);
			//System.out.println("From Server : Just connected to " + receiver.getRemoteSocketAddress());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public int getReceiverPort() {
		return receiverSocket.getLocalPort();
	}
	
	public void setRoot(Parent root) {
		this.root = root;
		//message = (Label) root.lookup("#message");
		messageListView = (ScrollPane) root.lookup("#messageList");
		//messageListView.setBackground(new Background(BackgroundFill));
		messageListView.setStyle("-fx-control-inner-background: black;");
	}
	
//	public String getReceivedMessage()
//	{
//		
//		
//	}
	

}
