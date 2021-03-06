<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title name="nameTitle">Wicket Quickstart Archetype Homepage</title>
<link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <h1>Basic</h1>
    <strong>Wicket Quickstart Archetype Homepage</strong>


    <p class="example">
        Current date is: <span id="idDate"><%=new org.joda.time.DateTime().toString("HH:mm:ss")%></span>
        Current date plus 1 hour is: <span id="idDateFormat"><%=new org.joda.time.DateTime().plusHours(1).toString("HH:mm:ss")%></span>
    </p>
    
    <%! int version = 0; %>
    <span id="old" version="<%= version %>">
    Old school <%= version++ %>. <a href="basic.jsp" id="link">Refresh</a>
    </span>
    
    <span id="target" class="formated">A attribute check of 'class' with formated.</span>
</body>
</html>
