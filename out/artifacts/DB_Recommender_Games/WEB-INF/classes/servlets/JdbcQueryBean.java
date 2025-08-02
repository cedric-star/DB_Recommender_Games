package servlets;

import jakarta.servlet.http.HttpServlet;
import utility.QueryReader;
import utility.PasswordProcessor;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;


public class JdbcQueryBean extends HttpServlet {

	private Connection connection;
	private Statement statement;

	private String result = null;
	
	public int userid=0;

	private static final String driver = "oracle.jdbc.driver.OracleDriver";
	private static final String dbUrl = "jdbc:oracle:thin:@regenstein.hs-harz.de:1521:DBHSH01";
	private static final String login = "blank_future";
	private static final String password = "future123";

	private PreparedStatement ps_nutzerEinfuegen = null;
	private PreparedStatement ps_initRatings = null;
	private PreparedStatement ps_getUserPasswort = null;
	private PreparedStatement ps_getUserSalt = null;
	private PreparedStatement ps_getUserid = null;
	private PreparedStatement ps_getRatingList = null;
	private PreparedStatement ps_updateRatings = null;
	private PreparedStatement ps_getAnzahlBewertungen = null;
	private PreparedStatement ps_getAehnlicheBenutzer = null;
	private PreparedStatement ps_getProduktempfehlungen = null;
	private PreparedStatement ps_getBeliebteProdukte = null;

	boolean logged = false;

	public JdbcQueryBean() {
		this.establishDBConnection();
	}

	public void establishDBConnection() {

		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(dbUrl, login, password);
			statement = connection.createStatement();
			initPreparedStatement();
		} catch (java.lang.ClassNotFoundException cnfe) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(cnfe.getMessage());
			connection = null;
		} catch (SQLException se) {
			System.err.print("SQLException: ");
			System.err.println(se.getMessage());
			connection = null;
		} catch (Exception ex) {
			System.err.print("Other Error while connecting to the database : ");
			System.err.println(ex.getMessage());
			connection = null;
		}
	}
	
	private void initPreparedStatement() {
		try {
			ps_nutzerEinfuegen = connection.prepareStatement(QueryReader.getQuery("insertUser"));
			ps_initRatings = connection.prepareStatement(QueryReader.getQuery("initRatings"));
			ps_getUserPasswort = connection.prepareStatement(QueryReader.getQuery("getUserPassword"));
			ps_getUserSalt = connection.prepareStatement(QueryReader.getQuery("getUserSalt"));
			ps_getUserid = connection.prepareStatement(QueryReader.getQuery("getUserID"));
			ps_getRatingList = connection.prepareStatement(QueryReader.getQuery("getRatingList"));
			ps_updateRatings = connection.prepareStatement(QueryReader.getQuery("updateRatings"));
			ps_getAnzahlBewertungen = connection.prepareStatement(QueryReader.getQuery("getRatingCount"));
			ps_getAehnlicheBenutzer = connection.prepareStatement(QueryReader.getQuery("getSimilarUsers"));
			ps_getProduktempfehlungen = connection.prepareStatement(QueryReader.getQuery("getProductRecommendations"));
			ps_getBeliebteProdukte = connection.prepareStatement(QueryReader.getQuery("getPopularProducts"));
		} catch (SQLException e) {
			System.err.println("Error occured during fetching of SQL Statements: ");
			System.err.println(e.getMessage());
		}
	}

	public String getResult() {
		return result;
	}

	/*
	protected void finalize() {
		try {
			connection.close();
			statement.close();
		} catch (SQLException e) {
		}
	}
 */

	/**
	 * Executes a query on the blank_future database. The results are given as a resultset.
	 * The resultset can be read in a while-loop (resultset.next()). The pointer starts before the first
	 * row of data and then goes thru the rest. The resultset can contain zero or more results.
	 * @param query Query die auszuführen ist
	 * @return Resultset as described above
	 */
	public ResultSet executeQuery(String query){
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(query);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		return rs;
	}

	public void register(String user, String pass) throws InvalidKeySpecException, NoSuchAlgorithmException {

		result = null;
		byte[] salt = PasswordProcessor.getRandomSalt();
		String hashedPW = PasswordProcessor.getHashedPW(pass, salt);

		try {
			ps_nutzerEinfuegen.setString(1, user);
			ps_nutzerEinfuegen.setString(2, hashedPW);
			ps_nutzerEinfuegen.setString(3, PasswordProcessor.getHashHex(salt));
			ps_nutzerEinfuegen.executeQuery();

			ps_initRatings.executeQuery();

		} catch (SQLException e) {
			result = "<P> SQL error: <PRE> " + e + " </PRE> </P>\n";
			System.err.println(e.getMessage());
		} catch (Exception ignored) {
			result = "<P> Error: <PRE> " + ignored + " </PRE> </P>\n";
			System.err.println(ignored.getMessage());
		}
	}

	public String getLogin() {

		if (logged) {
			return "<center><p>Login erfolgreich</p><br><a href=\"index.jsp\">weiter</a></center>";
		} else {
			return "<center><p>Login Fehlgeschlagen</p><br><a href=\"index.jsp\">weiter</a></center>";
		}

	}

	public boolean login(String user, String pass, LoginBean loginBean) {

		boolean logged = baseLogin(user, pass);
		loginBean.logged = logged;
		return logged;
	}

	public boolean baseLogin(String user, String pass) {

		try {
			ps_getUserPasswort.setString(1, user);
			ResultSet rs = ps_getUserPasswort.executeQuery();
			String dbPass = "";
			while (rs.next()) {
				dbPass = rs.getString(1);
			}

			ps_getUserSalt.setString(1, user);
			ResultSet rs2 = ps_getUserSalt.executeQuery();
			String dbSalt = "";
			while (rs2.next()) {
				dbSalt = rs2.getString(1);
			}

			String proofPW = PasswordProcessor.getHashedPW(pass, PasswordProcessor.hexStringToByteArray(dbSalt));

			if (dbPass.equals(proofPW)) {
				logged = true;
				ps_getUserid.setString(1, user);
				rs = ps_getUserid.executeQuery();
				while (rs.next()) {
					userid = Integer.parseInt(rs.getString(1));
				}
			} else {
				logged=false;
			}
		} catch (SQLException e) {
			result = "<P> SQL error: <PRE> " + e + " </PRE> </P>\n";
			System.out.println(e);
		} catch (Exception ignored) {
			result = "<P> Error: <PRE> " + ignored + " </PRE> </P>\n";
			System.out.println(ignored);
		}
		return logged;
	}

	public int executeGetUserID(String username) throws SQLException {
		ResultSet rs = null;
		try {
			ps_getUserid.setString(1, username);
			rs = ps_getUserid.executeQuery();
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }

		int id = 0;
		while (rs.next()) {
			id = Integer.parseInt(rs.getString(1));
		}
		return id;
    }

	public ResultSet executeGetRatingList(int userid, int zufall) {
		ResultSet results = null;
		try {
			ps_getRatingList.setInt(1, userid);
			ps_getRatingList.setInt(2, zufall);
			results = ps_getRatingList.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public void executeUpdateRatings(String[] ratings, String[] productids, int userid) {
		try {
			for(int i = 0; i<ratings.length;i++) {
				ps_updateRatings.setString(1, ratings[i]);
				ps_updateRatings.setInt(2, userid);
				ps_updateRatings.setString(3, productids[i]);
				ps_updateRatings.executeQuery();
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public ResultSet executeGetAnzahlBewertungen(int userid) {
		ResultSet rs = null;
		try {
			ps_getAnzahlBewertungen.setInt(1, userid);
			rs = ps_getAnzahlBewertungen.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public ResultSet executeGetBeliebteProdukte() {
		ResultSet rs = null;
		try {
			rs = ps_getBeliebteProdukte.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	
	public ResultSet executeGetAehnlicheBenutzer(String userid, double mindestAehnlichkeit) {
		ResultSet rs = null;
		try {
			ps_getAehnlicheBenutzer.setString(1, userid);
			ps_getAehnlicheBenutzer.setString(2, userid);
			ps_getAehnlicheBenutzer.setString(3, userid);
			ps_getAehnlicheBenutzer.setString(4, userid);
			ps_getAehnlicheBenutzer.setDouble(5, mindestAehnlichkeit);
			rs = ps_getAehnlicheBenutzer.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public ResultSet executegetProduktempfehlungen(String userid, int anzahlEmpfehlungen, String userliste, double mindestRating) {
		ResultSet rs = null;
		System.out.println("userliste: "+userliste);
		System.out.println("userid: "+userid);
		System.out.println("mindestRating: "+mindestRating);
		System.out.println("anzahlEmpfehlungen: "+anzahlEmpfehlungen);
		try {
			ps_getProduktempfehlungen.setString(1, userliste);
			ps_getProduktempfehlungen.setString(2, userid);
			ps_getProduktempfehlungen.setDouble(3, mindestRating);
			ps_getProduktempfehlungen.setInt(4, anzahlEmpfehlungen);
			System.out.println("prod: "+ps_getProduktempfehlungen.toString());
			rs = ps_getProduktempfehlungen.executeQuery();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return rs;
	}

	public ResultSet executegetProduktempfehlungen2(String userid, int anzahlEmpfehlungen, String userliste, double mindestRating) {
		ResultSet rs = null;
		System.out.println("userliste: "+userliste);
		System.out.println("userid: "+userid);
		System.out.println("mindestRating: "+mindestRating);
		System.out.println("anzahlEmpfehlungen: "+anzahlEmpfehlungen);
		String sql2 = "select * from (select produktid, produktname, dense_rank () over (order by rating desc) Rang, round (Rating, 2) Rating " + 
				"from (select a.produktid, b.produktname, rating " + 
				"from (select * from (select avg(Bewertung) Rating, produktid " + 
				"from (select * from ZZZ_RATING where userid in ("+userliste+") " + 
				"and not produktid in (select produktid from ZZZ_RATING where userid = "+userid+" and Bewertung <> 0)) " + 
				"group by produktid ) where Rating > "+mindestRating+" ) a, ZZZ_Produkte b where a.produktid = b.produktid )) " + 
				"where Rang <= "+anzahlEmpfehlungen+" order by Rang asc";
		try {
			System.out.println("prod: "+ps_getProduktempfehlungen.toString());
			rs = statement.executeQuery(sql2);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return rs;
	}

}
