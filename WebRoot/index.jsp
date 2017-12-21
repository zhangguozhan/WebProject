<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script src="js/jquery-2.1.0.js" type="text/javascript" charset="utf-8"></script>
	<script type="text/javascript">
	var  href = window.location.href;
	href = href.substring(href.indexOf("//"), href.lastIndexOf("/")) ;
//	alert(href);
	function goUrl() {
		var url = "http://localhost:8080/WebProject/servlet/LoginServlet";
		var param = 'param={"name":"张三", "age":"18"}';
		window.location.href = url+"?"+encodeURI(param);
	}
	function doAjax() {
		var url = "http://localhost:8080/WebProject/servlet/AjaxServlet"
		var param = {"method":"tradeCreate"};
		$.post(url, param, function(data) {
			alert(data.retCode);
		}, "json");
	}
	</script>
  </head>
  
  <body>
    This is my JSP page. <br>
    
    <input type="button" onclick="goUrl()" value="跳转">
    <input type="button" onclick="doAjax()" value="ajax测试">
  </body>
</html>
