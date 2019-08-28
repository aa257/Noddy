package noddyserver;

import java.io.*;
import java.util.*;

public class RequestHack
{
    public static String firstLine(BufferedReader bR) throws Exception
    {
        String inputLine = bR.readLine();
        String result = inputLine;
            
        while (true)
        {
            if (inputLine == null)
                break;
                
            if (inputLine.length() == 0)
                break;

            inputLine = bR.readLine();
        }

        return result;
    }
    
    public static String parse(String string, char begin, char end)
    {
        char[] chars = string.toCharArray​();
        StringBuffer result = new StringBuffer();

        int i = 0;
        int I = chars.length;
        char c;

        for (; i < I; i++)
        {
            c = chars[i];
            
            if (c != begin)
                continue;

            for (i++; i < I; i++)
            {
                c = chars[i];
                
                if (c == end)
                {
                    i = I;
                    break;
                }
                
                result.append(c);
            }
        }
        
        return result.toString();
    }

    public static RequestType getRequestType(String request)
    {
        String A = parse(request,'/',' ');
        String a = A.toLowerCase();

        if (a.equals("reset"))
            return RequestType.RESET;

        if (a.equals("list"))
            return RequestType.LIST;

        String B = parse(request,'/','=');
        String b = B.toLowerCase();

        if (b.equals("insert?license"))
            return RequestType.INSERT;
        
        return RequestType.BAD;
    }

    public static String getLicense(String L) throws Exception
    {
        String result = parse(L,'=',' ');

        if (result.length() < 1)
            throw new Exception("Bad License Parameter");

        return result;
    }
            
    
    public static String response(DBHack dB, BufferedReader bR, ArrayList<String> errors, ArrayList<String> results)
    {
        results.clear();
        errors.clear();

        String request;
        
        try
        {
            request = firstLine(bR);
        }
        catch(Exception x)
        {
            results.add("bad request");
            errors.add(x.getLocalizedMessage());
            return "HTTP/1.1 400 Bad Request";
        }

        String newLicense;
        RequestType rT = getRequestType(request);

        if (rT == RequestType.INSERT)
        try
        {
            newLicense = getLicense(request);
            results.add("insert");
            results.add(newLicense);
            dB.insert(newLicense, errors, results);
        }
        catch(Exception x)
        {
            results.add("bad request");
            errors.add(x.getLocalizedMessage());
            return "HTTP/1.1 400 Bad Request";
        }
        
        if (rT == RequestType.LIST)
        {
            results.add("list");
            dB.list(errors, results);
        }

        if (rT == RequestType.RESET)
        {
            results.add("reset");
            dB.recreate(errors, results);
        }

        if (results.isEmpty())
        {
            results.add("bad request");
            return "HTTP/1.1 400 Bad Request";
        }

        if (errors.isEmpty())
        {
            errors.add("success");
            return "HTTP/1.1 200 OK";
        }

        return "HTTP/1.1 500 Internal Server Error";
    }
}
