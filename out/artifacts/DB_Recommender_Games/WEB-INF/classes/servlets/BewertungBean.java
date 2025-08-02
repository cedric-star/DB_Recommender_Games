package servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import SortedList.SortedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BewertungBean
 */
public class BewertungBean extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	StringBuffer sb = new StringBuffer();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BewertungBean() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    public String getResult(){
    	return sb.toString();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}


	public void showElements(JdbcQueryBean queryBean) {
		
		int countElements=0;
		int userid = queryBean.userid;
		String query = null;
		sb = new StringBuffer();
		
		ResultSet rs=null;
		ResultSet rs2=null;
		//query = "SELECT p.produktid, produktname, bewertung "
			//	+ "from Vorlage_Produkte p, Vorlage_Rating r " 
				//+ "where r.produktid = p.produktid and userid = "+userid+" and " 
				//+ "ora_hash (p.produktid, (select to_char(sysdate, 'ss') from dual)) < 10";
		
		//System.out.println("query: "+query);
		//rs=queryBean.executeQuery(query);
		
		rs = queryBean.executeGetRatingList(userid, 2);
		rs2 = queryBean.executeGetAnzahlBewertungen(userid);
		try {
        while(rs2.next()) {
        	sb.append("Anzahl Bewertungen bisher:"+rs2.getString(1)+"!");
        	}
		
		/*
		 * Eventuell pruefen ob schon eine Bewertung abgegeben wurde bzw. alte 
		 * bewertung loeschen wenn User seine Meinung aendern kann
		 * 
		 */
		
		sb.append("<center><p><b>Bewertung</b></p></center>");
		sb.append("<center>Eingeloggt mit UserID: "+userid+"</center>");
		sb.append("<center><FORM ACTION=\"ControllerServlet?doAction=bewertungSubmit\" METHOD=\"post\">");
		sb.append("<table>");
		
			while(rs.next()) {	
				countElements++;
				sb.append("<tr><td>");
				sb.append("<input type=\"hidden\" value=\""+rs.getString(1)+"\" name=\"produktid\">");
				sb.append(rs.getString(2));
				sb.append("<p>");
				sb.append(rs.getString(4));
				sb.append(": </td><td><select name=\"bewertung\">" +
						"<option>"+rs.getString(3)+"</option>" +
						"<option>1</option>" +							
						"<option>2</option>" +
						"<option>3</option>" +
						"<option>4</option>" +
						"<option>5</option>" +
						"<option>6</option>" +
						"<option>7</option>" +
						"<option>8</option>" +
						"<option>9</option>" +
						"</select>" +
						"</td></tr>");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("<input type=\"hidden\" value="+countElements+" name=\"countElements\" >");
		sb.append("</table>");
		sb.append("<input type=\"submit\" value=\"Bewerten\"></form></center>");
		sb.append("<a href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");
		/*if (countElements>=1) {sb.append("<input type=\"submit\" value=\"Bewerten\"></form></center>");}
		else {sb.append("<a href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");}
		*/}
	


	public void abgegeben(JdbcQueryBean queryBean, String[] bewertungen, String[] produktid) {
		
		int userid=queryBean.userid;
		String query2 = null;
		
//		for (int i=0; i<bewertungen.length; i++){
//			query2 = "update Vorlage_Rating set bewertung= "
//					+bewertungen[i]+" where userid = "+userid+" and produktid = "+produktid[i]+"";
//		
//			queryBean.executeQuery(query2);
//			//System.out.println("query2: "+query2);
//		}
		
		queryBean.executeUpdateRatings(bewertungen, produktid, userid);
		
		sb=new StringBuffer("Bewertung erfolgreich abgegeben");
		
		for (int i=0;i<bewertungen.length;i++){
			
			System.out.println(bewertungen[i]);
		}
		
				
	}
	
	public void showEmpfehlungen(JdbcQueryBean queryBean, int anzahl) {
		
		int userid = queryBean.userid;
		//String userid = ""+queryBean.userid;
		sb = new StringBuffer();
		double minAehnlichkeit = 0.2;
		double mindestRating = 0;
		int benoetigteAnzahl = 10;
		SortedList liste = new SortedList();
		String[] median = null;
		String userListe = null;
		ResultSet aehnliche = null;
		ResultSet beliebte = null;
		
		sb.append("Hier könnten Empfehlungen stehen");
		sb.append("<br><br>");
		sb.append("Du kannst aber noch weitere Bewertungen abgeben");
		sb.append("<br><br>");
		sb.append("<a href=\"ControllerServlet?doAction=bewertung\">Zur Bewertung</a>");
		sb.append("<br><br>");
		sb.append("<a href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");
		sb.append("<br><br>");
		aehnliche = getAehnlicheBenutzer(queryBean, Integer.toString(userid), minAehnlichkeit);
		
		try {
			while(aehnliche.next()) {
				liste.insert(aehnliche.getString(2), aehnliche.getString(1));
				System.out.println("userid"+queryBean.userid);
				liste.ausgeben();
			}
				if(liste.size() != 0) {
					System.out.println("Liste Gr��e: "+liste.size());
					median = liste.getMedian();
					System.out.println("Median: "+median[0]+" "+median[1]);
					while(liste.size() < benoetigteAnzahl && median != null) {
						aehnliche = getAehnlicheBenutzer(queryBean, median[1], minAehnlichkeit);
						while(aehnliche.next()) {
							liste.insert(aehnliche.getString(2), aehnliche.getString(1));
							System.out.println("Ähnliche" + aehnliche.getString(2));
							System.out.println("Ähnliche2" + aehnliche.getString(1));
						}
						liste.ausgeben();
						median = liste.getMedian();
						System.out.println("Median1: "+median[0]+" "+median[1]);
					}
					if(liste.size()>=benoetigteAnzahl) {
						for(int i = 0; i<liste.size(); i++) {
							if(i==0) {
								userListe = liste.get(i)[1];
							}else {
								userListe = userListe+","+liste.get(i)[1];
							}
							
						}
						System.out.println("user Anzahl: "+liste.size());
						System.out.println("UserListe: "+userListe);
						ResultSet empfehlungen = getProduktempfehlungen(queryBean, Integer.toString(userid), anzahl, userListe, mindestRating);
						
						sb.append("<center><table width=\"80%;\">");
						sb.append("<tr><td colspan=\"2\">");
						sb.append("<p style=\"text-align: center; font-size: 1.5em; color:red; font-weight: bold;\">");
						sb.append("Dies solltest Du UNBEDINGT anschauen!</p>");
						sb.append("</td></tr>");
						sb.append("<tr><td> <p style=\"font-size: 1.3em; color:green;\">");
						sb.append("<u>Empfehlungen</u></p></td><td><p style=\"font-size: 1.3em; color:red;\"><u>Bewertung</u></p>");
						sb.append("</td></tr>");
						
						while(empfehlungen.next()) {
							
							sb.append("<tr>");
							sb.append("<td><p style=\"font-size: 1.3em; color:black;\">"+empfehlungen.getString(2)+"</p></td>");
							sb.append("<td><p style=\"font-size: 1.3em; color:blue;\">"+empfehlungen.getString(4)+"</p></td>");
							sb.append("</tr>");
							
						}
						sb.append("</table></center>");
					}else {
						sb.append("Bitte besuche uns sp�ter wieder. Wir haben leider keine Empfehlungen für dich");
					}
				} else {
					sb.append("Du bist ein graues Schaf");
					sb.append(". Trotzdem kannst du dir natürlich gerne die folgenden Empfehlungen ansehen ...");
					beliebte = queryBean.executeGetBeliebteProdukte();
					while(beliebte.next())
					{
						
						sb.append("<tr>");
						sb.append("<td><p style=\"font-size: 1.3em; color:black;\">"+beliebte.getString(1)+"</p></td>");
						//sb.append("<td><p style=\"font-size: 1.3em; color:blue;\">"+beliebte.getString(2)+"</p></td>");
						sb.append("</tr>");
						
					}
					sb.append("<a href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");
				}
				liste.clearList();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int anzahlBewertungen(JdbcQueryBean queryBean) {
		
		int userid = queryBean.userid;
		int anzahlBewertungen = 0;
		String query = null;
		
		try {
//			query = "select count (*) from Vorlage_Rating "
//					+"where userid = "+userid+" and bewertung <> 0";
//			System.out.println("query in anzahlBewertungen: "+query);
//			
//			ResultSet anzahlrs = queryBean.executeQuery(query);
			System.out.println("bewertungssubmit userid "+userid);
			ResultSet anzahlrs = queryBean.executeGetAnzahlBewertungen(userid);
			
			while (anzahlrs.next()) {
				anzahlBewertungen = anzahlrs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return anzahlBewertungen;
	}
	
	public ResultSet getAehnlicheBenutzer(JdbcQueryBean queryBean, String userid, double mindestAehnlichkeit) {
//		String query = "select * from (select zaehler.userid, round(zaehler.zaehler / nenner.nenner,2) aehnlichkeit " + 
//				"from (select userid, sum (xxx) zaehler " + 
//				"from (select otheruser.userid, otheruser.produktid, activuser.bewertung * otheruser.bewertung xxx " + 
//				"from (select * from p_rating where userid = " +userid+ 
//				") activuser,(select * from p_rating where userid != " + userid + 
//				") otheruser where " + 
//				"activuser.produktid = otheruser.produktid) " + 
//				"group by userid) zaehler, " + 
//				"(select b.userid, a.quadrat * b.quadrat nenner " + 
//				"from (select userid, sqrt( sum( POWER (bewertung, 2) ) ) quadrat " + 
//				"from (select * from p_rating where userid = " +userid+ ") " + 
//				"group by userid) a, " + 
//				"(select userid, sqrt( sum( POWER (bewertung, 2) ) ) quadrat " + 
//				"from (select * from p_rating where userid != " +userid+ 
//				") group by userid ) b) nenner " +  
//				"where nenner.userid = zaehler.userid) where aehnlichkeit > "+mindestAehnlichkeit;
//		
//		System.out.println("Aehnliche Benutzerquery: "+query);
//		ResultSet rsAehnliche = queryBean.executeQuery(query);
		
		ResultSet rsAehnliche = queryBean.executeGetAehnlicheBenutzer(userid, mindestAehnlichkeit);
		return rsAehnliche;
	}
	
	public ResultSet getProduktempfehlungen(JdbcQueryBean queryBean, String userid, int anzahlEmpfehlungen, String userliste, double mindestRating) {
		String query = null;
		query = 
				"select * from (select produktid, produktname, dense_rank () over (order by rating desc) Rang, round (Rating, 2) Rating "
				+"from (select a.produktid, b.produktname, rating "
				+"from (select * from (select avg(Bewertung) Rating, produktid "
				+"from (select * from zzz_rating where userid in ("+userliste+ ")"
				+"and not produktid in (select produktid from zzz_rating where userid = "+userid+" and Bewertung <> 0)) "
				+"group by produktid )where Rating > "+mindestRating+" ) a, ZZZ_Produkte b where a.produktid = b.id )) "
				+"where Rang <= "+anzahlEmpfehlungen+" order by Rang asc"; 
		
		System.out.println("Empfehlungen Benutzerquery: "+query);
		ResultSet rsEmpfehlungen = queryBean.executeQuery(query);
		
//		ResultSet rsEmpfehlungen = queryBean.executegetProduktempfehlungen2(userid,anzahlEmpfehlungen,userliste,mindestRating);
		return rsEmpfehlungen;	
	}
	
}
