package noddyserver; 

import java.net.*;
import java.io.*;
import java.util.*;

public class NoddyServer extends Thread
{
    private static final String[] pageTop =
    {
        "Content-Type: text/html",
        "<meta http-equiv='Cache-Control' content='no-cache, no-store, must-revalidate'/>",
        "<meta http-equiv='Pragma' content='no-cache'/>",
        "<meta http-equiv='Expires' content='0'/>",
        "",
        "",
        "<html>",
        "<head></head>",
        "<body>",
        "DaSync",
        "<br><hr><br>"
    };

    private static final String divider = "<br><hr><br>";
    
    private static final String[] pageMid =
    {
        "<br><hr><br>",
        "<form action='/insert' method='get'>",
        "<input type='text' name='license' size='32' maxlength='32' value="
    };

    private static final String[] pageBot =
    {
        "/><br>",
        "<input type='submit'/>",
        "</form>",
        
        "<form action='/reset' method='post'>",
        "<input type='submit' value='Reset Table' />",
        "</form>",

        "<form action='/list' method='post'>",
        "<input type='submit' value='List Table' />",
        "</form>",

        "</body>",
        "</html>",
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

            ArrayList<String> results = new ArrayList<>();
            ArrayList<String> errors  = new ArrayList<>();

            String result = RequestHack.response(db, in, errors, results);
            
            out.println(result);

            for (String html : pageTop)
                out.println(html);

            for (String html : results)
            {
                out.println(html);
                out.println("<br>");
            }

            out.println(divider);

            for (String html : errors)
            {
                out.println(html);
                out.println("<br>");
            }

            for (String html : pageMid)
                out.println(html);

            out.println("'"+license+"'");

            for (String html : pageBot)
                out.println(html);

            out.flush();
            socket.shutdownOutput();
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
            String       dsn  = args[0];
            DBHack       db   = new DBHack(dsn);

            String       port = args[1];
            ServerSocket S    = new ServerSocket(Integer.valueOf(port));

            Random       rnd  = new Random(45829);

            while(true)
            {
                NoddyServer mainThread = new NoddyServer(db, S.accept(), String.valueOf(rnd.nextLong()));
//	        mainThread.run();
	        mainThread.start();
            }
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }
}
