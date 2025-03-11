package httpfs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//uses tcp sockets to establish connection with client
public class MyHttpServer {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private int port;
	
	public MyHttpServer(int portNumber) {
		this.port = portNumber;
		try {
			this.serverSocket = new ServerSocket(portNumber);
			//Socket clientSocket = serverSocket.accept();
			//PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			//BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
	
	//waits for connection then reads input
	//returns httprequest for parsed request
	public HttpRequest Accept() {
		//String totalInput = "";
		HttpRequest request = new HttpRequest();
		
		try {
			clientSocket = serverSocket.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			int c;
			String line = "", method = "";
			boolean lf = false, terminate = false;
			int contentLength = 0;
			
			while (((c = in.read()) != -1) && terminate == false) {
				//totalInput += (char) c;
				line += (char) c;
				
				//if c is line feed
				if (c == 10) {
					lf = true;
				}
				
				//after reading a line feed process line
				if (lf) {
					//read type of request
					if ((method.length() == 0) && (line.length() >= 4)) {
						if (line.substring(0, 3).compareToIgnoreCase("get") == 0) {
							method = "get";
							request.SetMethod("get");
							request.SetPath(line.substring(4, line.indexOf("HTTP") - 1));
						} else if(line.substring(0, 4).compareToIgnoreCase("post") == 0) {
							method = "post";
							request.SetMethod("post");
							request.SetPath(line.substring(5, line.indexOf("HTTP") - 1));
						}
					//end type of request
					} else if ((line.length() >= 14) && (line.substring(0, 14).compareToIgnoreCase("content-length") == 0)) {
						contentLength = Integer.parseInt(line.substring(16, line.length()-1));
						request.AddHeader(line.substring(0, line.length()-1));
					//if the line is lf
					} else if (line.compareTo("\n") == 0) {
						if (method.compareTo("post") == 0) {
							//read body
							line = ""; //reset line
							for (int i = 0; i < contentLength; i++) {
								c = in.read();
								line += (char) c;
							}
							request.SetBody(line);
						}
						terminate = true;
						break;
					}else {
						request.AddHeader(line.substring(0, line.length()-1));
					}
					//reset line
					line = "";
					lf = false;
				}//end process line
			}//end read request
			
			
		} catch (IOException e) {
			System.out.println("Exception caught when trying to read from client on port " + port);
			System.out.println(e.getMessage());
		}
		//System.out.println("Server finished reading request from client");
		//return the message from client to file server
		return request;
	}
	
	//writes to tcp outputstream
	public void Send(HttpResponse response) {
		//System.out.println("Sending response: " + response.GetStatus());
		//receive optional headers and body to send to client through socket
		
		try {
			OutputStream outputStream = clientSocket.getOutputStream();
			
			out = new PrintWriter(outputStream, true);
			//write headers
			out.println(response.GetStatus());
			
			for (String header : response.GetHeaders()) {
				out.println(header);
			}
			
			out.println();
			
			//if the response has a body write it into socket
			if (response.HasBody()) {
				byte[] byteArray = response.GetBody();
				//write the file's bytes to the output stream
	        	outputStream.write(byteArray,0,byteArray.length);
			}
        	
        	//cleanup
        	out.flush();
        	outputStream.flush();
			
			//flush
			out.close();
			outputStream.close();
			
		} catch (IOException e) {
			System.out.println("Exception caught when trying to send response on port " + port);
			System.out.println(e.getMessage());
		}
		
	}
}
