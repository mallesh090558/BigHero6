package bigheroUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnection 
{
    Statement stmt;
    Connection con;
    ArrayList<HighScore> ar;
    
    
    public DBConnection() throws SQLException 
    {
        String host="jdbc:derby://localhost:1527/highscore";
        String username="herologin";
        String password="herologin";
        DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
        con=DriverManager.getConnection(host, username, password);
        stmt=con.createStatement();
    }
    
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
