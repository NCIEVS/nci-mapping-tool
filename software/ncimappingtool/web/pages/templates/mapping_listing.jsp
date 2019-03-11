<h:form>
<%
String warning_msg = (String) request.getSession().getAttribute("msg");
request.getSession().removeAttribute("msg");

mapping_hashmap = (HashMap) request.getSession().getAttribute("mapping_hashmap");
%>
<% if (warning_msg != null) { %>
<p class="textbodyred">&nbsp;<%= warning_msg %></p>
<%
}
%>

<%
if (mapping_hashmap == null || mapping_hashmap.keySet().size() == 0) {
%>
<p>
No mapping is available.
</p>
<%
} else {
%>
Please select a mappping then press <b>Continue</b> to continue editing the selected mapping,
or press <b>Delete</b> to remove the selected mapping permanently.
<p></p>
<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
<%
String indent = "&nbsp;&nbsp;";
Iterator it = mapping_hashmap.keySet().iterator();
int lcv = 0;
while (it.hasNext()) {
    lcv++;
    String mapping_name = (String) it.next();
    MappingTask mt = (MappingTask) mapping_hashmap.get(mapping_name);
    String cs_name = mt.getTargetCodingScheme();
    String display_label = mapping_name + " (" + cs_name + ")";
  %>

    <tr align="top">
      <td width="25px"></td>
      <td class="textbody">
      
	<%
	String checkedStr = "";
	if (lcv == 1) {
	    checkedStr = "checked";
	}
	%>
	<%= indent %>
	<input
	    type="radio"
	    name="mapping_name"
	    value="<%=mapping_name%>"
	    <%=checkedStr%>
	/>
	<%=display_label%>
      </td>
   </tr>
<%
}
%>

<tr><td>&nbsp;</td></tr>    
	  <tr><td>
	    <h:commandButton id="edit" value="edit" action="#{mappingSessionBean.editMappingAction}"
	      image="/images/continue.gif"
	      alt="Submit"
	      tabindex="2">
	    </h:commandButton>
            &nbsp;
  	    <h:commandButton id="delete" value="delete" action="#{mappingSessionBean.deleteMappingAction}"
  	      image="/images/delete.gif"
  	      alt="Upload mapping results from a file"
  	      tabindex="2">
	    </h:commandButton> 	
	  </td>
	  </tr>	    
</table>
<%
} 
%>
</h:form>   