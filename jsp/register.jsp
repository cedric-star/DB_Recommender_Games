<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    
    <jsp:useBean  id="registerBean"  class="servlets.RegisterBean"  scope="session" />
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>REC-Registrierung</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css" />
</head>
<body>
<div class="header">
    <img src="style/img/Logo.png" alt="Logo" class="logo">
</div>
    <h1>Nutzerregistrierung</h1>
    <br>
    <FORM ACTION="ControllerServlet?doAction=sendRegisterData" METHOD="POST">
        <P>Benutzername<BR>
            <INPUT TYPE="TEXT" NAME="username"  SIZE="30" MAXLENGTH="30"></P>
        <P>Passwort<BR>
            <INPUT TYPE="password" NAME="password" SIZE="30" MAXLENGTH="30"></P>
        <INPUT TYPE="submit" VALUE="Registrieren">
    </FORM>
<%-- <jsp:getProperty name="registerBean" property="result"/> --%>
</body>
</html>