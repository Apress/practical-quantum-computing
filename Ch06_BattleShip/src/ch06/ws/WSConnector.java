package ch06.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch06.io.IOTools;



@ServerEndpoint(value = "/WSBattleship") 
public class WSConnector {

	/** ALL Remote connections of type {@link WSConnector}. */
	//private static final Set<WSConnectionDescriptor> connections = new CopyOnWriteArraySet<WSConnectionDescriptor>();
    private static final List<WSConnectionDescriptor> connections = new CopyOnWriteArrayList<WSConnectionDescriptor>();
    
	private static final Map<String, JSONObject> data = new HashMap<String, JSONObject>();

	static void LOGD(String text) {
	    System.out.println("[DBG] " + text);
	}

	static void LOGW(String text) {
        System.out.println("[WRN] " + text);
    }
	
	static void LOGE(String text) {
        System.err.println("[ERR] " + text );
    }
	
    static void LOGE(String text, Throwable t) {
        System.err.println("[ERR] " + text );
        t.printStackTrace();
    }

	

	/** The client id for this WS */
	String clientId;
	

	/** Base folder of the python code */
	static String SCRIPT_ROOT;
	
	/**
	 * Construct: Note - the container will create 1 instance of this class for every WS client.
	 */
	public WSConnector() {

	    if ( !IOTools.OS_NAME.contains("Windows")) {
            try {
                // Get the base path for the python code
                // ...webapps/BattleShip/python/
                String root = IOTools.getResourceAbsolutePath("/") + "../../";
                
                if ( SCRIPT_ROOT != null) {
                    return;
                }
                //System.out.println("Script Root=" + root);
                IOTools.setScriptPerms(root);
                SCRIPT_ROOT = root;
            } catch (Exception e) {
                e.printStackTrace();
            }
	    }
	}
	
	private String getSessionParameter (Session session, String key) {
		if ( ! session.getRequestParameterMap().containsKey(key)) {
			return null;
		}
		return session.getRequestParameterMap().get(key).get(0);	
	}
	
	/**
	 * Send a message to a give WS {@link Session}.
	 * @param session The WS {@link Session}.
	 * @param message The message.
	 * @return true if the message was dispatched successfully else false.
	 */
	static boolean unicast (Session session, String message) {
		// java.lang.IllegalStateException: The WebSocket session has been closed and no method (apart from close()) may be called on a closed session
		try {
			if ( session.isOpen()) {
				session.getBasicRemote().sendText(message);
				return true;
			}
			else {
				LOGW("Session closed: Failed to dispatch " + message);
				return false;
			}
		} catch (IOException e) {
			LOGE("Unicast ", e);
			return false;
		}
	}
	
	@OnOpen
	public void open(Session session) {
		clientId = getSessionParameter(session, "clientId");
		/* 
		 * 11/20/2017 https://tomcat.apache.org/tomcat-7.0-doc/web-socket-howto.html
		 * The write timeout used when sending WebSocket messages in blocking mode defaults to 20000 milliseconds (20 seconds).
		 * http://nacr208.nacr.com:6091/issue/UNIFIED_CC-417
		 */
		session.getUserProperties().put("org.apache.tomcat.websocket.BLOCKING_SEND_TIMEOUT", 1500);
		
		LOGD("WSOpen: " + clientId ); 

		// no duplicates?
		WSConnectionDescriptor conn = findConnection(clientId); //, windowId);
		
		if ( conn != null) {
			unicast(conn.session, WSMessages.createStatusMessage(400, "Rejected duplicate session.").toString());
		}
		else {
			connections.add(new WSConnectionDescriptor(clientId,/* windowId,*/ session));
		} 
		dumpConnections("ONOPEN " +  clientId ); 
	}

	@OnClose
	public void end() {
		if ( !connections.remove(findConnection(clientId/*, windowId*/))) {
			LOGE("Unable to remove WS connection for " + clientId); // + "/" + windowId);
		}
		
		LOGD(String.format("WSClose: %s has disconnected.", clientId)); //, windowId));
		dumpConnections(String.format("ONCLOSE - %s", clientId)); //, windowId));
	}

	private void win32Simulate () throws JSONException {
        // TEST Create sample damage for both players [[0,0,0,0,0],[0,0,0,0,0]]
        JSONArray damage = new JSONArray();
        
        for (int i = 0; i < 2; i++) {
            JSONArray dplayer = new JSONArray();
            for (int j = 0; j < 5; j++) {
                dplayer.put(Math.random());
            }
            damage.put(dplayer);
        }
        // send Dummy {status: 200, message: '', damage: [x,x,x,x,x]}}
        for (int i = 0; i < connections.size(); i++) {
            JSONObject resp = WSMessages.createStatusMessage(200, "Executing python.");
            resp.put("damage", damage.get(i));
            unicast(connections.get(i).session, resp.toString());
        }
	}

	private String getArgs () throws JSONException {
	    // Gane state hash map
	    // Player-1 => {"name":"Player-1","ships":"0,1,2","bombs":"1,0,0,0,0","device":"local_qasm_simulator"}
	    // Player-2 => {"name":"Player-1","ships":"0,1,2","bombs":"1,0,0,0,0","device":"local_qasm_simulator"}
	    Iterator<JSONObject> it = data.values().iterator();
	    JSONObject p1 = it.next();
	    JSONObject p2 = it.next();

	    // ARGS: ships1 ships2 bombs1 bombs2 device
	    return p1.getString("ships") + " " + p2.getString("ships") 
	            + " " + p1.getString("bombs") + " " + p2.getString("bombs")
	            + " " + p1.getString("device");
	}
	
	private void linuxExecPython (String args) throws Exception {
        // Execute Python script
	    // STDOUT {status: 200, message: 'Some text', damage: [[0,0,0,0,0],[0,0,0,0,0]]}
        StringBuffer stdout = IOTools.executePython(SCRIPT_ROOT, args);
        JSONObject resp = new JSONObject(stdout.toString());

        System.out.println("*** GOT STDOUT: " + stdout + " JSON:" + resp);
        
        JSONArray damage = resp.getJSONArray("damage");
        resp.remove("damage");
        final int size = damage.length() - 1;
        
        for (int i = 0; i < connections.size(); i++) {
            resp.put("damage", damage.get( size - i));
            unicast(connections.get(i).session, resp.toString());
            resp.remove("damage");
        }
	    
	}
	
	@OnMessage
	public void incoming(String message) {
	    // {"name":"Alice","ships":"0,1,2","bombs":"1,0,0,0,0","device":"local_qasm_simulator"}
		LOGD("Message: " + message);

		// echo for now
		WSConnectionDescriptor d = findConnection(clientId); //, windowId);
		try {
	        JSONObject root = new JSONObject(message);
	        String name    = root.getString("name");
	        String action  = root.optString("action");
	        
	        if ( action.equalsIgnoreCase("reset")) {
	            multicat(WSMessages.createStatusMessage(300, "Game reset by " + name).toString());
	            data.clear();
	            return;
	        }
	        LOGD("JSON: " + root);
	        
	        data.put(clientId/* name*/, root);
	        int size = data.keySet().size();
	        
	        //System.out.println("** DATA=" + data + " SIZE=" + size);
	        
	        // send resp
	        if ( size < 2) {
	            d.session.getBasicRemote().sendText(WSMessages.createStatusMessage(300, "Waiting for other player to join").toString());
	            return;
	        }
	        if ( size > 2) {
	            d.session.getBasicRemote().sendText(WSMessages.createStatusMessage(400, "Only two players are allowed.").toString());
	            return;
	        }
	        // size == 2. All ok . execute python
	        String args = getArgs();
	        System.out.println("*** ARGS=" + args);
	        
	        if  ( IOTools.OS_IS_WINDOWS) {
	            win32Simulate();
	        }
	        else {
	            linuxExecPython(args);
	        }
	        
		} catch (Exception e) {
			LOGE("OnMessage", e);
	         if ( d != null ) {
	             try {
                    d.session.getBasicRemote().sendText(WSMessages.createStatusMessage(500, e.toString()).toString());
                } catch (IOException e1) {
                } 
	         } 
		}
	}
	
	@OnError
	public void onError(Throwable t) throws Throwable {
		/* CHROME
		 * java.io.IOException: java.util.concurrent.ExecutionException: java.net.SocketException: Software caused connection abort: socket write error
		 * 	at org.apache.tomcat.websocket.WsRemoteEndpointImplBase.startMessageBlock(WsRemoteEndpointImplBase.java:243)
		 * 	at org.apache.tomcat.websocket.WsSession.sendCloseMessage(WsSession.java:487)
		 */
		LOGE("WSError: " + t.toString());
	}

	WSConnectionDescriptor findConnection ( String clientId /*, String windowId*/) {
		for ( WSConnectionDescriptor conn : connections) {
			if ( conn.clientId.equals(clientId) /*&& conn.windowId.equals(windowId)*/) {
				return conn;
			}
		}
		return null;
	}

	private void dumpConnections(String label) {
		int count 			= 0;
		StringBuffer buf 	= new StringBuffer();
		buf.append(" -- WS Connections " + label + " --\n");
		
		for ( WSConnectionDescriptor conn : connections) {
			buf.append("[" + (count++) + "] " + conn + "\n");
		}
		buf.append(" -- End WS Connections --");
		LOGD("<websocket>\n"  + buf.toString() + "</websocket>");
	}
	
	static void multicat ( String message ) {
	    for ( WSConnectionDescriptor conn : connections) {
	        unicast(conn.session, message);
	    }
	}
	
}

