<h:form> 
<%
String warning_msg = (String) request.getSession().getAttribute("msg");
request.getSession().removeAttribute("msg");
%>
<% if (warning_msg != null) { %>
<p class="textbodyred">&nbsp;<%= warning_msg %></p>
<%
}
%>

Please enter or upload a list of terms (and optionally the code of each term) in the text field below. Press <b>Continue</b> to proceed.
<p></p>
<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
<%
String ng = (String) request.getSession().getAttribute("ng");
String msg = (String) request.getSession().getAttribute("msg");
if (msg != null) {
%> 
<table>
	<tr><td class="textbodyred">
	<%=msg%>
	</td></tr> 
</table>	
<%  
}
String data = (String) request.getSession().getAttribute("data");
if (data == null) {
    data = "";
} else {
    request.getSession().removeAttribute("data");
}
%>
    <tr align="top">
      <td class="textbody">
      <textarea name="data" cols="80" rows=15 tabindex="3"><%=data%></textarea>
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