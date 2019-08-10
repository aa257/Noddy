package noddyserver;

import java.sql.*;

public class DBHack
{
    private Connection conn;
    private String sql = "insert into noddysync values(?)";

    public DBHack(String url) throws Exception // leak database resources, don't try to clean up on failure
    {
        conn = DriverManager.getConnection(url);
    }
    
    public void insert(String license) throws Exception
    {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        {
            pstmt.setString(1, license);
            pstmt.executeUpdate();
        }
    }
}
