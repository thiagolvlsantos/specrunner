
<script type="text/javascript" src="script.js"></script>

<style type="text/css">
	.example {
		border: 1px solid black;
	}
	.example td {
		border: 1px dotted gray;
		padding: 4px;
	}
</style>

<table class="example">
<tr>
	<td>

Current date is:
<span id="idDate"><%=new org.joda.time.DateTime().toString("HH:mm:ss")%></span>

<p>Table example:
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
		<td>Aá</td>
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
						<td colspan=2>
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
					</tr>	
				</tbody>			
			</table>
		</td>
	</tr>
</table>
</td>
<td>
<form>
	<fieldset>
		<legend>Checkboxes and Radios</legend>
	Multiple:<input type="checkbox" checked="checked" id="check1" name="checkGroup" value="1"/>C1
				<input type="checkbox" checked="checked" id="check2" name="checkGroup" value="2" disabled="disabled"/>C2
					<input type="checkbox" id="check3" name="checkGroup" value="3"/>C3
					
	<p>
	Radio:<input type="radio" id="rad1" name="radioGroup">R1
			<input type="radio" checked="checked" id="rad2" name="radioGroup">R2
				<input type="radio" id="rad3" name="radioGroup">R3
	</fieldset>
	<fieldset>
	<legend>Selections</legend>
	Disable single:<input type="checkbox" id="idDisableSingle" onchange="toogle(document.forms[0].sel1);"/>
	<br>
	Single Selection:<select id="sel1">
				<option value="1">Option 1</option>
				<option value="2" selected="selected">Option 2</option>
				<option value="3">Option 3</option>
			</select>
	<p>
	Disable Multiple:<input type="checkbox" id="idDisableMultiple" onchange="toogle(document.forms[0].sel2);"/>
	<br>
	Multiple Selection:<select id="sel2" multiple="multiple">
				<option value="1">Option 1</option>
				<option value="2" selected="selected">Option 2</option>
				<option value="3">Option 3</option>
				<option value="4" selected="selected">Option 4</option>
			</select>
			
	</fieldset>
	<fieldset>
	<legend>Text fields and areas</legend>
	Text field: <input type="text" name="txtName"/>
	<p>
	Text area: <textarea rows="4" cols="10" name="areaName">type here</textarea>
	</fieldset>
</form>
</td>
</tr>
</table>
