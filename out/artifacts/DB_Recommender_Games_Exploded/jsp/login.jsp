<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <jsp:useBean  id="queryBean"  class="servlets.JdbcQueryBean"  scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Recommender</title>
</head>
<body>
<div class="header">
    <img src="style/img/Logo.png" alt="Logo" class="logo">
</div>
<jsp:getProperty name="queryBean" property="login" /> 
</body>
</html>