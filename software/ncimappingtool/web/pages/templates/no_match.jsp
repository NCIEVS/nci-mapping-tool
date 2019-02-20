<h:form>  
<table>

<%
Mapping mapping = (Mapping) request.getSession().getAttribute("mapping");
List<gov.nih.nci.evs.mapping.bean.MappingEntry> entries = mapping.getEntries();

System.out.println("entries: " + entries.size());
Vector nomatch_vec = new Vector();
request.getSession().removeAttribute("partial_matches"); 

for (int i=0; i<entries.size(); i++) {
    gov.nih.nci.evs.mapping.bean.MappingEntry entry = (gov.nih.nci.evs.mapping.bean.MappingEntry) entries.get(i);
    String targetCode = entry.getTargetCode();
    if (targetCode.compareTo("") == 0) {
        nomatch_vec.add(entry); 
    }
}

String no_match_cnt_str = "" + new Integer(nomatch_vec.size()).intValue();
System.out.println("(*) no_match_cnt_str: " + no_match_cnt_str);
String codingSchemeName = (String) request.getSession().getAttribute("codingSchemeName");

%>

<tr>
  <td align="left" class="texttitle-blue">Count of no matches using <%=codingSchemeName%>:&nbsp;<%=no_match_cnt_str%></td>
  <td class="textbody" align="right">
    <h:commandLink
	value="Export Excel"
	action="#{mappingSessionBean.exportNoMatchToExcelAction}"
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
</table>

<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
   <th class="datatable_960Header" width="120px" scope="col" align="left">Source Code</th>
   <th class="datatable_960Header" width="350px" scope="col" align="left">Source Label</th>
<%

for (int i=0; i<nomatch_vec.size(); i++) {
    gov.nih.nci.evs.mapping.bean.MappingEntry entry = (gov.nih.nci.evs.mapping.bean.MappingEntry) nomatch_vec.elementAt(i);
    String sourceCode = entry.getSourceCode();
    String sourceTerm = entry.getSourceTerm();
    String targetCode = entry.getTargetCode();
    String targetLabel = entry.getTargetLabel();
    
    String t = sourceTerm;
    t = t.replaceAll(" ", "%20");
    
    String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight";

%>
    <tr class="<%=rowColor%>">
      <td width="120px" class="textbody"><%=sourceCode%></td>
      <td width="350px" class="textbody">
      <a href="<%= request.getContextPath()%>/pages/sparql_search.jsf?term=<%=t%>">
      <%=sourceTerm%>
      </a>       
      </td>
    </tr>
<%
}
%>
 
</table>
<p></p>

<!--
<table>
<tr>
         <td>
	    <h:commandButton id="back" value="back" action="#{mappingSessionBean.backAction}"
	      image="/images/back.gif"
	      alt="Back"
	      tabindex="1">
	    </h:commandButton>
	 </td>         
</tr>
</table>
-->

</h:form>   