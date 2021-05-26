package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.CityOperations;

public class sj160179_CityOperations implements CityOperations {

	@Override
	public int deleteCity(String... names) {
		// TODO Auto-generated method stub
		int sum=0;
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		
		try {
			ps=conn.prepareStatement("delete from City where Name=? ");
			for(int i=0;i<names.length; i++) {
				ps.setString(1, names[i]);
				int retVal=ps.executeUpdate();
				if(retVal>0) sum+=retVal;
			}
			return sum;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	
	}

	@Override
	public boolean deleteCity(int idCity) {
		Connection conn=MyDB.getInstance().getConnection();
		sj160179_AddressOperation addr=new sj160179_AddressOperation();
		String s="delete from City where idCity=? ";
		PreparedStatement ps=null;
		try {
			//brisanje prvo adresa
			addr.deleteAllAddressesFromCity(idCity);
			ps=conn.prepareStatement(s);
			ps.setInt(1, idCity);
			int resVal=ps.executeUpdate();
			if(resVal>0) return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
	private String printAllCities(List<Integer> list) {
		StringBuilder sb=new StringBuilder();
		
		for(int i=0;i<list.size(); i++) sb.append(list.get(i)+" ");
		
		sb.append("\n");
		return sb.toString();
		
		
	}

	@Override
	public List<Integer> getAllCities() {
		List<Integer> list=new ArrayList<Integer>();
		Connection conn=MyDB.getInstance().getConnection();
		Statement s=null;
		ResultSet rs;
		try {
			s=conn.createStatement();
			rs=s.executeQuery("select idCity from City order by idCity asc " );
			while(rs.next()) {
				list.add(new Integer(rs.getInt(1)));
				
			}
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		finally {
			if(s!=null)
				try {
					s.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
		
	
	}

	@Override
	public int insertCity(String name, String postalCode) {
		Connection conn=MyDB.getInstance().getConnection();
		String s="insert into City(Name,PostalCode) values(?,?)";
		PreparedStatement ps=null;
		try {
			ps=conn.prepareStatement(s,PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ps.setString(2, postalCode);
			ps.executeUpdate();
			ResultSet rs=ps.getGeneratedKeys();
			if(rs.next()) {
				//System.out.println("Dodat je grad ciji je primarni kljuc: "+rs.getInt(1));
				return rs.getInt(1);
			}
			else return-1;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
		sj160179_CityOperations city=new sj160179_CityOperations();
		//int retVal=city.insertCity("Pariz", "75000");
		/*if(city.deleteCity(4)) System.out.println("IZBRISALAAAAAAAAAA");
		if(!city.deleteCity(3)) System.out.println(" nisaaaaaam IZBRISALAAAAAAAAAA");
		int retVal=city.insertCity("Parizdsadasda", "750005");
		*/
		System.out.println(city.printAllCities(city.getAllCities()));
		
		System.out.println("Brisanje :) ");
		String[] arr=new String[] {"AK","Parizdsadasda","AKJDSAKJHDAKJ","janka","sreten"};
		city.deleteCity(arr);
		System.out.println(city.printAllCities(city.getAllCities()));
		
		
	}


}
