<h:form> 
<%
String mapping_name = (String) request.getSession().getAttribute("mapping_name");
if (mapping_name == null) {
    mapping_name = "";
} 
String mapping = "";
if (mapping_name != null && mapping_name.length() > 0) {
    mapping = mapping_name;
    int m = mapping_name.lastIndexOf(".");
    if (m != -1) {
        mapping = mapping_name.substring(0, m);
    }
}
System.out.println("mapping_data.jsp: mapping = " + mapping);

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
if (mapping.length() > 0) {
%>  
<tr>
<td align="left" class="texttitle-blue">Mapping:&nbsp;<%=mapping%></td>
</tr>
<tr><td>&nbsp;</td><tr>
<%
}
%>   
Please enter or upload a list of terms (or paris of code and term separated by a tab character) 
in the text area below. Press <b>Continue</b> to proceed.</b><br></br>
<%  
if (msg != null) {
%>    
	<tr><td class="textbodyred">&nbsp;<%= msg %>
	</td></tr>  
<%
}
%>

    <tr align="top">
      <td class="textbody">
      <textarea name="data" cols="112" rows=15 tabindex="3"><%=data%></textarea>
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