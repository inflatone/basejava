<%@ page import="ru.javaops.basejava.webapp.model.ContactType" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <title>Список всех резюме</title>
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<section>
    <a href="resume?action=add"><img src="img/add.png" alt="Add"></a>
    <table border="1" cellpadding="8" cellspacing="0">
        <tr>
            <th>Имя</th>
            <th>Email</th>
            <th></th>
            <th></th>
        </tr>
        <jsp:useBean id="resumes" scope="request" type="java.util.List"/>
        <c:forEach items="${resumes}" var="resume">
            <jsp:useBean id="resume" type="ru.javaops.basejava.webapp.model.Resume"/>
        <tr>
            <td><a href="resume?uuid=${resume.uuid}&action=view">${resume.fullName}</a></td>
            <td>${ContactType.MAIL.toHtml(resume.getContact(ContactType.MAIL))}</td>
            <td><a href="resume?uuid=${resume.uuid}&action=delete"><img src="img/delete.png" alt="Delete"></a></td>
            <td><a href="resume?uuid=${resume.uuid}&action=edit"><img src="img/pencil.png" alt="Edit"></a></td>

        </tr>
        </c:forEach>
    </table>

</section>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>
