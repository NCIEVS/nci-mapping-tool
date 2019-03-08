<%@ page import="gov.nih.nci.evs.restapi.ui.*"%>
<%@ page import="gov.nih.nci.evs.restapi.util.*"%>

<%
String term = (String) request.getParameter("term");
if (term == null) {
    term = (String) request.getSession().getAttribute("term");
}
String searchstring = (String) request.getParameter("searchstring");
if (searchstring == null) {
    searchstring = (String) request.getSession().getAttribute("searchstring");
}

if (searchstring == null) {
    searchstring = "";
}


String named_graph = (String) request.getSession().getAttribute("named_graph");
if (named_graph == null) {
    named_graph = (String) request.getParameter("named_graph");
}
if (named_graph == null) {
    named_graph = (String) request.getParameter("ng");
}
if (named_graph == null) {
	named_graph = gov.nih.nci.evs.browser.common.Constants.NCIT_NG;
}

Vector partial_matches = (Vector) request.getSession().getAttribute("partial_matches");  
//request.getSession().removeAttribute("partial_matches"); 
String msg = (String) request.getSession().getAttribute("msg");

%>
<h:form>  
<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
<tr>
<td align="left" class="texttitle-blue">Term:&nbsp;<%=term%></td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
<tr>
<td align="left" class="textbody">Please enter a search string in the text field below and press <b>Search</b> to proceed.</td>
</tr>
     <tr align="top">
         <td class="textbody">
            <label for="term">Search String: </label>
            <input type="text" name="searchstring" value="<%=searchstring%>" size="70" tabindex="3"></input>
         </td>
         
<%  
if (msg != null) {
%>    
	<tr><td class="textbodyred">
	<%=msg%>
	</td></tr>  
<%	
} else if (partial_matches != null && partial_matches.size() > 0) {    
%> 

<tr><td>
<br></br>
</td></tr>

<tr><td>
<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
   <th class="datatable_960Header" width="15px" scope="col" align="left"></th>
   <th class="datatable_960Header" width="120px" scope="col" align="left">Target Code</th>
   <th class="datatable_960Header" width="350px" scope="col" align="left">Target Label</th>
<%         
for (int i=0; i<partial_matches.size(); i++) {
    String line = (String) partial_matches.elementAt(i);
    Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(line, '|');
    String targetCode = (String) u.elementAt(0);
    String targetLabel = (String) u.elementAt(1);
    String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight";
    String jsp = "ncimappingtool/pages/concept_details.jsf";
    //http://localhost:8080/ncimappingtool/pages/concept_details.jsf?ng=http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl&code=C12345

    String hyperlink_str = gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, named_graph, targetCode, targetCode);

%>
    <tr class="<%=rowColor%>">
      <td><input type="checkbox" name="concepts" value="<%=targetCode%>"></td>
      <td width="120px" class="textbody">
      <%=hyperlink_str%>
      </td>
      <td width="350px" class="textbody"><%=targetLabel%></td>
    </tr>
<%
}
%>


</table>
</td></tr>
<tr>
<td>&nbsp;</td> 
</tr>
<tr>
<td class="textbody">Please select matched concepts and press <b>Save</b> to proceed.</td> 
</tr> 
<tr><td>
<br></br>
</td></tr>
<%         
}   
%> 
</table>
<p></p>
<table>
<tr>
         <td>
	    <h:commandButton id="search" value="search" action="#{mappingSessionBean.searchAction}"
	      image="/images/search.gif"
	      alt="Search"
	      tabindex="1">
	    </h:commandButton>
	    
	    &nbsp;
	    
		<%         
		if (partial_matches != null && partial_matches.size() > 0) {      
		%>	    
	    &nbsp;
	    <h:commandButton id="save" value="save" action="#{mappingSessionBean.saveAction}"
	      image="/images/save.gif"
	      alt="Submit"
	      tabindex="1">
	    </h:commandButton>	 
	    
	    &nbsp;
	    
		<%         
		}     
		%> 	    

	    <h:commandButton id="back" value="back" action="#{mappingSessionBean.backAction}"
	      image="/images/back.gif"
	      alt="Back"
	      tabindex="1">
	    </h:commandButton>
  	    
	    
	 </td>         
</tr>
</table>
<input type="hidden" name="term" id="term" value="<%=term%>">
<input type="hidden" name="named_graph" id="named_graph" value="<%=named_graph%>">
</h:form>   