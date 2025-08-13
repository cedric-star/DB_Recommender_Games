package servlets;

import java.io.IOException;
import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import SortedList.SortedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BewertungBean
 */
public class BewertungBean extends HttpServlet {
	@Serial
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
		sb = new StringBuffer();
		
		ResultSet rs = null;
		ResultSet rs2 = null;

		rs = queryBean.executeGetRatingList(userid, 5);
		rs2 = queryBean.executeGetRatingCount(userid);

		try {

			sb.append("<div class=\"input-area\">");
			sb.append("<center><p class=\"login-title\">Bewertung</p></center>");
			sb.append("<p class=\"text-dark-large\">Geben Sie Bewertungen zu Produkten ab,\num (verbesserte) Empfehlungen zu erhalten!</p>");

        	while(rs2.next()) {
        		sb.append("<p class=\"text-dark-large\">Anzahl Bewertungen bisher: ").append(rs2.getString(1)).append("</p>");
        	}
			sb.append("<p class=\"text-dark-large\">Eingeloggt mit UserID: ").append(userid).append("</p>");
			sb.append("<center><FORM ACTION=\"ControllerServlet?doAction=bewertungSubmit\" METHOD=\"post\">");
			sb.append("</div>");

			sb.append("<table>");
			while(rs.next()) {	
				countElements++;
				sb.append("<tr><td>");
				sb.append("<input type=\"hidden\" value=\"" + rs.getString(1) + "\" name=\"produktid\">");
				sb.append("<p class=\"result-text-big\">" + rs.getString(2) + " - " + rs.getString(6) + "</p>");
				sb.append("<p class=\"result-text\">");
				sb.append(rs.getString(4));
				sb.append("</p>");
				sb.append("</td><td><select class=\"my-select\" name=\"bewertung\">" +
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
		sb.append("<a class=\"result-text\" href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");
	}
	


	public void abgegeben(JdbcQueryBean queryBean, String[] bewertungen, String[] produktID) {
		
		int userid = queryBean.userid;
		queryBean.executeUpdateRatings(bewertungen, produktID, userid);
		
		sb=new StringBuffer("Bewertung erfolgreich abgegeben.");
		
		for (int i=0;i<bewertungen.length;i++){
			
			System.out.println(bewertungen[i]);
		}
		
				
	}
	
	public void showEmpfehlungen(JdbcQueryBean queryBean, int anzahl) {
		
		int userid = queryBean.userid;
		sb = new StringBuffer();
		double minAehnlichkeit = 0.2;
		int benoetigteAnzahl = 10;
		SortedList similarUserList = new SortedList();
		String[] median = null;
		ResultSet aehnliche = null;

		sb.append("<div class=\"input-area\">");
		sb.append("<h1 class=\"login-title\">Empfehlungen</h1>");
		sb.append("<a class=\"subtitle-text\"> Hier finden Sie ihre Empfehlungen! </a>");
		sb.append("<br>");
		sb.append("<a class=\"subtitle-text\"> Sie können jedoch weitere Empfehlungen abgeben!</a>");
		sb.append("<br>");
		sb.append("<a class=\"simple-link\" href=\"ControllerServlet?doAction=bewertung\">Weitere Bewertungen abgeben.</a>");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("<a class=\"simple-link\" href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");
		sb.append("</div>");
		sb.append("<br><br>");

		aehnliche = queryBean.executeGetSimilarUsers(String.valueOf(userid), minAehnlichkeit);

		fillSimilarUserList(aehnliche, similarUserList);

		sb.append(showRecBasics());

		sb.append(contentRecommendations(queryBean));

        if (!similarUserList.isEmpty()) {
            System.out.println("Liste Groesse: "+similarUserList.size());
            if (similarUserList.size() >= benoetigteAnzahl) {
                sb.append(showCollaborativeUserRecs(queryBean, similarUserList, userid, benoetigteAnzahl));
            }
        }

		sb.append("</table></center>");
		sb.append("</div>");

		sb.append(showPopularProducts(queryBean));

        similarUserList.clearList();
    }

	public String showRecBasics() {
		StringBuilder sb = new StringBuilder();

		sb.append("<div class=\"table-area\">");
		sb.append("<center><table width=\"80%;\">");
		sb.append("<tr><td colspan=\"2\">");
		sb.append("<h1 class=\"login-title\">Dies sollten Sie sich unbedingt anschauen!</h1>");
		sb.append("</td></tr>");
		sb.append("<tr>");
		sb.append("<td><p class=\"rating-table-header\">Empfehlungen</p></td>");
		sb.append("<td><p class=\"rating-table-header\">Bewertung/Ähnliche Produkte</p>");
		sb.append("</td></tr>");

		return sb.toString();
	}

	public String showCollaborativeUserRecs(JdbcQueryBean queryBean, SortedList similarUserList, int userID, int recCount) {

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

		try {
			ResultSet recommendations = queryBean.executeGetProductRecommendations(userID, recCount, sqlUserList, mindestRating);
			while(recommendations.next()) {
				sb.append("<tr>");
				sb.append("<td><p class=\"result-text-big\">" + recommendations.getString(2) + " - " + recommendations.getString(5) + " (CF)</p></td>");
				sb.append("<td><p class=\"result-text\">" + recommendations.getString(4) + "</p></td>");
				sb.append("</tr>");
			}
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }

		return sb.toString();
	}

	public String showPopularProducts(JdbcQueryBean queryBean) {
		StringBuilder sb = new StringBuilder();

		sb.append("<div class=\"table-area\">");
		sb.append("<center><table width=\"80%;\">");
		sb.append("<tr><td colspan=\"3\">");
		sb.append("<p class=\"login-title\">Nichts gefunden, das Ihnen gefällt? Schauen Sie sich die beliebtesten Produkte an!</p>");
		sb.append("</td></tr>");
		sb.append("<tr>");
		sb.append("<td><p class=\"rating-table-header\">Platzierung</p></td>");
		sb.append("<td><p class=\"rating-table-header\">Empfehlung</p></td>");
		sb.append("<td><p class=\"rating-table-header\">Beschreibung</p></td>");
		sb.append("</tr>");

		try {
			ResultSet popularProducts = queryBean.executeGetBeliebteProdukte();
			int num = 1;
			while(popularProducts.next())
			{
				sb.append("<tr>");
				sb.append("<td class=\"result-text-big\"><p>" + num + "</p></td>");
				sb.append("<td><p class=\"result-text-big\">"+popularProducts.getString(1)+"</p></td>");
				sb.append("<td><p class=\"result-text\">"+popularProducts.getString(2)+"</p></td>");
				sb.append("</tr>");
				num += 1;
			}
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }
		sb.append("</table></center>");
        sb.append("<a class=\"simple-link\" href=\"ControllerServlet?doAction=logout\">Zum Logout</a>");
		sb.append("</div>");
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
		
		try {
			System.out.println("Bewertungen abgegeben für UserID " + userid);
			ResultSet anzahlrs = queryBean.executeGetRatingCount(userid);
			
			while (anzahlrs.next()) {
				anzahlBewertungen = anzahlrs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return anzahlBewertungen;
	}

	public String contentRecommendations(JdbcQueryBean queryBean) {
		int userID = queryBean.userid;
		int minRatingCount = 5;
		int contentRecAmount = 10;
		StringBuilder sb = new StringBuilder();
		ArrayList<String[]> recs = new ArrayList<>();

		try {
			ResultSet rs = queryBean.executeGetContentBasedRecs(userID, minRatingCount);

			while (rs.next()) {
				String recommendation = rs.getString(1);
				String source = rs.getString(2);
				String creator = rs.getString(3);

				String[] recCombo;

				if (recs.isEmpty()) {
					recCombo = new String[] { recommendation, source, creator };
					recs.add(recCombo);
				} else {
					String[] currRecCombo = recs.get(recs.size() - 1);
					if (currRecCombo[0].equals(recommendation)) {
						if (currRecCombo[1].split(",").length < 5) {
							currRecCombo[1] = currRecCombo[1] + ", " + source;

						} else if (!currRecCombo[1].contains("und weitere...") && currRecCombo[1].split(",").length >= 5) {
							currRecCombo[1] = currRecCombo[1] + " und weitere...";
						}

					} else {
						recCombo = new String[]{recommendation, source, creator};
						recs.add(recCombo);
					}
				}
			}
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }

		recs.sort(Comparator.comparingInt(array -> array[1].split(",").length));
		Collections.reverse(recs);

		System.out.println("All recs: ");
		for (String[] rec : recs) {
			System.out.println("Rec: " + rec[0] + " with " + rec[1].split(",").length);
		}

		ArrayList<String[]> finalRecs = new ArrayList<>();

		for (int i = 0; i < recs.size(); i++) {
			System.out.println("Rec: " + recs.get(i)[0] + " with " + recs.get(i)[1].split(",").length);
			if (i < contentRecAmount) {
				finalRecs.add(recs.get(i));
				System.out.println("Final Rec: " + recs.get(i)[0] + " with " + recs.get(i)[1].split(",").length);
			}
		}

		for (String[] recCombo : finalRecs) {
			sb.append("<tr>");
			sb.append("<td><p class=\"result-text-big\">" + recCombo[0] + " - " + recCombo[2] + " (CN)</p></td>");
			sb.append("<td><p class=\"result-text\"> Empfehlung basiert auf: " + recCombo[1] + "</p></td>");
			sb.append("</tr>");
		}

		return sb.toString();
    }
}
