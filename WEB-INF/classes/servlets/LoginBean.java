package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginBean
 */
public class LoginBean extends HttpServlet {
	private static final long serialVersionUID = 1L;

	boolean logged = false;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getresult() {

		StringBuffer sb = new StringBuffer();

		sb.append("<center><p></p><br>");

		if (logged) {

			sb.append("<a href=\"ControllerServlet?doAction=bewertung\">Zur Bewertung</a><br>");
			sb.append("<a href=\"ControllerServlet?doAction=logout\">Logout</a>");
		} else {
			sb.append("<h1>Bitte registrieren oder einloggen</h1>");
			sb.append("<a href=\"ControllerServlet?doAction=register\">Zur Registrierung</a><br>"
							+ "    	<br>"
							+ "    	<FORM ACTION=\"ControllerServlet?doAction=login\" METHOD=\"POST\">"
							+ "    	<p>Benutzername </p><input type=\"text\" name=\"username\"></input>"
							+ "    	<p>Passwort </p><input type=\"password\" name=\"password\"></input><br><br>"
							+ "    	<INPUT TYPE=\"submit\" VALUE=\"einloggen\">");
		}
		sb.append("    	</form>" + "    	</center>");

		return sb.toString();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
