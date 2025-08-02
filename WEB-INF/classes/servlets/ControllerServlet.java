package servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ControllerServlet extends HttpServlet {

	private JdbcQueryBean queryBean = null;
	private RegisterBean registerBean = null;
	private BewertungBean bewertungBean = null;
	private LoginBean loginBean = null;

	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		newBean();

	}
	
	private void newBean() {
		queryBean = new JdbcQueryBean();
		registerBean = new RegisterBean();
		bewertungBean = new BewertungBean();
		loginBean = new LoginBean();
	}

	private void setBean(HttpSession session) {
		session.setAttribute("queryBean", queryBean);
		session.setAttribute("registerBean", registerBean);
		session.setAttribute("bewertungBean", bewertungBean);
		session.setAttribute("loginBean", loginBean);
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		
		try {
			HttpSession session = request.getSession(true);
			
			//get Beans from Session
			queryBean = (JdbcQueryBean) session.getAttribute("queryBean");
			if (queryBean == null) {
				newBean();
				setBean(session);
			}
			else {
				registerBean = (RegisterBean) session.getAttribute("registerBean");
				loginBean = (LoginBean) session.getAttribute("loginBean");
				bewertungBean = (BewertungBean) session.getAttribute("bewertungBean");
			}
			
			//Anfrage abarbeiten

			String doAction = request.getParameter("doAction");
			String ActionURL = "ControllerServlet";
			String url = null;
			String controlParam = null;
			int benoetigteAnzahlBewertungen = 10;
			int anzahlEmpfehlungen = 10;

			if (doAction.equals("register")) {

				System.out.println("Redirecting to register.jsp");

				controlParam = "index";
				registerBean.showFormular();

				url = "/jsp/register.jsp";

			} else if (doAction.equals("login")) {

				String user = request.getParameter("username");
				String pass = request.getParameter("password");

				System.out.println(user + " " + pass);

				queryBean.login(user, pass, loginBean);
				
				if(queryBean.login(user, pass, loginBean)) {

					int anzahl = bewertungBean.anzahlBewertungen(queryBean);
					
					if (anzahl >= benoetigteAnzahlBewertungen) {
						bewertungBean.showEmpfehlungen(queryBean, anzahlEmpfehlungen);
						url = "/jsp/empfehlung.jsp";
					} else {
						bewertungBean.showElements(queryBean);
						url = "/jsp/bewertung.jsp";
					}
					
				} else {
					url = "/jsp/login.jsp";
				}

			} else if (doAction.equals("sendRegisterData")) {


				String[] data = new String[2];
				String user = request.getParameter("username");
				String pass = request.getParameter("password");

				System.out.println("Sending register data for " + user + " with password \"" + pass + "\"");

				queryBean.register(user, pass);
				url = "/index.jsp";

			} else if (doAction.equals("bewertung")) {

				bewertungBean.showElements(queryBean);

				url = "/jsp/bewertung.jsp";
			} else if (doAction.equals("bewertungSubmit")) {

				int angezeigteAnzahl = Integer.parseInt(request.getParameter("countElements"));

				String[] bewertungen = request.getParameterValues("bewertung");
				String[] produktid = request.getParameterValues("produktid");

				bewertungBean.abgegeben(queryBean, bewertungen, produktid);
				int anzahl = bewertungBean.anzahlBewertungen(queryBean);
				
				System.out.println("bewertungssubmit anzahl "+anzahl);
				
				if(anzahl >= benoetigteAnzahlBewertungen) {
					bewertungBean.showEmpfehlungen(queryBean, anzahlEmpfehlungen);
					url = "/jsp/empfehlung.jsp";
				} else {
					bewertungBean.showElements(queryBean);
					url = "/jsp/bewertung.jsp";
				}

				
			} else if (doAction.equals("empfehlung")){
				url = "/jsp/empfehlung.jsp";
			} else {
				url = "/jsp/Error.jsp";
			}
			setBean(session);

			if (doAction.equals("logout")) {
			
				newBean();

				session.removeAttribute("queryBean");
				session.removeAttribute("registerBean");
				session.removeAttribute("bewertungBean");
				session.removeAttribute("loginBean");
				
				url="/index.jsp";

			}
			
			RequestDispatcher dispatcher = getServletContext()
			.getRequestDispatcher(url);
	dispatcher.forward(request, response);

		} catch (ServletException ex) {
			System.out.println("Exception when forwarding the request");
			ex.printStackTrace();

		} catch (Exception ex) {
			System.out.println("Exception when reading or writing the request");
			ex.printStackTrace();
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		this.doGet(request, response);

	}
}
