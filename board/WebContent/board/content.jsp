<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>자료실</title>
<script src="${pageContext.request.contextPath}/board/script.js"></script> 
<link href="${pageContext.request.contextPath}/board/css/style.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/board/css/contentstyle.css" rel="stylesheet" type="text/css"/>
</head>
<body> 
<section>
<b>글내용 보기</b>  
<br>
<form>
<table class="contenttable">
	<tr>
		<th>글 번호</th>
		<td>${article.num}</td>
		<th>조회수</th>
		<td>${article.readcount}</td>
	</tr>
	<tr>
		<th>작성자</th>
		<td>${article.writer}</td>
		<th>작성일</th>
		<td>${article.regdate}</td>
	</tr>
	<tr>
		<th>글제목</th>
		<td colspan="3" class="contenttitle">${article.subject}</td>
	</tr>
	<tr>
		<th>설명</th>
		<td colspan="3" class="content">
		<pre>${article.content}</pre></td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td colspan="3" class="file"><a href="${pageContext.request.contextPath}/board/downloadPro.do?num=${article.num}">${article.filename}</a>
	</tr>
	<tr>
		<td colspan="4">
		<input type="button" value="수 정" onClick="document.location.href='${pageContext.request.contextPath}/board/updateForm.do?num=${article.num}&pageNum=${pageNum}'">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value="삭 제" onClick="document.location.href='${pageContext.request.contextPath}/board/deleteForm.do?num=${article.num}&pageNum=${pageNum}'">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value="답 글" onClick="document.location.href='${pageContext.request.contextPath}/board/writeForm.do?num=${article.num}&ref=${article.ref}&step=${article.step}$depth=${article.depth}'">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value="목 록" onClick="document.location.href='${pageContext.request.contextPath}/board/list.do?pageNum=${pageNum}'">
		&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	</tr>
</table>
</form>
</section>
</body>
</html>