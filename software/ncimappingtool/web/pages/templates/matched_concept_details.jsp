<h:form>
<table>
<tr><td class="textbody" align="right" width="75%">
			<a href="<%=request.getContextPath() %>/pages/matched_concepts.jsf?format=html">
         		HTML</a> 
                |
			<a href="<%=request.getContextPath() %>/pages/matched_concepts.jsf?format=json">
         		Json</a> 
                |
			<a href="<%=request.getContextPath() %>/pages/matched_concepts.jsf?format=xml">
         		XML</a> 
                               
</td></tr>
</table>                
<%
String format = (String) request.getParameter("format");
if (format == null) {
    format = "html";
}
String content = (String) request.getSession().getAttribute("matched_concepts");
if (format.compareTo("json") == 0) {
    gov.nih.nci.evs.mapping.bean.PropertyData pd = (gov.nih.nci.evs.mapping.bean.PropertyData) request.getSession().getAttribute("propertyData");
    content = pd.toJson();
} else if (format.compareTo("xml") == 0) {
    gov.nih.nci.evs.mapping.bean.PropertyData pd = (gov.nih.nci.evs.mapping.bean.PropertyData) request.getSession().getAttribute("propertyData");
    content = pd.toXML();
    content = content.replaceAll("<", "&lt;");
    content = content.replaceAll(">", "&gt;");
}
%>
<%
if (format.compareTo("html") != 0) {
%>
<pre>
<%
}
%>
<%=content%>
<%
if (format.compareTo("html") != 0) {
%>
</pre>
<%
}
%>
</h:form>   