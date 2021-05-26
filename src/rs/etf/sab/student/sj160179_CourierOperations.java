package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.CourierOperations;

public class sj160179_CourierOperations implements CourierOperations {

	@Override
	public boolean deleteCourier(String courierUserName) {
		//obrisi sve koji imaju referencu ka kuriru
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		try {
			ps=conn.prepareStatement("delete from Courier where userName=? ");
			ps.setString(1, courierUserName);
			
			if(ps.executeUpdate()>0) return true;
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
		}
	}

	@Override
	public List<String> getAllCouriers() {
		//System.out.println("Dohvatanje svih kurira");
		List<String> list = new ArrayList<String>();
		Connection conn = MyDB.getInstance().getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery("select userName from Courier  ");
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			sj160179_UserOperations user=new sj160179_UserOperations();
			System.out.println(user.printAllUsernames(list));
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Override
	public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder(" select avg(profit) from Courier ") ;
		if(numberOfDeliveries!=-1)  sb.append(" where numOfDelivered=? ");
		try {
			ps=conn.prepareStatement(sb.toString());
			if(numberOfDeliveries!=-1) ps.setInt(1, numberOfDeliveries);
			 rs=ps.executeQuery();
			 rs.next();
			return rs.getBigDecimal(1);
			
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
	public List<String> getCouriersWithStatus(int statusOfCourier) {
		List<String> list=new ArrayList<String>();
		if(statusOfCourier>1 || statusOfCourier<0) {
			System.out.println("Pogresan status za kurira. Vrednost mora biti 0 ili 1.");
			return null; //mozda samo list
		}
		System.out.println("Kuriri sa statusom: "+(statusOfCourier==0?"ne vozi.":"vozi."));
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String s="select userName from Courier where status=? ";
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(s);
			ps.setInt(1, statusOfCourier);
			rs=ps.executeQuery();
			while(rs.next()) {
				list.add(rs.getString(1));
			}
			sj160179_UserOperations user=new sj160179_UserOperations();
			System.out.println(user.printAllUsernames(list));
			return list;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;  //PROVERI???
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
	public boolean insertCourier(String courierUserName, String licencePlateNumber) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqlinsert="insert into Courier(userName,licence) values(?,?) ";
		try {
			ps=conn.prepareStatement(sqlinsert);
			ps.setString(1, courierUserName);
			ps.setString(2,licencePlateNumber);
			if(ps.executeUpdate()>0) {
				System.out.println("Uspesno dodavanje kurira sa korisnickim imenom "+courierUserName+" vozackom dozvolom: "+licencePlateNumber);
				return true;
			}else {
				System.out.println("Neuspelo dodavanje kurira u tabelu.");
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
					e.printStackTrace();
				}
		}
		
	}
	public static void main(String[] args) {
		sj160179_CourierOperations courier=new sj160179_CourierOperations();
		//courier.insertCourier("janka2405","123455678");
		//courier.insertCourier("panda123","123ca56");
		courier.getAllCouriers();
		//courier.deleteCourier("panda123");
		courier.getCouriersWithStatus(0);
		
		courier.getCouriersWithStatus(1);
		
		System.out.println(courier.getAverageCourierProfit(0));
		System.out.println(courier.getAverageCourierProfit(10));
		System.out.println(courier.getAverageCourierProfit(-1));
		
		
		
	}

}
