package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyDB {
	private static final String username="sa";
    private static final String password="janka";
    private static final String database="sj160179";
    private static final int port=1433; 
    private static final String server="DESKTOP-M6K7CAK\\JANKASQL";
    private Connection conn;
    private MyDB(){
        try {
            conn=DriverManager.getConnection(connectionUrl, username, password);
        } catch (SQLException ex) {
            Logger.getLogger(MyDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private  static final  String connectionUrl = "jdbc:sqlserver://"+server+":"+port+";databaseName="+database;
    private static MyDB db=null;
    public static MyDB getInstance(){
        if(db==null){
            db=new MyDB();
        }
        return db;
    }
    public Connection getConnection() {
    	return conn;
    }

}
