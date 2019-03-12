<h:form>
<table class="global-nav" border="0" width="100%" height="15px" cellpadding="0" cellspacing="0">
  <tr valign="bottom">
      <td align="right">
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
    content = content.replaceAll("gov.nih.nci.evs.mapping.bean.", "");
}
if (format.compareTo("json") == 0) {
%>
    <textarea id="json-input" autocomplete="off">
    <%=content%>
    </textarea>
    <p>
      Options:
      <label><input type="checkbox" id="collapsed" />Collapse nodes</label>
    </p>
    <button id="btn-json-viewer" title="run jsonViewer()">Execute</button>
    <pre id="json-renderer"></pre>    
<%
} else if (format.compareTo("xml") == 0) {
%>
<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
<tr><td class="textbody">
<pre>
   <%=content%>
</pre>
</td><tr>
</table>
<%
} else {
%>
<%=content%>
<%
}
%>
</h:form>   