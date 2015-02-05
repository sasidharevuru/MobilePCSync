
import java.net.*;
import java.io.*;

public class FileTransferServer {
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Please enter the server port number ");
		if (args.length != 1) {
			System.err.println("Usage: java KnockKnockServer <port number>");
			System.exit(1);
		}
		
		int portNumber = Integer.parseInt(args[0]); // assigning the port number for the server from console.
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber); // creating a server socket
			if(serverSocket != null)
			{
				System.out.println("Started server with port number :"+portNumber);
			}
			while(true)
			{ 
				// while true because it is a server and we don't want terminate it until we manually terminate it.
				
				Socket clientSocket = serverSocket.accept(); // the server keeps waiting at this point for the clients to send a socket connection request.
				
				String fileDirectory = "C:\\Server\\"; // default directory on th server where files are stored.
				
				OutputStream out = new PrintStream(clientSocket.getOutputStream(),true); // opening output stream with the client socket.
				
				DataInputStream in = new DataInputStream(clientSocket.getInputStream()); // opening input stream with the client socket.
				
				FileInputStream requestedfile = null;
				String receivedInputFromClient = null;
				receivedInputFromClient = in.readLine();
				/*
				 * At this point the client sends the server a request in the form of a string.
				 * If the request is for a file transfer the string contains file information.
				 * else if the request is for terminating the connection. The client sends "Bye" in the form of a string.
				 * */
				while (in!=null && !receivedInputFromClient.equals("Bye")) {
					String firstLineClient = receivedInputFromClient;
					long clientFileTS = Long.parseLong(firstLineClient.split(":")[1]); // gives the time stamp of the file the client sent.
					String filename = firstLineClient.split(":")[3]; // file name of the file the client sent
					
					// code to check whether the file is already existing in the given file path of the server
					String completeFilePath = fileDirectory+filename;
					File file = new File(completeFilePath);
					long serverFileTS = -1; // if the file is already existing we take the existing time stamp into this. Otherwise we create new file and take the timestamp as -1
					
					if (file.exists()) {
						serverFileTS = file.lastModified();
					} else {
						file.createNewFile(); // creating a new file if the file with that name doesn't exist.
					}
					
					byte[] buffer = new byte[1];
					if (serverFileTS <= clientFileTS) {
						
						// The server doesn't have the latest one. So requesting the client for the latest one.
						((PrintStream) out).println("MineIsObsolete: "+ file.length());

						
						
						int size = Integer.parseInt(firstLineClient.split(":")[2]);
						System.out.println("Started writing to server");
						byte[] item = new byte[size];
						for (int i = 0; i < size; i++)
							item[i] = in.readByte(); // reading data in bytes and storing it in item array.
						FileOutputStream clientfile = new FileOutputStream(completeFilePath);
						BufferedOutputStream bos = new BufferedOutputStream(clientfile);
						bos.write(item); // writing content into the file from item.
						System.out.println("done writing to server");
						bos.close();

					}
					else 
					{
						// The server have the latest one and it sends the client the latest file.
						requestedfile = new FileInputStream(completeFilePath);
						((PrintStream) out).println("Content-Length: "+ file.length());
						System.out.println("Started writing to client");
						while ((requestedfile.read(buffer) != -1)) 
						{
							out.write(buffer);
							out.flush();
						}
						System.out.println("Done writing to client");
					}
					receivedInputFromClient = in.readLine(); // after the file transfer the server waits for the "Bye" from the client.
				}
				// At this point we have to close all the connection we opened up with client because we are terminating the connection.
				if(requestedfile!=null)
				{
					requestedfile.close();
				}
				out.close();
				in.close();
				clientSocket.close();	
			}
		

		} catch (IOException e) {
			System.out
					.println("Exception caught when trying to listen on port "
							+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}

	}
}