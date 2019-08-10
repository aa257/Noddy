package noddyserver;

import java.io.BufferedReader;

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
    
    public static String parse(String string)
    {
        char[] chars = string.toCharArray​();
        StringBuffer result = new StringBuffer();

        boolean match = false;
        for (char c : chars)
        {
            if (match)
                result.append(c);
            
            if (c == '=')
                match = true;

            if (match)
                if (c == ' ')
                    break;
        }
        
        return result.toString();
    }
}
