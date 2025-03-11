package httpfs;

import java.util.ArrayList;

public class HttpResponse {
	private String status;
	private byte[] body;
	private boolean hasBody;
	private ArrayList<String> headers;
	
	public HttpResponse() {
		this.status = "";
		this.body = null;
		this.headers = new ArrayList<String>();
		this.headers.add("Server: httpfs");
		this.hasBody = false;
	}
	
	public void SetStatus(String newStatus) {
		this.status = newStatus;
	}
	public void SetBody(byte[] newBody) {
		this.body = newBody;
		this.hasBody = true;
	}
	public void AddHeader(String newHeader) {
		this.headers.add(newHeader);
	}
	
	public String GetStatus() {
		return this.status;
	}
	public byte[] GetBody() {
		return this.body;
	}
	public boolean HasBody() {
		return this.hasBody;
	}
	public ArrayList<String> GetHeaders(){
		return this.headers;
	}
}
