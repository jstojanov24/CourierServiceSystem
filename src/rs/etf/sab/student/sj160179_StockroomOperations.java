package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.StockroomOperations;

public class sj160179_StockroomOperations implements StockroomOperations {

	private int countPackagesInStockroom(Connection conn, int idStockroom) throws SQLException  {
		PreparedStatement ps=conn.prepareStatement("select count(idStockroom) from PackagesInStockroom  where idStockroom=? ");
		ps.setInt(1, idStockroom);
		ResultSet rs=ps.executeQuery();
		int res;
		if(rs.next()) res=rs.getInt(1);
		else res= 0;
		if(ps!=null) ps.close();
		if(rs!=null) rs.close();
		return res;
		
	}
	private int countVehiclesInStockroom(Connection conn, int idStockroom) throws SQLException  {
		//taken->0 ako je u magacinu
		//taken->1 ako je u voznji
		PreparedStatement ps=conn.prepareStatement("select count(licencePlateNum) from Vehicle  where idStockroom=? and taken=0  ");
		ps.setInt(1, idStockroom);
		ResultSet rs=ps.executeQuery();
		int res;
		if(rs.next()) res= rs.getInt(1);
		else res= 0;
		if(ps!=null) ps.close();
		if(rs!=null) rs.close();
		return res;
	}
	private int getStockroomFromCity(Connection conn,int idCity) throws SQLException {
		int idS;
		String sqlquery="select s.idStockroom "
				+ " from Stockroom s inner join Address a on s.idDistrict=a.idDistrict  "
				+ " inner join City c on a.idCity=c.idCity  "
				+ " where c.idCity=? ";
		PreparedStatement ps=conn.prepareStatement(sqlquery);
		ps.setInt(1, idCity);
		ResultSet rs=ps.executeQuery();
		if(rs.next()) idS=rs.getInt(1);
		else idS=-1;
		if(ps!=null) ps.close();
		if(rs!=null) rs.close();
		return idS;
	}
	@Override
	public boolean deleteStockroom(int idStockroom) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqldelete="delete from Stockroom where idStockroom=? ";
		try {
		int countP=this.countPackagesInStockroom(conn, idStockroom);
		int countV=this.countVehiclesInStockroom(conn, idStockroom);
		if(countP>0 || countV>0 )return false;
		ps=conn.prepareStatement(sqldelete);
		ps.setInt(1, idStockroom);
		if(ps.executeUpdate()>0) return true;
		else return false;
		}catch(SQLException  e) {
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
			
		}
		
		
	}

	@Override
	public int deleteStockroomFromCity(int idCity) {
		Connection conn=MyDB.getInstance().getConnection();
		try {
			int idS=this.getStockroomFromCity(conn, idCity);
			if(this.deleteStockroom(idS)) return idS;
			else return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
	}

	@Override
	public List<Integer> getAllStockrooms() {
		List<Integer> list=new ArrayList<Integer>();
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqlquery=" select idStockroom from Stockroom ";
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sqlquery);
			rs=ps.executeQuery();
			while(rs.next()) list.add(new Integer(rs.getInt(1)));
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if(ps!=null)
				try {
					ps.close();
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

	@Override
	public int insertStockroom(int address) {
		Connection conn=MyDB.getInstance().getConnection();
		String s="insert into Stockroom(idDistrict) values(?) ";
		PreparedStatement ps=null;
		try {
			ps=conn.prepareStatement(s,PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setInt(1, address);
			ps.executeUpdate();
			ResultSet rs=ps.getGeneratedKeys();
			if(rs.next()) {
				System.out.println("Dodat je magacin ciji je primarni kljuc: "+rs.getInt(1));
				return rs.getInt(1);
			}
			else return-1;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		finally {
			if(conn!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
					
				}
		}
	}
	
	public static void main(String[]args) {
		
	}


	

}
