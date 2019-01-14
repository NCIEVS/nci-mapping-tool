package gov.nih.nci.evs.mapping.util;
import gov.nih.nci.evs.mapping.common.*;
import gov.nih.nci.evs.mapping.bean.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class Text2HTMLConverter {
   String inputfile = null;
   String outputfile = null;
   MappingUtils mu = null;
   String sourceTerminology = null;
   String targetTerminology = null;

   static String DEFAULT_MAPPPING_TOOL_URL = "http://localhost:8080/ncimappingtool/pages/search.jsf?ng=http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl&term=";
   String mappingToolURL = DEFAULT_MAPPPING_TOOL_URL;

   public Text2HTMLConverter() {
       mu = new MappingUtils();
       mappingToolURL = DEFAULT_MAPPPING_TOOL_URL;
   }

   public Text2HTMLConverter(String sourceTerminology, String targetTerminology) {
       mu = new MappingUtils();
       mappingToolURL = DEFAULT_MAPPPING_TOOL_URL;
       assignTerminologies(sourceTerminology, targetTerminology);
   }

   //http://localhost:8080/ncimappingtool/pages/search.jsf?ng=http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl&term=
   public void assignMappingToolURL(String url) {
	   this.mappingToolURL = url;
   }

  //    public static String DEFAULT_MAPPPING_TOOL_URL = "http://localhost:8080/ncimappingtool/pages/search.jsf?ng=http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl&term=";

   public void assignMappingToolURL(String host, String namedGraph) {
	   this.mappingToolURL = "http://localhost:8080/" + "ncimappingtool/pages/search.jsf?ng="
	    + namedGraph + "&term=";
   }

   public String getMappingToolURL() {
	   return this.mappingToolURL;
   }

   public void assignTerminologies(String sourceTerminology, String targetTerminology) {
	   this.sourceTerminology = sourceTerminology;
	   this.targetTerminology = targetTerminology;
   }

   public void writeHeader(PrintWriter out, String title) {
      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
      out.println("<html lang=\"en\" xmlns:c=\"http://java.sun.com/jsp/jstl/core\">");
      out.println("  <head>");
      out.println("    <script");
      out.println("        src=\"//assets.adobedtm.com/f1bfa9f7170c81b1a9a9ecdcc6c5215ee0b03c84/satelliteLib-4b219b82c4737db0e1797b6c511cf10c802c95cb.js\">");
      out.println("    </script>");
      out.println("    <title>" + title + "</title>");
      out.println("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
      out.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"css/ncitbrowser/styleSheet.css\" />");
      out.println("    <link rel=\"shortcut icon\" href=\"ncitbrowser/favicon.ico\" type=\"image/x-icon\" />");
      out.println("    <script type=\"text/javascript\" src=\"ncitbrowser/js/script.js\"></script>");
      out.println("    <script type=\"text/javascript\" src=\"ncitbrowser/js/search.js\"></script>");
      out.println("    <script type=\"text/javascript\" src=\"ncitbrowser/js/dropdown.js\"></script>");
      out.println("  </head>");
      out.println("  <body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\">");
      out.println("    <script type=\"text/javascript\" src=\"ncitbrowser/js/wz_tooltip.js\"></script>");
      out.println("    <script type=\"text/javascript\" src=\"ncitbrowser/js/tip_centerwindow.js\"></script>");
      out.println("    <script type=\"text/javascript\" src=\"ncitbrowser/js/tip_followscroll.js\"></script>");
      out.println("");
   }

   public void writeTableHeaders(PrintWriter out) {
      out.println("");
      out.println("            <table class=\"datatable_960\" border=\"0\" width=\"100%\">");
      out.println("");
      out.println("              <th class=\"dataTableHeader\" width=\"100px\" scope=\"col\" align=\"left\">Source</th>");
      out.println("");
      out.println("              <th class=\"dataTableHeader\" width=\"100px\" scope=\"col\" align=\"left\">");
      out.println("                 Source Code");
      out.println("");
      out.println("              </th>");
      out.println("");
      out.println("              <th class=\"dataTableHeader\" scope=\"col\" align=\"left\">");
      out.println("");
      out.println("");
      out.println("                  <a");
      out.println("                      href=\"ncitbrowser/pages/mapping.jsf?nav_type=mappings&dictionary=GO_to_NCIt_Mapping&version=1.1&sortBy=2\">");
      out.println("");
      out.println("                    Source Name</a>");
      out.println("");
      out.println("");
      out.println("              </th>");
      out.println("");
      out.println("              <th class=\"dataTableHeader\" width=\"30px\" scope=\"col\" align=\"left\">");
      out.println("");
      out.println("");
      out.println("                  <a");
      out.println("                      href=\"ncitbrowser/pages/mapping.jsf?nav_type=mappings&dictionary=GO_to_NCIt_Mapping&version=1.1&sortBy=4\">");
      out.println("");
      out.println("                    REL</a>");
      out.println("");
      out.println("");
      out.println("              </th>");
      out.println("");
      out.println("");
      out.println("                <th class=\"dataTableHeader\" width=\"35px\" scope=\"col\" align=\"left\">");
      out.println("");
      out.println("");
      out.println("                      Map Rank");
      /*
      out.println("                    <a");
      out.println("                        href=\"ncitbrowser/pages/mapping.jsf?nav_type=mappings&dictionary=GO_to_NCIt_Mapping&version=1.1&sortBy=5\">");
      out.println("");
      out.println("                      Map Rank</a>");
      out.println("");

      out.println("                    <a");
      out.println("                        href=\"#\"");
      out.println("                        onclick=\"javascript:window.open('ncitbrowser/pages/rank_help_info.jsf', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');\">");
      out.println("");
      out.println("                      <img");
      out.println("                          src=\"ncitbrowser/images/help.gif\"");
      out.println("                          alt=\"Rank Definitions\"");
      out.println("                          title=\"Rank Definitions\"");
      out.println("                          border=\"0\">");
      out.println("");
      out.println("                    </a>");
      */
      out.println("");
      out.println("");
      out.println("");
      out.println("                </th>");
      out.println("");
      out.println("");
      out.println("");
      out.println("              <th class=\"dataTableHeader\" width=\"100px\" scope=\"col\" align=\"left\">Target</th>");
      out.println("");
      out.println("              <th class=\"dataTableHeader\" width=\"100px\" scope=\"col\" align=\"left\">");
      out.println("");
      out.println("");
      out.println("                  <a");
      out.println("                      href=\"ncitbrowser/pages/mapping.jsf?nav_type=mappings&dictionary=GO_to_NCIt_Mapping&version=1.1&sortBy=6\">");
      out.println("");
      out.println("                    Target Code</a>");
      out.println("");
      out.println("");
      out.println("              </th>");
      out.println("");
      out.println("              <th class=\"dataTableHeader\" scope=\"col\" align=\"left\">");
      out.println("");
      out.println("");
      out.println("                  <a");
      out.println("                      href=\"ncitbrowser/pages/mapping.jsf?nav_type=mappings&dictionary=GO_to_NCIt_Mapping&version=1.1&sortBy=7\">");
      out.println("");
      out.println("                    Target Name</a>");
      out.println("");
      out.println("");
      out.println("              </th>");
      out.println("");
   }

	public void writeRowData(PrintWriter out, String line, int i) {
		boolean is_even = isEven(i);
		String colorstr = "datacollight";
		if (is_even) {
			colorstr = "datacoldark";
		}
        Vector v = mu.parseData(line, '|');
        String sourceCode = (String) v.elementAt(0);
        String sourceTerm = (String) v.elementAt(1);
        String targetCode = (String) v.elementAt(2);
        String targetLabel = (String) v.elementAt(3);

		out.println("                  <tr>");
		out.println("                    <td class=\"" + colorstr + "\" scope=\"row\">" + sourceTerminology + "</td>");
		out.println("                    <td class=\"" + colorstr + "\">");
		if (sourceTerminology != null && sourceCode.compareTo("NA") != 0) {
			out.println("                      <a");
			out.println("                          href=\"https://nciterms65.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=" + sourceTerminology + "&code=" + sourceCode + "\">");
			out.println("                        " + sourceCode);
			out.println("                      </a>");
		} else {
			out.println("                        " + sourceCode);
		}
		out.println("                    </td>");

		if (targetCode != null && targetCode.length() > 0) {
			out.println("                    <td class=\"" + colorstr + "\">" + sourceTerm + "</td>");
		} else {
			out.println("<td class=\"datacoldark\">");
			out.println("<a href=\"" + this.mappingToolURL + sourceTerm + "\">");
			out.println(sourceTerm);
			out.println("</a>");
			out.println("</td>");
		}

		out.println("                    <td class=\"textbody\">SY</td>");
		out.println("                      <td class=\"textbody\">1</td>");
		out.println("                    <td class=\"" + colorstr + "\">" + targetTerminology + "</td>");
		out.println("                    <td class=\"" + colorstr + "\">");
		out.println("                      <a");
		//out.println("                          href=\"ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&version=18.10e&ns=ncit&code=C17087\">");
		out.println("                          href=\"https://nciterms65.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=" + targetTerminology + "&code=" + targetCode + "\">");
		out.println("                        " + targetCode);
		out.println("                      </a>");
		out.println("                    </td>");
		out.println("                    <td class=\"" + colorstr + "\">" + targetLabel + "</td>");
		out.println("");
		out.println("                  </tr>");
	}

	public void generate(String inputfile) {
		if (sourceTerminology == null && targetTerminology == null) {
			sourceTerminology = getMappingSource(inputfile);
			targetTerminology = getMappingTarget(inputfile);
		}
		generate(inputfile, null);
	}

    public static boolean isEven(int i) {
        return (i % 2) == 0;
    }

	public void generate(PrintWriter pw, String title) {
		writeHeader(pw, "Mapping");
		writeTableHeaders(pw);
		Vector v = mu.readFile(this.inputfile);
		for (int i=1; i<v.size(); i++) {
		  String line = (String) v.elementAt(i);
		  writeRowData(pw, line, i);
		}
		pw.println("            </table>");
		pw.println("  </body>");
		pw.println("</html>");
	}

	public void generate(String inputfile, String outputfile) {
		if (this.sourceTerminology == null && this.targetTerminology == null) {
			this.sourceTerminology = getMappingSource(inputfile);
			this.targetTerminology = getMappingTarget(inputfile);
		}
		this.inputfile = inputfile;
		if (outputfile == null) {
			outputfile = this.outputfile;
		}
		long ms = System.currentTimeMillis();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			int n = inputfile.lastIndexOf(".");
			String title = inputfile.substring(0, n);
			generate(pw, title);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
	}


	public static String getMappingSource(String mappingfile) {
		int n = mappingfile.indexOf("_");
		if (n != -1) {
			return mappingfile.substring(0, n);
		}
		return null;
	}

	public static String getMappingTarget(String mappingfile) {
		int n = mappingfile.lastIndexOf("_");
		if (n != -1) {
			String t = mappingfile.substring(0, n);
			String s = getMappingSource(mappingfile);
			t = t.substring(s.length() + "_to_".length(), t.length());
			return t;
		}
		return null;
	}

    public static void main(String[] args) {
		/*
		String source = args[0];
		if (source.compareTo("null") == 0) {
			source = null;
		}
		String target = args[1];
		String inputfile = args[2];
		String outputfile = null;
		if (args.length > 3) {
		    outputfile = args[3];
		} else {
			int n = inputfile.lastIndexOf(".");
			outputfile = inputfile.substring(0, n) + "_table.html";
		}
		Text2HTMLConverter test = new Text2HTMLConverter(source, target);
		*/
		Text2HTMLConverter test = new Text2HTMLConverter();
		String mappingUrl = "http://localhost:8080/ncimappingtool/pages/search.jsf?ng=http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl&term=";
		test.assignMappingToolURL(mappingUrl);
		String inputfile = args[0];

		System.out.println("inputfile: " + inputfile);


			int n = inputfile.lastIndexOf(".");

			System.out.println("n: " + n);

			String outputfile = inputfile.substring(0, n) + "_table.html";

			System.out.println("outputfile: " + outputfile);

		test.generate(inputfile, outputfile);
	}

}