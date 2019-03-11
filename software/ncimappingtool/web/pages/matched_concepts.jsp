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
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="gov.nih.nci.evs.mapping.util.*"%>
<%@ page import="gov.nih.nci.evs.mapping.bean.*"%>
<%@ page import="gov.nih.nci.evs.restapi.bean.*"%>
<%@ page import="gov.nih.nci.evs.restapi.util.*"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<title>NCI Mapping Tool</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/script.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/search.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdown.js"></script>

<script src="http://code.jquery.com/jquery-2.1.3.min.js"></script>
<script src="js/jquery.json-viewer.js"></script>
<link href="css/jquery.json-viewer.css" type="text/css" rel="stylesheet" />

<style type="text/css">
body {
  margin: 0 100px;
  font-family: sans-serif;
}
textarea#json-input {
  width: 100%;
  height: 200px;
}
pre#json-renderer {
  border: 1px solid #aaa;
  padding: 0.5em 1.5em;
}
</style>

<script>
$(function() {
  $('#btn-json-viewer').click(function() {
    try {
      var input = eval('(' + $('#json-input').val() + ')');
    }
    catch (error) {
      return alert("Cannot eval JSON: " + error);
    }
    var options = {
      collapsed: $('#collapsed').is(':checked'),
      withQuotes: $('#with-quotes').is(':checked')
    };
    $('#json-renderer').jsonViewer(input, options);
  });

  // Display JSON sample on load
  $('#btn-json-viewer').click();
});
</script>
    
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

   </script>
   <f:view>
     
        <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
      <!-- End Skip Top Navigation -->
            
      <%@ include file="/pages/templates/header.jsp"%>
      <div class="center-page_960">         
         <!-- Main box -->
         <div id="main-area_960">
            <%@ include file="/pages/templates/sub-header.jsp" %>
            <div>
               <div> 
                  <%@ include file="/pages/templates/matched_concept_details.jsp"%>
               </div>
                  <%@ include file="/pages/templates/nciFooter.jsp"%>
            </div> <!--  End pagecontentLittlePadding -->  
            
         </div> <!--  End main-area_960 -->
         <div class="mainbox-bottom"><img src="<%=request.getContextPath()%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
      </div> <!-- End center-page_960 -->
      
   </f:view>
</body>
</html>
