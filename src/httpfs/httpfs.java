package httpfs;

import java.io.File;

//this class manages instances of file servers
public class httpfs {

	//create a file server
	public static void main(String[] args) {
		boolean verbose = false;
		int portNumber = 8080;
		String rootDirectory = System.getProperty("user.dir");
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].compareTo("-v") == 0) {
				verbose = true;
				
			}else if(args[i].substring(0, 2).compareTo("-p") == 0) {
				i++;
				portNumber = Integer.parseInt(args[i]);
			}else if(args[i].substring(0, 2).compareTo("-d") == 0) {
				i++;
				rootDirectory = args[i];
			}
		}
		
		//check if working directory exists
		File f = new File(rootDirectory + "/FileServer");
		
		if (!f.exists() || !f.isDirectory()) {
			f.mkdirs();
		}
		
		rootDirectory = f.getAbsolutePath();
		
		HttpFileServer fs = new HttpFileServer(portNumber, rootDirectory, verbose);
		
		//tells server to start listening
		//the server will continue to listen forever
		while(true) {
			fs.Start();
		}
		
	}

}
