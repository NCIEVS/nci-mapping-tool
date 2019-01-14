<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>

<%@ page import="gov.nih.nci.evs.restapi.util.*"%>
<%@ page import="gov.nih.nci.evs.restapi.common.*"%>
<%@ page import="gov.nih.nci.evs.restapi.ui.*"%>

<%@ page import="gov.nih.nci.evs.mapping.util.*"%>
<%@ page import="gov.nih.nci.evs.mapping.bean.*"%>
<%@ page import="gov.nih.nci.evs.mapping.common.*"%>

<%@ page contentType="text/html;charset=UTF-8"%>

        <%
        
	String named_graph = (String) request.getParameter("ng");    
	String concept_code = (String) request.getParameter("code");
	String jsp = "ncimappingtool/pages/concept_details.jsf";
                
        gov.nih.nci.evs.restapi.util.VIHUtils vih_utils = new gov.nih.nci.evs.restapi.util.VIHUtils();
        OWLSPARQLUtils utils = null;
        String entityDescription = null;
        utils = new OWLSPARQLUtils(serviceUrl, null, null);
        
	Vector v = utils.getLabelByCode(named_graph, concept_code);
	String line = (String) v.elementAt(0);
	
	String label = new ParserUtils().getValue(line);        
        
        entityDescription = label; 
      
        
	gov.nih.nci.evs.restapi.util.FormatUtils formatUtils = new gov.nih.nci.evs.restapi.util.FormatUtils();	
	String description = null;
	Vector roles = null;
	Vector concepts = null;
	gov.nih.nci.evs.restapi.util.ParserUtils parser = new gov.nih.nci.evs.restapi.util.ParserUtils();
	
	
	%>
	
<%@ include file="/pages/templates/content-header.jsp"%></div>	
<%@ include file="/pages/templates/menu-bar.jsp"%>


<%
   
  String message = (String) request.getSession().getAttribute("message");
  request.getSession().removeAttribute("message");
  if (message != null) {
%>  
      <p class="textbodyred"><%=message%></p>
<%      
  } 
%>

<center>
<h2 class="texttitle-blue"><%=entityDescription%> (Code <%=concept_code%>)</h2>

</center>
<h3 class="texttitle-blue-small">Terms & Properties</h3>

      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
    
        
        HashMap prop_map = utils.getPropertyHashMapByCode(named_graph, concept_code);
        
        String firstColumnHeading = "Name";
        String secondColumnHeading = "Value";
        int firstPercentColumnWidth = 30;
        int secondPercentColumnWidth = 70;        
        
        
        String prop_table = gov.nih.nci.evs.restapi.util.FormatUtils.formatTable(prop_map, firstColumnHeading, secondColumnHeading,
        	firstPercentColumnWidth, secondPercentColumnWidth, true);
        	
        	
        %>

        <%=prop_table%>
        
      </div>

<br></br>       
<h3 class="texttitle-blue-small">Synonyms</h3>

      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
        Vector synonyms = utils.getSynonyms(named_graph, concept_code);
        
        List list = parser.getSynonyms(synonyms);


        
%>

      <table class="datatable_960" border="0" width="100%">
        <tr>
          <th class="dataTableHeader" scope="col" align="left">Term</th>
          <th class="dataTableHeader" scope="col" align="left">Source</th>
          <th class="dataTableHeader" scope="col" align="left">Type</th>
          <th class="dataTableHeader" scope="col" align="left">Code</th>
        </tr>  
<%  

        if (list != null && list.size() > 0) {
        
        System.out.println(list.size());   
        
		for (int i=0; i<list.size(); i++) {
		    gov.nih.nci.evs.restapi.bean.Synonym syn = (gov.nih.nci.evs.restapi.bean.Synonym) list.get(i);
			
			String term_name = syn.getTermName();
			String term_group = syn.getTermGroup();
			if (term_group == null) term_group = "";
			String term_source = syn.getTermSource();
			if (term_source == null) term_source = "";
			String term_code = syn.getSourceCode();
			if (term_code == null) term_code = "";
       
                        String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight";
%> 
			<tr class="<%=rowColor%>">
			  <td class="datacelltext" scope="row"><%=term_name%></td>
			  <td class="datacelltext"><%=term_source%></td>
			  <td class="datacelltext"><%=term_group%></td>
			  <td class="datacelltext"><%=term_code%></td>
			</tr>
    <%
                }
        }
    %>
    </table>
    </div>
      
      
<br></br>       
<h3 class="texttitle-blue-small">Relationships with other <%=prop_dictionary%> Concepts</h3>

      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
        concepts = new Vector();
	Vector superconcept_vec = utils.getSuperclassesByCode(named_graph, concept_code); 
	
	if (superconcept_vec != null && superconcept_vec.size() > 0) {
		int n1 = superconcept_vec.size()/2;
		for (int k1 = 0; k1<n1; k1++) {
		    int i0 = k1*2;
		    int i1 = k1*2+1;
		    String s0 = parser.getValue((String) superconcept_vec.elementAt(i0));
		    String s1 = parser.getValue((String) superconcept_vec.elementAt(i1));
		    concepts.add(s0 + "|" + s1);
		}
	}
        if (concepts != null && concepts.size() > 0) {
        
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_SUPERCONCEPT, false);
        
        firstColumnHeading = "Name";
        secondColumnHeading = "Code";
        firstPercentColumnWidth = 30;
        secondPercentColumnWidth = 70; 
        
        Vector u = new Vector();
        for (int i=0; i<concepts.size(); i++) {
            String t = (String) concepts.elementAt(i);
            
            Vector u2 = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
            String s1 = (String) u2.elementAt(0);
            String s2 = (String) u2.elementAt(1);
            String s = s1 + "|" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, named_graph, s2, s2);
            u.add(s);
        }
        
        prop_table = gov.nih.nci.evs.restapi.util.FormatUtils.formatTable(u, firstColumnHeading, secondColumnHeading,
        	firstPercentColumnWidth, secondPercentColumnWidth);
        %>
        <%=description%>
        <%=prop_table%>
        <%
        } else {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_SUPERCONCEPT, true);
        %>
        <%=description%>
      <%  
      }
      %>
      </div>

<br></br> 
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
        
        concepts = new Vector();
        Vector subconcept_vec = utils.getSubclassesByCode(named_graph, concept_code); 				    
	if (subconcept_vec != null && subconcept_vec.size() > 0) {
		int n2 = subconcept_vec.size()/2;
		for (int k1 = 0; k1<n2; k1++) {
		    int i0 = k1*2;
		    int i1 = k1*2+1;
		    String s0 = parser.getValue((String) subconcept_vec.elementAt(i0));
		    String s1 = parser.getValue((String) subconcept_vec.elementAt(i1));
		    concepts.add(s0 + "|" + s1);
		}
	}
        
        if (concepts != null && concepts.size() > 0) {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_SUBCONCEPT, false);
        
        firstColumnHeading = "Name";
        secondColumnHeading = "Code";
        firstPercentColumnWidth = 30;
        secondPercentColumnWidth = 70;        
        
        Vector u = new Vector();
        for (int i=0; i<concepts.size(); i++) {
            String t = (String) concepts.elementAt(i);
            Vector u2 = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
            String s1 = (String) u2.elementAt(0);
            String s2 = (String) u2.elementAt(1);
            //String s = s1 + "|" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(s2);
            String s = s1 + "|" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, named_graph, s2, s2);
            u.add(s);
        }        
        
        prop_table = gov.nih.nci.evs.restapi.util.FormatUtils.formatTable(u, firstColumnHeading, secondColumnHeading,
        	firstPercentColumnWidth, secondPercentColumnWidth);
        %>
        <%=description%>
        <%=prop_table%>
        <%
        } else {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_SUBCONCEPT, true);
        %>
        <%=description%>
      <%  
      }
      %>
      </div>
      
<br></br> 
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
        
        roles = new Vector();
        Vector role_vec = utils.getOutboundRolesByCode(named_graph, concept_code); 
	if (role_vec != null && role_vec.size() > 0) {
		int n3 = role_vec.size()/3;
		for (int k1 = 0; k1<n3; k1++) {
		    int i0 = k1*3;
		    int i1 = k1*3+1;
		    int i2 = k1*3+2;
		    String s0 = parser.getValue((String) role_vec.elementAt(i0));
		    String s1 = parser.getValue((String) role_vec.elementAt(i1));
		    String s2 = parser.getValue((String) role_vec.elementAt(i2));
		    roles.add(s0 + "|" + s1 + "|" + s2);
		}
	}       
        
        if (roles != null && roles.size() > 0) {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_ROLE, false);

        Vector u = new Vector();
        for (int i=0; i<roles.size(); i++) {
            String t = (String) roles.elementAt(i);
            Vector u2 = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
            String s1 = (String) u2.elementAt(0);
            String s2 = (String) u2.elementAt(1);
            String s3 = (String) u2.elementAt(2);
            //String s = s1 + "|" + s2 + " (" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(s3) + ")";
            String s = s1 + "|" + s2 + " (" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, named_graph, s3, s3) + ")";
            u.add(s);
        }
        
        firstColumnHeading = "Role Name";
        secondColumnHeading = "Target Concept";
        firstPercentColumnWidth = 30;
        secondPercentColumnWidth = 70;        
        
        prop_table = gov.nih.nci.evs.restapi.util.FormatUtils.formatTable(u, firstColumnHeading, secondColumnHeading,
        	firstPercentColumnWidth, secondPercentColumnWidth);
        %>
        <%=description%>
        <%=prop_table%>
        <%
        } else {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_ROLE, true);
        %>
        <%=description%>
      <%  
      }
      %>
      </div>
      
<br></br> 
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
        
        roles = new Vector();
	Vector inv_role_vec = utils.getInboundRolesByCode(named_graph, concept_code);  
	if (inv_role_vec != null && inv_role_vec.size() > 0) {
		int n4 = inv_role_vec.size()/3;
		for (int k1 = 0; k1<n4; k1++) {
		    int i0 = k1*3;
		    int i1 = k1*3+1;
		    int i2 = k1*3+2;
		    String s0 = parser.getValue((String) inv_role_vec.elementAt(i0));
		    String s1 = parser.getValue((String) inv_role_vec.elementAt(i1));
		    String s2 = parser.getValue((String) inv_role_vec.elementAt(i2));
		    roles.add(s0 + "|" + s1 + "|" + s2);
		}
	}    	
        
        if (roles != null && roles.size() > 0) {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_INVERSE_ROLE, false);

        Vector u = new Vector();
        for (int i=0; i<roles.size(); i++) {
            String t = (String) roles.elementAt(i);
            Vector u2 = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
            String s1 = (String) u2.elementAt(0);
            String s2 = (String) u2.elementAt(1);
            String s3 = (String) u2.elementAt(2);
            //String s = s1 + " (" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(s2) + ")" + "|" + s3;
            String s = s1 + " (" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, named_graph, s2, s2) + ")" + "|" + s3;
            u.add(s);
        }
        
        firstColumnHeading = "Source Concept";
        secondColumnHeading = "Role Name";
        firstPercentColumnWidth = 70;
        secondPercentColumnWidth = 30;        
        
        prop_table = gov.nih.nci.evs.restapi.util.FormatUtils.formatTable(u, firstColumnHeading, secondColumnHeading,
        	firstPercentColumnWidth, secondPercentColumnWidth);
        %>
        <%=description%>
        <%=prop_table%>
        <%
        } else {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_INVERSE_ROLE, true);
        %>
        <%=description%>
      <%  
      }
      %>
      </div>
      
<br></br> 
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
        
        roles = new Vector();
        Vector asso_vec = utils.getAssociationsByCode(named_graph, concept_code);  				    
  	if (asso_vec != null && asso_vec.size() > 0) {
 		int n5 = asso_vec.size()/3;
 		for (int k1 = 0; k1<n5; k1++) {
 		    int i0 = k1*3;
 		    int i1 = k1*3+1;
 		    int i2 = k1*3+2;
 		    String s0 = parser.getValue((String) asso_vec.elementAt(i0));
 		    String s1 = parser.getValue((String) asso_vec.elementAt(i1));
 		    String s2 = parser.getValue((String) asso_vec.elementAt(i2));
 		    roles.add(s0 + "|" + s1 + "|" + s2);
 		}
	}    
	
        if (roles != null && roles.size() > 0) {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_ASSOCIATION, false);

        Vector u = new Vector();
        for (int i=0; i<roles.size(); i++) {
            String t = (String) roles.elementAt(i);
            Vector u2 = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
            String s1 = (String) u2.elementAt(0);
            String s2 = (String) u2.elementAt(1);
            String s3 = (String) u2.elementAt(2);
            //String s = s1 + "|" + s2 + " (" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(s3) + ")";
            String s = s1 + "|" + s2 + " (" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, named_graph, s3, s3) + ")";
            u.add(s);
        }
        
        firstColumnHeading = "Association Name";
        secondColumnHeading = "Target Concept";
        firstPercentColumnWidth = 30;
        secondPercentColumnWidth = 70;        
        
        prop_table = gov.nih.nci.evs.restapi.util.FormatUtils.formatTable(u, firstColumnHeading, secondColumnHeading,
        	firstPercentColumnWidth, secondPercentColumnWidth);
        %>
        <%=description%>
        <%=prop_table%>
        <%
        } else {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_ASSOCIATION, true);
        %>
        <%=description%>
      <%  
      }
      %>
      </div>     
      
<br></br> 
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
     
        roles = new Vector();
        Vector inv_asso_vec = utils.getInverseAssociationsByCode(named_graph, concept_code); 				    
  	if (inv_asso_vec != null && inv_asso_vec.size() > 0) {
 		int n6 = inv_asso_vec.size()/3;
 		for (int k1 = 0; k1<n6; k1++) {
 		    int i0 = k1*3;
 		    int i1 = k1*3+1;
 		    int i2 = k1*3+2;
 		    String s0 = parser.getValue((String) inv_asso_vec.elementAt(i0));
 		    String s1 = parser.getValue((String) inv_asso_vec.elementAt(i1));
 		    String s2 = parser.getValue((String) inv_asso_vec.elementAt(i2));
 		    roles.add(s0 + "|" + s1 + "|" + s2);
 		}
	}        
        
        if (roles != null && roles.size() > 0) {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_INVERSE_ASSOCIATION, false);

        Vector u = new Vector();
        for (int i=0; i<roles.size(); i++) {
            String t = (String) roles.elementAt(i);
            Vector u2 = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
            String s1 = (String) u2.elementAt(0);
            String s2 = (String) u2.elementAt(1);
            String s3 = (String) u2.elementAt(2);
            //String s = s1 + " (" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(s2) + ")" + "|" + s3;
            String s = s1 + " (" + gov.nih.nci.evs.restapi.ui.UIUtils.getHyperlink(jsp, named_graph, s2, s2) + ")" + "|" + s3;
            u.add(s);
        }
        
        firstColumnHeading = "Source Concept";
        secondColumnHeading = "Association Name";
        firstPercentColumnWidth = 70;
        secondPercentColumnWidth = 30;        
        
        prop_table = gov.nih.nci.evs.restapi.util.FormatUtils.formatTable(u, firstColumnHeading, secondColumnHeading,
        	firstPercentColumnWidth, secondPercentColumnWidth);
        %>
        <%=description%>
        <%=prop_table%>
        <%
        } else {
        description = formatUtils.getRelationshipTableLabel(null, gov.nih.nci.evs.restapi.common.Constants.TYPE_INVERSE_ASSOCIATION, true);
        %>
        <%=description%>
      <%  
      }
      %>
      </div>        
      
<br></br> 
<!--
<h:form> 
<center>
    <h:commandButton id="home" value="Home" action="#{userSessionBean.backAction}"
      accesskey="15"
      image="/images/close.gif"
      alt="Close">
    </h:commandButton>
</center>
</h:form> 
--> 


