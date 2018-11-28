package ch06.io;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class IOTools {

    /**
     * OS Helper vars.
     */
    public static final String  OS_NAME                 = System.getProperty("os.name");
    public static final boolean OS_IS_WINDOWS           = OS_NAME.toLowerCase().contains("windows");
    public static final String  USER_HOME               = System.getProperty("user.home");

    public static final String  DEFAULT_ENCODING        = "UTF-8";

    /**
     * Get the base path of a resource available in the classpath
     * @return Full path of the class-path resource.
     * @throws UnsupportedEncodingException 
     */
    public static String getResourceAbsolutePath(String resourceName) throws UnsupportedEncodingException {
        URL url     = IOTools.class.getResource(resourceName);
        String path = URLDecoder.decode(url.getFile(), DEFAULT_ENCODING);
        
        // path -> Windows: /C:/Temp/Workspaces/.../30-LP-Genesys/
        // path-> Linux:    /home/users/foo...
        if ( path.startsWith("/") && OS_IS_WINDOWS) {
            // gotta remove the first / in Windows only!
            path = path.replaceFirst("/", "");
        }
        return path;
    }

    
    public static void setScriptPerms (String base) throws IOException, InterruptedException {
        // Special *&$# chars don't work 
        String cmd = "/bin/chmod 755 " + base +  "python" + File.separator; 
        
        String[] names = { "Qconfig.py", "qiskit-basic-test.py"
                , "qiskit-driver.sh", "qbattleship-sim.py", "qbattleship.py"};
        
        for (int i = 0; i < names.length; i++) {
            //System.out.println("Set permissions: " + cmd + names[i]);
            SysRunner r = new SysRunner(cmd + names[i]);
            r.run();
        }
    }

    public static StringBuffer executePython (String base, String args) throws IOException, InterruptedException {
        String driver  = base + File.separator + "python" + File.separator + "qiskit-driver.sh";
        String program =  base + File.separator + "python" + File.separator + "qbattleship.py";
        String cmd = driver + " " + program + ( args != null ? " " + args : "");
        
        //System.out.println("Execute Python: " + cmd );
        SysRunner r = new SysRunner(cmd);
        r.run();
        return r.getStdOut();
    }
    
    /**
     * Join an array of strings.
     * @param array Array of strings.
     * @param sep Join Separator.
     * @return
     */
    static public String join(String[] array, String sep) {
        if (array.length == 0) 
            return "";
        
        StringBuilder sb = new StringBuilder();
        int i;
        
        for( i=0; i < array.length - 1 ;i++)
            sb.append(array[i] + sep);
        
        return sb.toString() + array[i];
    }
    
    static public String join(Object[] array, String sep) {
        if (array.length == 0) 
            return "";
        
        StringBuilder sb = new StringBuilder();
        int i;
        
        for( i=0; i < array.length - 1 ;i++)
            sb.append(array[i] + sep);
        
        return sb.toString() + array[i];
    }

}
