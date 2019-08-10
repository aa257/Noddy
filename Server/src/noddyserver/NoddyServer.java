package noddyserver; 

import java.net.*;
import java.io.*;
import java.util.*;

public class NoddyServer extends Thread
{
    private String[] pageTop =
    {
        "HTTP/1.1 200 OK",
        "Content-Type: text/html",
        "<meta http-equiv='Cache-Control' content='no-cache, no-store, must-revalidate'/>",
        "<meta http-equiv='Pragma' content='no-cache'/>",
        "<meta http-equiv='Expires' content='0'/>",
        "",
        "",
        "<html>",
        "<head></head>",
        "<body>",
        "<form action='/noddy_cgi' method='get'>",
        "<input type='text' name='license' size='32' maxlength='32' value="
    };

    private String[] pageMid =
    {
        "/><br>",
        "<input type='submit'/>",
        "</html>",
        "</form><br><hr><br>"
    };   

    private String[] pageBot =
    {
        "</body>"
    };   
    
    private Socket socket  = null;
    private String license = null;
    private DBHack db      = null;

    public NoddyServer(DBHack db, Socket socket, String license)
    {
        super("NoddyThread");
        this.socket  = socket;
        this.license = license;
        this.db      = db;
    }
    
    public void run()
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            for (String html : pageTop)
                out.println(html);

            out.println("'"+license+"'");

            for (String html : pageMid)
                out.println(html);

            String getRequest = RequestHack.firstLine(in);
            String newLicense = RequestHack.parse(getRequest);
            
            if (newLicense.length() > 0)
                db.insert(newLicense);

            // output here?
            
            for (String html : pageBot)
                out.println(html);

            out.flush();
            socket.close();
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }

    
    public static void main(String[] args) throws IOException
    {
        try
        {
            String       url = args[0];
            DBHack       db  = new DBHack(url);
            ServerSocket S   = new ServerSocket(80);
            Random       rnd = new Random(45829);

            while(true)
            {
                NoddyServer mainThread = new NoddyServer(db, S.accept(), String.valueOf(rnd.nextLong()));
	        mainThread.run();
//	        mainThread.start();
            }
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }
}
