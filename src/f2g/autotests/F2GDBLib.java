package f2g.autotests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Administrator on 10/17/2017.
 */
public class F2GDBLib {

    public static ArrayList<String> getAllExternalUsers(){
        ArrayList externalUsersList = new ArrayList();
        try {
            Connection con = establishConnection();
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM [Files2Go-DB2].[dbo].[ExternalUser]";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                String name = (rs.getString("Login"));
                externalUsersList.add(name);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return externalUsersList;
    }

    private static Connection establishConnection(){
        Connection con = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String server = "localhost\\sqlexpress";
            int port = 50222;
            String user = "sa";
            String password = "Q1w2e3r4";
            String database = "Files2Go-DB2";
            String jdbcURL = "jdbc:sqlserver://" + server + ":" + port + ";user=" + user + ";password=" + password + ";databaseName=" + database;

            con = DriverManager.getConnection(jdbcURL);

        } catch(Exception ex){
            System.out.println("Error: " + ex);
        }
        return con;
    }
}
