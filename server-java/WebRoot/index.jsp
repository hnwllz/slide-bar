<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
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
  </head>
  
  <body>
    This is my JSP page. <br>
    <img id="img1"/>
    <img id="img2"/>
  </body>
  <script src="js/jquery.min.js"></script>
  <script type="text/javascript">
  		$.ajax({
  			url: 'getImageVerifyCode',
  			type: 'post',
  			dataType: 'json',
  			success: function(data){
  				console.log(data);
  				if(data.code === 200){
  					var imgObj = $.parseJSON(data.data);
  					console.log(imgObj);
  					$('#img1').attr('src', 'data:image/png;base64,'+imgObj.bigImage);
  					$('#img2').attr('src', 'data:image/png;base64,'+imgObj.smallImage);
  				}
  			},
  			error: function(err){
  				console.log(err);
  			}
  		});
  </script>
</html>
