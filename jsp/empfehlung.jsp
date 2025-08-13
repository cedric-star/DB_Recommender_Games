<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    
<jsp:useBean  id="bewertungBean"  class="servlets.BewertungBean"  scope="session" />
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Recommender</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css" />
</head>
<body>
<div class="header">
    <img src="style/img/Logo.png" alt="Logo" class="logo">
</div>
<jsp:getProperty name="bewertungBean" property="result" /> 
</body>
</html>