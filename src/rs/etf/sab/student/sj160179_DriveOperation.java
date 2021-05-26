package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.DriveOperation;

public class sj160179_DriveOperation implements DriveOperation {

	@Override
	public List<Integer> getPackagesInVehicle(String courierUsername) {
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Integer> list = new ArrayList<Integer>();
		try {
			ps = conn.prepareStatement(
					" select  ptd.idPackage " + " from Courier c inner join Delivery d on d.userName=c.userName  "
							+ " inner join PackagesToDeliver ptd on ptd.idDelivery=d.idDelivery  "
							+ "	where c.status=1 and c.userName=? ");
			ps.setString(1, courierUsername);
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new Integer(rs.getInt(1)));
			}
			return list;
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
	private int getStockroomId(Connection conn,String name) {
		PreparedStatement ps=null;
		ResultSet rs=null;
		String sqlquery=" select s.idStockroom from Stockroom s"
				+ "			inner join Address a on a.idDistrict=s.idDistrict  "
				+ "			where s.idDistrict in ( "
				+ "			select a.idDistrict "
				+ "			from Address a inner join City c on c.idCity=a.idCity "
				+ "			where c.idCity=( "
				+ " 		select  c.idCity"
				+ "			from Courier cr inner join [User] u on u.userName=cr.userName "
				+ "			inner join Address a on a.idDistrict=u.idDistrict "
				+ "			inner join City c on c.idCity=a.idCity "
				+ "			where cr.userName=? ) ) ";
		try {
			ps=conn.prepareStatement(sqlquery);
			ps.setString(1, name);
			rs=ps.executeQuery();
			if(rs.next()) return rs.getInt(1);
			else return -1;
		} catch (SQLException e) {
			e.printStackTrace(); return -1;
		}
	}
	private void endDrive(Connection conn,String name) {
		int idStockroom=this.getStockroomId(conn, name);
		PreparedStatement ps=null;
		PreparedStatement ps2=null;
		PreparedStatement ps3=null;
		PreparedStatement ps4=null;
		ResultSet rs=null;
		String sqlquery=" select ptd.idPackage "
				+ "			from Courier c inner join Delivery d on d.userName=c.userName "
				+ "			inner join PackagesToDeliver ptd on ptd.idDelivery=d.idDelivery "
				+ "			where c.userName=? and c.status=1 ";
		String sqlinsert=" insert into PackagesInStockroom (idStockroom,idPackage) values (?,?) ";
		String sqldelete=" delete from PackagesToDeliver where idDelivery=( "
				+ "		   select TOP 1 d.idDelivery from Delivery d inner join Courier c on c.userName=d.userName	"
				+ "			where c.status=1 and c.userName=? )";
		
		String sqlupdate=" update Package set AddrCurr=(	"
				+ "		select  top 1 s.idDistrict from Stockroom s where s.idStockroom=? )  where idPackage=? "; //dodato naknadno, radi i bez ovoga
		try {
			ps=conn.prepareStatement(sqlquery);
			ps2=conn.prepareStatement(sqlinsert);
			ps4=conn.prepareStatement(sqlupdate);
			ps.setString(1, name);
			rs=ps.executeQuery();
			while(rs.next()) {
				ps2.setInt(1, idStockroom);
				ps2.setInt(2, rs.getInt(1));
				ps2.executeUpdate();
				ps4.setInt(1, idStockroom);
				ps4.setInt(2, rs.getInt(1));
				ps4.executeUpdate();
			}
			ps3=conn.prepareStatement(sqldelete);
			ps3.setString(1, name);
			ps3.executeUpdate();
			this.returnVehicle(conn, name, idStockroom);
			this.setCourierInActive(conn, name);
			this.deleteDelivery(conn,name);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps2 != null)
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps3 != null)
				try {
					ps3.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps4 != null)
				try {
					ps4.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	private void deleteDelivery(Connection conn,String name) {
		PreparedStatement ps=null;
		String sqldelete=" delete from Delivery where userName=? ";
		try {
			ps=conn.prepareStatement(sqldelete);
			ps.setString(1, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}

	@Override
	public int nextStop(String courierUsername) {
		Connection conn = MyDB.getInstance().getConnection();
		int state = this.getDeliveryStatus(conn, courierUsername);
		switch (state) {
		case 0: // uzimanje paketa za isporuku iz grada u kom je Kurir
			int addr = this.pickUpPackagesToDeliver(conn, courierUsername);
			if (addr == -1)
				{  System.out.println("PUKAO KADA ZOVEM PICKUPPACKAGES"); return -1; }
			this.updateCurrentAddress(conn, addr, courierUsername);//za pakete
			this.deleteFromPickUpRoutes(conn, courierUsername, addr); //izbrisi rutu iz tabele
			if(this.pickUpTableEmpty(conn,courierUsername)){
					//promeni status na 1
				this.setDeliveryStatus(conn, courierUsername, 1);
				//uzmi prvu adresu i tabele DeliveryRoutes
				this.setNextAddress2(conn, courierUsername);
			}else {
				this.setNextAddress(conn, courierUsername);
			}
			return -2;

		case 1: // ?
			int idPackage=this.deliverNextPackage(conn, courierUsername);
			if(idPackage==-1) {
				System.out.println("Kraj voznje,povratak u magacin");
				this.endDrive(conn,courierUsername);
				
				return -1;
			} else System.out.println("Vozac sa imenom "+courierUsername +" je isporucio paket: "+idPackage);
			int currAddr=this.getCurrentLocation(conn, courierUsername);
			System.out.println("Trenutna lokacijaa vozacaa "+courierUsername+" je "+currAddr);
			this.updateCurrentAddress(conn, currAddr, courierUsername);
			int cnt=this.getRoutesCountSameCity(conn, courierUsername);
			if(cnt==-1) return -1;
			else System.out.println("Broj  preostalih ruta u tekucem gradu je: "+(cnt));
			if(cnt==0) {
				//poslednja ruta u tom gradu
				//vidi ima li paketa koji treba da se pokupe iz toga grada
				int idCity=this.getCurrentRouteCity(conn, courierUsername);
				if(idCity==-1) return -1;
				int lastPackageForPickUp=this.insertRoutes2(conn, courierUsername, idCity);
				if (lastPackageForPickUp != -1) {
					int currRouteAddress = this.getAddressFromLastPickUpPackage(conn, lastPackageForPickUp);
					int idDelivery=this.getDeliveryId(conn, courierUsername);
					this.sortRoutesForDelivery(conn, idDelivery, currRouteAddress);
					this.setDeliveryStatus(conn, courierUsername, 2);
					this.setNextAddress(conn, courierUsername);
				}
				else this.setNextAddress2(conn, courierUsername);
					
			} else this.setNextAddress2(conn, courierUsername); //postavila u tabeli vec sledecu rutu
			return idPackage ;
			
		case 2:
				int addr2 = this.pickUpPackagesToDeliver(conn, courierUsername);
				if (addr2 == -1)
					return -1;
				this.updateCurrentAddress(conn, addr2, courierUsername);//za pakete
				this.deleteFromPickUpRoutes(conn, courierUsername, addr2); //izbrisi rutu iz tabele
				if(this.pickUpTableEmpty(conn,courierUsername)){
					//promeni status na 1
					this.setDeliveryStatus(conn, courierUsername, 1);
					//uzmi prvu adresu i tabele DeliveryRoutes
					this.setNextAddress2(conn, courierUsername);
				}else {
					this.setNextAddress(conn, courierUsername);
				}
				return -2;
		}
		return -1;
	}
	private int getCurrentLocation(Connection conn,String name) {
		PreparedStatement ps=null;
		ResultSet rs=null;
		String sqlquery=" select d.currAddr from Delivery d inner join Courier c "
				+ "			on c.userName=d.userName and c.status=1 "
				+ "			where c.userName=? ";
		try {
			ps=conn.prepareStatement(sqlquery);
			ps.setString(1, name);
			rs=ps.executeQuery();
			if(rs.next()) return rs.getInt(1);
			else return -1;
		} catch (SQLException e) {
			e.printStackTrace(); return -1;
		}
		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	private int getDeliveryId(Connection conn,String name) {
		PreparedStatement ps=null;
		ResultSet rs=null;
		String sqlquery=" select d.idDelivery from Delivery d inner join Courier c "
				+ "			on c.userName=d.userName and c.status=1 "
				+ "			where c.userName=? ";
		try {
			ps=conn.prepareStatement(sqlquery);
			ps.setString(1, name);
			rs=ps.executeQuery();
			if(rs.next()) return rs.getInt(1);
			else return -1;
		} catch (SQLException e) {
			e.printStackTrace(); return -1;
		}
		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	private int deliverNextPackage(Connection conn,String name) {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		PreparedStatement ps6 = null;
		ResultSet rs = null;
		int addr = -1;
		int idd = -1;
		double sum = 0;
		double currWeight = 0;
		double price=0;
		double sumPrice=0;
		int idPackage=-1;
		// idDelivery,idPackage,weight,currAddr,currWeight,PackagePrice, currSumDelivery
		String sqlquery = " select d.idDelivery, p.idPackage , p.weight, pur.addr, d.currWeight ,p.Price , d.priceSum "
				+ "			from Package p inner join DeliverRoutes pur on p.idPackage=pur.idPackage "
				+ "			inner join Delivery d on d.idDelivery=pur.idDelivery  "
				+ "			inner join Courier c on c.userName=d.userName "
				+ "			where c.status=1 and c.userName=? " + "			and pur.addr=d.currAddr"
						+ "	 ";
		String sqldelete = " delete from  PackagesToDeliver where idPackage=? ";
		String sqlupdate = " update Package set status=3, AddrCurr=null where idPackage=? ";
		String sqlupdate2 = " update Delivery set currWeight=? , priceSum=? where idDelivery=? ";
		String sqldelete2=" delete from DeliverRoutes where idPackage=? ";
		String sqlupdate3=" update Courier set numOfDelivered=numOfDelivered+1 where userName=? ";

		try {
			ps = conn.prepareStatement(sqlquery);
			ps.setString(1, name);
			rs = ps.executeQuery();
			ps2 = conn.prepareStatement(sqldelete);
			ps3 = conn.prepareStatement(sqlupdate);
			ps4 = conn.prepareStatement(sqlupdate2);
			ps5=conn.prepareStatement(sqlupdate3);
			ps6=conn.prepareStatement(sqldelete2);
			if (rs.next()) {
				idPackage=rs.getInt(2);
				sum = rs.getBigDecimal(3).doubleValue(); //tezina paketa
				addr = rs.getInt(4); //tek adresa
				idd = rs.getInt(1); //idDelivery
				price=rs.getBigDecimal(6).doubleValue(); //cena paketa
				currWeight = rs.getBigDecimal(5).doubleValue(); //tekuca tezina u Vozilu
				sumPrice=rs.getBigDecimal(7).doubleValue();
				ps2.setInt(1, rs.getInt(2)); //izbrisala sam iz tabele PackagesToDeliver
				ps2.executeUpdate(); //trigger za broj poslatih paketa 
				ps3.setInt(1, rs.getInt(2));
				ps3.executeUpdate(); //update za paket 
				ps6.setInt(1, rs.getInt(2));
				ps6.executeUpdate();
				
				
			} else return -1;
			currWeight -= sum;
			sumPrice+=price;
			ps4.setBigDecimal(1, new BigDecimal(currWeight));
			ps4.setBigDecimal(2, new BigDecimal(sumPrice));
			ps4.setInt(3, idd);
			ps4.executeUpdate(); //update podatka u tabeli 
			//Dodaj sortiranje ruta u odnosu na addr
			ps5.setString(1, name);
			ps5.executeUpdate();
			this.sortRoutesForDelivery(conn, idd, addr);
			return idPackage;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;

		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps4 != null)
				try {
					ps4.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps5 != null)
				try {
					ps5.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps2 != null)
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps3 != null)
				try {
					ps3.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps6 != null)
				try {
					ps6.close();
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
	private void setDeliveryStatus(Connection conn, String name, int state) {
		PreparedStatement ps = null;
		String sqlquery = "	update Delivery set deliveryState=? "
				+ "			where idDelivery=( "
				+ "				select d.idDelivery"
				+ "				from Delivery d inner join Courier c on d.userName=c.userName"
				+ "				where d.userName=? and c.status=1 ) " + "				";
		try {
			ps = conn.prepareStatement(sqlquery);
			ps.setInt(1, state);
			ps.setString(2, name);
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	private boolean pickUpTableEmpty(Connection conn,String name) {
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement("select pur.addr "
					+ " from PickUpRoutes pur inner join Delivery d on d.idDelivery=pur.idDelivery "
					+ " inner join Courier c on c.userName=d.userName "
					+ " where c.userName=? ");
			ps.setString(1, name);
			rs=ps.executeQuery();
			if(!rs.next()) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace(); return true;
		}
		finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		
	}

	/////// Next Stop Pomocne metode/////////////////////
	private void deleteFromPickUpRoutes(Connection conn, String name, int addr) {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		String sqldelete = " delete from PickUpRoutes " + " where addr=? and idDelivery = ("
				+ "		 select d.idDelivery from Delivery d inner join Courier c  " + "	on  c.userName=d.userName "
				+ " where c.status=1 and c.userName=? ) ";
		//izbrisi i iz magacina
		//addr je zapravo adresa magacina iz kog kupimo 
		String sqldelete2="  delete from PackagesInStockroom where idPackage in "
				+ " (select pur.idPackage from PickUpRoutes pur inner join Delivery d "
				+ " on d.idDelivery=pur.idDelivery inner join Courier c on c.userName=d.userName "
				+ " where pur.addr=? and c.userName=? )";
		try {
			ps = conn.prepareStatement(sqldelete);
			ps2 = conn.prepareStatement(sqldelete2);
			ps2.setInt(1, addr);
			ps2.setString(2, name);
			ps2.executeUpdate();
			
			ps.setInt(1, addr);
			ps.setString(2, name);
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		//Dodaj ako je u magacinu, da izbrises taj paket iz magacina!!!!!

	}

	private int pickUpPackagesToDeliver(Connection conn, String name) {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		ResultSet rs = null;
		int addr = -1;
		int idd = -1;
		double sum = 0;
		double currWeight = 0;
		// idDelivery,idPackage,weight,currAddr,currWeight
		String sqlquery = " select d.idDelivery, p.idPackage , p.weight, pur.addr, d.currWeight "
				+ "			from Package p inner join PickUpRoutes pur on p.idPackage=pur.idPackage "
				+ "			inner join Delivery d on d.idDelivery=pur.idDelivery  "
				+ "			inner join Courier c on c.userName=d.userName "
				+ "			where c.status=1 and c.userName=? " + "			and pur.addr=d.currAddr ";
		String sqlinsert = " insert into PackagesToDeliver(idDelivery,idPackage,flag) values(?,?,0) ";
		String sqlupdate = " update Package set status=2 where idPackage=? ";
		String sqlupdate2 = " update Delivery set currWeight=? where idDelivery=? ";

		try {
			ps = conn.prepareStatement(sqlquery);
			ps.setString(1, name);
			rs = ps.executeQuery();
			ps2 = conn.prepareStatement(sqlinsert);
			ps3 = conn.prepareStatement(sqlupdate);
			ps4 = conn.prepareStatement(sqlupdate2);
			while (rs.next()) {
				sum = rs.getBigDecimal(3).doubleValue();
				addr = rs.getInt(4);
				idd = rs.getInt(1);
				currWeight = rs.getBigDecimal(5).doubleValue();
				ps2.setInt(1, rs.getInt(1));
				ps2.setInt(2, rs.getInt(2));
				ps2.executeUpdate();
				ps3.setInt(1, rs.getInt(2));
				ps3.executeUpdate();
			}
			currWeight += sum;
			ps4.setBigDecimal(1, new BigDecimal(currWeight));
			ps4.setInt(2, idd);
			ps4.executeUpdate();
			return addr;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;

		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps2 != null)
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps3 != null)
				try {
					ps3.close();
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

	private int getDeliveryStatus(Connection conn, String name) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sqlquery = " select d.deliveryState "
				+ "			from Delivery d inner join Courier c on d.userName=c.userName"
				+ "			where d.userName=? and c.status=1 " + "				";
		try {
			ps = conn.prepareStatement(sqlquery);
			ps.setString(1, name);
			rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt(1);
			else
				return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
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

	private void updateCurrentAddress(Connection conn, int addr, String name) {
		PreparedStatement ps = null;
		String sqlupdate = " update Package set AddrCurr=?" + " " + "	 where idPackage in ( "
				+ "				select p.idPackage "
				+ "				from Package p inner join PackagesToDeliver ptd on p.idPackage=ptd.idPackage "
				+ "				inner join Delivery d	on d.idDelivery=ptd.idDelivery "
				+ "				where d.idDelivery=( " + "					select d1.idDelivery"
				+ "					from Delivery d1 inner join Courier c on c.userName=d1.userName"
				+ "					where c.status=1 and c.userName=? " + "					)" + "			)";
		try {
			ps = conn.prepareStatement(sqlupdate);
			ps.setInt(1, addr);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public boolean planingDrive(String courierUsername) {
		Connection conn = MyDB.getInstance().getConnection();
		String licPlateNum = this.chooseVehicle(conn, courierUsername);
		///// NEMA SLOBODNIH VOZILA////
		if (licPlateNum == null)
			return false;
		////// NEUSPESNA PROMENA STATUSA ZA VOZILO//////
		if (!this.setVehicleTaken(conn, licPlateNum))
			return false;
		///// Neuspesno dodavanje u tabelu Drives
		if (!this.insertPairVehicleCourier(conn, licPlateNum, courierUsername))
			return false;
		//// Neuspesna promena statusa kurira
		if (!this.setCourierActive(conn, licPlateNum, courierUsername))
			return false;
		/// Neuspesno dohvatanje kapaciteta za vozilo
		BigDecimal cap = this.getVehicleCapacity(conn, licPlateNum);
		if (cap == null)
			return false;
		//// Neuspesno dodavanje u tabelu Delivery
		int idDelivery = this.insertDelivery(conn, licPlateNum, courierUsername, cap);
		if (idDelivery == -1)
			return false;
		//// Dodavanje ruta
		int lastPackageForPickUp = this.insertRoutes(conn, idDelivery, cap, courierUsername);
		/// Nema paketa koji treba da se isporuce
		if (lastPackageForPickUp == -1)
			return false;
		// dohvati adr
		int currRouteAddress = this.getAddressFromLastPickUpPackage(conn, lastPackageForPickUp);
		System.out.println("ADRESA ZA POSLEDNJI PAKET KOJI SE PREUZIMA JE: " + currRouteAddress);
		this.sortRoutesForDelivery(conn, idDelivery, currRouteAddress);
        
		//ovde dodaj pocetnu udaljenost 
		// postavljanje prve adrese iz tabele kao currAddr
		this.setNextAddress(conn, courierUsername);

		return true;
	}
	//Uzima sledecu rutu iz tabele PickUpRoutes
	private void setNextAddress(Connection conn, String name) {
		this.addDistance(conn, name, 0);
		PreparedStatement ps = null;
		String sqlupdate = " update Delivery  " + "			set currAddr= (" + "				select TOP 1 pur.addr "
				+ "				from Courier c inner join  Delivery d1 on c.userName=d1.userName inner join PickUpRoutes  pur on pur.idDelivery=d1.idDelivery  "
				+ "				where d1.userName=?	 and c.status=1 " + "				order by pur.idRoute " + "  ) "
						+ "		where userName=? ";
		try {
			ps = conn.prepareStatement(sqlupdate);
			ps.setString(1, name);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	//Uzima sledecu rutu iz tabele DeliverRoutes
	private void addDistance(Connection conn,String name,int t) {
		String sql="";
		if(t==1) sql=" select TOP 1 pur.addr "   
				+ "				from Courier c inner join  Delivery d1 on c.userName=d1.userName inner join DeliverRoutes  pur on pur.idDelivery=d1.idDelivery  "
				+ "				where d1.userName=?	 and c.status=1 " + "		order by pur.currDistance " + " ";
		else sql="select TOP 1 pur.addr " + 
				"	from Courier c inner join  Delivery d1 on c.userName=d1.userName inner join PickUpRoutes  pur on pur.idDelivery=d1.idDelivery " + 
				"	where d1.userName=?	 and c.status=1 	order by pur.idRoute  ";
		PreparedStatement ps = null;
		CallableStatement cs=null;
		PreparedStatement ps2 = null;
		ResultSet rs=null;
		ResultSet rs2=null;
		int a1=0;
		int a2=this.getCurrentLocation(conn, name);
		System.out.println("TEKUCA LOKACIJA: "+a2);
		int addr=-1;
		String sqlquery=" select s.idDistrict from Stockroom s"
				+ "			inner join Address a on a.idDistrict=s.idDistrict  "
				+ "			where s.idDistrict in ( "
				+ "			select a.idDistrict "
				+ "			from Address a inner join City c on c.idCity=a.idCity "
				+ "			where c.idCity=( "
				+ " 		select  c.idCity"
				+ "			from Courier cr inner join [User] u on u.userName=cr.userName "
				+ "			inner join Address a on a.idDistrict=u.idDistrict "
				+ "			inner join City c on c.idCity=a.idCity "
				+ "			where cr.userName=? ) ) ";
		try {
			ps2=conn.prepareStatement(sqlquery);
			ps2.setString(1, name);
			rs2=ps2.executeQuery();
			rs2.next();
			addr=rs2.getInt(1); //adresa magacina
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, name);
			rs=ps.executeQuery();
			if(rs.next()) { //druga adresa je adresa sledece lokacije 
					a1=rs.getInt(1);
			}
			else { //druga adresa je adresa magacina
				a1=addr;
				System.out.println("*****************DRUGA ADRESA JE ADRESA MAGACINAA****  "+a1);
			}
			if(a2<=0) { a2=addr;   System.out.println("*****************PRVA  ADRESA JE ADRESA MAGACINAA****  "+a2);}
			cs=conn.prepareCall(" { call spDodajDistancu(?,?,?) }");
			System.out.println("Prva adresa: "+a1+ " Druga adresa: "+a2);
			cs.setInt(1, a1);
			cs.setInt(2, a2);
			cs.setString(3, name);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps2!= null)
				try {
					ps2.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (cs != null)
				try {
					cs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (rs2 != null)
				try {
					rs2.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
		
	}
	private void setNextAddress2(Connection conn, String name) {
		this.addDistance(conn, name, 1);
		PreparedStatement ps = null;
		String sqlupdate = " update Delivery  " + "			set currAddr= (" + "				select TOP 1 pur.addr "
				+ "				from Courier c inner join  Delivery d1 on c.userName=d1.userName inner join DeliverRoutes  pur on pur.idDelivery=d1.idDelivery  "
				+ "				where d1.userName=?	 and c.status=1 " + "		order by pur.currDistance " + "  )  "
						+ "		where userName=? ";
		try {
			ps = conn.prepareStatement(sqlupdate);
			ps.setString(1, name);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	private int getRoutesCountSameCity(Connection conn, String name) {
		PreparedStatement ps=null;
		String sqlquery="   select count(*) "
				+ "			from City ci inner join Address a on a.idCity=ci.idCity  "
				+ "			inner join DeliverRoutes dr on a.idDistrict=dr.addr inner join Delivery d on dr.idDelivery=d.idDelivery "
				+ "			inner join Courier c on c.userName=d.userName "
				+ "			where c.status=1 and c.userName=? and "
				+ "			ci.idCity=(  "
				+ "					select c.idCity "
				+ "					from Courier cr inner join Delivery d on "
				+ "					d.userName=cr.userName  inner join Address a on a.idDistrict=d.currAddr "
				+ "					inner join City c on  a.idCity=c.idCity  "
				+ "					where cr.userName=? and cr.status=1 "
				+ ")";
		ResultSet rs=null;
		
		try {
			ps=conn.prepareStatement(sqlquery);
			ps.setString(1, name);
			ps.setString(2, name);
			rs=ps.executeQuery();
			if(rs.next()) return rs.getInt(1);
			else return -1;
		} catch (SQLException e) {
			e.printStackTrace(); return -1;
		}finally {
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

	private int getCurrentRouteCity(Connection conn,String name) {
		PreparedStatement ps=null;
		ResultSet rs=null;
		String sqlquery=" select c.idCity"
				+ "			from City c inner join Address a on a.idCity=c.idCity"
				+ "			inner join Delivery d on d.currAddr=a.idDistrict "
				+ "			inner join Courier cr on cr.userName=d.userName "
				+ "			where cr.userName=? and cr.status=1 ";
		try {
			ps=conn.prepareStatement(sqlquery);
			ps.setString(1, name);
			rs=ps.executeQuery();
			if(rs.next()) return rs.getInt(1);
			else return -1;	
			
		} catch (SQLException e) {
			e.printStackTrace(); return -1;
		}
		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	private int getAddressFromLastPickUpPackage(Connection conn, int idp) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select addr from PickUpRoutes where idPackage=? ");
			ps.setInt(1, idp);
			rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt(1);
			else
				return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	//////////////////// VEHICLE OPERATIONS////////////////////////
	private BigDecimal getVehicleCapacity(Connection conn, String num) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select capacity from Vehicle where licencePlateNum=? ");
			ps.setString(1, num);
			rs = ps.executeQuery();
			if (rs.next())
				return rs.getBigDecimal(1);
			else
				return null;
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
		}

	}

	private boolean setCourierActive(Connection conn, String num, String name) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(" update Courier set currentVehicle=? , status=1 where userName=? ");
			ps.setString(1, num);
			ps.setString(2, name);
			if (ps.executeUpdate() > 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	private boolean setCourierInActive(Connection conn,String name) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(" update Courier set currentVehicle=null , status=0 where userName=? ");
			ps.setString(1, name);
			if (ps.executeUpdate() > 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	private int insertDelivery(Connection conn, String num, String name, BigDecimal cap) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// nije podesen tek grad, a trebalo bi verovatno
			// verovatno moras da cuvas adresu
			ps = conn.prepareStatement(" insert into Delivery(userName,cap,deliveryState) values(?,?,0) ",
					PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ps.setBigDecimal(2, cap);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next())
				return rs.getInt(1);
			else
				return -1;

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	private boolean insertPairVehicleCourier(Connection conn, String num, String name) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(" insert into Drives(licencePlateNum,userName) values(?,?) ");
			ps.setString(1, num);
			ps.setString(2, name);
			if (ps.executeUpdate() > 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}
    
	private void returnVehicle(Connection conn,String name,int idStockroom) {
		PreparedStatement ps=null;
		//updejtuj kurira 
		PreparedStatement ps2=null;
		CallableStatement cs=null;
		ResultSet rs2=null;
		String sqlquery=" select v.licencePlateNum "
				+ "			from Courier c inner join Vehicle v on v.licencePlateNum=c.currentVehicle "
				+ "			where c.status=1 and c.userName=? ";
		int idDelivery=this.getDeliveryId(conn, name);
		String vehicle;
		String sqlupdate=" update Vehicle "
				+ "			set idStockroom=? , taken=0 "
				+ "			where licencePlateNum= ("
				+ "				select  c.currentVehicle "
				+ "				from Courier c "
				+ "				where c.userName=? ) ";
		try {
			ps2=conn.prepareStatement(sqlquery);
			ps2.setString(1, name);
			rs2=ps2.executeQuery();
			rs2.next(); 
			vehicle=rs2.getString(1);
			
			cs = conn.prepareCall(" { call spIzracunajProfit(?,?,?) } ");
			cs.setString(1,vehicle);
			cs.setString(2, name);
			cs.setInt(3, idDelivery);
			cs.executeUpdate();
			ps=conn.prepareStatement(sqlupdate);
			ps.setInt(1, idStockroom);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
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
			if(rs2!=null)
				try {
					rs2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
		}
	}
	private boolean setVehicleTaken(Connection conn, String num) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(" update Vehicle set taken=1 , idStockroom=null where licencePlateNum=? ");
			ps.setString(1, num);
			if (ps.executeUpdate() > 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	private String chooseVehicle(Connection conn, String courierUsername) {
		// dohvatis sva vozila kojima je idStockroom u istom gradu
		// i kojima je status taken=0
		// podesis da je status sada taken=1
		// dodas u tabelu drives par(courier,vehicle)
		// dodaj delivery
		// dodaj ga kuriru
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sqlquery = " select TOP 1 v.licencePlateNum " + " from Vehicle v "
				+ " where v.taken=0 and v.idStockroom in ( " + "	select s.idStockroom "
				+ "	from Stockroom s inner join Address a on s.idDistrict=a.idDistrict inner join City g on g.idCity=a.idCity "
				+ "	where g.idCity= ( " + "		select g.idCity "
				+ "		from Courier c inner join [User] u on u.userName=c.userName inner join Address a on a.idDistrict=u.idDistrict "
				+ "		inner join City g on g.idCity=a.idCity " + "		where c.userName=? " + "	) " + " "
				+ " ) ";
		try {
			ps = conn.prepareStatement(sqlquery);
			ps.setString(1, courierUsername);
			rs = ps.executeQuery();

			if (rs.next())
				return rs.getString(1);
			else
				return null;
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
		}
	}

	

	///////////////// ROUTES OPERATIONS/////////////////////
	
	//Dodavanje ruta iz nekog drugog grada
	private int insertRoutes2(Connection conn,String name, int idCity) {
		PreparedStatement ps=null;
		PreparedStatement ps2=null;
		PreparedStatement ps3=null;
		PreparedStatement ps4=null;
		ResultSet rs=null;
		ResultSet rs2=null;
		ResultSet rs3=null;
		double currCap=0;
		double cap=0;
		double sum=0;
		boolean full = false;
		int idDelivery=-1;
		int lastPackage=-1;
		String sqlquery=" select p.idPackage, p.weight, p.AddrStart, p.AddrEnd "
				+ "			 from Package p inner join Address a on p.AddrStart=a.idDistrict "
				+ "			inner join City c on c.idCity=a.idCity "
				+ "			where p.status=1 and "
				+ "			p.AddrStart in ( "
				+ "					select a2.idDistrict "
				+ "					from City c2 inner join Address a2 on a2.idCity=c2.idCity "
				+ "					where c2.idCity=? "
				+ ")"
				+ " order by DateAccepted asc  ";
		//String sqlinsert2 = "insert into DeliverRoutes(addr,idDelivery,idPackage) values(?,?,?) ";
		String sqlinsert = " insert into PickUpRoutes(addr,idDelivery,idPackage) values(?,?,?) ";
		String sqlquery2=" select  d.cap, d.currWeight "
				+ "			from  Delivery d inner join Courier c on c.userName=d.userName "
				+ "			where c.userName=? and c.status=1 ";
		String sqlquery3=" select  d.idDelivery "
				+ "			from Delivery d inner join Courier c on c.userName=d.userName "
				+ "			where c.userName=? and c.status=1 ";
		try {
			ps3=conn.prepareStatement(sqlquery3);
			ps3.setString(1,name);
			rs3=ps3.executeQuery();
			if(rs3.next())  idDelivery=rs3.getInt(1);
			else return -1;
			ps2=conn.prepareStatement(sqlquery2);
			ps2.setString(1, name);
			rs2=ps2.executeQuery();
			if(rs2.next()) {
					cap=rs2.getBigDecimal(1).doubleValue();
					currCap=rs2.getBigDecimal(2).doubleValue();
					sum=currCap;
			}else return -1;
			ps4=conn.prepareStatement(sqlinsert);
			ps=conn.prepareStatement(sqlquery);
			ps.setInt(1, idCity);
			rs=ps.executeQuery();
			while(rs.next()) {
				if (sum+rs.getBigDecimal(2).doubleValue()<=cap) {
					ps4.setInt(1,rs.getInt(3));
					ps4.setInt(2,idDelivery);
					ps4.setInt(3, rs.getInt(1));
					if (ps4.executeUpdate() > 0 ) {
						lastPackage = rs.getInt(1); // id poslednjeg paketa koji je dodat
						sum += rs.getBigDecimal(2).doubleValue();
					}
				} else {
					full = true;
					break;
				}	
			}
			//if(currCap==sum) return -1; //nista nije dodato, idi dalje 
			int lp = -1;
			if (!full)
				lp=this.insertRoutesFromStockroom2(conn, idDelivery, cap, sum, name, idCity);
			if (lp != -1)
				return lp;
			else
				return lastPackage; 
			
		} catch (SQLException e) {
			e.printStackTrace(); return -1;
		}
		finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps3 != null)
				try {
					ps3.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps2 != null)
				try {
					ps2.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps4 != null)
				try {
					ps4.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs2 != null)
				try {
					rs2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs3 != null)
				try {
					rs3.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}	
	}
	//Dodavanje ruta iz maticnog grada
	private int insertRoutes(Connection conn, int idDelivery, BigDecimal cap, String name) {
		// AddrStart mora da bude u odg gradu
		// status mora da bude 1
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs = null;
		boolean full = false;
		double sum = 0;
		int lastPackage = -1;
		String sqlinsert2 = "insert into DeliverRoutes(addr,idDelivery,idPackage) values(?,?,?) ";
		String sqlinsert = " insert into PickUpRoutes(addr,idDelivery,idPackage) values(?,?,?) ";
		String sqlquery = " select p.idPackage,p.weight, p.AddrEnd , p.AddrStart "
				+ " from Package p inner join Address a1 on p.AddrStart=a1.idDistrict inner join City c2 on c2.idCity=a1.idCity "
				+ " where p.AddrStart in ( select  a.idDistrict from Address a inner join	City c on a.idCity=c.idCity "
				+ " where c.idCity= ( " + "		select g.idCity "
				+ "		from Courier c inner join [User] u on u.userName=c.userName inner join Address a on a.idDistrict=u.idDistrict "
				+ "		inner join City g on g.idCity=a.idCity " + "		where c.userName=? " + "		)  " + " ) "
				+ " and p.status=1 " + "	 order by DateAccepted asc  ";
		try {
			ps = conn.prepareStatement(sqlquery);
			ps2 = conn.prepareStatement(sqlinsert);
			ps3 = conn.prepareStatement(sqlinsert2);
			ps.setString(1, name);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (sum + rs.getBigDecimal(2).doubleValue() <= cap.doubleValue()) {
					ps2.setInt(1, rs.getInt(4));
					ps2.setInt(2, idDelivery);
					ps2.setInt(3, rs.getInt(1));
					ps3.setInt(1, rs.getInt(3));
					ps3.setInt(2, idDelivery);
					ps3.setInt(3, rs.getInt(1));
					if (ps2.executeUpdate() > 0 && ps3.executeUpdate() > 0) {
						lastPackage = rs.getInt(1); // id poslednjeg paketa koji je dodat
						sum += rs.getBigDecimal(2).doubleValue();
					}
				} else {
					full = true;
					break;
				}
			}
			/*if (sum == 0) {
				// nista nisam dodala
				return -1;  //ne smes da vratis -1 moras magacin da proveris
			}*/
			int lp = -1;
			if (!full)
				lp = this.insertRoutesFromStockroom(conn, idDelivery, cap, name, sum);
			if (lp != -1)
				return lp;
			else
				return lastPackage;

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps3 != null)
				try {
					ps3.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps2 != null)
				try {
					ps2.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
    private int insertRoutesFromStockroom2(Connection conn, int idDelivery, double cap,double sum,String name,int idCity) {
    	PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		int lastPackage = -1;
		//String sqlinsert2 = "insert into DeliverRoutes(addr,idDelivery,idPackage) values(?,?,?) ";
		String sqlinsert = " insert into PickUpRoutes(addr,idDelivery,idPackage) values(?,?,?) ";
		String sqlquery = " select p.weight, pis.idPackage, s.idDistrict,p.AddrEnd "
				+ " from Package p inner join PackagesInStockroom pis on p.idPackage=pis.idPackage inner join Stockroom s on s.idStockroom=pis.idStockroom "
				+ " where s.idDistrict in (  "
				+ "	select  a.idDistrict from Address a inner join	City c on a.idCity=c.idCity  "
				+ "					 where c.idCity= ? "
				+ "			 ) order by p.DateAccepted asc  ";
		try {
		ps = conn.prepareStatement(sqlquery);
		ps2 = conn.prepareStatement(sqlinsert);
		//ps3 = conn.prepareStatement(sqlinsert2);
		ps.setInt(1, idCity);
		rs = ps.executeQuery();
		while (rs.next()) {
			if (sum + rs.getBigDecimal(1).doubleValue() <= cap) {
				ps2.setInt(1, rs.getInt(3));
				ps2.setInt(2, idDelivery);
				ps2.setInt(3, rs.getInt(2));
				if (ps2.executeUpdate() > 0 ) {
					lastPackage = rs.getInt(2);
					sum += rs.getBigDecimal(1).doubleValue();
				}
			} else
				break;
		}
		return lastPackage;
		} catch(SQLException e) { e.printStackTrace(); return -1;
		}
		finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps2 != null)
				try {
					ps2.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
    }
	private int insertRoutesFromStockroom(Connection conn, int idDelivery, BigDecimal cap, String name, double sum) {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs = null;
		int lastPackage = -1;
		String sqlinsert2 = "insert into DeliverRoutes(addr,idDelivery,idPackage) values(?,?,?) ";
		String sqlinsert = " insert into PickUpRoutes(addr,idDelivery,idPackage) values(?,?,?) ";

		String sqlquery = " select p.weight, pis.idPackage, s.idDistrict,p.AddrEnd "
				+ " from Package p inner join PackagesInStockroom pis on p.idPackage=pis.idPackage inner join Stockroom s on s.idStockroom=pis.idStockroom "
				+ " where s.idDistrict in (  "
				+ "	select  a.idDistrict from Address a inner join	City c on a.idCity=c.idCity  "
				+ "					 where c.idCity= ( " + "						select g.idCity  "
				+ "						from Courier c inner join [User] u on u.userName=c.userName inner join Address a on a.idDistrict=u.idDistrict "
				+ "							inner join City g on g.idCity=a.idCity "
				+ "							where c.userName=? " + "						)  "
				+ "			 ) order by p.DateAccepted asc  ";

		try {
			ps = conn.prepareStatement(sqlquery);
			ps2 = conn.prepareStatement(sqlinsert);
			ps3 = conn.prepareStatement(sqlinsert2);
			ps.setString(1, name);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (sum + rs.getBigDecimal(1).doubleValue() <= cap.doubleValue()) {
					ps2.setInt(1, rs.getInt(3));
					ps2.setInt(2, idDelivery);
					ps2.setInt(3, rs.getInt(2));

					ps3.setInt(1, rs.getInt(4));
					ps3.setInt(2, idDelivery);
					ps3.setInt(3, rs.getInt(2));
					if (ps2.executeUpdate() > 0 && ps3.executeUpdate() > 0) {
						lastPackage = rs.getInt(2);
						sum += rs.getBigDecimal(1).doubleValue();
					}
				} else
					break;
			}
			return lastPackage;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps3 != null)
				try {
					ps3.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (ps2 != null)
				try {
					ps2.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	private void sortRoutesForDelivery(Connection conn, int idDelivery, int currAddr) {
		CallableStatement cs = null;
		try {
			cs = conn.prepareCall(" { call spSortirajRute(?,?) } ");
			cs.setInt(1, currAddr);
			cs.setInt(2, idDelivery);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cs != null)
				try {
					cs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}
}
