<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Checkbox and radios example</title>
<script type="text/javascript" src="script.js"></script>
</head>
<body>
    <h1>Select</h1>
    <form>
        <legend>Selections</legend>
        Disable single:<input type="checkbox" id="idDisableSingle" onchange="toogle(document.forms[0].sel1);" /> <br>
        Single Selection:<select id="sel1">
            <option value="1">Option 1</option>
            <option value="2" selected="selected">Option 2</option>
            <option value="3">Option 3</option>
        </select>
        <p>
            Disable Multiple:<input type="checkbox" id="idDisableMultiple" onchange="toogle(document.forms[0].sel2);" />
            <br> Multiple Selection:<select id="sel2" multiple="multiple">
                <option value="1">Option 1</option>
                <option value="2" selected="selected">Option 2</option>
                <option value="3">Option 3</option>
                <option value="4" selected="selected">Option 4</option>
            </select>
        </fieldset>
    </form>
</body>
</html>