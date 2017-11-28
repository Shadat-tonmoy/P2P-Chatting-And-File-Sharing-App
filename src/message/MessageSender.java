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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MessageSender extends Thread{
	
	private String destinationIP;
	private int destinationPort;
	private Socket client;
	private Pane root;
	@FXML private ScrollPane messageListView;
	private String messageToSend;
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
		OutputStream outToServer = client.getOutputStream();
		DataOutputStream out = new DataOutputStream(outToServer);
		out.writeUTF(jsonObject.toJSONString());
		//out.writeBoolean(false);
		
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
				BorderPane borderPane = new BorderPane();
				borderPane.setRight(messageLabel);
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
	
	

}
