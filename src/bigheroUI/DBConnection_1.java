package bigheroUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnection_1 
{
    Statement stmt;
    Connection con;
    ArrayList<HighScore> ar;
    public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    public String protocol = "jdbc:derby:";
    /*    
    public DBConnection_1() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException 
    {
        Class.forName(driver).newInstance();
        conn = DriverManager.getConnection(protocol + "derbyDB;create=true", props);
        String host="jdbc:derby://localhost:1527/highscore";
        String username="herologin";
        String password="herologin";
        DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
        con=DriverManager.getConnection(host, username, password);
        stmt=con.createStatement();
    }
    */
    public ArrayList getHighScrores() throws SQLException
    {
        String query="select * from highscores";
        ResultSet rs=stmt.executeQuery(query);
        
        ar=new ArrayList<>();
        
        while(rs.next())
        {
            HighScore h=new HighScore();
            
            h.setName(rs.getString(1));
            h.setScore(Integer.parseInt(rs.getString(2)));
            
            ar.add(h);
        }
        
        return ar;
    }
    
    public void insertHighScore(String name,int score) throws SQLException
    {
        String query="insert into highscores values('"+name+"',"+score+")";
        stmt.executeUpdate(query);
    }
    
}
