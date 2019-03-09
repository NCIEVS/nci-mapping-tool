<h:form> 
<%
String mapping_name = (String) request.getSession().getAttribute("mapping_name");
if (mapping_name == null) {
    mapping_name = "";
} 
String msg = (String) request.getSession().getAttribute("msg");
request.getSession().removeAttribute("msg");
String ng = (String) request.getSession().getAttribute("ng");
String data = (String) request.getSession().getAttribute("data");
if (data == null) {
    data = "";
} else {
    request.getSession().removeAttribute("data");
}
%>
<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
<%  
String mapping = "";
if (mapping_name != null && mapping_name.length() > 0) {
    mapping = mapping_name;
    int m = mapping_name.lastIndexOf(".");
    if (m != -1) {
        mapping = mapping_name.substring(0, m);
    }
}
if (mapping.length() > 0) {
%>  
<tr>
<td align="left" class="texttitle-blue">Mapping:&nbsp;<%=mapping%></td>
</tr>
<tr><td>&nbsp;</td><tr>
<%
}
%>        
<%  
if (msg != null) {
%>    
	<tr><td class="textbodyred">&nbsp;<%= msg %>
	</td></tr>  
<%
}
%>
<tr>
<td class="textbody">
Please specifiy a mapping name, then enter or upload a list of terms (and optionally the code of each term) in the text field below. Press <b>Continue</b> to proceed.
</td> 
</tr> 
    <tr align="top">
      <td class="textbody">
      <textarea name="data" cols="100" rows=15 tabindex="3"><%=data%></textarea>
      </td>
     </tr>	 

    <tr align="top">
      <td class="textbody">
      &nbsp;&nbsp;
      </td>
     </tr>  
</table>
<p></p>
<table>
	  <tr><td>
	    <h:commandButton id="upload" value="upload" action="#{mappingSessionBean.uploadDataAction}"
	      image="/images/upload.gif"
	      alt="Upload mapping source data from a file"
	      tabindex="2">
	    </h:commandButton> 	  
	    &nbsp;
	    
	    <h:commandButton id="continue" value="continue" action="#{mappingSessionBean.mappingAction}"
	      image="/images/continue.gif"
	      alt="Submit"
	      tabindex="2">
	    </h:commandButton>
	    
	    &nbsp;&nbsp;

          <input type="image" src="/ncimappingtool/images/reset.gif" onclick="javascript:reset();"/>
    	    
	  </td>	  
	  </tr>	    
  
</table>
<input type="hidden" name="ng" id="ng" value="<%=ng%>">
</h:form>   