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
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils"%>

<%
  String tab = "&nbsp;&nbsp;&nbsp;&nbsp;";
  String tab2 = tab + tab;
  String arrowImage = request.getContextPath() + "/images/up_arrow.jpg";
  //String ncit_build_info = new DataUtils().getNCITBuildInfo();
  //String application_version = new DataUtils().getApplicationVersion();
  //String anthill_build_tag_built = new DataUtils().getNCITAnthillBuildTagBuilt();
  //String evs_service_url = new DataUtils().getEVSServiceURL();
%>
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
        
          <!-- Page content -->
           <a name="evs-content" id="evs-content" tabindex="0"></a>
            <!-- ======================================= -->
            <!-- HELP CONTENT -->
            <!-- ======================================= -->
            <div class="texttitle-blue">Help</div>

            <p class="textbody">
              <a href="#introduction">Introduction</a>
              <br/>
              <a href="#homePage">NCI Mapping Tool Home Page</a>
              <br/>
              <a href="#sourceTerms">Enter Source Terms</a>
              <br/> 
              <a href="#matchedConcepts">Matched Concepts</a>
              <br/>  
              <a href="#unmatchedTerms">Unmatched Terms</a>
              <br/> 
              <a href="#manualMapping">Manual Mapping</a>
              <br/>               
<!--              
              <a href="#terminologies">Terminologies Tab</a>
              <br/>
              <%= tab %>
              <a href="#sources">Sources</a>
              <br/>
              <%= tab %>
              <a href="#searchBox">Using the Search Box</a>
              <br/>
              <%= tab %>
              <a href="#searchResults">Search Results</a>
              <br/>
              <%= tab %>
              <a href="#searchingOther">Searching Other/Multiple Versions of a Terminology</a>
              <br/>
              <%= tab %>
              <a href="#advancedSearch">Advanced Search</a>
              <br/>
              <%= tab %>
              <a href="#conceptDetails">Concept Details</a>
              <br/>
              <%= tab %>
              <a href="#additionalLinks">Additional Links for Individual Terminologies</a>
              <br/>
              <%= tab %>
              <a href="#cartAndExport">Cart and Export Functionality</a>
              <br/>
              <a href="#valueSetsTab">Value Sets Tab</a>
              <br/>
              <%= tab %>
              <a href="#valueSetsSearchBox">Using the Search Box</a>
              <br/>
              <%= tab %>
              <a href="#conceptDetails">Concept Details</a>
              <br/>
              <a href="#mappingsTab">Mappings Tab</a>
              <br/>
              <%= tab %>
              <a href="#mappingSearchBox">Using the Search Box</a>
              <br/>
              
-->              
              
            </p>        
         </div>


<%------------------------------------------------------------------%>
<table>
<tr><td>
<div class="textbody">
  <table width="920px" cellpadding="0" cellspacing="0" border="0" role='presentation'>
    <tr>
      <td>
        <h2>
          <a name="introuction">Introduction</a>
        </h2>
      </td>
    </tr>
  </table>
  <p>
    The NCI Mapping Tool is developed for supporting the development of mappings from 
    a collection of source terms to a target terminology using SPARQL queries and heuristic rules.
  </p>
</div>
</td></tr>
<tr><td>&nbsp;</td></tr>
<%------------------------------------------------------------------%>  
<tr><td>
<div class="textbody">
  <table width="920px" cellpadding="0" cellspacing="0" border="0" role='presentation'>
    <tr>
      <td>
        <h2>
          <a name="homePage">NCI Mapping Tool Home Page</a>
        </h2>
      </td>
    </tr>
  </table>
  <p>
    The Home page shows a list of supported terminologies. Select a target terminology for mapping by
    clicking on the radio button to the left of the corresponding terminoloy label and click on the <b>New</b> button to 
    construct a mapping using the selected target terminology.
  </p>
</div>
</td></tr>
<tr><td>&nbsp;</td></tr>
<%------------------------------------------------------------------%>  
<tr><td>
<div class="textbody">
  <table width="920px" cellpadding="0" cellspacing="0" border="0" role='presentation'>
    <tr>
      <td>
        <h2>
          <a name="sourceTerms">Enter Source Terms</a>
        </h2>
      </td>
    </tr>
  </table>
  <p>
    You can enter source term data by clicking on the <b>Upload</b> button.
    The input file can be a list of terms with each line containin a term, or 
    it can be a list of code and term pairs with each pair of code and term seperated by a tab character.
    Alternatively, you can enter soure term data directly to the text area using cut and paste.
    Press the <b>Continue</b> button to start the automated mapping of your terms to the target terminology.
    Use the <b>Reset</b> button to reset the text field.
  </p>
</div>
</td></tr>
<tr><td>&nbsp;</td></tr>
<%------------------------------------------------------------------%> 
<tr><td>
<div class="textbody">
  <table width="920px" cellpadding="0" cellspacing="0" border="0" role='presentation'>
    <tr>
      <td>
        <h2>
          <a name="matchedConcepts">Matched Concepts</a>
        </h2>
      </td>
    </tr>
  </table>
  <p>
The Matched Concepts page contains a list of mapping entries. )
Each mapping entry contains source code (if available), source label (i.e., source term), target code, and target label (i.e., the "preferred name" of the target
cocept) if a match is found by the mapping tool.
Otherwise, the corresponding row will only contain the source code and source label.
You may remove some mapping entries by first checking the checkboxes corresponding to each mapping entry (i.e., row) 
and then press the <b>Delete</b> button.
Click on the <b>Continue</b> button to find out terms that have not been mapped to any concept in the target terminology.
You may export the mapping results to a CSV formatted file by clicking on the <i>Export Excel</i> link.
  </p>
</div>
</td></tr>
<tr><td>&nbsp;</td></tr>
<%------------------------------------------------------------------%> 
<tr><td>
<div class="textbody">
  <table width="920px" cellpadding="0" cellspacing="0" border="0" role='presentation'>
    <tr>
      <td>
        <h2>
          <a name="unmatchedTerms">Unmatched Terms</a>
        </h2>
      </td>
    </tr>
  </table>
  <p>
The Unmatched Terms page contains a table showing the list of terms that have not been mapped to any concept in the target terminology.
Each row in the table contains a source code (if available), and a source label.
You may click on the label of any unmatched term to search for matched concepts manually. 
You may export the unmatched terms to a CSV formatted file by clicking on the <i>Export Excel</i> link.
  </p>
</div>
</td></tr>
<tr><td>&nbsp;</td></tr>
<%------------------------------------------------------------------%> 
<tr><td>
<div class="textbody">
  <table width="920px" cellpadding="0" cellspacing="0" border="0" role='presentation'>
    <tr>
      <td>
        <h2>
          <a name="manualMapping">Manual Mapping</a>
        </h2>
      </td>
    </tr>
  </table>
  <p>
The Manual Mapping page provides an interface for you to search for possible concepts in the target terminology that
match with the specific term.
Enter words in the text field and press <b>Search</b> to perform the search.
Check the checkboxes corresponding to the concepts that match well with the source term and press 
<b>Save</b> to update the mapping results.
  </p>
</div>
</td></tr>
<%------------------------------------------------------------------%> 
</table>
        
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
