package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegisterBean
 */
public class RegisterBean extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	StringBuffer sb = new StringBuffer();
	
    public RegisterBean() {
        super();
        // TODO Auto-generated constructor stub
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
	
	public String getresult () {	
		
		sb = new StringBuffer();
		
		sb.append("<center><h1>MyRegistrierung</h1></center>");
		sb.append("<br>\n");
		sb.append("<br>\n");
		
		
		
		//Hier beliebige Eingabefelder hinzufuegen
		//und auf gueltigkeit pruefen
		//username bereits vorhanden?
		//eingaben zu lang?
		sb.append("<center><FORM ACTION=\"ControllerServlet?doAction=sendRegisterData\" METHOD=\"POST\">\n" +
				"<P>MyBenutzername<BR>\n" +
				"<INPUT TYPE=\"TEXT\" NAME=\"username\"  SIZE=\"30\" MAXLENGTH=\"30\"></P>\n" +
				"<P>MyPasswort<BR>\n" +
				"<INPUT TYPE=\"password\" NAME=\"password\"  SIZE=\"30\" MAXLENGTH=\"30\"></P>\n" +
				"<INPUT TYPE=\"submit\" VALUE=\"Registrieren\">\n"+
				"</FORM></center>\n");	
		
		return sb.toString();
		
	}

	public String[] showFormular() {
		// TODO Auto-generated method stub
		return null;
	}

}
