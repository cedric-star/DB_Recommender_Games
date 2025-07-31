package servlets;

import utility.QueryReader;
import utility.PasswordProcessor;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

public class JdbcQueryBean { // Probleme: initRatingsd funktioniert nicht; Bewertungen werden nicht aktualisiert- ANmeldung, registrierung funktioniert soweit

	private Connection connection;
	private Statement statement;
	private Statement statement2;

	private String result = null;
	
	public int userid=0;

	/*
	 * Hier Bitte die eigenen Benutzerdaten eintragen und daf�r sorgen, dass die
	 * Tabellen in die Eingetragen wird existieren
	 */
	private static final String driver = "oracle.jdbc.driver.OracleDriver";
	private static final String dbUrl = "jdbc:oracle:thin:@regenstein.hs-harz.de:1521:DBHSH01";
	private static final String login = "blank_future";
	private static final String password = "future123";

	private PreparedStatement ps_nutzerEinfuegen = null;
	private PreparedStatement ps_initRatings = null;
	private PreparedStatement ps_getUserPasswort = null;
	private PreparedStatement ps_getUserSalt = null;
	private PreparedStatement ps_getUserid = null;
	private PreparedStatement ps_getBewertungsliste = null;
	private PreparedStatement ps_updateBewertungen = null;
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
			ps_getBewertungsliste = connection.prepareStatement(QueryReader.getQuery("getRatingList"));
			ps_updateBewertungen = connection.prepareStatement(QueryReader.getQuery("updateRatings"));
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
	
	public ResultSet executeQuery(String query){
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(query);
		} catch(Exception e) {			
			System.out.println(e);
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
			System.out.println(e);
		} catch (Exception ignored) {
			result = "<P> Error: <PRE> " + ignored + " </PRE> </P>\n";
			System.out.println(ignored);
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

	public ResultSet executegetBewertungsliste(int userid, int zufall) {
		ResultSet rs = null;
		try {
			ps_getBewertungsliste.setInt(1, userid);
			ps_getBewertungsliste.setInt(2, zufall);
			rs = ps_getBewertungsliste.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public void executeupdateBewertungen(String[] bewertung, String[] produktid, int userid) {
		System.out.println(bewertung);
		try {
			for(int i = 0; i<bewertung.length;i++) {
				ps_updateBewertungen.setString(1, bewertung[i]);
				ps_updateBewertungen.setInt(2, userid);
				ps_updateBewertungen.setString(3, produktid[i]);
				ps_updateBewertungen.executeQuery();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet executegetAnzahlBewertungen(int userid) {
		ResultSet rs = null;
		try {
			ps_getAnzahlBewertungen.setInt(1, userid);
			rs = ps_getAnzahlBewertungen.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public ResultSet executegetBeliebteProdukte() {
		ResultSet rs = null;
		try {
			rs = ps_getBeliebteProdukte.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	
	public ResultSet executegetAehnlicheBenutzer(String userid, double mindestAehnlichkeit) {
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
	
	/*public ResultSet executegetProduktempfehlungen_neu(String userid, int anzahlEmpfehlungen, double mindestRating, double mindestAehnlichkeit) {
		ResultSet rs = null;
		System.out.println("userid: "+userid);
		System.out.println("mindestRating: "+mindestRating);
		System.out.println("anzahlEmpfehlungen: "+anzahlEmpfehlungen);
		try {
			ps_getProduktempfehlungen_neu.setString(1, userid);
			ps_getProduktempfehlungen_neu.setString(2, userid);
			ps_getProduktempfehlungen_neu.setString(3, userid);
			ps_getProduktempfehlungen_neu.setString(4, userid);
			ps_getProduktempfehlungen_neu.setDouble(5, mindestAehnlichkeit);
			ps_getProduktempfehlungen_neu.setString(6, userid);
			ps_getProduktempfehlungen_neu.setDouble(7, mindestRating);
			ps_getProduktempfehlungen_neu.setInt(8, anzahlEmpfehlungen);
			System.out.println("prod: "+ps_getProduktempfehlungen_neu.toString());
			rs = ps_getProduktempfehlungen.executeQuery();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return rs;
	}
	*/

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
