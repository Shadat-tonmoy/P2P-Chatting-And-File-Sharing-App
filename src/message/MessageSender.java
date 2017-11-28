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
				System.out.println("Waiting from client");
				try{
					client = new Socket(destinationIP, destinationPort);
					
					
				} catch(IOException e){
					System.out.println("Port Not Found.Retrying....");
					Thread.sleep(1000);
				}
			}
			System.out.println("From Client : Just connected to " + client.getRemoteSocketAddress());
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
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("isFile", false);
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
		jsonObject.put("name", fileName);
		jsonObject.put("fileStream", Base64.getEncoder().encodeToString(byteArray));
		DataOutputStream out = new DataOutputStream(outToServer);
		out.writeUTF(jsonObject.toJSONString());
		out.flush();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				
				Label messageLabel = new Label("File Sent");
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
	
	public void setRoot(Parent root) {
		this.root = root;
		//message = (Label) root.lookup("#message");
		messageListView = (ScrollPane) root.lookup("#messageList");
		messageListView.setFitToWidth(true);
		//messageListView.setBackground(new Background(BackgroundFill));
		messageListView.setStyle("-fx-control-inner-background: black;");
	}

}
