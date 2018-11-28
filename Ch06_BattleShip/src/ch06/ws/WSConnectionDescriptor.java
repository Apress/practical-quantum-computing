package ch06.ws;

import javax.websocket.Session;

final class WSConnectionDescriptor {
	String clientId;
	Session session;
	
	public WSConnectionDescriptor(String clientId, /*String windowId,*/ Session session) {
		super();
		this.clientId = clientId;
		this.session = session;
	}
	
	@Override
	public String toString() {
	    return String.format("%s %s", clientId,  session.getRequestURI());
	}
}