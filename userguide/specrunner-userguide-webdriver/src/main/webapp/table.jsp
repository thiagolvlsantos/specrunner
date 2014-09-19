<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Table example</title>
</head>
<body>
  <h1>Table</h1>
  <table id="tableId" border=1>
    <caption>Dynamic dates</caption>
    <tr>
      <th>ID</th>
      <th>Creation</th>
      <th>Name</th>
    </tr>
    <tr>
      <td>1</td>
      <td><%=new org.joda.time.DateTime().toString("HH:mm")%></td>
      <td>A <span class="blue">á</span></td>
    </tr>
    <tr>
      <td>2</td>
      <td><%=new org.joda.time.DateTime().toString("HH:mm:ss")%></td>
      <td>B</td>
    </tr>
    <tr>
      <td colspan=3 type="table">
        <table border=1>
          <caption>Inner table</caption>
          <tr>
            <th>Name</th>
            <th>Date</th>
          </tr>
          <tr>
            <th>Thiago</th>
            <td><%=new org.joda.time.DateTime().toString("HH:mm:ss")%></td>
          </tr>
          <tbody>
            <tr>
              <td>
                <table border=1>
                  <caption>Super Inner table</caption>
                  <tr>
                    <th>Name</th>
                    <th>Month</th>
                  </tr>
                  <tr>
                    <td>Santos</td>
                    <td>January</td>
                  </tr>
                </table>
              </td>
              <td><%=Math.random()%></td>
            </tr>
          </tbody>
          <tbody>
            <tr>
              <td colspan="2"><input type="text" value="Linha" />
                <textarea>Texto</textarea></td>
            </tr>
            <tr>
              <td colspan="2"><img src="img/image.jpg" /></td>
            </tr>
            <tr>
              <td colspan="2">Field:<input type="text"
                id="txtField" value="Example if sub query" /></td>
            </tr>
          </tbody>
        </table>
      </td>
    </tr>
  </table>

  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <table>
    <caption>Parameters</caption>
    <tr>
      <th>Name</th>
      <th>Value</th>
    </tr>
    <tr>
      <td>dir</td>
      <td>user.dir</td>
    </tr>
  </table>
</body>
</html>