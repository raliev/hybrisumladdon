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
<table width=100% border=0><tr align=top><td>
<table border=0><tr valign=top><td>
<b>Extensions:</b><br>
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
<br>
<br>
<input type="submit" style="width:100px;height:40px">
<script>
   document.getElementById('r_${relations}').selected = true;
   document.getElementById('da_${displayAttributes}').selected = true;
</script>
</td><td></td></tr></table>
</form>
<hr>
<form target="ifram" action="${request.contextPath}/showTreeGenerateGraph" method="POST">
<table>
<tr>
<td><textarea style="width:500px;height:200px" name="dot1">${script}</textarea>
<input type="hidden" name="dot" value="${script2}"/>
<br>
<br>Install <a href="http://www.graphviz.org/Download.php">Graphviz</a> and copy-paste to GVEDIT
<br>Copy-paste to <a href="http://dreampuf.github.io/GraphvizOnline/">http://dreampuf.github.io/GraphvizOnline/</a>
</tr>
</table>
<input type="checkbox" name="download"/>download png?
<input type="submit" value="Visualize!" onClick=""> to make it work you need to install Graphviz and configure the extension, see README in the root directory
<input type="hidden" name="CSRFToken" value="${CSRFToken}">
</form>
</td><td width=100%>
<iframe src="about:blank" style="width:100%;height:100%" id=ifram name=ifram></iframe>
</td></tr></table>