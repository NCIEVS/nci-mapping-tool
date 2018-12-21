<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-mapping-tool/LICENSE.txt for details.
L--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="gov.nih.nci.evs.browser.properties.*" %>

<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<html>
 <body>
 
<%
String _mode_of_operation = DataUtils.getModeOfOperation();

if (_mode_of_operation == null) {
    System.out.println("_mode_of_operation == null");
}


if (_mode_of_operation != null && _mode_of_operation.compareTo(NCImtBrowserProperties.INTERACTIVE_MODE_OF_OPERATION) == 0) {

System.out.println("INTERACTIVE_MODE_OF_OPERATION -- forwarding to home.jsf");


%>
  <jsp:forward page="/pages/home.jsf" />
<%  
} else {
%>
  <jsp:forward page="/pages/start.jsf" />
<% 
}
%>  
 </body>
</html>
