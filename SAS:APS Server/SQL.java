/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 */
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Deben Oldert
 */

public class SQL
{
  Connection conn = null;
  Statement stmt = null;
  SQL() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
  {
      try {
          Class.forName("com.mysql.jdbc.Driver");
          conn = DriverManager.getConnection("jdbc:mysql://192.168.2.240:3306/sas?user=Implementation&password=Test123456");
          stmt = conn.createStatement();
      } catch (ClassNotFoundException ex) {
          Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  public Statement Connect() {
      return stmt;
  }
  public void threadUpdate(String threadId, String state, String data) throws SQLException, ClassNotFoundException {
      switch (state) {
          case "terminate":
              stmt.execute("DELETE FROM thread WHERE threadId='"+threadId+"'");
              System.out.println("SQL ==> TERMINATE");
              break;
          case "birth":
              stmt.execute("INSERT INTO thread (threadId,state,`data`) VALUES ('"+threadId+"','"+state+"','"+data+"')");
              System.out.println("SQL ==> BIRTH");
              break;
          default:
              stmt.execute("UPDATE thread set `state`='"+state+"',`data`='"+data+"' WHERE `threadId`='"+threadId+"'");
              System.out.println("SQL ==> UPDATE");
              break;
      }
  }
  public void clean() throws SQLException {
      stmt.execute("TRUNCATE TABLE thread");
  }
}