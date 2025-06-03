package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

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

				controlParam = "index";
				registerBean.showFormular();

				url = "/jsp/register.jsp";

			} else if (doAction.equals("login")) {

				
				//System.out.println("HALLOOOOOOO13");
				String[] loginData = new String[2];

				loginData[0] = request.getParameter("username");
				loginData[1] = request.getParameter("password");

				queryBean.login(loginData, loginBean);
				//System.out.println("username"+loginData[0]+"password"+loginData[1]);
				url = "/jsp/login.jsp";
				
				if(queryBean.login(loginData, loginBean)) {
					
					//System.out.println("HALLOOOOOOO");
				
					int anzahl = bewertungBean.anzahlBewertungen(queryBean);
					
					System.out.println("anzahl "+anzahl);
					
					if(anzahl >= benoetigteAnzahlBewertungen) {
						bewertungBean.showEmpfehlungen(queryBean, anzahlEmpfehlungen);
						url = "/jsp/empfehlung.jsp";
					} else {
						bewertungBean.showElements(queryBean);
						url = "/jsp/bewertung.jsp";
					}
					
				} else {
					url = "/login.jsp";
				}

			} else if (doAction.equals("sendRegisterData")) {

				String[] data = new String[2];
				data[0] = request.getParameter("username");
				data[1] = request.getParameter("password");

				queryBean.register(data);
				url = "/index.jsp";

			} else if (doAction.equals("bewertung")) {

				bewertungBean.showElements(queryBean);

				url = "/jsp/bewertung.jsp";
			} else if (doAction.equals("bewertungsubmit")) {

				int angezeigteAnzahl = Integer.parseInt(request.getParameter("countElements"));
				
				String[] bewertungen = new String[Integer.parseInt(request
						.getParameter("countElements"))];
				
				String[] produktid = new String[Integer.parseInt(request
						.getParameter("countElements"))];

				bewertungen = request.getParameterValues("bewertung");
				produktid = request.getParameterValues("produktid");

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
