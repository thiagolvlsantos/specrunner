<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title name="nameTitle">Wicket Quickstart Archetype Homepage</title>
</head>
<body>
    <h1>Basic</h1>
    <strong>Wicket Quickstart Archetype Homepage</strong>


    <p class="example">
        Current date is: <span id="idDate"><%=new org.joda.time.DateTime().toString("HH:mm:ss")%></span>
    </p>

    <%! int version = 0; %>
    <span id="old" version="<%= version %>">
    Old school <%= version++ %>. <a href="basic.jsp" id="link">Refresh</a>
    </span>
</body>
</html>
