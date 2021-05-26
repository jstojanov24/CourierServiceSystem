package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import rs.etf.sab.operations.GeneralOperations;

public class sj160179_GeneralOperations implements GeneralOperations {

	@Override
	public void eraseAll() {
		/*
		Connection conn=MyDB.getInstance().getConnection();
		Statement s=null;
		Statement s2=null;
		Statement s3=null;
		Statement s4=null;
		try {/*
			 s=conn.createStatement(); //resi triggerom
			
			 s.execute("delete from Address where idCity>0 " );
			 s2=conn.createStatement();
			 s2.execute("delete from City where idCity>0 " );
			 s3=conn.createStatement();
			 s3.execute("delete from [User] where 1=1 " );
			 
			 s4=conn.createStatement();
			 s4.execute("delete from Administrator where 1=1 " );*/
		/*
			s4=conn.createStatement();
			 s4.execute("delete from Package where 1=1 " );
			 s3=conn.createStatement();
			 s3.execute("delete from [User] where 0=0 " );
			 s2=conn.createStatement();
			 s2.execute("delete from City where idCity>0 " );
			
			
			 
			 
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(s!=null)
				try {
					s.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(s2!=null)
				try {
					s2.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(s3!=null)
				try {
					s3.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(s4!=null)
				try {
					s4.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}*/
		this.eraseAllDBData();
		this.addPackageType();
		
	}
	private void eraseAllDBData() {
		Connection conn=MyDB.getInstance().getConnection();
		CallableStatement cs=null;
		String sqlcall="{ call sp_MSForEachTable (?) }";
		String[] s= new String[]{" DISABLE TRIGGER ALL ON ? ",
		" ALTER TABLE ? NOCHECK CONSTRAINT ALL ", " DELETE FROM ? ",
		" ALTER TABLE ? CHECK CONSTRAINT ALL ", " ENABLE TRIGGER ALL ON ? "};
		try {
			cs=conn.prepareCall(sqlcall);
			for(int i=0;i<s.length; i++) {
				cs.setString(1, s[i]);
				cs.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(cs!=null)
				try {
					cs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	private void addPackageType() {
		Connection conn=MyDB.getInstance().getConnection();
		Statement s=null;
		try {
			s=conn.createStatement();
			s.executeUpdate("insert into PackageType(idType,type) values (0,'mali paket'), (1,'standardni paket'), "+
			" (2,'paket nestandardnih dimenzija'),(3,'lomljiv paket')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(s!=null)
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}

}
