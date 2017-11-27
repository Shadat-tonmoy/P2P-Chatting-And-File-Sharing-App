package message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.annotation.processing.Messager;
import javax.swing.GroupLayout.Alignment;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MessageSender extends Thread{
	
	private String destinationIP;
	private int destinationPort;
	private Socket client;
	private Parent root;
	@FXML private ScrollPane messageListView;
	private String messageToSend;
	public MessageSender()
	{
		
	}
	public MessageSender(String destinationIP, int destinationPort)
	{	
		this.destinationIP = destinationIP;
		this.destinationPort = destinationPort;
	}
	@Override
	public void run() {
		try {
			System.out.println("From Client : Connecting to " + destinationIP + " on port " + destinationPort);
			client = null;
			while(client==null)
			{
				try{
					client = new Socket(destinationIP, destinationPort);
					
				} catch(IOException e){
					System.out.println("Port Not Found.Retrying....");
					Thread.sleep(1000);
				}
			}
//			System.out.println("From Client : Just connected to " + client.getRemoteSocketAddress());
//			OutputStream outToServer;
//			DataOutputStream out;
//			InputStream inFromServer;
//			DataInputStream in;
//			//while (true) {
//				System.out.println("Enter a message to send to Server");
//				Scanner sc = new Scanner(System.in);
//				String inputLine = sc.nextLine();
//				outToServer = client.getOutputStream();
//				out = new DataOutputStream(outToServer);
//				out.writeUTF("'Client' Said : " + inputLine);
//				out.flush();// + " at address " + client.getLocalSocketAddress());
//				inFromServer = client.getInputStream();
//				System.out.println(inFromServer);
//				in = new DataInputStream(inFromServer);
//				System.out.println(in);
				
				//System.out.println(in.readUTF());
				//in.close();
				//out.close();
				//in.close();
				//out.close();
				//client.close();
			//}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public int getSenderPort()
	{
		return client.getPort();
	}
	
	public void sendMessage(String message) throws IOException
	{
		this.messageToSend = message;
		System.out.println("Sending messsage....");
		if(messageToSend.length()>50)
		{
			int len = messageToSend.length();
			String partA = "", partB = "";
			for(int i=0;i<len;i+=50)
			{
				if(i>0)
				{
					partA = messageToSend.substring(0, i);
					partB = messageToSend.substring(i+1, len);
					messageToSend = partA + "\n" + partB;
				}
			}
		}
		OutputStream outToServer = client.getOutputStream();
		DataOutputStream out = new DataOutputStream(outToServer);
		out.writeUTF(message);
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				
				Label messageLabel = new Label(messageToSend);
				//messageLabel.setPadding(new Insets(10,10,10,10));
				messageLabel.setFont(new Font(15));
				messageLabel.setStyle("-fx-background-color:#2ecc71;-fx-padding:10;-fx-background-radius:8;");
				messageLabel.setTextFill(Color.WHITE);
				BorderPane borderPane = new BorderPane();
				borderPane.setRight(messageLabel);
				MessageReceiver.vbox.getChildren().add(borderPane);
				messageListView.setContent(MessageReceiver.vbox);
				//ObservableList<String> messages = messageListView.getItems();
				//for(int i=0;i<messages.size();i++)
				//	System.out.println(messages.get(i));
				
				
				//message.setText(finalMessage);
				//System.out.println("Running......"+message.getText());
				
			}
		});
//		InputStream inFromServer = client.getInputStream();
//		System.out.println(inFromServer);
//		DataInputStream in = new DataInputStream(inFromServer);
//		System.out.println(in);
		
	}
	
	public void setRoot(Parent root) {
		this.root = root;
		//message = (Label) root.lookup("#message");
		messageListView = (ScrollPane) root.lookup("#messageList");
		messageListView.setFitToWidth(true);
		//messageListView.setBackground(new Background(BackgroundFill));
		messageListView.setStyle("-fx-control-inner-background: black;");
	}

}
