<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-mapping-tool/LICENSE.txt for details.
L--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>

<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*"%>

<%@ page import="java.util.ResourceBundle"%>

<%@ page import="org.apache.log4j.*" %>
<%@ page import="javax.faces.model.*" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>NCI EVS Mapping Tool</title>
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>

  <script type="text/javascript"> 
      window.history.forward(1);
      function noBack() {
         window.history.forward(1);
      }
  </script> 


</head>


<body> 


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





<%
 
    String type = (String) request.getParameter("type"); //through restriction link, or refresh
    if (type == null) {
    	type = (String) request.getSession().getAttribute("type"); 
    }
    if (type == null) {
    	type = "upload_data"; 
    }
     String action = (String) request.getSession().getAttribute("action");
    if (action == null) {
    	action = "upload_data"; 
    }
    
     String warning_msg= (String) request.getSession().getAttribute("message");
     if (warning_msg != null && warning_msg.compareTo("null") != 0) {
	 request.getSession().removeAttribute("message");
	 
     %>
	<p class="textbodyred"><%=warning_msg%></p>
     <%
     }

%>

 <font size="3">
 
 <%
 if (action.compareTo("upload_data") == 0) {
 %>
 <b>Upload mapping source data from file</b></font><br/>
 <%
 } else {
 %>
 <b>Upload mapping results from file</b></font><br/>
 <%
 }

 
 %>

<form enctype="multipart/form-data" method="post" action="<%= request.getContextPath() %>/upload?action=<%=action%>&type=<%=type%>" >
<p>
Please specify a file:<br>

<input type="file" id="fileInput" name="fileInput" size='50' >

</p>
<div>


<INPUT TYPE="submit" value="Submit" ALT="Submit Form">


</div>
</form>

        
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




