<h:form>
<%
String content = (String) request.getSession().getAttribute("matched_concepts");
%>
<%=content%>
</h:form>   