package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import rs.etf.sab.operations.UserOperations;

public class sj160179_UserOperations implements UserOperations {

	@Override
	public boolean declareAdmin(String userName) {
		//System.out.println("Metoda deklarisanja admina sa imenom " + userName);
		Connection con = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		PreparedStatement ps2=null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			ps = con.prepareStatement("select userName from [User] where userName=? ");
			ps.setString(1, userName);
			rs = ps.executeQuery();
			if (!rs.next()) {
				System.out.println("Korisnik sa imenom: " + userName + " ne postoji u bazi.");
				return false;
			} else {
				// korisnik postoji proveri ima li ga u bazi
				ps2 = con.prepareStatement("select userName from Administrator where userName=? ");
				ps2.setString(1, userName);
				rs2 = ps2.executeQuery();
				if (rs2.next()) {
					System.out.println("Korisnik sa imenom: " + userName + " je vec admin.");
					return false;
				} else {
					System.out.println("Deklarisanje da je korisnik sa imenom " + userName + " admin.");
					this.insertAdmin(con, userName);
					return true;
				}
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (ps2 != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs2 != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	private void insertAdmin(Connection conn, String userName) throws SQLException {

		PreparedStatement ps = conn.prepareStatement("insert into Administrator (userName) values(?) ");
		ps.setString(1, userName);
		ps.executeUpdate();

	}

	@Override
	public int deleteUsers(String... userNames) {
		System.out.println("Delete users metoda");
		int sum = 0;
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("delete from [User] where userName=? ");
			for (int i = 0; i < userNames.length; i++) {
				ps.setString(1, userNames[i]);
				int retVal = ps.executeUpdate();
				if (retVal > 0)
					sum += retVal;
			}
			System.out.println("Izbrisao sam ukupno: " + sum + " korisnika iz tabele.");
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
	public List<String> getAllUsers() {
		//System.out.println("Dohvatanje svih korisnika");
		List<String> list = new ArrayList<String>();
		Connection conn = MyDB.getInstance().getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery("select userName from [User]  ");
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			this.printAllUsernames(list);
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	public String printAllUsernames(List<String> list) {
		StringBuilder sb = new StringBuilder();
		if (list.isEmpty())
			sb.append("Lista je prazna.");
		for (int i = 0; i < list.size(); i++)
			sb.append(list.get(i) + " ");

		sb.append("\n");
		return sb.toString();
	}

	@Override
	public int getSentPackages(String... userNames) {
		System.out.println("Dohvatanje paketa");
		int sum = 0;
		boolean flag = false;
		Connection conn = MyDB.getInstance().getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		// mora da vrati null ako nema takvih korisnika

		try {
			ps = conn.prepareStatement("select numOfSentPackages from [User] where userName=? ");
			for (int i = 0; i < userNames.length; i++) {
				ps.setString(1, userNames[i]);
				rs = ps.executeQuery();
				if (rs.next()) {
					sum += rs.getInt(1);
					flag = true;
				}
			}
			if(flag==false) return -1;
			else return sum;
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


	private boolean checkUserData(String firstName, String lastName, String password) {
		// veliko slovo i spec karakter zakomentarisi za testove
		//String myPattern="^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*_!]).*$";
		//String myPattern = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z]).*$";
		String myPattern="^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$";
		
		if (!Character.isUpperCase(firstName.charAt(0)) || !Character.isUpperCase(lastName.charAt(0))
				|| !password.matches(myPattern))
			return false;
		else
			return true;
	}

	@Override
	public boolean insertUser(String userName, String firstName, String lastName, String password,int idAddress) {
		Connection conn = MyDB.getInstance().getConnection();
		String s = "insert into [User] (userName,firstName,lastName,password, idDistrict) values(?,?,?,?,?)";
		PreparedStatement ps = null;
		if (!this.checkUserData(firstName, lastName, password)) {
			System.out.println("Neuspelo dodavanje! Neispravni podaci!");
			return false;
		}

		try {
			ps = conn.prepareStatement(s, PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, userName);
			ps.setString(2, firstName);
			ps.setString(3, lastName);
			ps.setString(4, password);
			ps.setInt(5, idAddress);

			if (ps.executeUpdate() > 0) {
				/*System.out.println("Uspesno dodat korisnika sa sledecim podacima:");
				System.out.println("Username: " + userName + ", Firstname: " + firstName + ", Lastname: " + lastName);*/
				return true;
			} else {
				System.out.println("Greska prilikom dodavanja korisnika!");
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();

				}

		}
	}

	public static void main(String[] args) {
		sj160179_UserOperations user = new sj160179_UserOperations();
		// user.insertUser("janka2405", "Jana", "Stojanovic", "jankaaajankaaa");
		// user.insertUser("zirafaaa", "Sreten", "Petronijevic", "volimjanku1234");
		// user.insertUser("panda123", "Andjela", "Vulic", "glupasifra18");
		/*System.out.println(user.printAllUsernames(user.getAllUsers()));
		Integer n = user.getSentPackages(new String[] { "panda123", "andjvu", "joca" });
		if (n != null)
			System.out.println(n);
		int res = user.declareAdmin("panda123");
		System.out.println(res);

		res = user.declareAdmin("caocao");
		System.out.println(res);

		res = user.declareAdmin("joca");
		System.out.println(res); */
		//user.insertUser("janka2405", "Jana", "Stojanovic", "jankaaajankaa2a");
		//user.insertUser("zirafaaa", "Sreten", "Petronijevic", "volimjanku1234");
		
		/*user.insertUser("panda123", "Andjela", "Vulic", "glupasifra18");
		System.out.println(user.printAllUsernames(user.getAllUsers()));*/

	}

}
