<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-mapping-tool/LICENSE.txt for details.
L--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="gov.nih.nci.evs.restapi.util.*"%>

<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.mapping.util.*"%>
<%@ page import="gov.nih.nci.evs.mapping.bean.*"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI EVS Concept Mapping Tool</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body onLoad="document.forms.searchTerm.matchText.focus();">

  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>

<%
String hm_basePath = request.getContextPath(); 

System.out.println("hm_basePath : " + hm_basePath);

Vector cs_data = new Vector();
DataManager dm = (DataManager) request.getSession().getAttribute("dm");
if (dm == null) {
	String serviceUrl = NCImtProperties._service_url;
	System.out.println("serviceUrl : " + serviceUrl);
	
	String data_directory = NCImtProperties._data_directory;
	System.out.println("data_directory : " + data_directory);
	
	dm = new DataManager(serviceUrl, data_directory);
	Vector terminologies = dm.getTerminologies();
	for (int i=0; i<terminologies.size(); i++) {
	    Terminology terminology = (Terminology) terminologies.elementAt(i);
	    cs_data.add(terminology.getCodingSchemeName() + "|" + terminology.getCodingSchemeVersion() + "|" + terminology.getNamedGraph());
	    System.out.println(terminology.getCodingSchemeName());
	}
	cs_data = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(cs_data);
	request.getSession().setAttribute("cs_data", cs_data);
	request.getSession().setAttribute("dm", dm);
	request.getSession().setAttribute("codingSchemeName", "NCI_Thesaurus");
}


%>
<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation -->
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page_960">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area_960">
      <%@ include file="/pages/templates/content-header.jsp" %>
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
       
        <%@ include file="/pages/templates/vocabulary_listing.jsp" %>
       
        
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=request.getContextPath()%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>
