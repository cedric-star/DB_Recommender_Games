package servlets;

import java.sql.*;
import java.util.*;

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
	
	private String nutzerEinfuegen = "insert into ZZZ_User (Userid, Username, Password) values(AAC_VORLAGEN_SEQUENCE.nextval,? ,?)";
	private String initRatings = "insert into ZZZ_Rating (select (select max (userid) from ZZZ_User), id, 0, 13 from ZZZ_Produkte)";
	private String getUserPasswort = "select password from ZZZ_User where username = ? ";
	private String getUserid = "select userid from ZZZ_user where username=?";
	private String getBewertungsliste = "SELECT p.id, produktname, bewertung, description " + 
							" from ZZZ_Produkte p, ZZZ_Rating r " + 
							" where r.produktid = p.id and userid = ? and " + 
							" ora_hash (p.id, (select to_char(sysdate, 'ss') from dual)) < ?";
	private String updateBewertungen = "update ZZZ_Rating set bewertung= ?" + 
										" where userid = ? and produktid = ?";
	private String getAnzahlBewertungen = "select count (*) from ZZZ_Rating where userid = ? and bewertung <> 0";
	private String getAehnlicheBenutzer = "select * from (select zaehler.userid, round(zaehler.zaehler / nenner.nenner,2) aehnlichkeit " + 
			" from (select userid, sum (xxx) zaehler " + 
			" from (select otheruser.userid, otheruser.produktid, activuser.bewertung_pears * otheruser.bewertung_pears xxx " + 
			" from (select * from ZZZ_NORMPEARS where userid = ? " + 
			" ) activuser,(select * from ZZZ_NORMPEARS where userid != ? " + 
			" ) otheruser where " + 
			" activuser.produktid = otheruser.produktid) " + 
			" group by userid) zaehler, " + 
			" (select b.userid, a.quadrat * b.quadrat nenner " + 
			" from (select userid, sqrt( sum( POWER (bewertung_pears, 2) ) ) quadrat " + 
			" from (select * from ZZZ_NORMPEARS where userid = ? ) " + 
			" group by userid) a, " + 
			" (select userid, sqrt( sum( POWER (bewertung_pears, 2) ) ) quadrat " + 
			" from (select * from ZZZ_NORMPEARS where userid != ? " + 
			" ) group by userid ) b) nenner " + 
			" where nenner.userid = zaehler.userid and nenner <> 0) where aehnlichkeit > ? ";
	/*private String getAehnlicheBenutzer = "select * from (select zaehler.userid, round(zaehler.zaehler / nenner.nenner,2) aehnlichkeit " + 
			" from (select userid, sum (xxx) zaehler " + 
			" from (select otheruser.userid, otheruser.produktid, activuser.bewertung_pears * otheruser.bewertung_pears xxx " + 
			" from (select * from VFILMDIG_BEWERTUNGNORM where userid = ? " + 
			" ) activuser,(select * from VFILMDIG_BEWERTUNGNORM where userid != ? " + 
			" ) otheruser where " + 
			" activuser.produktid = otheruser.produktid) " + 
			" group by userid) zaehler, " + 
			" (select b.userid, a.quadrat * b.quadrat nenner " + 
			" from (select userid, sqrt( sum( POWER (bewertung_pears, 2) ) ) quadrat " + 
			" from (select * from VFILMDIG_BEWERTUNGNORM where userid = ? ) " + 
			" group by userid) a, " + 
			" (select userid, sqrt( sum( POWER (bewertung_pears, 2) ) ) quadrat " + 
			" from (select * from VFILMDIG_BEWERTUNGNORM where userid != ? " + 
			" ) group by userid ) b) nenner " + 
			" where nenner.userid = zaehler.userid and nenner <> 0) where aehnlichkeit > ? ";*/
	/*private String getAehnlicheBenutzer = "select * from (select zaehler.userid, round(zaehler.zaehler / nenner.nenner,2) aehnlichkeit " + 
			" from (select userid, sum (xxx) zaehler " + 
			" from (select otheruser.userid, otheruser.produktid, activuser.bewertung * otheruser.bewertung xxx " + 
			" from (select * from p_rating where userid = ? " + 
			" ) activuser,(select * from p_rating where userid != ? " + 
			" ) otheruser where " + 
			" activuser.produktid = otheruser.produktid) " + 
			" group by userid) zaehler, " + 
			" (select b.userid, a.quadrat * b.quadrat nenner " + 
			" from (select userid, sqrt( sum( POWER (bewertung, 2) ) ) quadrat " + 
			" from (select * from p_rating where userid = ? ) " + 
			" group by userid) a, " + 
			" (select userid, sqrt( sum( POWER (bewertung, 2) ) ) quadrat " + 
			" from (select * from p_rating where userid != ? " + 
			" ) group by userid ) b) nenner " + 
			" where nenner.userid = zaehler.userid) where aehnlichkeit > ? ";
			*/
	private String getProduktempfehlungen = "select * from (select produktid, produktname, dense_rank () over (order by rating desc) Rang, round (Rating, 2) Rating " + 
											"from (select a.produktid, b.produktname, rating " + 
											"from (select * from (select avg(Bewertung) Rating, produktid " + 
											"from (select * from ZZZ_RATING where userid in (?) " + 
											"and not produktid in (select produktid from ZZZ_RATING where userid = ? and Bewertung <> 0)) " + 
											"group by produktid ) where Rating > ? ) a, ZZZ_Produkte b where a.produktid = b.produktid )) " + 
											"where Rang <= ? order by Rang asc";	
	/*
	  private String getProduktempfehlungen_neu = "select * from (select produktid, produktname, dense_rank () over (order by rating desc) Rang, round (Rating, 2) Rating " + 
	 
			"from (select a.produktid, b.produktname, rating " + 
			"from (select * from (select avg(Bewertung) Rating, produktid " + 
			"from (select * from ZZZ_RATING where userid in (select * from (select zaehler.userid, round(zaehler.zaehler / nenner.nenner,2) aehnlichkeit" + 
			"from (select userid, sum (xxx) zaehler" + 
			"from (select otheruser.userid, otheruser.produktid, activuser.bewertung * otheruser.bewertung xxx \" + \r\n" + 
			"from (select * from p_rating where userid = ? " + 
			") activuser,(select * from p_rating where userid != ? " + 
			") otheruser where " + 
			"activuser.produktid = otheruser.produktid) " + 
			"group by userid) zaehler, " + 
			"(select b.userid, a.quadrat * b.quadrat nenner " + 
			"from (select userid, sqrt( sum( POWER (bewertung, 2) ) ) quadrat " + 
			"from (select * from p_rating where userid = ? ) " + 
			"group by userid) a, " + 
			"(select userid, sqrt( sum( POWER (bewertung, 2) ) ) quadrat " + 
			"from (select * from p_rating where userid != ? " + 
			") group by userid ) b) nenner " + 
			"where nenner.userid = zaehler.userid) where aehnlichkeit > ? ) " + 
			"and not produktid in (select produktid from ZZZ_RATING where userid = ? and Bewertung <> 0)) " + 
			"group by produktid ) where Rating > ? ) a, ZZZ_Produkte b where a.produktid = b.produktid )) " + 
			"where Rang <= ? order by Rang asc";
			*/	
	private String getBeliebteProdukte = "select produktname, description from ZZZ_produkte " +
		       "where id in (select id from (" +
		       "select id, rank() over (order by xxx desc) yyy from " +
		       "(select distinct id, avg(bewertung_pears) over (partition by produktid) xxx " +
		       "from ZZZ_NORMPEARS where bewertung_pears > 0)) where yyy <= 5)";

	private PreparedStatement ps_nutzerEinfuegen = null;
	private PreparedStatement ps_initRatings = null;
	private PreparedStatement ps_getUserPasswort = null;
	private PreparedStatement ps_getUserid = null;
	private PreparedStatement ps_getBewertungsliste = null;
	private PreparedStatement ps_updateBewertungen = null;
	private PreparedStatement ps_getAnzahlBewertungen = null;
	private PreparedStatement ps_getAehnlicheBenutzer = null;
	private PreparedStatement ps_getProduktempfehlungen = null;
	private PreparedStatement ps_getProduktempfehlungen_neu = null;
	private PreparedStatement ps_getBeliebteProdukte = null;
	
	/*
	 * Benutzte Daten auf Datenbank sind:
	 * 
	 * table ZZZ_user (userid, name, password)
	 * table ZZZ_produkt (produktname) (hier sollte es auch eine ID geben)
	 * table bewertungen (userid, produkt, bewertung)
	 * 
	 * user_seq (beginnt mit 1, wird um 1 incrementiert) 
	 * 
	 *  
	 *  Diese gilt es an das eigene System anzupassen
	 * 
	 */
	

	boolean logged = false;

	public JdbcQueryBean() {

		this.dbConnection();
	}

	public void dbConnection() {

		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(dbUrl, login, password);
			statement = connection.createStatement();
			statement2 = connection.createStatement();
			initPreparedStatement();
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
			connection = null;

		} catch (SQLException ex) {
			System.err.print("SQLException: ");
			System.err.println(ex.getMessage());
			connection = null;
		} catch (Exception ne) {
			System.err.print("Other Error while connecting to the database : ");
			System.err.println(ne.getMessage());
			connection = null;
		}
	}
	
	private void initPreparedStatement() {
		try {
			ps_nutzerEinfuegen = connection.prepareStatement(nutzerEinfuegen);
			ps_initRatings = connection.prepareStatement(initRatings);
			ps_getUserPasswort = connection.prepareStatement(getUserPasswort);
			ps_getUserid = connection.prepareStatement(getUserid);
			ps_getBewertungsliste = connection.prepareStatement(getBewertungsliste);
			ps_updateBewertungen = connection.prepareStatement(updateBewertungen);
			ps_getAnzahlBewertungen = connection.prepareStatement(getAnzahlBewertungen);
			ps_getAehnlicheBenutzer = connection.prepareStatement(getAehnlicheBenutzer);
			ps_getProduktempfehlungen = connection.prepareStatement(getProduktempfehlungen);
			//ps_getProduktempfehlungen_neu = connection.prepareStatement(getProduktempfehlungen_neu);
			ps_getBeliebteProdukte = connection.prepareStatement(getBeliebteProdukte);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getResult() {

		return result;

	}

	protected void finalize() {
		try {
			connection.close();
			statement.close();
		} catch (SQLException e) {
		}
	}
	
	public ResultSet executeQuery(String query){
		ResultSet rs=null;
		try {
		rs = statement.executeQuery(query);
		} catch(Exception e) {			
			System.out.println(e);
			return null;
		}
		return rs;
	}

	public void register(String[] data) {

		/*
		 * Fuer bessere Sicherheit Passwort Hashen bevor in Datenbank
		 * geschrieben wird
		 */

		result = null;

		/*
		 * Durch SQL eine Sequenz user_seq erstellen
		 * oder UserId durch eine Abfrage ermitteln		
		 */
		
		//String query = "insert into ZZZ_User values(AAC_VORLAGEN_SEQUENCE,'" + data[0] + "','"
			//	+ data[1] + "')";
		

		
		//System.out.println("query in QB: "+query);
		//String query2 = "insert into ZZZ_rating(" + 
			//	"select (select max (userid) from ZZZ_User), produktid, 0, 13 from ZZZ_produkte)";
		//System.out.println("query2 in QB: "+query2);

		try {
			ps_nutzerEinfuegen.setString(1, data[0]);
			ps_nutzerEinfuegen.setString(2, data[1]);
			//statement.executeQuery(query);
			ps_nutzerEinfuegen.executeQuery();
			
			//statement2.executeQuery(query2);
			ps_initRatings.executeQuery();
			StringBuffer sb = new StringBuffer();
			
			

		} catch (SQLException e) {
			result = "<P> SQL error: <PRE> " + e + " </PRE> </P>\n";
			System.out.println(e);
		} catch (Exception ignored) {
			result = "<P> Error: <PRE> " + ignored + " </PRE> </P>\n";
			System.out.println(ignored);
		}

	}

	public String getlogin() {

		if (logged) {
			return "<center><p>Login erfolgreich</p><br><a href=\"index.jsp\">weiter</a></center>";
		} else {
			return "<center><p>Login Fehlgeschlagen</p><br><a href=\"index.jsp\">weiter</a></center>";
		}

	}

	public boolean login(String[] loginData, LoginBean loginBean) {

		//String query = "select password from ZZZ_User where username = '"
			//	+ loginData[0] + "'";

		try {
			
			ps_getUserPasswort.setString(1, loginData[0]);
			ResultSet rs = ps_getUserPasswort.executeQuery();
			
			//ResultSet rs = statement.executeQuery(query);
			StringBuffer sb = new StringBuffer();
			String dbPass = new String();

			while (rs.next()) {
				dbPass = rs.getString(1);
			}

			if (dbPass.equals(loginData[1])) {
				logged = true;
				loginBean.logged=true;
				
				//rs = statement.executeQuery("select userid from ZZZ_user where username='"+loginData[0]+"'");
				ps_getUserid.setString(1, loginData[0]);
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
			System.out.println("Beliebte: "+getBeliebteProdukte);
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
			System.out.println("Aehnliche Benutzer: "+getAehnlicheBenutzer);
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
