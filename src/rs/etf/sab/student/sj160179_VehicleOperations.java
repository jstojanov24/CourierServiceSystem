package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.VehicleOperations;

public class sj160179_VehicleOperations implements VehicleOperations {

	@Override
	public boolean changeCapacity(String licensePlateNumber, BigDecimal capacity) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqlupdate=" update Vehicle set capacity=? where licencePlateNum=? and taken=0  and idStockroom is not null ";
		try {
			ps=conn.prepareStatement(sqlupdate);
			ps.setBigDecimal(1, capacity);
			ps.setString(2, licensePlateNumber);
			if(ps.executeUpdate()>0) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace(); return false;
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
	public boolean changeConsumption(String licensePlateNumber, BigDecimal fuelConsumption) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqlupdate=" update Vehicle set fuelConsumtion=? where licencePlateNum=? and taken=0 and  idStockroom is not null";
		try {
			ps=conn.prepareStatement(sqlupdate);
			ps.setBigDecimal(1, fuelConsumption);
			ps.setString(2, licensePlateNumber);
			if(ps.executeUpdate()>0) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace(); return false;
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
	public boolean changeFuelType(String licensePlateNumber, int fuelType) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqlupdate=" update Vehicle set fuelType=? where licencePlateNum=? and taken=0  and idStockroom is not null ";
		try {
			ps=conn.prepareStatement(sqlupdate);
			ps.setInt(1, fuelType);
			ps.setString(2, licensePlateNumber);
			if(ps.executeUpdate()>0) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace(); return false;
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
	public int deleteVehicles(String... licencePlateNumbers) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqldelete="delete from Vehicle where licencePlateNum=? ";
		int sum=0;
		try {
			ps=conn.prepareStatement(sqldelete);
			for(int i=0;i<licencePlateNumbers.length; i++) {
				ps.setString(1, licencePlateNumbers[i]);
				int res=ps.executeUpdate();
				if(res>0) sum+=res;	
			}
			return sum;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
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
	public List<String> getAllVehichles() {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqquery=" select licencePlateNum from Vehicle ";
		List<String> list=new ArrayList<String>();
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sqquery);
			rs=ps.executeQuery();
			while(rs.next()) list.add(rs.getString(1));
			return list;
		} catch (SQLException e) {
			e.printStackTrace(); return null;
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
	public boolean insertVehicle(String licencePlateNumber, int fuelType, BigDecimal fuelConsumtion,
			BigDecimal capacity) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqlinsert=" insert into Vehicle(licencePlateNum,fuelType,fuelConsumtion, capacity,taken) "+
		" values(?,?,?,?,0) ";
		try {
			ps=conn.prepareStatement(sqlinsert);
			ps.setString(1, licencePlateNumber);
			ps.setInt(2, fuelType);
			ps.setBigDecimal(3, fuelConsumtion);
			ps.setBigDecimal(4, capacity);
			if(ps.executeUpdate()>0) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace(); return false;
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
	public boolean parkVehicle(String licencePlateNumbers, int idStockroom) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		String sqlupdate="update Vehicle set idStockroom=? where licencePlateNum=? and taken=0 ";
		try {
			ps=conn.prepareStatement(sqlupdate);
			ps.setInt(1, idStockroom);
			ps.setString(2, licencePlateNumbers);
			if(ps.executeUpdate()>0) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace(); return false;
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

	
	

}
