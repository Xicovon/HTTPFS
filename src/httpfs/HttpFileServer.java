package httpfs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

//this class is the server 
public class HttpFileServer {
	private boolean verbose;
	
	//tcp connection
	MyHttpServer httpServer;
	
	//root dir
	String rootDirectory;

	//constructor creates socket on specified port
	public HttpFileServer(int port, String path, boolean v) {
		this.httpServer = new MyHttpServer(port);
		this.rootDirectory = path;
		this.verbose = v;
	}
	
	//listens on port
	//reads input
	//sends a response
	public void Start() {
		HttpRequest request;
		
		request = this.httpServer.Accept();
		
		if (verbose) {
			System.out.println("File Server Received " + request.GetMethod() + " Request");
			System.out.println("Server is processing request");
		}
		
		//read input and do operations
		//formulate response
		HttpResponse response;
		
		if (request.GetMethod().compareTo("get") == 0) {
			response = Get(request);
		} else {
			response = Post(request);
		}
		
		
		//send response
		if (verbose) {
			System.out.println("Server sending Response to Client");
		}
		this.httpServer.Send(response);
	}
	
	//process a get request
	public HttpResponse Get(HttpRequest request) {
		
		if (verbose) {
			System.out.println("Client is requesting " + request.GetPath());
		}
		
		//object to begin crafting response
		HttpResponse response = new HttpResponse();
		
		File f = new File(rootDirectory + request.GetPath());
		
		if (f.exists()) {
			response.SetStatus("HTTP/1.1 200 OK");
			response.AddHeader("Status: 200");
			if (verbose) {
				System.out.println("Resource exists");
			}
			
			if (f.isDirectory()) {
				String responseBody = "";
				System.out.println(f.getAbsolutePath() + " is a directory");
				response.AddHeader("Content-Type: text");
				
				//create list of resources in directory
				for (final File file : f.listFiles()) {
					if (file.isDirectory() || file.isFile()) {
						responseBody += file.getAbsolutePath().substring(f.getAbsolutePath().length()+1) + "\n";
					}
				}
				
				byte[] byteArray = responseBody.getBytes();
				
				//put list of resources in body as byte array
				response.SetBody(byteArray);
				
				//add header for length of file
	        	response.AddHeader("Content-Length: " + byteArray.length);
				
			}else if (f.isFile()) {
				System.out.println(f.getAbsolutePath() + " is a file");
				response.AddHeader("Content-Type: text/html");
				
				try {
					FileInputStream fis = new FileInputStream(f);
					BufferedInputStream bis = new BufferedInputStream(fis);
					byte[] byteArray = new byte [(int)f.length()];
		        	bis.read(byteArray,0,byteArray.length);
		        	
		        	//add header for length of file
		        	response.AddHeader("Content-Length: " + byteArray.length);
		        	
		        	//put file in body of response
		        	response.SetBody(byteArray);
		        	
		        	bis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
	        	
			}
		}else {
			//the resource does not exist
			if (verbose) {
				System.out.println("Resource does not exist");
			}
			response.SetStatus("HTTP/1.1 404 Not Found");
			response.AddHeader("Status: 404");
		}
		return response;
		//if requested resource is a file
		//read file into buffer and send
		
		//if requested resource is a directory return list of files and directories in directory
		//send list according to requested data type
	}
	
	//process a post request
	public HttpResponse Post(HttpRequest request) {
		HttpResponse response = new HttpResponse();
		response.SetStatus("HTTP/1.1 200 OK");
		response.AddHeader("Status: 200");
		
		if (verbose) {
			System.out.println("Client is requesting to POST to " + request.GetPath());
		}
		
		//should create or overwrite the file specified
		
		File f = new File(rootDirectory + request.GetPath());
		
		File parentDir = new File(f.getParent());
		
		//if the directory directly above the requested file does not exist create all directories leading up to it
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		
		//write body from request into new file
		try {
			FileWriter writer = new FileWriter(f);
			writer.write(request.GetBody());
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (verbose) {
			System.out.println("Server created file: " + rootDirectory + request.GetPath());
		}
		
		return response;
	}
}
