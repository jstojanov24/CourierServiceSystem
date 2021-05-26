package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.PackageOperations;

public class sj160179_PackageOperations implements PackageOperations {

	private void incNumberOfSentPackages(int idPackage) {
		Connection conn=MyDB.getInstance().getConnection();
		CallableStatement cs=null;
		try {
			cs=conn.prepareCall("{call spIncBrojPoslatih(?) }");
			cs.setInt(1, idPackage);
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public boolean acceptAnOffer(int offerId) {
		//NAPOMENA:offerId je idPaketa
		// IZMENI:OfferId ti je idPaketa
		// sacuvaj cenu u tabelu Paket
		// status na 1
		// vreme prihvatanja
		// izbrises iz tabele
		//inc broj poslatih paketa za odgovarajuceg korisnika
		//trigger??
		BigDecimal price = this.getOfferPrice(offerId);
		
		if (price == null) {
			System.out.println("Cena je null");
			return false;
		}
		else {
			System.out.println(price);
			if (this.setOfferAccepted(price, offerId)) {
				this.incNumberOfSentPackages(offerId);
				if (this.deletePackageOffer(offerId))
					return true;
				else
					return false;
			} else {
				System.out.println("Neuspesno updejtovanje tabele Package");
				return false;
			}
				
		}

	}
/////DODAJ DA NE MOZES DA MENJAS PODATKE AKO PAKET NIJE U STATUSU KREIRAN/////
	private BigDecimal getOfferPrice(int offerId) {
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sqlquery = "select price from PackageOffer where idPackage=? ";
		try {
			ps = conn.prepareStatement(sqlquery);
			ps.setInt(1, offerId);
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	private boolean setOfferAccepted(BigDecimal p, int offerId) {
		//offerId je idPaketa!!!!!
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		/*String sqlupdate = " update Package set status=1, DateAccepted=CURRENT_TIMESTAMP, "
				+ " Price=?  where idPackage=( " + 
				"	select idPackage " + 
				"	from PackageOffer " + 
				"	where id=? "+ 
				") ";*/
		
		String sqlupdate = " update Package set status=1, DateAccepted=CURRENT_TIMESTAMP, "
				+ " Price=?  where idPackage=? " ;
		try {
			ps = conn.prepareStatement(sqlupdate);
			ps.setBigDecimal(1, p);
			ps.setInt(2, offerId);
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
	private boolean setOfferRejected(int offerId) {
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		String sqlupdate = " update Package set status=4, DateAccepted=CURRENT_TIMESTAMP "
				+ " where idPackage=? ";
		try {
			ps = conn.prepareStatement(sqlupdate);
			ps.setInt(1, offerId);
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

	private boolean deletePackageOffer(int offerId) {
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		String sqldelete = "delete from PackageOffer where idPackage=? ";
		try {
			ps = conn.prepareStatement(sqldelete);
			ps.setInt(1, offerId);
			if (ps.executeUpdate() > 0) {
				System.out.println("Uspesno izbrisana ponuda ciji je idPaketa=" + offerId);
				return true;
			} else {
				System.out.println("Neuspesno brisanje ponude ciji je idPaketa=" + offerId);
				return false;
			}

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

	@Override
	public boolean changeType(int packageId, int newType) {
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		String sqlupdate = " update Package set idType=? where idPackage=? and status=0 ";
		try {
			ps = conn.prepareStatement(sqlupdate);
			ps.setInt(1, newType);
			ps.setInt(2, packageId);
			if (ps.executeUpdate() > 0) {
				System.out.println("Promena tipa paketa");
				return true;
			} else {
				System.out.println("Promena paketa nije uspela");
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	@Override
	public boolean changeWeight(int packageId, BigDecimal newWeight) {
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		String sqlupdate = " update Package set weight=? where idPackage=? and status=0  ";
		try {
			ps = conn.prepareStatement(sqlupdate);
			ps.setBigDecimal(1, newWeight);
			ps.setInt(2, packageId);
			if (ps.executeUpdate() > 0) {
				System.out.println("Promena tezine paketa");
				return true;
			} else {
				System.out.println("Promena tezine paketa nije uspela");
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	@Override
	public boolean deletePackage(int packageId) {
		// Dodaj da se brise samo paket koji je u kreiran ili odbijen
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		String sqldelete = "delete from Package where idPackage=? and ( status=0 or status=4) ";
		try {
			ps = conn.prepareStatement(sqldelete);
			ps.setInt(1, packageId);
			if (ps.executeUpdate() > 0) {
				System.out.println("Uspesno izbrisan paket ciji je id=" + packageId);
				return true;
			} else {
				System.out.println("Neuspesno brisanje paketa ciji je id=" + packageId);
				return false;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

	@Override
	public Date getAcceptanceTime(int packageId) {
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sqlselect = "select DateAccepted from Package where idPackage=? ";
		try {
			ps = conn.prepareStatement(sqlselect);
			ps.setInt(1, packageId);
			rs = ps.executeQuery();
			if (rs.next()) {

				return rs.getDate(1);
			} else
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	public void printPackagesId(List<Integer> list) {
		if (list == null)
			return;
		StringBuilder sb = new StringBuilder();
		if (list.isEmpty())
			sb.append("Lista je prazna.");
		else {
			for (int i = 0; i < list.size(); i++)
				sb.append(" " + list.get(i));
			sb.append("\n");
		}

		System.out.println(sb.toString());

	}

	@Override
	public List<Integer> getAllPackages() {
		List<Integer> list = new ArrayList<Integer>();
		Connection conn = MyDB.getInstance().getConnection();
		ResultSet rs = null;
		Statement s = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery("select idPackage from Package ");
			while (rs.next()) {
				list.add(new Integer(rs.getInt(1)));
			}
			this.printPackagesId(list);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (s != null)
				try {
					s.close();
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

	@Override
	public List<Integer> getAllPackagesCurrentlyAtCity(int cityId) {
		/*Dohvatanje liste svih neisporucenih paketa koji se trenutno nalaze u nekom gradu. 
		Paketi se nalaze u nekom gradu ukoliko se nalaze na pocetnoj ili krajnjoj adresi 
		ili se nalaze u magacinu u tom gradu.
		Pakete koji se nalaze u vozilu, nije potrebno dohvatati.*/
		//paketi kojima je startAddr u datom gradu i status=1,zahtev prihvacen
		//paketi kojima je EndAddr u datom gradu i status=3,paket isporucen
		//paketi koji se nalaze u magacinu u datom gradu 
		
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		List<Integer> list=new ArrayList<Integer>();
		String sqlquery=" 	select p.idPackage "
				+ "			from Package p inner join Address a on a.idDistrict= p.AddrStart "
				+ "			inner join City c on c.idCity=a.idCity "
				+ "			where p.status=1 and c.idCity=? "
				+ "			union  "
				+ "			select p.idPackage " + 
				"			from Package p inner join Address a on a.idDistrict= p.AddrEnd " + 
				"			inner join City c on c.idCity=a.idCity" + 
				"			where p.status=3 and c.idCity=? " + 
				"			union 	"
				+ "			select p.idPackage "
				+ "			from Package p inner join PackagesInStockroom pis on p.idPackage=pis.idPackage "
				+ "			inner join Stockroom s on s.idStockroom=pis.idStockroom "
				+ "			inner join Address a on a.idDistrict=s.idDistrict inner join City c on"
				+ "			c.idCity=a.idCity"
				+ "			where p.status=2 and c.idCity=? ";
		try {
			ps=conn.prepareStatement(sqlquery);
			ps.setInt(1, cityId);
			ps.setInt(2, cityId);
			ps.setInt(3, cityId);
			rs=ps.executeQuery();
			while(rs.next()) {
					list.add(new Integer(rs.getInt(1)));
			}
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
	public List<Integer> getAllPackagesWithSpecificType(int type) {
		List<Integer> list = new ArrayList<Integer>();
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sqlquery = "select idPackage from Package where idType=? ";
		try {
			ps = conn.prepareStatement(sqlquery);
			ps.setInt(1, type);
			rs = ps.executeQuery();
			while (rs.next())
				list.add(new Integer(rs.getInt(1)));
			this.printPackagesId(list);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (ps != null)
				try {
					ps.close();
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
	public List<Integer> getAllUndeliveredPackages() {
		/*
		 *  Paketi su neisporuceni ukoliko je ponuda za njihovo slanje prihvacena(stanje 1)
		 *   ili su paketi vec preuzeti, ali oni i dalje nisu isporuceni.(stanje 2)
		 */
		List<Integer> list = new ArrayList<Integer>();
		Connection conn = MyDB.getInstance().getConnection();
		ResultSet rs = null;
		Statement s = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery("select idPackage from Package where status=1 or status=2 ");
			while (rs.next()) {
				list.add(new Integer(rs.getInt(1)));
			}
			this.printPackagesId(list);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (s != null)
				try {
					s.close();
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

	@Override
	public List<Integer> getAllUndeliveredPackagesFromCity(int cityId) {
		//status->2 ili 1
		//AddrStart -> mora biti u gradu cityId
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		List<Integer> list=new ArrayList<Integer>();
		String sqlquery="select pa.idPackage " + 
				"from Package pa " + 
				"where pa.idPackage in ( " + 
				"	select p.idPackage " + 
				"	from Package p inner join Address a on p.AddrStart=a.idDistrict inner join City c on a.idCity=c.idCity " + 
				"	where c.idCity=? " + 
				" ) and (status=2 or status=1)";
		try {
			ps=conn.prepareStatement(sqlquery);
			ps.setInt(1, cityId);
			rs=ps.executeQuery();
			while(rs.next()) list.add(new Integer(rs.getInt(1)));
			this.printPackagesId(list);
			return list;
		} catch (SQLException e) {
			return null;
		}
		 finally {
				if (ps != null)
					try {
						ps.close();
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

	@Override
	public int getCurrentLocationOfPackage(int packageId) {
		/*id grada u kome se paket nalazi, ukoliko se nalazi u nekom od magacina 
		ili na pocetnoj ili krajnjoj adresi. Ukoliko se nalazi u vozilu, vraca -1.*/
		//0 – zahtev kreiran, 
		//1 – prihvacena aponuda, 
		//2 - paket preuzet, 
		//3 – isporucen,
		//4 - ponuda odbijena
		Connection conn=MyDB.getInstance().getConnection();
		CallableStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareCall("{? = call dohvatiGrad(?) }");
			ps.registerOutParameter(1, Types.INTEGER);
			ps.setInt(2, packageId);
			ps.execute();
			return ps.getInt(1);
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}

	@Override
	public int getDeliveryStatus(int packageId) {
		Connection conn=MyDB.getInstance().getConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		String sqlquery="select status from Package where idPackage=? ";
		try {
			ps=conn.prepareStatement(sqlquery);
			ps.setInt(1, packageId);
			rs=ps.executeQuery();
			if(rs.next()) return rs.getInt(1);
			else return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
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
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public BigDecimal getPriceOfDelivery(int packageId) {
		Connection conn = MyDB.getInstance().getConnection();
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sqlquery = "select price from Package where idPackage=? ";
		try {
			ps = conn.prepareStatement(sqlquery);
			ps.setInt(1, packageId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getBigDecimal(1);
			} else
				return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (ps != null)
				try {
					ps.close();
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
	public int insertPackage(int addressFrom, int addressTo, String userName, int packageType, BigDecimal weight) {
		// dodaj paket
		// trigger za offer
		// trigger za numOfSentPackages->kada se salje paket
		Connection conn = MyDB.getInstance().getConnection();
		if(weight==null || weight.doubleValue()==0) weight=new BigDecimal(10);
		
		String s = "insert into Package(userName,AddrStart,AddrEnd,idType,weight) values(?,?,?,?,?)";
		String updatesql = " Update Package " + " set DateCreated=CURRENT_TIMESTAMP where idPackage=? ";
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(s, PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, userName);
			ps.setInt(2, addressFrom);
			ps.setInt(3, addressTo);
			ps.setInt(4, packageType);
			ps.setBigDecimal(5, weight);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				System.out.println("Dodat je paket ciji je primarni kljuc: " + rs.getInt(1));

				ps2 = conn.prepareStatement(updatesql);
				ps2.setInt(1, rs.getInt(1));
				ps2.executeUpdate();

				return rs.getInt(1);
			} else
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
			if (ps2 != null)
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();

				}
		}
		// mozda bi mogla da dodas za update trigger
		// da se ne menja cena ako je vec prihvacena ponuda

	}

	@Override
	public boolean rejectAnOffer(int offerId) {
		//treba da setuje status na 4
		//izbrise iz tabele PackageOffer
		if(this.setOfferRejected(offerId)) {
			if(this.deletePackageOffer(offerId)) return true;
			else return false;
		}else {
			System.out.println("Neuspesna promena statusa nakon odbijanja paketa");
			return false;
		}
	}

	public static void main(String[] args) {
		sj160179_PackageOperations pack = new sj160179_PackageOperations();
		// int primaryKey=pack.insertPackage(13, 14, "janka2405", 1, new BigDecimal(7));
		// System.out.println(primaryKey);
		pack.getAllPackages();
		/*
		 * pack.deletePackage(primaryKey); pack.getAllPackages();
		 */
		/*
		 * pack.changeWeight(12, new BigDecimal(1.2)); pack.changeType(12, 1);
		 * pack.changeWeight(15, new BigDecimal(0.5));
		 */

		/*java.sql.Date d = pack.getAcceptanceTime(7);
		if (d != null)
			System.out.println(d);

		System.out.println(pack.getPriceOfDelivery(7));

		pack.getAllPackagesWithSpecificType(1);
		pack.changeType(7, 0);
		pack.getAllPackagesWithSpecificType(2);*/
		//////////////PRIHVATANJE ZAHTEVA///////////////
		/*if(pack.acceptAnOffer(3)) System.out.println("Uspesno prihvacen zahtev");
		else System.out.println("Neuspesno prihvatanje zahteva");*/
		///////////ODBIJANJE ZAHTEVA////////////////////
		/*if(pack.rejectAnOffer(4)) System.out.println("Odbijen zahtev");
		else System.out.println("Neuspesno odbijanje zahteva");*/
		
		//////////////DOHVATANJE STATUSA PAKETA////////////
		/*Integer i=pack.getDeliveryStatus(9);
		if(i!=null) System.out.println(i);
		i=pack.getDeliveryStatus(12);
		if(i!=null) System.out.println(i);
		i=pack.getDeliveryStatus(14);
		if(i!=null) System.out.println(i);*/
		pack.getAllUndeliveredPackagesFromCity(1401);
		pack.getAllUndeliveredPackagesFromCity(1402);
		
		
		

	}

}
