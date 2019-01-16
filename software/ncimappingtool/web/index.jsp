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
String _mode_of_operation = (String) request.getParameter("mode");
if (_mode_of_operation == null) {
    _mode_of_operation = NCImtBrowserProperties.getModeOfOperation();
}
String mode = _mode_of_operation;
NCImtBrowserProperties.setModeOfOperation(mode);
if (mode != null && mode.compareTo(NCImtBrowserProperties.INTERACTIVE_MODE_OF_OPERATION) == 0) {
%>
  <jsp:forward page="/pages/home.jsf" />
<%  
} else if (_mode_of_operation != null && _mode_of_operation.compareTo(NCImtBrowserProperties.BATCH_MODE_OF_OPERATION) == 0) {
%>
  <jsp:forward page="/pages/start.jsf" />
<% 
} else {
%>
  <jsp:forward page="/pages/home_alt.jsf" />
<% 
}
%>  
 </body>
</html>
