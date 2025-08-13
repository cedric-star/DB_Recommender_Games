package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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

	public String getResult() {

		StringBuffer sb = new StringBuffer();

		sb.append("<center><p></p><br>");

		if (logged) {
			sb.append("<a class=\"result-text\" href=\"ControllerServlet?doAction=bewertung\">Zur Bewertung</a><br>");
			sb.append("<a class=\"result-text\" href=\"ControllerServlet?doAction=logout\">Logout</a>");
		} else {
			sb.append("<img src=\"style/img/Logo.png\" alt=\"Logo\" id=\"large-logo\">");
			sb.append("<h1 class=\"login-title\" >Anmeldung</h1>");
			sb.append("<p class=\"subtitle-text\">Du brauchst noch einen Account? Hier geht es zur <a class=\"simple-link\" href=\"ControllerServlet?doAction=register\">Registrierung</a>.</p>"
							+ "    	<br>"
							+ "    	<FORM ACTION=\"ControllerServlet?doAction=login\" METHOD=\"POST\">"
							+ "    	<p class=\"text-dark-large\">Benutzername </p><input type=\"text\" name=\"username\"></input>"
							+ "    	<p class=\"text-dark-large\">Passwort </p><input type=\"password\" name=\"password\"></input><br><br>"
							+ "    	<INPUT TYPE=\"submit\" VALUE=\"Login\">");
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
