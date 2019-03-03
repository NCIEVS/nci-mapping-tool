<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-mapping-tool/LICENSE.txt for details.
L--%>

<%  
String namedGraph = (String) request.getSession().getAttribute("ng");
if (namedGraph == null) {
    namedGraph = NCImtBrowserProperties.get_default_named_graph();
    request.getSession().setAttribute("ng", namedGraph);
} 
String vocabulary = (String) request.getSession().getAttribute("scheme");
if (vocabulary == null) {
    vocabulary = NCImtBrowserProperties.get_TERMINOLOGY();
    request.getSession().setAttribute("scheme", vocabulary);
} 

%>
<table class="global-nav" border="0" width="100%" height="15px" cellpadding="0" cellspacing="0">
  <tr valign="bottom">
      <td align="right">
	<a href="<%=request.getContextPath() %>/pages/sparql.jsf?ng=<%=namedGraph%>" title="Sparql Query Endpoint.">Sparql Query</a>    		
	|  
        <a href="<%= request.getContextPath() %>/pages/home_alt.jsf" >Home</a>
        |
        <a href="#" onClick="window.open('<%= request.getContextPath() %>/pages/help_sparql.jsf', '_blank')">Help</a>&nbsp;&nbsp;&nbsp;
    </td>
  </tr>
</table>
