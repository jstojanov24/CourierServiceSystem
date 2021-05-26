package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.CourierRequestOperation;

public class sj160179_CourierRequestOperation implements CourierRequestOperation {

	@Override
	public boolean changeDriverLicenceNumberInCourierRequest(String userName,String licencePlateNumber) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqlupdate="update CourierRequest set licencePlateNum=? where userName=? ";
		try {
			ps=conn.prepareStatement(sqlupdate);
			ps.setString(1, licencePlateNumber);
			ps.setString(2, userName);
			if(ps.executeUpdate()>0) {
				System.out.println("Uspesna promena vozacke dozvole.");
				return true;
			}
			else {
				System.out.println("Neuspela promena vozacke dozvole.");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			try{if(ps!=null) ps.close();} catch(SQLException ex) {ex.printStackTrace();}
		}
		
	}

	@Override
	public boolean deleteCourierRequest(String userName) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqldelete="delete from CourierRequest where userName=? ";
		try {
			ps=conn.prepareStatement(sqldelete);
			ps.setString(1, userName);
			if(ps.executeUpdate()>0) {
				System.out.println("Uspesno brisanje zahteva. ");
				return true;
			}
			else {
				System.out.println("Neuspelo brisanje zahteva. Ne postoji zahtev za datog korisnika. ");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			try{if(ps!=null) ps.close();} catch(SQLException ex) {ex.printStackTrace();}
		}
	}

	@Override
	public List<String> getAllCourierRequests() {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		List<String> list=new ArrayList<String>();
		String sqlselectall="select userName from CourierRequest ";
		try {
			ps=conn.prepareStatement(sqlselectall);
			rs=ps.executeQuery();
			while(rs.next()) {
				list.add(rs.getString(1));
			}
			sj160179_UserOperations user=new sj160179_UserOperations();
			System.out.println(user.printAllUsernames(list));
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		finally {
			
		}
		
		
	}

	@Override
	public boolean grantRequest(String username) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		sj160179_CourierOperations courier=new sj160179_CourierOperations();
		String sqlselect="select licencePlateNum from CourierRequest where userName=? ";
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sqlselect);
			ps.setString(1, username);
			rs=ps.executeQuery();
			if(rs.next()) {
				String lic=rs.getString(1);
				if(courier.insertCourier(username, lic)) {
					//insertovan i izbrisi ga iz tabele
					this.deleteCourierRequest(username);
					return true;
					
				}else return false;	
			}else {
				System.out.println("Korisnik sa datim imenom nije poslao zahtev. ");
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Override
	public boolean insertCourierRequest(String userName, String licencePlateNumber) {
		//vidi da zabranis da se doda zahtev ako je vec kurir
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		PreparedStatement ps2=null;
		ResultSet rs=null;
		String s=" insert into CourierRequest(userName,licencePlateNum) values (?,?) ";
		String sqlquery="select userName from Courier where userName=? ";
		try {
			ps2=conn.prepareStatement(sqlquery);
			ps2.setString(1, userName);
			rs=ps2.executeQuery();
			if(rs.next()) return false;
			ps=conn.prepareStatement(s);
			ps.setString(1, userName);
			ps.setString(2, licencePlateNumber);
			if(ps.executeUpdate()>0) {
				System.out.println("Uspesno slanje zahteva za korisnika "+userName+", sa vozackom dozvolom "+licencePlateNumber);
				return true;
			}
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(ps2!=null)
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	public static void main(String[] args) {
		sj160179_CourierRequestOperation req=new sj160179_CourierRequestOperation();
		/*req.deleteCourierRequest("zirafaaa");
		
		req.insertCourierRequest("zirafaaa", "01239");*/
		
		//req.changeVehicleInCourierRequest("zirafaaa", "12313");
		//req.getAllCourierRequests();
		/*sj160179_CourierOperations courier =new sj160179_CourierOperations();
		sj160179_UserOperations user=new sj160179_UserOperations();
		System.out.print("*****Tabela svih korisnika pre dodavanja: ");
		System.out.println(user.printAllUsernames(user.getAllUsers()));
		user.insertUser("sreteeen", "Sreten", "Escobar Gaviria", "caocao1234");
		System.out.print("*****Tabela svih korisnika nakon dodavanja: ");
		System.out.println(user.printAllUsernames(user.getAllUsers()));
		///dodat user
		
		//svi zahtevi
		System.out.print("*****PRE SLANJA ZAHTEVA,TABELA ZAHTEVA: " );
		System.out.println(user.printAllUsernames(req.getAllCourierRequests()));
		req.insertCourierRequest("sreteeen", "123456789");
		System.out.print("*****POSLE SLANJA ZAHTEVA,TABELA ZAHTEVA: " );
		System.out.println(user.printAllUsernames(req.getAllCourierRequests()));
		
		//svi kuriri
		System.out.print("**********Svi kuriri pre dodavanja novog: ");
		System.out.println(user.printAllUsernames(courier.getAllCouriers()));
		
		req.grantRequest("sreteeen");
		
		//dodao kurira 
		
		System.out.print("**********Svi kuriri nakon dodavanja novog: ");
		System.out.println(user.printAllUsernames(courier.getAllCouriers()));
		*/
		
		
		
		
	}

	
}
