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
			<li class="active"><a href="/">任务</a></li>
			<li><a href="/source">数据源</a></li>
		</ul>
	</div>
	<div class="main">
		<table width="100%" cellpadding="5" cellspacing="0">
			<thead>
				<tr>
					<th align="center">源数据</th>
					<th align="center">目标数据</th>
					<th align="center">当前状态</th>
					<th align="center">操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="item">
					<tr>
						<td align="center">${item.s_name}#${item.s_table}</td>
						<td align="center">${item.t_name}#${item.t_table}</td>
						<td align="center">${item.state}</td>
						<td align="center">
							<a href="">站码映射</a>
							<a href="">字段映射</a>
							<a href="">特殊字段映射</a>
							<a href="">日志</a>
							<a href="/resumeJob?id=${item.id}">运行</a>
							<a href="/pauseJob?id=${item.id}">暂停</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table> 
	</div>
</body>
</html>