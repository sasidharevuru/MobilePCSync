/*
 * Authors ::
 * Group : Networking 1
 * Venkatesh Avula
 * Sasidhar Evuru
 * */

package com.codeoncloud.androidclient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DownloadActivity extends ActionBarActivity {

	private EditText fileName;
	public final String FOLDER_NAME="MsgFolder";
	public   String FILE_NAME;
	BufferedReader readFile;
	private Socket socket;
	private OutputStream out;
	private static int SERVERPORT ;
	private static   String SERVER_IP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		Intent intent = getIntent();
		// reading the port and ip address of the server from the previous activity.
		SERVERPORT = Integer.parseInt(intent.getStringExtra(MainActivity.PORT_NUMBER)); 
		SERVER_IP = intent.getStringExtra(MainActivity.IP_ADDDR);
		// A socket will be opened in a separate thread.
		new Thread(new ClientThread()).start();
		while(true)
		{
			if(socket!=null)
			{
				Toast.makeText(getApplicationContext(), "Connection established..",
						Toast.LENGTH_SHORT).show();
				break;	
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("NewApi")
	/*
	 * This method will be executed on the click of the exit button.
	 */
	public void exit(View v) {
	    try {
			socket.close(); // closing the socket when terminating the connection.
			finish();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "There was an error occured while closing connection ",
					Toast.LENGTH_SHORT).show();
		
		}	
	}
	
	/**
	 * This method will be executed when the user clicks on "sync" button.
	 * 
	 */
	@SuppressLint("NewApi")
	public void syncfile(View v) {
		fileName = (EditText)findViewById(R.id.editText1);
		String currentFile =fileName.getText().toString();
		FILE_NAME=currentFile.trim().length()>0?currentFile:FILE_NAME;
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
			File newFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
			          FOLDER_NAME);
			

			try 	 
			{
				long lastmodifieddate = -1;
				FileInputStream requestedfile = null;
				/*
				 * This code conatins the file name which is entered in the text box.
				 * */
				while (!currentFile.equals("Bye")) {
					
					File clientFile = new File(newFolder, FILE_NAME);	
					if (clientFile.exists())
					{
						// if the file already exists we just get the last modified date.
						lastmodifieddate = clientFile.lastModified();
					}
					else 
					{
						// if the file doesn't exist we create a new few file and keep time stamp as -1
						clientFile.createNewFile();
					}
					
					requestedfile =new FileInputStream(clientFile);

					((PrintStream) out).println("MyFile :"+ lastmodifieddate + ":"+ clientFile.length() + ":"+ clientFile.getName()); // writing into the server out buffer about the string which contains the file information.
						
					DataInputStream in = new DataInputStream(socket.getInputStream()); 
					String firstLine = in.readLine(); // listens for the servers response. 
					String firstString = firstLine.split(": ")[0];
					if ("MineIsObsolete".equalsIgnoreCase(firstString)) 
					{
						// client has the latest one so the client has to send the file to server.
						byte[] buffer = new byte[1];
						while ((requestedfile.read(buffer) != -1)) {
							out.write(buffer);
							out.flush();
						}
						requestedfile.close();
					} 
					else 
					{
						// server has the latest one and the server sends the latest file to client. 
						int size = Integer.parseInt(firstLine.split(": ")[1]);
						byte[] item = new byte[size];
						for (int i = 0; i < size; i++)
							item[i] = in.readByte(); // reading in bytes from server.
						// write file in client 
						FileOutputStream serverFile = new FileOutputStream(
								clientFile);
						BufferedOutputStream bos = new BufferedOutputStream(
								serverFile);
						bos.write(item); // writing the content in item to the file. 
						bos.close();
						serverFile.close();
					}
					currentFile = "Bye"; // after the file transfer is finished sending the bye from the client side.
					}
				((PrintStream) out).println("Bye");
				out.close();
				socket.close();
				Toast.makeText(getApplicationContext(), "Transfer is done and closing ..",
						Toast.LENGTH_SHORT).show();
				finish();
						
			} catch (UnknownHostException e) {
				Toast.makeText(getApplicationContext(), "Don't know about host",
				Toast.LENGTH_SHORT).show();
				System.err.println("Don't know about host " );
				
				System.exit(1);
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Don't know about host",
						Toast.LENGTH_SHORT).show();
				System.exit(1);
			}
			
			

	}
	 class ClientThread implements Runnable {
		  
		  
         @Override
         public void run() {
  
             try {
                 InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
  
                 socket = new Socket(serverAddr, SERVERPORT); // opening the socket
                 out = new PrintStream(socket.getOutputStream(),
 						true);
                 new DataInputStream(socket.getInputStream());
             } catch (UnknownHostException e1) {
                 e1.printStackTrace();
             } catch (IOException e1) {
                 e1.printStackTrace();
             }
  
         }
  
     }

}
