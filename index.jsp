<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
     <jsp:useBean  id="loginBean"  class="servlets.LoginBean"  scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="style/style.css">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Recommender</title>
</head>
<body>
<a href="./jsp/myTest.jsp">Empfehlungen</a>

<jsp:getProperty name="loginBean" property="result" /> 

</body>
</html>