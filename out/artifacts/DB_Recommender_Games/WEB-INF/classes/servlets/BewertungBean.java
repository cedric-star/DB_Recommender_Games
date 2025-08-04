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
		
		rs = queryBean.executeGetRatingList(userid, 50);
		rs2 = queryBean.executeGetRatingCount(userid);

		try {
        while(rs2.next()) {
        	sb.append("Anzahl Bewertungen bisher: "+rs2.getString(1)+"");
        	}
		
		/*
		 * Eventuell pruefen ob schon eine Bewertung abgegeben wurde bzw. alte 
		 * bewertung loeschen wenn User seine Meinung aendern kann
		 * 
		 */
		
		sb.append("<center><p><b>Bewertung</b></p></center>");
		sb.append("<center>Eingeloggt mit UserID: " + userid + "</center>");
		sb.append("<center><FORM ACTION=\"ControllerServlet?doAction=bewertungSubmit\" METHOD=\"post\">");
		sb.append("<table>");
		
			while(rs.next()) {	
				countElements++;
				sb.append("<tr><td>");
				sb.append("<input type=\"hidden\" value=\""+rs.getString(1)+"\" name=\"produktid\">");
				sb.append(rs.getString(2));
				sb.append("<p>");
				sb.append(rs.getString(4));
				sb.append("</td><td><select name=\"bewertung\">" +
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
		
		int userid = queryBean.userid;
		queryBean.executeUpdateRatings(bewertungen, produktid, userid);
		
		sb=new StringBuffer("Bewertung erfolgreich abgegeben.");
		
		for (int i=0;i<bewertungen.length;i++){
			
			System.out.println(bewertungen[i]);
		}
		
				
	}
	
	public void showEmpfehlungen(JdbcQueryBean queryBean, int anzahl) {
		
		int userid = queryBean.userid;
		sb = new StringBuffer();
		double minAehnlichkeit = 0.2;
		int benoetigteAnzahl = 5;
		SortedList similarUserList = new SortedList();
		String[] median = null;
		ResultSet aehnliche = null;

		sb.append("<h1>Empfehlungen</h1>");
		sb.append("Hier finden Sie ihre Empfehlungen!");
		sb.append("<br>");
		sb.append("Sie können jedoch weitere Empfehlungen <a href=\"ControllerServlet?doAction=bewertung\">hier</a> abgeben!");
		sb.append("<br><br>");
		sb.append("<a href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");
		sb.append("<br><br>");

		aehnliche = queryBean.executeGetSimilarUsers(String.valueOf(userid), minAehnlichkeit);

		fillSimilarUserList(aehnliche, similarUserList);

        if (!similarUserList.isEmpty()) {
            System.out.println("Liste Groesse: "+similarUserList.size());
            if (similarUserList.size() >= benoetigteAnzahl) {
                sb.append(showUserRecs(queryBean, similarUserList, userid, benoetigteAnzahl));
            } else {
                sb.append("Wir haben leider keine Empfehlungen für Sie. Schauen Sie später nochmal vorbei!");
            }
        } else {
            sb.append(showGreySheepRecs(queryBean));
        }

        similarUserList.clearList();
    }

	public String showUserRecs(JdbcQueryBean queryBean, SortedList similarUserList, int userID, int recCount) {

		int mindestRating = 5;
		String sqlUserList = "";
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < similarUserList.size(); i++) {
			if (similarUserList.get(i) == null) {
				System.out.println("Null Element!");
			}
			if(i == 0) {
				sqlUserList = similarUserList.get(i)[1];
			}else {
				sqlUserList = sqlUserList + "," + similarUserList.get(i)[1];
			}
		}

		System.out.println("Ähnliche User Anzahl: " + similarUserList.size());
		System.out.println("Ähnliche User: " + sqlUserList);

		sb.append("<center><table width=\"80%;\">");
		sb.append("<tr><td colspan=\"2\">");
		sb.append("<p style=\"text-align: center; font-size: 1.5em; color:red; font-weight: bold;\">");
		sb.append("Dies sollten Sie sich unbedingt anschauen!</p>");
		sb.append("</td></tr>");
		sb.append("<tr><td>");
		sb.append("<p><u class=\"rating-table\">Empfehlungen</u></p></td>");
		sb.append("<td><p><u class=\"rating-table\">Bewertung</u></p>");
		sb.append("</td></tr>");

		try {
			ResultSet recommendations = queryBean.executeGetProductRecommendations(userID, recCount, sqlUserList, mindestRating);
			while(recommendations.next()) {

				sb.append("<tr>");
				sb.append("<td><p style=\"font-size: 1.3em; color:black;\">"+recommendations.getString(2)+"</p></td>");
				sb.append("<td><p style=\"font-size: 1.3em; color:blue;\">"+recommendations.getString(4)+"</p></td>");
				sb.append("</tr>");

			}
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }

        sb.append("</table></center>");
		return sb.toString();
	}

	public String showGreySheepRecs(JdbcQueryBean queryBean) {
		StringBuilder sb = new StringBuilder();

		sb.append("Du bist ein graues Schaf. Niemand ist dir ähnlich.");
		sb.append("Trotzdem kannst du dir natürlich gerne die beliebten Produkte ansehen...");
		try {
			ResultSet popularProducts = queryBean.executeGetBeliebteProdukte();
			while(popularProducts.next())
			{
				sb.append("<tr>");
				sb.append("<td><p style=\"font-size: 1.3em; color:black;\">"+popularProducts.getString(1)+"</p></td>");
				sb.append("</tr>");
			}
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sb.append("<a href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");
		return sb.toString();
	}

	public void fillSimilarUserList(ResultSet rs, SortedList list) {
		try {
			while(rs.next()) {
				list.insert(rs.getString(2), rs.getString(1));
			}
			list.ausgeben();
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
	
	public int anzahlBewertungen(JdbcQueryBean queryBean) {
		
		int userid = queryBean.userid;
		int anzahlBewertungen = 0;
		String query = null;
		
		try {
			System.out.println("Bewertungen abgegeben für UserID " + userid);
			ResultSet anzahlrs = queryBean.executeGetRatingCount(userid);
			
			while (anzahlrs.next()) {
				anzahlBewertungen = anzahlrs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return anzahlBewertungen;
	}

	
}
