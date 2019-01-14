<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Vector"%>

<%@ page import="gov.nih.nci.evs.restapi.util.*"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.mapping.util.*"%>
<%@ page import="gov.nih.nci.evs.mapping.bean.*"%>
<%@ page import="gov.nih.nci.evs.restapi.bean.*"%>
<%@ page import="gov.nih.nci.evs.restapi.util.*"%>

<%@ page import="java.util.*"%>


<!DOCTYPE html>
<html>
<title>EVS Sparql Endpoint</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />


<body>
  <script type="text/javascript">
    
    function toggle_visibility(id) {
       var e = document.getElementById(id);
       if(e.style.display == 'block') {
          document.getElementById('toggle').text = "[Show]";
          e.style.display = 'none';
       } else {
          document.getElementById('toggle').text = "[Hide]";
          e.style.display = 'block';
       }
    }    

    function reset() {
        document.getElementById('queryString').value = "";
    }
    
  </script>
  
<f:view>
  <%@ include file="/pages/templates/header.jsp" %>

      <!-- Page content -->
      <div class="pagecontent">
      
<center>
<div class="texttitle-blue">
Welcome to NCI Enterprise Vocabulary Services (EVS) SPARQL Endpoint
</div>
<%
String serviceUrl = NCImtProperties._service_url;
%>
<h1><%=serviceUrl%></h1>
<%
Vector cs_data = new Vector();
String ng = (String) request.getSession().getAttribute("ng");
DataManager dm = (DataManager) request.getSession().getAttribute("dm");
if (dm == null) {
	serviceUrl = NCImtProperties._service_url;
	String data_directory = NCImtProperties._data_directory;
	dm = new DataManager(serviceUrl, data_directory);
	Vector terminologies = dm.getTerminologies();
	for (int i=0; i<terminologies.size(); i++) {
	    Terminology terminology = (Terminology) terminologies.elementAt(i);
	    cs_data.add(terminology.getCodingSchemeName() + "|" + terminology.getCodingSchemeVersion() + "|" + terminology.getNamedGraph());
	    if (terminology.getCodingSchemeName().compareTo("NCI_Thesaurus") == 0) {
	    	ng = terminology.getNamedGraph();
	    }
	}
	cs_data = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(cs_data);
	request.getSession().setAttribute("cs_data", cs_data);
	request.getSession().setAttribute("dm", dm);
}
cs_data = (Vector) request.getSession().getAttribute("cs_data");
String codingSchemeName = (String) request.getSession().getAttribute("codingSchemeName");
if (codingSchemeName == null) {
    codingSchemeName = "NCI_Thesaurus";
}
String named_graph = ng;
OWLSPARQLUtils owlSPARQLUtils = new OWLSPARQLUtils(serviceUrl, null, null);

System.out.println(serviceUrl);

Vector count_vec = owlSPARQLUtils.getTripleCount(named_graph);
String count_line = (String) count_vec.elementAt(0);

System.out.println(count_line);


String tripleCountString = new gov.nih.nci.evs.restapi.util.ParserUtils().getValue(count_line);

 
%>
<h3>Sparql Endpoint URL:&nbsp;<%=serviceUrl%></h3>
<%

%>

    <a href="#" onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf',
    '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
    <img src="<%= request.getContextPath() %>/images/hierarchy.gif" alt="Hierarchy" title="Hierarchy" border="0">
</center>
<%
String requestContextPath = request.getContextPath();
String queryString = (String) request.getSession().getAttribute("queryString");
String time_taken = (String) request.getSession().getAttribute("time_taken");
if (queryString == null || queryString.compareTo("null") == 0) {
    queryString = "";
}
if (time_taken == null || time_taken.compareTo("null") == 0) {
    time_taken = "";
}

Vector result_vec = (Vector) request.getSession().getAttribute("result_vec");
%>

    <div align="center">
    <div class="purple">
    <!--
      <div style="float: left; width: 10%;">&nbsp;</div>
    -->  
      <div style="clear: both;"></div>
      <h3><center>
      Prefixes&nbsp;
      <a id="toggle" href="#" onclick="javascript:toggle_visibility('prefixes')">[Hide]</a>
      </center></h3>
       
<h:form id="queryForm">        
              
      <div id="prefixes" align="center" >
      <table class="borders" style="width: 40%;">
        
<%          
String prefixes = NCImtProperties.getPrefixes();
Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(prefixes, '|');
for (int i=0; i<u.size(); i++) {
    String t = (String) u.elementAt(i);
    t = t.replaceAll("<", "&lt;");
    t = t.replaceAll("<", "&gt;");
%>
    <tr><td align="left">
        <%=t%>
    </td></tr>
<%    
}
%>
         
          
      </table>
      </div>
    </div>
    <br/>
    <div class="yellow">
    <h3 style="text-align: center; margin: 0; margin-bottom: 10px;">Querying&nbsp;

   <h:outputLink
      value="/sparql/pages/sparql_examples.jsf"
      tabindex="3">
      <h:graphicImage value="/images/help.gif" alt="Search Help"
         style="border-width:0;" styleClass="searchbox-btn" />
   </h:outputLink>
   
   </h3>
   
   
<TABLE style="font-size: 10pt;">
  <TBODY>
    <TH style="text-align: right;">Format:</TH>
    <TD style="text-align: left;">
        <SELECT name="formatting"><OPTION
        selected="selected" value="HTML Table">HTML Table</OPTION>
        <OPTION value="XML">XML</OPTION>
        <OPTION value="Text">Text</OPTION>
        <OPTION value="JSON">JSON</OPTION>
        <OPTION value="CSV">CSV</OPTION>
      </SELECT>            
  </TD></TR>
  </TBODY></TABLE>
     
<%
String message = (String) request.getSession().getAttribute("message");
if (message != null) {
%>
      <p class="textbodyred"><%=message%></p>
<%
}
%>


      <center><p class="textbody">Number of triples: <%=tripleCountString%></p></center>

      
<P><textarea name="queryString" id="queryString" style="width: 800px;" rows="12" cols="110"><%=queryString%></textarea>
</P>
      <p>
      <table><tr><td>
            	    <h:commandButton id="reset" value="Reset" action="#{userSessionBean.resetAction}"
	    	      accesskey="14"
	    	      image="/images/reset.gif"
	    	      alt="Reset">
	    	    </h:commandButton>  
	    	    
	    	    &nbsp;&nbsp;
	          
	    	    <h:commandButton id="submit" value="Submit" action="#{userSessionBean.queryAction}"
	    	      accesskey="13"
	    	      onclick="javascript:cursor_wait();"
	    	      image="/images/submit.gif"
	    	      alt="Submit">
	            </h:commandButton> 
      </td></tr></table>
      </p>
      
    </h:form>
    
 </div>
</div>

<div align="center">
<table class="datatable_960" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
<%
if (result_vec != null) {
    String header_str = (String) result_vec.elementAt(0);
    Vector header_vec = gov.nih.nci.evs.restapi.util.StringUtils.parseData(header_str);
    for (int i=0; i<header_vec.size(); i++) {
        String header = (String) header_vec.elementAt(i);
%>
        <th class="dataTableHeader" scope="col" align="left"><%=header%></th>
<%
    }
    for (int i=1; i<result_vec.size(); i++) {
        String row = (String) result_vec.elementAt(i);
        Vector row_element_vec = gov.nih.nci.evs.restapi.util.StringUtils.parseData(row);
        
	    if (i % 2 == 0) {
	    %>
	      <tr class="dataRowDark">
	    <%
	    } else {
	    %>
	      <tr class="dataRowLight">
	    <%
	    }
	for (int j=0; j<row_element_vec.size(); j++) {
	    String row_element = (String) row_element_vec.elementAt(j);
	    row_element = gov.nih.nci.evs.restapi.util.StringUtils.encode_term(row_element); 
    
        %>
	    <td class="dataCellText" scope="row">
		 <%=row_element%>
	    </td>
<%
        }
%>        
        </tr>
<%        
    }
%>
<br/>
    <div class="results">
<b>Time Taken:</b>&nbsp;<%=time_taken%> milliseconds</p>
<%
}
%>
</table>
</div>

        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->        
<!--        
    <div class="mainbox-bottom"><img src="/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
-->

</f:view>
</body>
</html>
