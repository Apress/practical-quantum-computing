package simple;

import java.io.IOException;

import org.json.JSONObject;

import ch06.io.SysRunner;

public class TestSysRunner {

    static void testPythonWin32 () throws IOException, InterruptedException {
        SysRunner r = new SysRunner("c:\\Python27\\python.exe C:\\Apache24\\cgi-bin\\qbattleship.py");
        //SysRunner r = new SysRunner("c:\\Python27\\python.exe -h");
        r.run();
        System.out.println("out=" + r.getStdOut());
        System.out.println("err=" + r.getStdErr());
        
    }
    
    public static void main(String[] args) {
        try {
            //testPythonWin32();
            //String json = "{'status': 200, 'message': 'Some text', 'damage': [[0,0,0,0,0],[0,0,0,0,0]]}";
            StringBuffer json = new StringBuffer();
            json.append("{\"status\": 200, \"message\":\"['/home/centos/apache-tomcat-8.5.6/webapps/BattleShip/WEB-INF/classes/../..//python/qbattleship-sim.py', '/home/centos/apache-tomcat-8.5.6/webapps/BattleShip/WEB-INF/classes/../..//python/qbattleship-sim.py']\", \"damage\":[[0.07947558763612506, 0.2718752773716122, 0.4991285047683087, 0.62758055548089, 0.9690984631261894], [0.2797695590216406, 0.5466411987348488, 0.15330268998140506, 0.18734101964412397, 0.7451779448087201]] }\n");
            JSONObject root = new JSONObject(json.toString());
            System.out.println("root=" + root);
            System.out.println("damage:" + root.getJSONArray("damage"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
