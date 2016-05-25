<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script>

function anyClick(selector, exportcsv)
{
  s = "";
  for (i=0;i<selector.length-1;i++)
  {
     selected = selector[i].selected;

     if (selected) {
        if (s!="") { s = s + ","; }
        s = s + selector[i].innerHTML;
     }
  }
  exportcsv.innerHTML = s;
}

function extensionClick()
{
   anyClick(document.forms[0].extensionSelector, document.forms[0].extension);
}
function typesClick()
{
   anyClick(document.forms[0].typesSelector, document.forms[0].types);
}
</script>
<form action="" method="GET">
<table><tr align=top><td><b>Extensions:</b><br>
<select name="extensionSelector" multiple size=10 onClick="extensionClick()">
	<c:forEach var="extension" items="${extensions}">
	  <option id="ext_${extension}">${extension}</option>
	</c:forEach>
</select><br>
<script>
 function s(a)
 {
    if (document.getElementById(a) != null) {
       document.getElementById(a).selected = true;
    }
 }
 <c:forEach var="extension" items="${selectedExtensions}">
   s("ext_${extension}");
 </c:forEach>
</script>
<textarea name="extension">${extensionscsv}</textarea>
</td><td><b>Types:</b><br>
<select name="typesSelector" size="10" multiple onClick="typesClick()">
	<c:forEach var="type" items="${typesList}">
	  <option id="${type.code}">${type.code}</option>
	</c:forEach>
	<script>
	 <c:forEach var="type" items="${selectedTypes}">
       s("type_${type}");
     </c:forEach>
	</script>
</select><br>
<textarea name="types" id="types">${typescsv}</textarea>
<a href="javascript:document.getElementById('types').value='';document.forms[0].submit();">reset</a>
</td><td><b>displayAttributes:</b><br>
<select name="displayAttributes">
<option id=da_yes>yes</option><option id=da_no>no</option>
</select><br>
<b>relations</b><br>
<select name="relations">
<option id=r_yes>yes</option><option id=r_no>no</option>
</select>
<script>
   document.getElementById('r_${relations}').selected = true;
   document.getElementById('da_${displayAttributes}').selected = true;
</script>
</td><td><input type="submit"></td></tr></table>
</form>
<hr>
<form action="/trainingstorefront/showTreeGenerateGraph" method="POST">
<textarea style="width:500px;height:200px" name="dot">${script}</textarea>
<input type="submit" value="Visualize!" onClick="document.forms[1].chl.value=document.forms[0].script.value;alert(document.forms[1].chl.value);">
<input type="hidden" name="CSRFToken" value="${CSRFToken}">
</form>
