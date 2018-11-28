package ch06.ws;

import org.json.JSONObject;


public class WSMessages {
	
	static JSONObject createStatusMessage(String type, int status, String message) { 
		try {
			JSONObject root = new JSONObject();
			root.put("type", type);
			root.put("status", status);
			root.put("message", message);
			return root;
		} catch (Exception e) {
			WSConnector.LOGE("createStatusMessage " + type + " " + message,  e);
			return null;
		}
	}

    static JSONObject createStatusMessage(int status, String message) { 
        try {
            JSONObject root = new JSONObject();
            root.put("status", status);
            root.put("message", message);
            return root;
        } catch (Exception e) {
            WSConnector.LOGE("createStatusMessage " + message,  e);
            return null;
        }
    }
	
}
