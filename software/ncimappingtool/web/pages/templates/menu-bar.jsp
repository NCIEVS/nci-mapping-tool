            
<table border="0" width="920px" style="margin:0px 0px 0px 0px;">
	<tr class="global-nav"> 
		<td width="25%"></td>                       
		<td align="right" width="75%">
		
<%

String focus_code = (String) request.getParameter("code");
if (focus_code == null) {
    focus_code = (String) request.getSession().getAttribute("code");
}
String namedGraph = (String) request.getParameter("ng");
if (namedGraph == null) {
    namedGraph = (String) request.getSession().getAttribute("ng");
}

if (namedGraph == null) {
    namedGraph = NCImtBrowserProperties.get_default_named_graph();
}

String scheme = prop_dictionary;
%>  
			<a href="#" onClick="javascript:popup_window('<%=request.getContextPath() %>/pages/hierarchy.jsf?ng=<%=namedGraph%>&scheme=<%=scheme%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" title="View Hierarchy" >
         		View Hierarchy</a> 
                |
			<a href="#" onClick="javascript:popup_window('<%=request.getContextPath() %>/pages/hierarchy.jsf?ng=<%=namedGraph%>&scheme=<%=scheme%>&code=<%=focus_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" title="View In Hierarchy" >
         		View In Hierarchy</a> 
                |
			<a href="#" onclick="javascript:popup_window('<%=request.getContextPath() %>/ajax?action=view_graph&ng=<%=namedGraph%>&code=<%=focus_code%>&type=ALL', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="12"
			title="This link displays a graph that recapitulates some information in the Relationships tab in a visual format.">
			View Graph</a>  
		|
		        <a href="<%=request.getContextPath() %>/pages/sparql.jsf?ng=<%=namedGraph%>" title="Sparql Query Endpoint.">Sparql Query</a>    		
		|

<%
Mapping mapping = (Mapping) request.getSession().getAttribute("mapping");
if (mapping != null) {
%>

<a href="<%=request.getContextPath() %>/pages/auto_mapping_results.jsf" title="Back to Mapping Results">Mapping Results</a>
|		
<%
}
%>
			<a href="#" onclick="javascript:popup_window('http://ncitermform.nci.nih.gov/ncitermform/?dictionary=NCI Thesaurus&code=<%=focus_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="12"
			title="Term Suggestion.">
			Suggest Changes</a>  
			
			
		</td>
	</tr>
</table>
