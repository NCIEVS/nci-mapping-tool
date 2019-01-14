<h:form>  
Please select a terminology, then press the <b>New</b> to start, 
or press the <b>Upload</b> button to load an existing mapping file.

<table border="0" cellpadding="0" cellspacing="0" role='presentation'>
<%
dm = (DataManager) request.getSession().getAttribute("dm");
if (dm == null) {
	String serviceUrl = NCImtProperties._service_url;
	System.out.println("vocabulary_listing serviceUrl: " + serviceUrl);
	
	String data_directory = NCImtProperties._data_directory;
	System.out.println("vocabulary_listing _data_directory: " + data_directory);
	
	dm = new DataManager(serviceUrl, data_directory);
	Vector terminologies = dm.getTerminologies();
	for (int i=0; i<terminologies.size(); i++) {
	    Terminology terminology = (Terminology) terminologies.elementAt(i);
	    cs_data.add(terminology.getCodingSchemeName() + "|" + terminology.getCodingSchemeVersion() + "|" + terminology.getNamedGraph());
	}
	cs_data = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(cs_data);
	request.getSession().setAttribute("cs_data", cs_data);
	request.getSession().setAttribute("dm", dm);
}

String codingSchemeName = (String) request.getSession().getAttribute("codingSchemeName");
if (codingSchemeName == null) {
    codingSchemeName = "NCI_Thesaurus";
}

String indent = "&nbsp;&nbsp;";
cs_data = (Vector) request.getSession().getAttribute("cs_data");
cs_data = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(cs_data);

  HashSet hset = new HashSet();
  for (int j=0; j<cs_data.size(); j++) {
	String line = (String) cs_data.elementAt(j);
	Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(line, '|');
	String scheme = (String) u.elementAt(0);
	String version = (String) u.elementAt(1);
	String ng = (String) u.elementAt(2); 
	String display_label = scheme + "&nbsp;(" + version + ")";

	if (!hset.contains(display_label)) {
	    hset.add(display_label);
	boolean isMapping = false;
	boolean checked = false;
	if (scheme.compareTo(codingSchemeName) == 0) {
	    checked = true;
	}
	
	if (!isMapping) {
  %>

    <tr align="top">
      <td width="25px"></td>
      <td class="textbody">
      
	<%
	//boolean checked = false;
	String checkedStr = checked ? "checked" : "";
	%>

	<%= indent %>
	<input
	    type="radio"
	    name="ng"
	    value="<%=ng%>"
	    <%=checkedStr%>
	/>
	<%
	
	String href = request.getContextPath() + "/pages/vocabulary_home.jsf" + "?ng=" + ng;
	if (scheme.compareTo("NCI_Thesaurus") == 0) {
	    href = request.getContextPath() + "/pages/ncit_home.jsf?ng=" + ng;
	}

	%>
	<a href="<%=href%>"><%= display_label %></a>
      </td>
   </tr>

  <%}}}%>

<tr><td>&nbsp;</td></tr>    
	  <tr><td>
	    <h:commandButton id="continue" value="continue" action="#{mappingSessionBean.selectTerminologyAction}"
	      image="/images/new.gif"
	      alt="Submit"
	      tabindex="2">
	    </h:commandButton>
            &nbsp;
  
  	    <h:commandButton id="upload" value="upload" action="#{mappingSessionBean.uploadMappingAction}"
  	      image="/images/upload.gif"
  	      alt="Upload mapping results from a file"
  	      tabindex="2">
	    </h:commandButton> 	  

	  </td>
	  </tr>	    
</table>

</h:form>   