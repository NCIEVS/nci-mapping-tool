<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>

<%@ page import="gov.nih.nci.evs.restapi.meta.util.*"%>
<%@ page import="gov.nih.nci.evs.restapi.meta.bean.*"%>

<%@ page import="gov.nih.nci.evs.restapi.util.*"%>
<%@ page import="gov.nih.nci.evs.mapping.util.*"%>
<%@ page import="gov.nih.nci.evs.mapping.bean.*"%>
<%@ page import="gov.nih.nci.evs.mapping.common.*"%>


<%@ page import="gov.nih.nci.evs.restapi.util.*"%>

<%@ page contentType="text/html;charset=UTF-8"%>

        <%

	String named_graph = (String) request.getParameter("ng");    
	String concept_code = (String) request.getParameter("code");
	String jsp = "ncimappingtool/pages/concept_details_others.jsf";
	
System.out.println(named_graph); 
String entityDescription = null;

TTLQueryUtils ttlQueryUtils = new TTLQueryUtils(serviceUrl);
Vector v = ttlQueryUtils.class_label_query(named_graph, code);
if (v != null && v.size() > 0) {
entityDescription = (String) v.elementAt(0);
}

TTLQueryUtilsRunner runner = NCImtProperties.getTTLQueryUtilsRunner();
/*
runner = request.getSession().getAttribute("runner");
if (runner == null) {
    runner = new TTLQueryUtilsRunner(serviceUrl);
    request.getSession().setAttribute("runner", runner);
}
*/

gov.nih.nci.evs.restapi.meta.bean.Concept c = runner.getConcept(named_graph, concept_code);
request.getSession().setAttribute("ng", named_graph);

ConceptDetailsPageGenerator generator = new ConceptDetailsPageGenerator();
String title = null;
String content = generator.getConceptContent(named_graph, c);
	
	%>
	
<%@ include file="/pages/templates/content-header.jsp"%></div>	
<%@ include file="/pages/templates/menu-bar.jsp"%>

<%
   
  String message = (String) request.getSession().getAttribute("message");
  request.getSession().removeAttribute("message");
  if (message != null) {
%>  
      <p class="textbodyred"><%=message%></p>
<%      
  } 
%>
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%=content%>  
      </div>

<br></br>
<!--
<h:form> 
<center>
    <h:commandButton id="home" value="Home" action="#{userSessionBean.homeAction}"
      accesskey="15"
      image="/images/close.gif"
      alt="Close">
    </h:commandButton>

</center>     
</h:form> 
-->


