package de.bsesw.appserver;

public interface RequestHandler {
	
	String handle(String sessionid,int method,String data);
	
}
