package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.AddressOperations;

public class sj160179_AddressOperation implements AddressOperations {

	@Override
	public int deleteAllAddressesFromCity(int idCity) {
		Connection conn=MyDB.getInstance().getConnection();
		String s="delete from Address where idCity=? ";
		PreparedStatement ps=null;
		
		try {
			ps=conn.prepareStatement(s);
			ps.setInt(1, idCity);
			return ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
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

	@Override
	public boolean deleteAdress(int idDistrict) {
		Connection conn=MyDB.getInstance().getConnection();
		String s="delete from Address where idDistrict=? ";
		PreparedStatement ps=null;
		try {
			ps=conn.prepareStatement(s);
			ps.setInt(1, idDistrict);
			int resVal=ps.executeUpdate();
			if(resVal>0) return true;
			else return false;
	
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if(conn!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();	
				}	}
		
	}

	@Override
	public int deleteAddresses(String name, int number) {
		Connection conn=MyDB.getInstance().getConnection();
		String s="delete from Address where Street=? and Number=? ";
		PreparedStatement ps=null;
		
		try {
			ps=conn.prepareStatement(s);
			ps.setString(1, name);
			ps.setInt(2, number);
			return ps.executeUpdate();
	
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
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

	@Override
	public List<Integer>  getAllAddresses() {
		
		List<Integer> list = new ArrayList<Integer>();
		Connection conn = MyDB.getInstance().getConnection();
		Statement s = null;
		ResultSet rs=null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery("select idDistrict from Address order by idDistrict asc ");
			while (rs.next()) {
				list.add(new Integer(rs.getInt(1)));

			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (s != null)
				try {
					s.close();
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
	public List<Integer> getAllAddressesFromCity(int idCity) {
		//treba da vrati null ako nema takvih 
		List<Integer> list = new ArrayList<Integer>();
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select idDistrict from Address where idCity=? ");
			ps.setInt(1, idCity);
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new Integer(rs.getInt(1)));
			}
			if(list.isEmpty()) return null;
			else return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

		}

	}

	private String printAllAddresses(List<Integer> list) {
		StringBuilder sb = new StringBuilder();
        if(list.isEmpty()) sb.append("Lista je prazna.");
		for (int i = 0; i < list.size(); i++)
			sb.append(list.get(i) + " ");

		sb.append("\n");
		return sb.toString();

	}
	

	@Override
	public int insertAddress(String street, int number, int cityId, int xCord, int yCord) {
		Connection conn = MyDB.getInstance().getConnection();
		String s = "insert into Address(Street,Number,xCord,yCord,idCity) values(?,?,?,?,?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(s, PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, street);
			ps.setInt(2, number);
			ps.setInt(3, xCord);
			ps.setInt(4, yCord);
			ps.setInt(5, cityId);

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				//System.out.println("Dodata je adresa ciji je primarni kljuc: " + rs.getInt(1));
				return rs.getInt(1);
			} else
				return -1;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			if (conn != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	public static void main(String[] args) {
		sj160179_AddressOperation addr = new sj160179_AddressOperation();
		/*
		 * addr.insertDistrict("Ilije Garasanina", 69, 1075, 23, 45);
		 * addr.insertDistrict("Dr Jovana Cvijica", 18, 1076, 36, 48);
		 */

		/*System.out.println(addr.printAllAddresses(addr.getAllDistricts()));

		addr.insertDistrict("Vojvode Stepe", 145, 1075, 23, 46);
		System.out.println(addr.printAllAddresses(addr.getAllDistricts()));*/
		
		/*System.out.println(addr.printAllAddresses(addr.getAllDistrictsFromCity(1075)));
		
		System.out.println(addr.printAllAddresses(addr.getAllDistrictsFromCity(1076)));
		
		System.out.println(addr.printAllAddresses(addr.getAllDistrictsFromCity(1077)));
		
		addr.deleteDistrict(2);
		
		System.out.println(addr.printAllAddresses(addr.getAllDistricts()));
		
		/*int sum=addr.deleteDistricts("Dr Jovana Cvijica", 18);
		System.out.println("Izbrisano je "+sum+" gradova!");
		
		int sum=addr.deleteAllAddressesFromCity(1076);
		System.out.println("Izbrisano je "+sum+" gradova!");
		System.out.println(addr.printAllAddresses(addr.getAllDistricts()));*/
		
		
	}





	

}
