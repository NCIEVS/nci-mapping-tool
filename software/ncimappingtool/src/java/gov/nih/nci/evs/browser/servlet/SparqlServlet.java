package gov.nih.nci.evs.browser.servlet;

import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.properties.*;

import gov.nih.nci.evs.restapi.util.*;
import gov.nih.nci.evs.restapi.bean.*;
import gov.nih.nci.evs.restapi.ui.*;

import gov.nih.nci.evs.restapi.meta.util.*;
import gov.nih.nci.evs.restapi.meta.ui.*;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

import org.json.*;
import gov.nih.nci.evs.browser.bean.*;

/*
   @author NGIS, Kim L. Ong
*/

public class SparqlServlet extends HttpServlet {
   GraphDrawer gd = null;
   TTLGraphDrawer ttl_gd = null;
   public void init(ServletConfig config)
	           throws ServletException
   {
      super.init(config);
      String sparql_endpoint = NCImtProperties._service_url;
      String serviceUrl = NCImtProperties._service_url;//GraphDrawer.endPoint2ServiceUrl(sparql_endpoint);
      gd = new GraphDrawer(serviceUrl);
      ttl_gd = new TTLGraphDrawer(serviceUrl);
   }

   public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
              throws IOException, ServletException
   {
        execute(request, response);
   }

   public void doPost(HttpServletRequest request,
                     HttpServletResponse response)
              throws IOException, ServletException
   {
        execute(request, response);
   }

   public void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
		String action = (String) request.getParameter("action");
		System.out.println("action: " + action);
//to be modified:
        String ontology_display_name = NCImtBrowserProperties.get_TERMINOLOGY();
        String ontology_version = null;

        if (action.equals("multiple_search")) {
            //multiple_search(request, response);

        } else if (action.equals("values")) {
            //resolve_value_set(request, response);

        } else if (action.equals("search_value_set")) {
           // search_value_set(request, response);


        } else if (action.equals("create_src_vs_tree")) {
			request.getSession().setAttribute("nav_type", "valuesets");
            create_src_vs_tree(request, response);

/*

        } else if (action.equals("create_cs_vs_tree")) {
			request.getSession().setAttribute("nav_type", "valuesets");
            create_cs_vs_tree(request, response);

        //vstreebranch
        } else if (action.equals("vstreebranch")) {
			String code = (String) request.getParameter("ontology_node_id");
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
				long ms1 = System.currentTimeMillis();
				JSONObject json = new JSONObject();
				JSONArray nodesArray = null;
				try {
					nodesArray = new JSONArray(CacheController.getInstance().getValueSetRootJSONString(ontology_display_name, ontology_version, code));
					if (nodesArray != null) {
						json.put("root_nodes", nodesArray);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(json.toString());
				System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms1));

            return;

        } else if (action.equals("expand_vstree")) {
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            String code = (String) request.getParameter("ontology_node_id");

				long ms1 = System.currentTimeMillis();
				JSONObject json = new JSONObject();
				JSONArray nodesArray = null;
				try {
					nodesArray = new JSONArray(CacheController.getInstance().getValueSetSubconceptJSONString(code));
					if (nodesArray != null) {
						json.put("nodes", nodesArray);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(json.toString());
				System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms1));

            return;
            */

        } else if (action.equals("build_tree")) {
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
				long ms1 = System.currentTimeMillis();
				JSONObject json = new JSONObject();
				JSONArray nodesArray = null;
				try {

					String named_graph = (String) request.getParameter("ng");
					if (named_graph == null) {
						named_graph = NCImtProperties.get_default_named_graph();
					}
					String str = SparqlCacheController.getInstance().getRootJSONString(named_graph, ontology_version);
					//nodesArray = new JSONArray(SparqlCacheController.getInstance().getRootJSONString(named_graph, ontology_version));
					nodesArray = new JSONArray(str);
					if (nodesArray != null) {
						json.put("root_nodes", nodesArray);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(json.toString());
				System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms1));

            return;
        } else if (action.equals("expand_tree")) {

            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            String code = (String) request.getParameter("ontology_node_id");

 String named_graph = (String) request.getParameter("ng");
 System.out.println("expand_tree " + code);

				long ms1 = System.currentTimeMillis();
				JSONObject json = new JSONObject();
				JSONArray nodesArray = null;
				try {
					String str = SparqlCacheController.getInstance().getSubconceptJSONString(named_graph, code);
					nodesArray = new JSONArray(str);
					if (nodesArray != null) {
						json.put("nodes", nodesArray);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(json.toString());
				System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms1));

            return;
            /*

        } else if (action.equals("build_ptree")) {
			/*
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
				long ms1 = System.currentTimeMillis();
				JSONObject json = new JSONObject();
				JSONArray nodesArray = null;
				try {
					nodesArray = new JSONArray(CacheController.getInstance().getPartonomyRootJSONString(ontology_display_name, ontology_version));
					if (nodesArray != null) {
						json.put("root_nodes", nodesArray);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(json.toString());
				System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms1));

            return;
            */

        } else if (action.equals("build_vstree")) {
			/*
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
				long ms1 = System.currentTimeMillis();
				JSONObject json = new JSONObject();
				JSONArray nodesArray = null;
				try {
					nodesArray = new JSONArray(CacheController.getInstance().getValueSetRootJSONString(ontology_display_name, ontology_version));
					if (nodesArray != null) {
						json.put("root_nodes", nodesArray);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(json.toString());
				System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms1));

            return;
            */

        } else if (action.equals("expand_ptree")) {
			/*
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            String code = (String) request.getParameter("ontology_node_id");

				long ms1 = System.currentTimeMillis();
				JSONObject json = new JSONObject();
				JSONArray nodesArray = null;
				try {
					nodesArray = new JSONArray(CacheController.getInstance().getPartonomySubconceptJSONString(code));
					if (nodesArray != null) {
						json.put("nodes", nodesArray);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(json.toString());
				System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms1));

            return;
            */
        } else if (action.equals("search")) {
            searchAction(request, response);

        } else if (action.equals("search_tree")) {
			String node_id = (String) request.getParameter("ontology_node_id");
			String ng = (String) request.getParameter("ng");
            search_tree(response, ng, node_id);

        } else if (action.equals("view_graph")) {
			String ng = (String) request.getParameter("ng");
			String code = (String) request.getParameter("code");
			String type =  (String) request.getParameter("type");
			String scheme = null;
			String version  = null;
			String named_graph = null;

			Vector cs_data = DataUtils.get_cs_data();
			for (int j=0; j<cs_data.size(); j++) {
				String line = (String) cs_data.elementAt(j);
				Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(line, '|');
				scheme = (String) u.elementAt(0);
				version = (String) u.elementAt(1);
				named_graph = (String) u.elementAt(2);

				if (named_graph.compareTo(ng) == 0) {
					break;
				}
			}
			String ns = scheme;
			if (type == null) {
				type = "ALL";
			}
			if (scheme.compareTo("NCI_Thesaurus") == 0) {
				PrintWriter pw = null;
				gd.view_graph(pw, request, response, "ncimappingtool", scheme, version, ng, ns, code, type);

			} else {
				code = (String) request.getParameter("code");
				type =  (String) request.getParameter("type");
				if (type == null) {
					type = "ALL";
				}
				PrintWriter pw = null;
				ttl_gd.view_graph(pw, request, response, "ncimappingtool", named_graph, code, type);
			}

        } else if (action.equals("reset_graph")) {
            String id = (String) request.getParameter("id");
            String scheme = (String) request.getSession().getAttribute("scheme");
            String version = (String) request.getSession().getAttribute("version");
            String ns = (String) request.getSession().getAttribute("ns");
            String nodes_and_edges = (String) request.getSession().getAttribute("nodes_and_edges");
            String code = findCodeInGraph(nodes_and_edges, id);
            gd.view_graph(null, request, response, scheme, version, gd.get_named_graph(), ns, code, "ALL");


/*
        } else if (action.equals("build_vs_tree")) {

            if (ontology_display_name == null)
                ontology_display_name = CODING_SCHEME_NAME;

            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            JSONObject json = new JSONObject();
            JSONArray nodesArray = null;// new JSONArray();
            try {
				String codingSchemeVersion = null;
                nodesArray =
                    CacheController.getInstance().getRootValueSets(
                        ontology_display_name, codingSchemeVersion);
                if (nodesArray != null) {
                    json.put("root_nodes", nodesArray);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            response.getWriter().write(json.toString());
            _logger.debug("Run time (milliseconds): "
                + (System.currentTimeMillis() - ms));
            return;

        } else if (action.equals("expand_vs_tree")) {
            if (node_id != null && ontology_display_name != null) {
                response.setContentType("text/html");
                response.setHeader("Cache-Control", "no-cache");
                JSONObject json = new JSONObject();
                JSONArray nodesArray = null;

                try {

                    nodesArray =
                        CacheController.getInstance().getSubValueSets(
                            ontology_display_name, ontology_version, node_id);


                    if (nodesArray != null) {
                       json.put("nodes", nodesArray);
                    }

                } catch (Exception e) {
                }
                response.getWriter().write(json.toString());
                _logger.debug("Run time (milliseconds): "
                    + (System.currentTimeMillis() - ms));
            }


        } else if (action.equals("expand_entire_vs_tree")) {
            if (node_id != null && ontology_display_name != null) {
                response.setContentType("text/html");
                response.setHeader("Cache-Control", "no-cache");
                JSONObject json = new JSONObject();
                JSONArray nodesArray = null;

                try {
                    nodesArray =
                        CacheController.getInstance().getSourceValueSetTree(
                            ontology_display_name, ontology_version, true);
                    if (nodesArray != null) {
                        json.put("root_nodes", nodesArray);
                    }
                } catch (Exception e) {
					e.printStackTrace();
                }
                response.getWriter().write(json.toString());
                _logger.debug("Run time (milliseconds): "
                    + (System.currentTimeMillis() - ms));
            }

        } else if (action.equals("expand_entire_cs_vs_tree")) {
            //if (node_id != null && ontology_display_name != null) {
                response.setContentType("text/html");
                response.setHeader("Cache-Control", "no-cache");
                JSONObject json = new JSONObject();
                JSONArray nodesArray = null;

                try {
                    nodesArray =
                        CacheController.getInstance().getCodingSchemeValueSetTree(
                            ontology_display_name, ontology_version, true);
                    if (nodesArray != null) {
                        json.put("root_nodes", nodesArray);
                    }

                } catch (Exception e) {
                }
                response.getWriter().write(json.toString());
                _logger.debug("Run time (milliseconds): "
                    + (System.currentTimeMillis() - ms));

        } else if (action.equals("build_cs_vs_tree")) {

            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            JSONObject json = new JSONObject();
            JSONArray nodesArray = null;// new JSONArray();
            try {
				//HashMap getRootValueSets(String codingSchemeURN)
				String codingSchemeVersion = null;
                nodesArray =
                    CacheController.getInstance().getRootValueSets(true);

                if (nodesArray != null) {
                    json.put("root_nodes", nodesArray);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            response.getWriter().write(json.toString());

            _logger.debug("Run time (milliseconds): "
                + (System.currentTimeMillis() - ms));
            return;
        } else if (action.equals("expand_cs_vs_tree")) {

			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			JSONObject json = new JSONObject();
			JSONArray nodesArray = null;

			//String vsd_uri = ValueSetHierarchy.getValueSetURI(node_id);
			node_id = StringUtils.getStringComponent(node_id, "$", 0);
			String vsd_uri = StringUtils.getStringComponent(node_id, "$", 1);

            //if (node_id != null && ontology_display_name != null) {
			if (node_id != null) {
				ValueSetDefinition vsd = DataUtils.getValueSetHierarchy().findValueSetDefinitionByURI(vsd_uri);
				if (vsd == null) {
				   try {
					   //
					    nodesArray = CacheController.getInstance().getRootValueSets(node_id, null);
						//nodesArray = CacheController.getInstance().getRootValueSets(node_id, null); //find roots (by source)

						if (nodesArray != null) {
							json.put("nodes", nodesArray);
						}

					} catch (Exception e) {
					}
			    } else {
					try {
						nodesArray =
							CacheController.getInstance().getSubValueSets(
								node_id, null, vsd_uri);

						if (nodesArray != null) {
							json.put("nodes", nodesArray);
						}

					} catch (Exception e) {
					}
				}

                response.getWriter().write(json.toString());
                _logger.debug("Run time (milliseconds): "
                    + (System.currentTimeMillis() - ms));
            }


        } else if (action.equals("build_src_vs_tree")) {


            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            JSONObject json = new JSONObject();
            JSONArray nodesArray = null;// new JSONArray();
            try {
				//HashMap getRootValueSets(String codingSchemeURN)
				String codingSchemeVersion = null;
                nodesArray =
                    //CacheController.getInstance().getRootValueSets(true, true);
                    CacheController.getInstance().build_src_vs_tree();

                if (nodesArray != null) {
                    json.put("root_nodes", nodesArray);
                } else {
					_logger.debug("*** AjaxServlet build_src_vs_tree returns null???");
				}



            } catch (Exception e) {
                e.printStackTrace();
            }

            response.getWriter().write(json.toString());
            _logger.debug("Run time (milliseconds): "
                + (System.currentTimeMillis() - ms));
            return;

        } else if (action.equals("expand_src_vs_tree")) {

            if (node_id != null && ontology_display_name != null) {
                response.setContentType("text/html");
                response.setHeader("Cache-Control", "no-cache");
                JSONObject json = new JSONObject();
                JSONArray nodesArray = null;
				nodesArray = CacheController.getInstance().expand_src_vs_tree(node_id);

                try {
                    if (nodesArray != null) {
                        json.put("nodes", nodesArray);
                    }
                } catch (Exception e) {
					e.printStackTrace();

                }
                response.getWriter().write(json.toString());
                _logger.debug("Run time (milliseconds): "
                    + (System.currentTimeMillis() - ms));
            }
*/

        } else if (action.equals("versions")) {
             selectCSVersionAction(request, response);

        } else if (action.equals("xmldefinitions")) {
            //exportVSDToXMLAction(request, response);

		} else {
			System.out.println("Action: " + action);
		}
   }


    public String valueSetDefinition2XMLString(String uri) {
		String s = null;
/*
        LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        String s = null;
        String valueSetDefinitionRevisionId = null;
        try {
			URI valueSetDefinitionURI = new URI(uri);
			StringBuffer buf = vsd_service.exportValueSetDefinition(valueSetDefinitionURI, valueSetDefinitionRevisionId);
            s = buf.toString();
        } catch (Exception ex) {
           ex.printStackTrace();
        }
*/
		return s;
	}

/*
    public void exportVSDToXMLAction(HttpServletRequest request, HttpServletResponse response) {
       String selectedvalueset = null;
       String multiplematches = HTTPUtils.cleanXSS((String) request.getParameter("multiplematches"));
        if (multiplematches != null) {
			selectedvalueset = HTTPUtils.cleanXSS((String) request.getParameter("valueset"));
		} else {
			selectedvalueset = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
			if (selectedvalueset != null && selectedvalueset.indexOf("|") != -1) {
				Vector u = DataUtils.parseData(selectedvalueset);
				selectedvalueset = (String) u.elementAt(1);
			}
	    }
        String uri = selectedvalueset;
		request.getSession().setAttribute("selectedvalueset", uri);

        String xml_str = valueSetDefinition2XMLString(uri);

		try {
			response.setContentType("text/xml");

			String vsd_name = DataUtils.valueSetDefinitionURI2Name(uri);
			vsd_name = vsd_name.replaceAll(" ", "_");
			vsd_name = vsd_name + ".xml";

		    response.setHeader("Content-Disposition", "attachment; filename="
					+ vsd_name);

			response.setContentLength(xml_str.length());

			ServletOutputStream ouputStream = response.getOutputStream();
			ouputStream.write(xml_str.getBytes("UTF8"), 0, xml_str.length());
			ouputStream.flush();
			ouputStream.close();

		} catch(IOException e) {
			e.printStackTrace();
		}

		FacesContext.getCurrentInstance().responseComplete();

	}

    public void exportVSDToXMLAction(HttpServletRequest request, HttpServletResponse response) {
        String vs_header_concept_code = (String) request.getParameter("vsd_uri");
        System.out.println(vs_header_concept_code);

	}
*/


    public static void search_tree(HttpServletResponse response, String named_graph, String node_id) {
		System.out.println("search_tree named_graph: " + named_graph);
		System.out.println("search_tree node_id: " + node_id);
        try {
            String jsonString =
            SparqlCacheController.getInstance().getViewInHierarchyJSONString(named_graph, node_id);

            if (jsonString == null)
                return;

            JSONObject json = new JSONObject();
            JSONArray rootsArray = new JSONArray(jsonString);
            json.put("root_nodes", rootsArray);

            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write(json.toString());
            response.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dumpHashMap(HashMap map) {
	    Iterator it = map.keySet().iterator();
	    while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println(key);
			List list = (List) map.get(key);
			for (int i=0; i<list.size(); i++) {
				String t = (String) list.get(i);
				System.out.println("\t" + t);
			}
		}
	}

/*
    public static void view_graph(HttpServletRequest request, HttpServletResponse response,
        String scheme, String version, String namespace, String code, String type) {
	  HashMap hmap = (HashMap) request.getSession().getAttribute("RelationshipHashMap");

	  if (hmap == null) {
		  String named_graph = "http://NCIt";
		  OWLSPARQLUtils owlSPARQLUtils = new OWLSPARQLUtils(NCImtBrowserProperties.get_SPARQL_SERVICE(), null, null);
		  //gov.nih.nci.evs.restapi.util.RelUtils relationshipUtils = new gov.nih.nci.evs.restapi.util.RelUtils(owlSPARQLUtils, named_graph);

		  gov.nih.nci.evs.restapi.util.RelationshipUtils relationshipUtils = new gov.nih.nci.evs.restapi.util.RelationshipUtils(owlSPARQLUtils, named_graph);

		  hmap = relationshipUtils.getRelationshipHashMap(scheme, version, code, namespace, true);
		  request.getSession().setAttribute("RelationshipHashMap", hmap);
	  }
	  // compute nodes and edges using hmap
	  VisUtils visUtils = new VisUtils(NCImtBrowserProperties.get_SPARQL_SERVICE());
	  String[] types = null;
	  if (type == null || type.compareTo("ALL") == 0) {
		  types = visUtils.ALL_RELATIONSHIP_TYPES;
	  } else {
		  types = new String[1];
		  types[0] = type;
	  }

	  int edge_count = countEdges(hmap, types);
	  //String nodes_and_edges =  visUtils.generateGraphScript(scheme, version, namespace, code, types, VisUtils.NODES_AND_EDGES, hmap);
      //boolean graph_reduced = false;
	  Vector v = visUtils.generateGraphScriptVector(scheme, version, namespace, code, types, VisUtils.NODES_AND_EDGES, hmap);
      String nodes_and_edges = null;
      String group_node_data = "";
      String group_node_id = null;
      String group_node_data_2 = "";
      String group_node_id_2 = null;
      boolean direction = true;
      HashMap group_node_id2dataMap = new HashMap();


      GraphReductionUtils graphReductionUtils = new GraphReductionUtils();
      int graph_size = graphReductionUtils.getNodeCount(v);

      //KLO, 02122016
      graphReductionUtils.initialize_group_node_id(graph_size);
      if (graph_size > graphReductionUtils.MINIMUM_REDUCED_GRAPH_SIZE) {

		  group_node_id = graphReductionUtils.getGroupNodeId(v);
		  int group_node_id_int = Integer.parseInt(group_node_id);
		  group_node_id_2 = new Integer(group_node_id_int+1).toString();
		  Vector w = graphReductionUtils.reduce_graph(v, direction);

		  boolean graph_reduced = graphReductionUtils.graph_reduced(v, w);
		  if (graph_reduced) {
			  group_node_data = graphReductionUtils.get_removed_node_str(v, direction);
			  Vector group_node_ids = graphReductionUtils.get_group_node_ids(w);
			  for (int k=0; k<group_node_ids.size(); k++) {
				  String node_id = (String) group_node_ids.elementAt(k);
				  if (!group_node_id2dataMap.containsKey(node_id)) {
					  group_node_id2dataMap.put(node_id, group_node_data);
					  break;
				  }
			  }

			  nodes_and_edges =  GraphUtils.generateGraphScript(w);
			  v = (Vector) w.clone();
		  }


		  direction = false;
		  w = graphReductionUtils.reduce_graph(v, direction);
		  graph_reduced = graphReductionUtils.graph_reduced(v, w);
		  if (graph_reduced) {
			  group_node_data_2 = graphReductionUtils.get_removed_node_str(v, direction);
			  Vector group_node_ids = graphReductionUtils.get_group_node_ids(w);
			  for (int k=0; k<group_node_ids.size(); k++) {
				  String node_id = (String) group_node_ids.elementAt(k);
				  if (!group_node_id2dataMap.containsKey(node_id)) {
					  group_node_id2dataMap.put(node_id, group_node_data_2);
					  break;
				  }
			  }
			  nodes_and_edges =  GraphUtils.generateGraphScript(w);
			  v = (Vector) w.clone();
		  }
      }

      if (group_node_id2dataMap.keySet().size() == 0) {
		  nodes_and_edges =  visUtils.generateGraphScript(scheme, version, namespace, code, types, VisUtils.NODES_AND_EDGES, hmap);
	  }

	  Vector group_node_ids = graphReductionUtils.get_group_node_ids(v);
	  boolean graph_available = true;
	  if (nodes_and_edges.compareTo(GraphUtils.NO_DATA_AVAILABLE) == 0) {
		  graph_available = false;
	  }
      response.setContentType("text/html");
      PrintWriter out = null;

      try {
      	  out = response.getWriter();
      } catch (Exception ex) {
		  ex.printStackTrace();
		  return;
	  }

      out.println("<!doctype html>");
      out.println("<html>");
      out.println("<head>");
      out.println("  <title>View Graph</title>");
      out.println("");
      out.println("  <style type=\"text/css\">");
      out.println("    body {");
      out.println("      font: 10pt sans;");
      out.println("    }");
      out.println("    #conceptnetwork {");
      out.println("      width: 1200px;");
      if (edge_count > 50) {
      	  out.println("      height: 800px;");
	  } else {
		  out.println("      height: 600px;");
	  }
      out.println("      border: 1px solid lightgray;");
      out.println("    }");
      out.println("    table.legend_table {");
      out.println("      border-collapse: collapse;");
      out.println("    }");
      out.println("    table.legend_table td,");
      out.println("    table.legend_table th {");
      out.println("      border: 1px solid #d3d3d3;");
      out.println("      padding: 10px;");
      out.println("    }");
      out.println("");
      out.println("    table.legend_table td {");
      out.println("      text-align: center;");
      out.println("      width:110px;");
      out.println("    }");
      out.println("  </style>");
      out.println("");
      out.println("  <script type=\"text/javascript\" src=\"/sparql/css/vis/vis.js\"></script>");
      out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/sparql/css/vis/vis.css\" />");
      out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/sparql/css/styleSheet.css\" />");

      out.println("");
      out.println("  <script type=\"text/javascript\">");
      out.println("    var nodes = null;");
      out.println("    var edges = null;");
      out.println("    var network = null;");
      out.println("");

      out.println("    function reset_graph(id) {");
      out.println("        window.location.href=\"/sparql/ajax?action=reset_graph&id=\" + id;");
      out.println("    }");


      out.println("    function destroy() {");
      out.println("      if (network !== null) {");
      out.println("        network.destroy();");
      out.println("        network = null;");
      out.println("      }");
      out.println("    }");
      out.println("");

      out.println("    function draw() {");

	  if (graph_available) {
		  out.println(nodes_and_edges);
	  }

      out.println("      // create a network");
      out.println("      var container = document.getElementById('conceptnetwork');");
      out.println("      var data = {");
      out.println("        nodes: nodes,");
      out.println("        edges: edges");
      out.println("      };");

      if (type.endsWith("path")) {
		  out.println("            var directionInput = document.getElementById(\"direction\").value;");
		  out.println("            var options = {");
		  out.println("                layout: {");
		  out.println("                    hierarchical: {");
		  out.println("                        direction: directionInput");
		  out.println("                    }");
		  out.println("                }");
		  out.println("            };");


	  } else {

		  out.println("      var options = {");
		  out.println("        interaction: {");
		  out.println("          navigationButtons: true,");
		  out.println("          keyboard: true");
		  out.println("        }");
		  out.println("      };");

      }

      out.println("      network = new vis.Network(container, data, options);");
      out.println("");
      out.println("      // add event listeners");


      out.println("      network.on('select', function(params) {");

      Iterator it = group_node_id2dataMap.keySet().iterator();
      while (it.hasNext()) {
		  String node_id = (String) it.next();
		  String node_data = (String) group_node_id2dataMap.get(node_id);
		  out.println("      if (params.nodes == '" + node_id + "') {");
		  out.println("         document.getElementById('selection').innerHTML = '" + node_data + "';");
		  out.println("      }");
	  }

      out.println("      });");
      out.println("			network.on(\"doubleClick\", function (params) {");

      String node_id_1 = null;
      String node_id_2 = null;
      it = group_node_id2dataMap.keySet().iterator();
      int lcv = 0;
      while (it.hasNext()) {
		  String node_id = (String) it.next();
		  if (lcv == 0) {
			  node_id_1 = node_id;
		  } else if (lcv == 1) {
			  node_id_2 = node_id;
		  }
		  lcv++;
  	  }

  	  if (node_id_1 != null && node_id_2 != null) {
		  out.println("      if (params.nodes != '" + node_id_1 + "' && params.nodes != '" + node_id_2 + "') {");
		  out.println("				params.event = \"[original event]\";");
		  out.println("				var json = JSON.stringify(params, null, 4);");
		  out.println("				reset_graph(params.nodes);");
		  out.println("      }");
  	  } else if (node_id_1 != null && node_id_2 == null) {
		  out.println("      if (params.nodes != '" + node_id_1 + "') {");
		  out.println("				params.event = \"[original event]\";");
		  out.println("				var json = JSON.stringify(params, null, 4);");
		  out.println("				reset_graph(params.nodes);");
		  out.println("      }");
  	  } else if (node_id_2 != null && node_id_1 == null) {
		  out.println("      if (params.nodes != '" + node_id_2 + "') {");
		  out.println("				params.event = \"[original event]\";");
		  out.println("				var json = JSON.stringify(params, null, 4);");
		  out.println("				reset_graph(params.nodes);");
		  out.println("      }");
  	  } else if (node_id_2 == null && node_id_1 == null) {
		  out.println("				params.event = \"[original event]\";");
		  out.println("				var json = JSON.stringify(params, null, 4);");
		  out.println("				reset_graph(params.nodes);");
	  }

      out.println("		    });");

      out.println("    }");
      out.println("  </script>");
      out.println("</head>");
      out.println("");
      out.println("<body onload=\"draw();\">");

      out.println("<div class=\"ncibanner\">");
      out.println("  <a href=\"http://www.cancer.gov\" target=\"_blank\">     ");
      out.println("    <img src=\"/sparql/images/logotype.gif\"");
      out.println("      width=\"556\" height=\"39\" border=\"0\"");
      out.println("      alt=\"National Cancer Institute\"/>");
      out.println("  </a>");
      out.println("  <a href=\"http://www.cancer.gov\" target=\"_blank\">     ");
      out.println("    <img src=\"/sparql/images/spacer.gif\"");
      out.println("      width=\"60\" height=\"39\" border=\"0\" ");
      out.println("      alt=\"National Cancer Institute\" class=\"print-header\"/>");
      out.println("  </a>");
      out.println("  <a href=\"http://www.nih.gov\" target=\"_blank\" >      ");
      out.println("    <img src=\"/sparql/images/tagline_nologo.gif\"");
      out.println("      width=\"219\" height=\"39\" border=\"0\"");
      out.println("      alt=\"U.S. National Institutes of Health\"/>");
      out.println("  </a>");
      out.println("  <a href=\"http://www.cancer.gov\" target=\"_blank\">      ");
      out.println("    <img src=\"/sparql/images/cancer-gov.gif\"");
      out.println("      width=\"125\" height=\"39\" border=\"0\"");
      out.println("      alt=\"www.cancer.gov\"/>");
      out.println("  </a>");
      out.println("</div>");
      out.println("<p></p>");

	  if (!graph_available) {
		  out.println("<p class=\"textbodyred\">&nbsp;No graph data is available.</p>");
	  }

      out.println("<form id=\"data\" method=\"post\" action=\"/sparql/ajax?action=view_graph\">");

      out.println("Relationships");
      out.println("<select name=\"type\" >");
      if (type == null || type.compareTo("ALL") == 0) {
     	  out.println("  <option value=\"ALL\" selected>ALL</option>");
      } else {
		  out.println("  <option value=\"ALL\">ALL</option>");
	  }
	  String rel_type = null;
	  String option_label = null;

	  for (int k=0; k<VisUtils.ALL_RELATIONSHIP_TYPES.length; k++) {
          rel_type = (String) VisUtils.ALL_RELATIONSHIP_TYPES[k];
          List list = (List) hmap.get(rel_type);
          if (list != null && list.size() > 0) {
			  option_label = VisUtils.getRelatinshipLabel(rel_type);
			  if (type.compareTo(rel_type) == 0) {
				  out.println("  <option value=\"" + rel_type + "\" selected>" + option_label + "</option>");
			  } else {
				  out.println("  <option value=\"" + rel_type + "\">" + option_label + "</option>");
			  }
	      }
	  }

	  boolean hasPartOf = new PartonomyUtils(NCImtBrowserProperties.get_SPARQL_SERVICE()).hasPartOfRelationships(hmap);
	  if (hasPartOf) {
		  rel_type = "type_part_of";
		  option_label = VisUtils.getRelatinshipLabel(rel_type);
		  if (type.compareTo(rel_type) == 0) {
			  out.println("  <option value=\"" + rel_type + "\" selected>" + option_label + "</option>");
		  } else {
			  out.println("  <option value=\"" + rel_type + "\">" + option_label + "</option>");
		  }

		  rel_type = "type_part_of_path";
		  option_label = VisUtils.getRelatinshipLabel(rel_type);
		  if (type.compareTo(rel_type) == 0) {
			  out.println("  <option value=\"" + rel_type + "\" selected>" + option_label + "</option>");
		  } else {
			  out.println("  <option value=\"" + rel_type + "\">" + option_label + "</option>");
		  }
	  }

      out.println("</select>");
      out.println("<input type=\"hidden\" id=\"scheme\" name=\"scheme\" value=\"" + scheme + "\" />");
      out.println("<input type=\"hidden\" id=\"version\" name=\"version\" value=\"" + version + "\" />");
      out.println("<input type=\"hidden\" id=\"ns\" name=\"ns\" value=\"" + namespace + "\" />");
      out.println("<input type=\"hidden\" id=\"code\" name=\"code\" value=\"" + code + "\" />");


      request.getSession().setAttribute("scheme", scheme);
      request.getSession().setAttribute("version", version);
      request.getSession().setAttribute("ns", namespace);
      request.getSession().setAttribute("code", code);
      request.getSession().setAttribute("nodes_and_edges", nodes_and_edges);


      out.println("");
      out.println("&nbsp;&nbsp;");
      out.println("<input type=\"submit\" value=\"Refresh\"></input>");
      out.println("</form>");
      out.println("");

      if (type.endsWith("path")) {

      out.println("<p>");
      out.println("    <input type=\"button\" id=\"btn-UD\" value=\"Up-Down\">");
      out.println("    <input type=\"button\" id=\"btn-DU\" value=\"Down-Up\">");
      out.println("    <input type=\"button\" id=\"btn-LR\" value=\"Left-Right\">");
      out.println("    <input type=\"button\" id=\"btn-RL\" value=\"Right-Left\">");
      out.println("    <input type=\"hidden\" id='direction' value=\"UD\">");
      out.println("</p>");
      out.println("<script language=\"javascript\">");
      out.println("    var directionInput = document.getElementById(\"direction\");");
      out.println("    var btnUD = document.getElementById(\"btn-UD\");");
      out.println("    btnUD.onclick = function () {");
      out.println("        directionInput.value = \"UD\";");
      out.println("        draw();");
      out.println("    }");
      out.println("    var btnDU = document.getElementById(\"btn-DU\");");
      out.println("    btnDU.onclick = function () {");
      out.println("        directionInput.value = \"DU\";");
      out.println("        draw();");
      out.println("    };");
      out.println("    var btnLR = document.getElementById(\"btn-LR\");");
      out.println("    btnLR.onclick = function () {");
      out.println("        directionInput.value = \"LR\";");
      out.println("        draw();");
      out.println("    };");
      out.println("    var btnRL = document.getElementById(\"btn-RL\");");
      out.println("    btnRL.onclick = function () {");
      out.println("        directionInput.value = \"RL\";");
      out.println("        draw();");
      out.println("    };");
      out.println("</script>");
      }

      out.println("<div style=\"width: 800px; font-size:14px; text-align: justify;\">");
      out.println("</div>");
      out.println("");
      out.println("<div id=\"conceptnetwork\"></div>");
      out.println("");
      out.println("<p id=\"selection\"></p>");
      out.println("</body>");
      out.println("</html>");

      out.flush();

		try {
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


   }


	public static int countEdges(HashMap relMap, String[] types) {
		if (relMap == null || types == null) return 0;
		int knt = 0;
		List typeList = Arrays.asList(types);
		for (int k=0; k<VisualizationUtils.ALL_RELATIONSHIP_TYPES.length; k++) {
			String rel_type = (String) VisualizationUtils.ALL_RELATIONSHIP_TYPES[k];
			if (typeList.contains(rel_type)) {
				List list = (ArrayList) relMap.get(rel_type);
				if (list != null) {
					knt = knt + list.size();
				}
			}
		}
        return knt;
	}
*/
    public String findCodeInGraph(String nodes_and_edges, String id) {
		String target = "{id: " + id + ", label:";
		int n = nodes_and_edges.indexOf(target);
		if (n == -1) return null;
		String t = nodes_and_edges.substring(n+target.length(), nodes_and_edges.length());
		target = ")'}";
		n = t.indexOf(target);
		t = t.substring(0, n);
		n = t.lastIndexOf("(");
		t = t.substring(n+1, t.length());
		return t;
	}

    private void create_src_vs_tree(HttpServletRequest request, HttpServletResponse response) {
       String vsd_uri = (String) request.getParameter("vsd_uri");
		try {
			String nextJSP = "/pages/src_value_set_tree.jsf";
			if (vsd_uri != null) {
                 nextJSP = "/pages/value_set_home.jsf?vsd_uri=" + vsd_uri;
			}
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
			dispatcher.forward(request,response);
			return;

		} catch (Exception ex) {
			 ex.printStackTrace();
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void searchAction(HttpServletRequest request, HttpServletResponse response) {
/*

		request.getSession().removeAttribute("ng");
		String named_graph = request.getParameter("ng");//get_named_graph();
		if (named_graph == null) {
			named_graph = NCImtBrowserProperties.get_default_named_graph();
		}

System.out.println("*** searchAction named_graph: " + named_graph);

		request.getSession().setAttribute("ng", named_graph);
        request.getSession().removeAttribute("message");
        request.getSession().removeAttribute("result_vec");


        String matchText = request.getParameter("matchText");
        if (matchText != null) {
			matchText = matchText.trim();
		}
        String algorithm = request.getParameter("algorithm");
        String searchTarget = request.getParameter("searchTarget");

System.out.println("*** SparqlServlet searchAction algorithm: " + algorithm);
System.out.println("*** SparqlServlet searchAction searchTarget: " + searchTarget);
System.out.println("*** SparqlServlet searchAction matchText: " + matchText);
System.out.println("*** SparqlServlet searchAction named_graph: " + named_graph);

        if (matchText == null || matchText.compareTo("") == 0) {
			String message = "Please enter a search string.";
			request.getSession().setAttribute("message", message);
			request.getSession().setAttribute("matchText", matchText);
			request.getSession().setAttribute("algorithm", algorithm);
			request.getSession().setAttribute("searchTarget", searchTarget);

			String nextJSP = "/pages/search_results.jsp";
			 try {
				 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
				 dispatcher.forward(request,response);
				 return;

			 } catch (Exception ex) {
				 ex.printStackTrace();
			 }
		}


System.out.println("SparqlServlet: named_graph " + named_graph);
System.out.println("SparqlServlet: matchText " + matchText);
System.out.println("SparqlServlet: algorithm " + algorithm);
System.out.println("SparqlServlet: searchTarget " + searchTarget);

GeneralizedQueryUtils gqu = NCImtBrowserProperties.createGeneralizedQueryUtils();

			try {
				if (searchTarget.compareTo("codes") == 0) {
                    String entityDescription = null;

System.out.println("Calling gqu.get_label named_graph " + named_graph);
System.out.println("Calling gqu.get_label matchText " + matchText);


if (NCImtBrowserProperties.isMetaThesaurusSource(named_graph)) {
			TTLQueryUtils ttlQueryUtils = new TTLQueryUtils(NCImtBrowserProperties.get_SPARQL_SERVICE());
			Vector v = ttlQueryUtils.class_label_query(named_graph, matchText);
			if (v != null && v.size() > 0) {
		    	entityDescription = (String) v.elementAt(0);
			}
} else {
	                //gqu = NCImtBrowserProperties.createGeneralizedQueryUtils();
 					entityDescription = gqu.get_label(named_graph, matchText);
}
System.out.println("entityDescription " + entityDescription);
					if (entityDescription != null && entityDescription.compareTo("") != 0) {

						String code = matchText;
						request.getSession().setAttribute("code", code);
						request.getSession().setAttribute("matchText", matchText);
						request.getSession().setAttribute("algorithm", algorithm);
						request.getSession().setAttribute("searchTarget", searchTarget);


						String nextJSP = "/ConceptReport.jsp?ng="+named_graph+"&code="+code;

System.out.println("nextJSP " + nextJSP);

						 try {
							 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
							 dispatcher.forward(request,response);
							 return;

						 } catch (Exception ex) {
							 ex.printStackTrace();
						 }

					} else {
						request.getSession().setAttribute("matchText", matchText);
						request.getSession().setAttribute("algorithm", algorithm);
						request.getSession().setAttribute("searchTarget", searchTarget);
						String message = "No match.";
						request.getSession().setAttribute("message", message);
						String nextJSP = "/pages/search_results.jsp";

System.out.println("nextJSP " + nextJSP + " " + message);

						 try {
							 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
							 dispatcher.forward(request,response);
							 return;

						 } catch (Exception ex) {
							 ex.printStackTrace();
						 }
					}

				} else if (searchTarget.compareTo("names") == 0) {
					Vector result_vec = null;
                    if (NCImtBrowserProperties.isMetaThesaurusSource(named_graph)) {
						TTLQueryUtils ttlQueryUtils = new TTLQueryUtils(NCImtBrowserProperties.get_SPARQL_SERVICE());
						result_vec = ttlQueryUtils.name_search(named_graph, matchText, -1);
					} else {
 				    	result_vec = gqu.search_by_name(named_graph, matchText, -1);
					}

					if (result_vec == null || result_vec.size() == 0) {
						request.getSession().setAttribute("matchText", matchText);
						request.getSession().setAttribute("algorithm", algorithm);
						request.getSession().setAttribute("searchTarget", searchTarget);

						String message = "No match.";
						request.getSession().setAttribute("message", message);
						String nextJSP = "/pages/search_results.jsp";

						 try {
							 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
							 dispatcher.forward(request,response);
							 return;

						 } catch (Exception ex) {
							 ex.printStackTrace();
						 }

					} else if (result_vec.size() == 1) {
						String t = (String) result_vec.elementAt(0);
						Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
						String code = (String) u.elementAt(1);
						request.getSession().setAttribute("code", code);
						request.getSession().setAttribute("matchText", matchText);
						request.getSession().setAttribute("algorithm", algorithm);
						request.getSession().setAttribute("searchTarget", searchTarget);

						String nextJSP = "/ConceptReport.jsp?ng="+named_graph+"&code="+code;

						 try {
							 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
							 dispatcher.forward(request,response);
							 return;

						 } catch (Exception ex) {
							 ex.printStackTrace();
						 }
						//return "match";
					} else {
						request.getSession().setAttribute("matchText", matchText);
						request.getSession().setAttribute("algorithm", algorithm);
						request.getSession().setAttribute("searchTarget", searchTarget);
						//result_vec = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(result_vec);
						request.getSession().setAttribute("result_vec", result_vec);

						String nextJSP = "/pages/search_results.jsp";
						 try {
							 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
							 dispatcher.forward(request,response);
							 return;

						 } catch (Exception ex) {
							 ex.printStackTrace();
						 }
					}

				} else if (searchTarget.compareTo("properties") == 0) {
					String serviceUrl = NCImtBrowserProperties.get_SPARQL_SERVICE();
					SPARQLSearchUtils sparqlSearchUtils = new SPARQLSearchUtils(serviceUrl, null, null);
					//String named_graph = request.getParameter("ng");//get_named_graph();

					String propertyName = null;
					SearchResult sr = sparqlSearchUtils.search(named_graph, propertyName, matchText, searchTarget, algorithm);

					List list = sr.getMatchedConcepts();

					if (list == null || list.size() == 0) {
						System.out.println("searchAction searcher.executeQuery returns NULL???");
						request.getSession().setAttribute("matchText", matchText);
						request.getSession().setAttribute("algorithm", algorithm);
						request.getSession().setAttribute("searchTarget", searchTarget);
						String message = "No match.";
						request.getSession().setAttribute("message", message);

					    String nextJSP = "/pages/search_results.jsf";
					    dispatch(request,response,nextJSP);
								//return "message";

					}

 					Vector result_vec = SearchResult.searchResult2DelimitedStrings(sr);

                    String code = null;
                    for (int i=0; i<list.size(); i++) {
						MatchedConcept mc = (MatchedConcept) list.get(i);
						code = mc.getCode();
						String entityDescription = mc.getLabel();
						result_vec.add(entityDescription + "|" + code);
					}
					if (result_vec.size() == 1) {
						String t = (String) result_vec.elementAt(0);
						Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
						code = (String) u.elementAt(1);
						request.getSession().setAttribute("code", code);
						request.getSession().setAttribute("matchText", matchText);
						request.getSession().setAttribute("algorithm", algorithm);
						request.getSession().setAttribute("searchTarget", searchTarget);
						//request.getSession().setAttribute("result_vec", result_vec);

					String nextJSP = "/pages/search_results.jsf";
					dispatch(request,response,nextJSP);
						//return "match";
					}

					request.getSession().setAttribute("matchText", matchText);
					request.getSession().setAttribute("algorithm", algorithm);
					request.getSession().setAttribute("searchTarget", searchTarget);
					//result_vec = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(result_vec);
					request.getSession().setAttribute("result_vec", result_vec);

					//return "search_results";
					String nextJSP = "/pages/search_results.jsf";
					dispatch(request,response,nextJSP);

				} else if (searchTarget.compareTo("relationships") == 0) {
					String serviceUrl = NCImtBrowserProperties.get_SPARQL_SERVICE();
					RelationSearchUtils relationSearchUtils = new RelationSearchUtils(serviceUrl, null, null);
					String associationName = null;
					SearchResult sr = relationSearchUtils.search(associationName, matchText, algorithm, RelationSearchUtils.MATCH_SOURCE);
					Vector result_vec = SearchResult.searchResult2DelimitedStrings(sr);
					if (result_vec.size() == 1) {
						String t = (String) result_vec.elementAt(0);
						Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(t);
						String code = (String) u.elementAt(1);

						request.getSession().setAttribute("code", code);
						request.getSession().setAttribute("matchText", matchText);
						request.getSession().setAttribute("algorithm", algorithm);
						request.getSession().setAttribute("searchTarget", searchTarget);

					String nextJSP = "/pages/search_results.jsf";
					dispatch(request,response,nextJSP);


						//return "match";
					}
					request.getSession().setAttribute("matchText", matchText);
					request.getSession().setAttribute("algorithm", algorithm);
					request.getSession().setAttribute("searchTarget", searchTarget);
					result_vec = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(result_vec);
					request.getSession().setAttribute("result_vec", result_vec);

					//return "search_results";
					String nextJSP = "/pages/search_results.jsf";
					dispatch(request,response,nextJSP);
				}
			} catch (Exception ex) {
                ex.printStackTrace();
			}
			String message = "No match found.";

			request.getSession().setAttribute("matchText", matchText);
			request.getSession().setAttribute("algorithm", algorithm);
			request.getSession().setAttribute("searchTarget", searchTarget);

			request.getSession().setAttribute("message", message);

					String nextJSP = "/pages/search_results.jsf";
					dispatch(request,response,nextJSP);
		*/
	}

/*

    private void resolve_value_set(HttpServletRequest request, HttpServletResponse response) {
		 String code = (String) request.getParameter("vsd_uri");
		 System.out.println("code " + code);

		 GeneralizedQueryUtils gqu = NCImtBrowserProperties.createGeneralizedQueryUtils();
		 String named_graph = NCImtBrowserProperties.get_default_named_graph();
		 System.out.println("named_graph " + named_graph);
		 OWLSPARQLUtils util = gqu.getOWLSPARQLUtils();
		 Vector w = util.getConceptsInSubset(named_graph, code);
		 w = new ParserUtils().getResponseValues(w);
		 w = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(w);
		 request.getSession().setAttribute("resovled_value_set", w);
		 String nextJSP = "/pages/resolved_value_set.jsf?nav_type=valuesets";
		 try {
			 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
			 dispatcher.forward(request,response);
			 return;

		 } catch (Exception ex) {
			 ex.printStackTrace();
		 }
	}
*/
    public void dispatch(HttpServletRequest request, HttpServletResponse response, String nextJSP) {
		 try {
			 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
			 dispatcher.forward(request,response);
			 return;

		 } catch (Exception ex) {
			 ex.printStackTrace();
		 }
	}

    public String uri2Code(String uri) {
		int n = uri.lastIndexOf("#");
		if (n == -1) {
			n = uri.lastIndexOf("/");
		}
		String code = uri.substring(n+1, uri.length());
		code = code.replaceAll("_", ":");
		return code;
	}

/*
    public Vector reformatSearchResults(Vector v) {
		if (v == null) return null;
		Vector w = new Vector();
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(line, '|');
			String ng = (String) u.elementAt(0);
			String cs_version = namedGraph2Key(ng);
			String uri = (String) u.elementAt(1);
			String id = uri2Code(uri);
			String label = (String) u.elementAt(2);
			w.add(cs_version + "|" + ng + "|" + label + "|" + id);
		}
		w = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(w);
		return w;
	}

    public String namedGraph2Key(String named_graph) {
		HashMap nameVersion2NamedGraphMap = NCImtBrowserProperties.getNameVersion2NamedGraphMap();
		Iterator it = nameVersion2NamedGraphMap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Vector u = gov.nih.nci.evs.restapi.util.StringUtils.parseData(key, '|');
			String scheme = (String) u.elementAt(0);
			String version = (String) u.elementAt(1);
			Vector v = (Vector) nameVersion2NamedGraphMap.get(key);
			if (v.contains(named_graph)) {
				return key;
			}
		}
		return null;
	}


    private void multiple_search(HttpServletRequest request, HttpServletResponse response) {
        GeneralizedQueryUtils gqu = NCImtBrowserProperties.createGeneralizedQueryUtils();
        int maxReturn = -1;

        String matchText = request.getParameter("matchText");
        if (matchText != null) {
			matchText = matchText.trim();
		}
        String algorithm = request.getParameter("algorithm");
        String searchTarget = request.getParameter("searchTarget");

        System.out.println("matchText: " + matchText);
        System.out.println("algorithm: " + algorithm);
        System.out.println("searchTarget: " + searchTarget);

		Vector w = gqu.search_by_name(matchText, maxReturn);
		if (w == null || w.size() == 0) {
			String message = "No match.";
			System.out.println(message);
			request.getSession().setAttribute("message", message);
		} else {
			w = reformatSearchResults(w);
			System.out.println("multiple_search_results: " + w.size());
			gov.nih.nci.evs.restapi.util.StringUtils.dumpVector("multiple_search_results", w);
		}

		request.getSession().setAttribute("multiple_search_results", w);
		String nextJSP = "/pages/multiple_search_results.jsf?nav_type=terminologies";
		try {
			 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
			 dispatcher.forward(request,response);
			 return;

		} catch (Exception ex) {
			 ex.printStackTrace();
		}
	}


    private void search_value_set(HttpServletRequest request, HttpServletResponse response) {
         ValueSetSearchUtils vssu = new ValueSetSearchUtils(NCImtBrowserProperties.get_SPARQL_SERVICE());
		 String matchText = (String) request.getParameter("matchText");
		 String algorithm = (String) request.getParameter("algorithm");
		 if (algorithm == null) {
			 algorithm = SPARQLSearchUtils.EXACT_MATCH;
		 }
		 String named_graph = NCImtBrowserProperties.get_default_named_graph();
		 Vector w = vssu.searchValueSets(named_graph, matchText, algorithm);
		 w = new gov.nih.nci.evs.restapi.util.SortUtils().quickSort(w);
		 request.getSession().setAttribute("value_set_search_results", w);
		 String nextJSP = "/pages/value_set_search_results.jsf?nav_type=valuesets&mode=1";
		 try {
			 RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
			 dispatcher.forward(request,response);
			 return;

		 } catch (Exception ex) {
			 ex.printStackTrace();
		 }
	}

*/

    public void selectCSVersionAction(HttpServletRequest request, HttpServletResponse response) {
		String uri = (String) request.getParameter("vsd_uri");
		request.getSession().setAttribute("vsd_uri", uri);
        try {
			String nextJSP = "/pages/resolve_value_set.jsf";
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
			dispatcher.forward(request,response);
			return;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}


