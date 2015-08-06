<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/s1/css/index.css">
<title>南京市水利局数据汇集程序</title>
</head>
<body>
	<div class="header">
		<h2>南京水利数据汇集系统</h2>
		<ul>
			<li><a href="/">任务</a></li>
			<li  class="active"><a href="/source">数据源</a></li>
		</ul>
	</div>
	<div class="main">
		<table width="100%" cellpadding="5" cellspacing="0">
			<thead>
				<tr>
					<th align="center">名称</th>
					<th align="center">类型</th>
					<th align="center">主机/端口</th>
					<th align="center">实例</th>
					<th align="center">认证</th>
					<th align="center">操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="item">
					<tr>
						<td align="center">${item.display_name}</td>
						<td align="center">${item.db_type}</td>
						<td align="center">${item.db_host}:${item.db_port}</td>
						<td align="center">${item.db_instance}</td>
						<td align="center">${item.db_username}/${item.db_pwd}</td>
						<td align="center">
							<a href="">运行的任务</a>
							<a href="">修改</a>
							<a href="">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table> 
	</div>
</body>
</html>