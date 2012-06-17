<html>
<head>
<title>Confirmation</title>
</head>
<body>
	<table border=1>
		<thead>
			<tr>
				<th colspan=2>Confirmation page</th>
		</thead>
		<tbody>
			<tr>
				<td colspan=2>Header <span id="hDate"><%= new org.joda.time.LocalDateTime().toString("HH:mm dd/MM/yyyy") %></span></td>
			</tr>
			<tr>
				<td>Menu</td>
				<td>Body</td>
			</tr>
			<tr>
				<td colspan=2>Footer</td>
			</tr>
		</tbody>
	</table>
	<a href="flow1.jsp">Previous</a> | <a href="flow3.jsp">Next</a>
</body>
</html>