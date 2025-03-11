package httpfs;

import java.util.ArrayList;

//contains all the information related to a request received by the server
public class HttpRequest {
	private String method;
	private String path;
	private ArrayList<String> headers;
	private String body;
	private boolean hasBody;
	
	public HttpRequest() {
		this.method = "";
		this.path = "";
		this.body = "";
		this.headers = new ArrayList<String>();
		this.hasBody = false;
	}
	
	public void SetMethod(String newMethod) {
		this.method = newMethod;
	}
	
	public void SetPath(String newPath) {
		this.path = newPath;
	}
	
	public void SetBody(String newBody) {
		this.body = newBody;
		this.hasBody = true;
	}
	
	public void AddHeader(String newHeader) {
		headers.add(newHeader);
	}
	
	public String GetMethod() {
		return this.method;
	}
	public String GetPath() {
		return this.path;
	}
	public String GetBody() {
		return this.body;
	}
	public boolean HasBody() {
		return this.hasBody;
	}
	public ArrayList<String> GetHeaders(){
		return this.headers;
	}
	public String GetHeaderValue(String headerToGet) {
		for (String header : headers) {
			//compare left side of : to headerToGet
			if (header.substring(0, header.indexOf(':')).compareToIgnoreCase(headerToGet) == 0) {
				return header.substring(header.indexOf(':')+2);
			}
		}
		return "-1";
	}
}
