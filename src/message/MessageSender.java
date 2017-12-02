package message;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import javax.annotation.processing.Messager;
import javax.swing.GroupLayout.Alignment;

import org.json.simple.JSONObject;

import application.MainController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MessageSender extends Thread{
	
	private String destinationIP;
	private int destinationPort;
	private Socket client;
	private Pane root;
	@FXML private ScrollPane messageListView;
	private String messageToSend,sender;
	private ArrayList<String> messageList;
	public MessageSender()
	{
		
	}
	public MessageSender(String destinationIP, int destinationPort)
	{	
		this.destinationIP = destinationIP;
		this.destinationPort = destinationPort;
		messageList = new ArrayList<String>();
	}
	@Override
	public void run() {
		try {
			System.out.println("From Client : Connecting to " + destinationIP + " on port " + destinationPort);
			client = null;
			while(client==null)
			{
				System.out.println("Waiting from client");
				try{
					client = new Socket(destinationIP, destinationPort);
					
					
				} catch(IOException e){
					System.out.println("Port Not Found.Retrying....");
					Thread.sleep(1000);
				}
			}
			System.out.println("From Client : Just connected to " + client.getRemoteSocketAddress());
//			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public int getSenderPort()
	{
		if(client!=null)
			return client.getPort();
		return -1;
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
		messageList.add(messageToSend);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("isFile", false);
		jsonObject.put("isColor", false);
		jsonObject.put("message", messageToSend);
		jsonObject.put("sender", sender);
		OutputStream outToServer = client.getOutputStream();
		DataOutputStream out = new DataOutputStream(outToServer);
		out.writeUTF(jsonObject.toJSONString());
		//out.writeBoolean(false);
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				
				Label messageLabel = new Label(messageToSend);
				Label senderLabel = new Label("You");
				MainController.allMessages.add(new String(sender+" : "+messageToSend));
				senderLabel.setFont(new Font(10));
				senderLabel.setStyle("-fx-padding:2;-fx-background-color:#2c3e50;");
				senderLabel.setTextFill(Color.WHITE);
				senderLabel.setVisible(false);
				
				messageLabel.setOnMouseEntered(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						senderLabel.setVisible(true);
						messageLabel.setOpacity(0.9);
					}
				});
				
				messageLabel.setOnMouseExited(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						senderLabel.setVisible(false);
						messageLabel.setOpacity(1.0);
					}
				});
				messageLabel.setCursor(Cursor.HAND);
				messageLabel.setFont(new Font(15));
				messageLabel.setStyle("-fx-background-color:#ecf0f1;-fx-padding:10;-fx-background-radius:8;");
				messageLabel.setTextFill(Color.BLACK);
				VBox messageInfo = new VBox(messageLabel,senderLabel);
				BorderPane borderPane = new BorderPane();
				borderPane.setRight(messageInfo);
				MessageReceiver.vbox.getChildren().add(borderPane);
				messageListView.setContent(MessageReceiver.vbox);
			}
		});
		
	}
	
	public void sendFile(byte[] byteArray,File file) throws IOException
	{
		//this.messageToSend = message;
		System.out.println("Sending file....");
//		
		FileInputStream fileInputStream = new FileInputStream(file);
		int i;
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        bufferedInputStream.read(byteArray,0,byteArray.length);
        String fileName = file.getName();
		OutputStream outToServer = client.getOutputStream();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("isFile", true);
		jsonObject.put("isColor", false);
		jsonObject.put("name", fileName);
		jsonObject.put("sender", sender);
		jsonObject.put("fileStream", Base64.getEncoder().encodeToString(byteArray));
		DataOutputStream out = new DataOutputStream(outToServer);
		out.writeUTF(jsonObject.toJSONString());
		out.flush();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				
				Label messageLabel = new Label(fileName+" is sent");
				//messageLabel.setPadding(new Insets(10,10,10,10));
				messageLabel.setFont(new Font(15));
				messageLabel.setStyle("-fx-background-color:#2ecc71;-fx-padding:10;-fx-background-radius:8;");
				messageLabel.setTextFill(Color.WHITE);
				Label senderLabel = new Label("You");
				senderLabel.setFont(new Font(10));
				senderLabel.setStyle("-fx-padding:2;-fx-background-color:#2c3e50;");
				senderLabel.setTextFill(Color.WHITE);
				senderLabel.setVisible(false);
				
				messageLabel.setOnMouseEntered(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						senderLabel.setVisible(true);
						messageLabel.setOpacity(0.9);
					}
				});
				
				messageLabel.setOnMouseExited(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						senderLabel.setVisible(false);
						messageLabel.setOpacity(1.0);
					}
				});
				VBox messageInfo = new VBox(messageLabel,senderLabel);
				BorderPane borderPane = new BorderPane();
				borderPane.setRight(messageInfo);
				MessageReceiver.vbox.getChildren().add(borderPane);
				messageListView.setContent(MessageReceiver.vbox);
				
			}
		});
	}
	
	public void sendBackgroundColor(String color) throws IOException
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("isColor", true);
		jsonObject.put("isFile", false);
		jsonObject.put("color", color);
		OutputStream outToServer = client.getOutputStream();
		DataOutputStream out = new DataOutputStream(outToServer);
		out.writeUTF(jsonObject.toJSONString());
		ObservableList<Node> observableList = MessageReceiver.vbox.getChildren();
    	for(Node i : observableList)
    	{
    		BorderPane gotBorderPane = (BorderPane) i;
    		VBox gotVBox = (VBox) gotBorderPane.getChildren().get(0);
    		Label sederLabel = (Label) gotVBox.getChildren().get(1);
    		if(!sederLabel.getText().equals("You"))
    		{
    			Label gotmessageLabel = (Label) gotVBox.getChildren().get(0);
    			
    			gotmessageLabel.setStyle("-fx-background-color:"+color+";-fx-padding:10;-fx-background-radius:8;");
    			System.out.println("Color is "+color);
    			
    		}
    			
    	}
	}
	
	
	public void setRoot(Pane root) {
		this.root = root;
		//message = (Label) root.lookup("#message");
		messageListView = (ScrollPane) root.lookup("#messageList");
		messageListView.setFitToWidth(true);
		//messageListView.setBackground(new Background(BackgroundFill));
		messageListView.setStyle("-fx-control-inner-background: black;");
	}
	public ArrayList<String> getMessageList() {
		return messageList;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	
	
	

}
