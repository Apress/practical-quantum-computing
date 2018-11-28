package simple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch06.io.SysRunner;

/**
 * Servlet implementation class WebTestSysRunner
 */
@WebServlet("/TestSysRunner")
public class WebTestSysRunner extends HttpServlet {
	private static final long serialVersionUID = 1L;

	final String OS_NAME = System.getProperty("os.name");
	String root;
	String driver;
	String program;
	
	@Override
	public void init() throws ServletException {
	    super.init();
	    root = getServletContext().getRealPath("/");
	    driver = root + File.separator + "python" + File.separator + "qiskit-driver.sh";
	    program = root + File.separator + "python" + File.separator + "qiskit-basic-test.py";
        System.out.println("PATH=" + root + " driver=" + driver + " OS=" + OS_NAME);
	    
	    // must set perms in scripts chmod 755 python/*
        if ( !OS_NAME.contains("Windows")) {
    	    try {
                setScriptPerms(root);
            } catch (Exception e) {
                throw new ServletException("Failed to set permissions @ " + root, e);
            }
        }
	}
	
	private void setScriptPerms (String base) throws IOException, InterruptedException {
	    // Special *&$# chars don't work 
	    String cmd = "/bin/chmod 755 " + base +  "python" + File.separator; // + "*";
	    
	    String[] names = { "Qconfig.py", "qiskit-basic-test.py", "qiskit-driver.sh"};
	    
	    for (int i = 0; i < names.length; i++) {
	        System.out.println("Set permissions: " + cmd + names[i]);
	        SysRunner r = new SysRunner(cmd + names[i]);
	        r.run();
        }
	}
	
    /**
     * Default constructor. 
     */
    public WebTestSysRunner() {
        // TODO Auto-generated constructor stub
    }

    private StringBuffer joinArgs(HttpServletRequest request) {
        Enumeration<String> names = request.getParameterNames();
        StringBuffer args = new StringBuffer();
        
        while ( names.hasMoreElements()) {
            String name = names.nextElement();
            String val = request.getParameter(name);
            args.append(val  + " ");
        }
        return args;
    }
    
    private void testShellExecution (HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cmd = joinArgs(request).toString();
        StringBuffer resp = new StringBuffer();
        resp.append("CMD=" + cmd);
        SysRunner r = new SysRunner(cmd.toString());
        try {
              r.run();

        } catch (Exception e) {
            resp.append(e.toString());
        }
        resp.append(r.getStdOut());
        response.getWriter().print(resp.toString());
    }
    
    private void testDriver (HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuffer args = joinArgs(request);
        String cmd = driver + " " + program + " " + args.toString();
        
        System.out.println("Exec " + cmd);
        
        StringBuffer resp = new StringBuffer();
        SysRunner r = new SysRunner(cmd.toString());
        try {
              r.run();
        } catch (Exception e) {
            resp.append(e.toString());
        }
        resp.append(r.getStdOut());
        resp.append(r.getStdErr());
        response.getWriter().print(resp.toString());
    }
    
    void testFakeDamageDataWin32(HttpServletResponse response) throws IOException {
        String program =  root + File.separator + "python" + File.separator + "qbattleship-sim.py";
        //String cmd = "C:/Python27/python.exe " + program;
        String cmd = "python.exe " + program;
        
        StringBuffer resp = new StringBuffer();
        SysRunner r = new SysRunner(cmd.toString());
        try {
              r.run();
        } catch (Exception e) {
            resp.append(e.toString());
        }
        resp.append(r.getStdOut());
        resp.append(r.getStdErr());
        response.getWriter().print(resp.toString());
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * http://localhost:8080/BattleShip/TestSysRunner
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    //testShellExecution(request, response);
	    //testDriver(request, response);
	    testFakeDamageDataWin32(response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
