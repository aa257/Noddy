package noddyserver;

import java.sql.*;
import java.util.*;

public class DBHack
{
    private Connection conn;

    public DBHack(String url) throws Exception // leak database resources, don't try to clean up on failure
    {
        conn = DriverManager.getConnection(url);
    }

    public void list(ArrayList<String> errors, ArrayList<String> results)
    {
        PreparedStatement stmt = null;

        try
        {
            stmt = conn.prepareStatement("select license from noddysync");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                results.add(rs.getString("license"));
        }
        catch (Exception x)
        {
            errors.add(x.getLocalizedMessage());
        }
        
        try
        {
            stmt.close();
        }
        catch (Exception x)
        {
            errors.add(x.getLocalizedMessage());
    	}
    }

    public void recreate(ArrayList<String> errors, ArrayList<String> results)
    {
        PreparedStatement a = null;
        PreparedStatement b = null;

        try
        {
            a = conn.prepareStatement("drop table noddysync");
            a.executeUpdate();
        }
        catch (Exception x)
        {
            errors.add(x.getLocalizedMessage());
        }
        
        try
        {
            b = conn.prepareStatement("create table noddysync(license text)");
            b.executeUpdate();
        }
        catch (Exception x)
        {
            errors.add(x.getLocalizedMessage());
        }

        try
        {
            a.close();
            b.close();
        }
        catch (Exception x)
        {
            errors.add(x.getLocalizedMessage());
    	}
    }

    
    public void insert(String license, ArrayList<String> errors, ArrayList<String> results)
    {
        PreparedStatement stmt = null;

        try
        {
            stmt = conn.prepareStatement("insert into noddysync values(?)");
            stmt.setString(1, license);
            stmt.executeUpdate();
        }
        catch (Exception x)
        {
            errors.add(x.getLocalizedMessage());
        }
        
        try
        {
            stmt.close();
        }
        catch (Exception x)
        {
            errors.add(x.getLocalizedMessage());
    	}
    }
}
