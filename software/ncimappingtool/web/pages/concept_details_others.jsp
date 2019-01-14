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
<%@ page import="gov.nih.nci.evs.restapi.util.*"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.mapping.util.*"%>
<%@ page import="gov.nih.nci.evs.mapping.bean.*"%>
<%@ page import="gov.nih.nci.evs.restapi.bean.*"%>
<%@ page import="gov.nih.nci.evs.restapi.util.*"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<%
String serviceUrl = NCImtProperties._service_url;
DataManager dm = (DataManager) request.getSession().getAttribute("dm");
String ng = (String) request.getParameter("ng");
Terminology terminology = dm.getTerminologyByNamedGraph(ng);
String prop_dictionary = terminology.getCodingSchemeName();

System.out.println("concept_details.jsp " + prop_dictionary);

String prop_version = null;
response.setContentType("text/html;charset=utf-8");
String cs_name = prop_dictionary;
String dictionary = prop_dictionary;
String short_name = cs_name; 	

%>
<title>NCI Mapping Tool</title>

<%
        boolean view_graph_link = false;
        String ncbo_id = null;
        String is_virtual = "true";
        String ncbo_widget_info = "";//NCItBrowserProperties.getNCBO_WIDGET_INFO();
        boolean view_graph = false;

%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/script.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/search.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdown.js"></script>
</head>
<body>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
   
   <script type="text/javascript">
	var newwindow;
	function popup_window(url)
	{
		newwindow=window.open(
		url, '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');
		if (window.focus) {
		    newwindow.focus();
		}
	}
	

	 function goBack() {
	     window.history.back();
	 }

	
	
   </script>
   <f:view>
      <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
      <!-- End Skip Top Navigation -->
      
 
            <%
                String code = (String) request.getParameter("code");
                if (code == null) {
                    code = (String) request.getSession().getAttribute("code");
                }
                System.out.println("CODE: " + code);
                String ns = null;
 		String type = null;
            %>
            
            
      <%@ include file="/pages/templates/header.jsp"%>
      <div class="center-page_960">         
         
         <!-- Main box -->
         <div id="main-area_960">
            <%@ include file="/pages/templates/sub-header.jsp" %>
            <div>
               <div> 
                  <%@ include file="/pages/concept_report_others.jsp"%>
               </div>
                  <%@ include file="/pages/templates/nciFooter.jsp"%>
            </div> <!--  End pagecontentLittlePadding -->  
            
         </div> <!--  End main-area_960 -->
         <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
      </div> <!-- End center-page_960 -->
      
   </f:view>
</body>
</html>
