package ch06.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SysRunner {
    //ProcessBuilder builder ;
    final String command;
    final StringBuffer stdout = new StringBuffer();
    final StringBuffer stderr = new StringBuffer();
    
    public SysRunner(String command) {
        this.command = command;
    }
    
    public void run () throws IOException, InterruptedException {
        /*
         * java.io.IOException: Cannot run program "c:\Python27\python.exe C:\Apache24\cgi-bin\qbattleship.py": CreateProcess error=2, The system cannot find the file specified
    at java.lang.ProcessBuilder.start(Unknown Source)
    at ch06.SysRunner.run(SysRunner.java:20)
         */
        /*
        ProcessBuilder builder = new ProcessBuilder(command);
        System.out.println(builder.environment());
        final Process process = builder.start(); */
        final Process process   = Runtime.getRuntime().exec(command);
        pipeStream(process.getInputStream(), stdout);
        pipeStream(process.getErrorStream(), stderr);
        process.waitFor();
    }
    
    private void pipeStream (InputStream is, StringBuffer buf) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
    }
    
    public StringBuffer getStdOut () {
        return stdout;
    }

    public StringBuffer getStdErr () {
        return stderr;
    }
    
}
