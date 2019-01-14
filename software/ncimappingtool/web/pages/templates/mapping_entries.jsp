<%@ page import="gov.nih.nci.evs.restapi.ui.*"%>
<%@ page import="gov.nih.nci.evs.browser.bean.*"%>

<%
String msg = (String) request.getSession().getAttribute("msg");
String codingSchemeName = (String) request.getSession().getAttribute("codingSchemeName");
System.out.println("codingSchemeName: " + codingSchemeName);
%>

<h:form>  
<table>
<tr>
  <td align="left" class="texttitle-blue">Results of mapping using <%=codingSchemeName%>&nbsp;</td>
  <td class="textbody" align="right">
    <h:commandLink
	value="Export Excel"
	action="#{mappingSessionBean.exportToExcelAction}"
	styleClass="texttitle-blue-small"
	title="Export Mapping in MS Excel format"
    />
    <a
	title="Download Plugin Microsoft Excel Viewer"
	href="https://products.office.com/en-US/excel?legRedir=true&CorrelationId=1229dc2e-5ff3-4e3b-adc8-2b6f59e21be4"
	target="_blank">

      <img
	  src="/ncimappingtool/images/link_xls.gif"
	  width="16"
	  height="16"
	  border="0"
	  alt="Download Plugin Microsoft Excel Viewer"
      />
    </a>
   </td>                                 
</tr>


<%  
if (msg != null) {
%>    
	<tr><td class="textbodyred">
	<%=msg%>
	</td></tr>  
<%  
}
%> 	
	
	
</table>

<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
   <th width="5px" scope="col" align="left"></th>
   <th class="datatable_960Header" width="120px" scope="col" align="left">Source Code</th>
   <th class="datatable_960Header" width="350px" scope="col" align="left">Source Label</th>
   <th class="datatable_960Header" width="120px" scope="col" align="left">Target Code</th>
   <th class="datatable_960Header" width="350px" scope="col" align="left">Target Label</th>
<%
gov.nih.nci.evs.mapping.bean.Mapping mapping = null;
String mapping_data = (String) request.getSession().getAttribute("mapping_data");
if (mapping_data != null) {
    //System.out.println(mapping_data);
    System.out.println("************************** creating mapping ...");
    mapping = MappingSessionBean.createMapping(mapping_data); 
    System.out.println("************************** Done creating mapping ..." + mapping.getEntries().size());
    request.getSession().removeAttribute("mapping_data");
    request.getSession().setAttribute("mapping", mapping);  
} else {
    mapping = (gov.nih.nci.evs.mapping.bean.Mapping) request.getSession().getAttribute("mapping");    
}

List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = mapping.getEntries();
String ng = (String) request.getSession().getAttribute("ng");
System.out.println("ng: " + ng);
System.out.println("entries.size(): " + entries.size());

for (int i=0; i<entries.size(); i++) {
    gov.nih.nci.evs.mapping.bean.MappingEntry entry = entries.get(i);
    Integer row_id = new Integer(i);
    String rowid = row_id.toString();
    String sourceCode = entry.getSourceCode();
    String sourceTerm = entry.getSourceTerm();
    String targetCode = entry.getTargetCode();
    String targetLabel = entry.getTargetLabel();
    String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight";
    String jsp = "ncimappingtool/pages/concept_details.jsf";
    String hyperlink_str = gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, ng, targetCode, targetCode);
    
    if (codingSchemeName != null && codingSchemeName.compareTo("NCI_Thesaurus") != 0) {
        jsp = "ncimappingtool/pages/concept_details_others.jsf";
        hyperlink_str = gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, ng, targetCode, targetCode);
    }

%>
    <tr class="<%=rowColor%>">
      <td><input type="checkbox" name="rowids" value="<%=rowid%>"></td>
      <td width="120px" class="textbody"><%=sourceCode%></td>
      <td width="350px" class="textbody"><%=sourceTerm%></td>
      <td width="120px" class="textbody"><%=hyperlink_str%></td>
      <td width="350px" class="textbody"><%=targetLabel%></td>
    </tr>
<%
}
%>
</table>
<p></p>
<table>
	  <tr><td>
	    <h:commandButton id="continue" value="continue" action="#{mappingSessionBean.manualMappingAction}"
	      image="/images/continue.gif"
	      alt="Submit"
	      tabindex="2">
	    </h:commandButton>
	    &nbsp;
	    
	    <h:commandButton id="delete" value="delete" action="#{mappingSessionBean.deleteAction}"
	      image="/images/delete.gif"
	      alt="Delete"
	      tabindex="1">
	    </h:commandButton>
	    
	    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    
	    <h:commandButton id="upload" value="upload" action="#{mappingSessionBean.uploadMappingAction}"
	      image="/images/upload.gif"
	      alt="Upload mapping results from a file"
	      tabindex="2">
	    </h:commandButton> 	  
	    		    
	    
	  </td>
	  </tr>	    
</table>  


</h:form>   