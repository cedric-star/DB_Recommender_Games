package servlets;

import jakarta.servlet.http.HttpServlet;

import java.io.Serial;

/**
 * Servlet implementation class RegisterBean
 */
public class RegisterBean extends HttpServlet {
	@Serial
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	StringBuffer sb = new StringBuffer();
	
    public RegisterBean() {
        super();
    }

	/***
	 * Gibt die benötigten Daten zur Anzeige der Eingabefelder mit. Bitte kein HTML einfügen.
	 * @return Werte
	 */
	public String getResult () {
		
		sb = new StringBuffer();
		
		return sb.toString();
		
	}

	public String[] showFormular() {
		// TODO Auto-generated method stub
		return null;
	}

}
