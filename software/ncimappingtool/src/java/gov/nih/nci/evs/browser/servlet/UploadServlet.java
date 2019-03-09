/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-mapping-tool/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.servlet;

import org.json.*;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.bean.MappingObject;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import gov.nih.nci.evs.mapping.*;


/**
 *
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history
 *     Initial implementation kim.ong@ngc.com
 *
 */

public final class UploadServlet extends HttpServlet {
    private static Logger _logger = Logger.getLogger(UploadServlet.class);
    /**
     * local constants
     */
    private static final long serialVersionUID = 1L;

    /**
     * Validates the Init and Context parameters, configures authentication URL
     *
     * @throws ServletException if the init parameters are invalid or any other
     *         problems occur during initialisation
     */
    public void init() throws ServletException {

    }

    /**
     * Route the user to the execute method
     *
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        execute(request, response);
    }

    /**
     * Route the user to the execute method
     *
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a Servlet exception occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        execute(request, response);
    }


	public String convertStreamToString(InputStream is, long size) throws IOException {

		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[(int)size];
			try {
				Reader reader = new BufferedReader(
				new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}

			} finally {
				is.close();
			}
			return writer.toString();

		} else {
			return "";
		}

	}




    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     *
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */

    public static Vector parseData(String line, char delimiter) {
		if(line == null) return null;
		Vector w = new Vector();
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<line.length(); i++) {
			char c = line.charAt(i);
			if (c == delimiter) {
				w.add(buf.toString());
				buf = new StringBuffer();
			} else {
				buf.append(c);
			}
		}
		w.add(buf.toString());
		return w;
	}

    public Vector<String> parseData(String line) {
		if (line == null) return null;
        char tab = '|';
        return parseData(line, tab);
    }


    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
		request.getSession().removeAttribute("msg");
		request.getSession().removeAttribute("mapping_name");

        // Determine request by attributes
        String action = (String) request.getParameter("action");
        System.out.println("action: " + action);
        String type = (String) request.getParameter("type");
        if (action == null) {
			action = "upload_data";
		}

		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		/*
		 *Set the size threshold, above which content will be stored on disk.
		 */
		fileItemFactory.setSizeThreshold(1*1024*1024); //1 MB
		/*
		 * Set the temporary directory to store the uploaded files of size above threshold.
		 */
		//fileItemFactory.setRepository(tmpDir);

		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		String mode = NCImtBrowserProperties.getModeOfOperation();

		System.out.println("(*) mode: " + mode);

        String s = null;
        String msg = null;
        String filename = null;
		try {
			/*
			 * Parse the request
			 */
			List items = uploadHandler.parseRequest(request);
			Iterator itr = items.iterator();
			while(itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				filename = item.getName();
				int n = filename.lastIndexOf("\\");
				if (n != -1) {
					filename = filename.substring(n+1, filename.length());
				} else {
					n = filename.lastIndexOf("/");
					if (n != -1) {
						filename = filename.substring(n+1, filename.length());
				    }
				}
				/*
				 * Handle Form Fields.
				 */
				if(item.isFormField()) {
					System.out.println("File Name = "+item.getFieldName()+", Value = "+item.getString());

				} else {
					//Handle Uploaded files.
					System.out.println("Field Name = "+item.getFieldName()+
						", File Name = "+item.getName()+
						", Content type = "+item.getContentType()+
						", File Size = "+item.getSize());

					s = convertStreamToString(item.getInputStream(), item.getSize());
					request.getSession().setAttribute("action", action);

                    if (mode.compareTo("batch") == 0 || mode.compareTo("interactive") == 0) {
						if (action.compareTo("upload_data") == 0) {
							request.getSession().setAttribute("codes", s);
						} else {
							Mapping mapping = new Mapping().toMapping(s);

							System.out.println("Mapping " + mapping.getMappingName() + " uploaded.");
							System.out.println("Mapping version: " + mapping.getMappingVersion());

							MappingObject obj = mapping.toMappingObject();
							HashMap mappings = (HashMap) request.getSession().getAttribute("mappings");
							if (mappings == null) {
								mappings = new HashMap();
							}
							mappings.put(obj.getKey(), obj);
							request.getSession().setAttribute("mappings", mappings);
						}
				    } else {
						if (action.compareTo("upload_data") == 0) {
							if (!filename.endsWith("txt")) {
								msg = "WARNING: Invalid file format.";
							}
							request.getSession().setAttribute("data", s);
						} else {
							if (!filename.endsWith("csv")) {
								msg = "WARNING: Invalid file format.";
							}
							request.getSession().setAttribute("mapping_data", s);
						}
					}
				}
			}
		}catch(FileUploadException ex) {
			log("Error encountered while parsing the request",ex);
		} catch(Exception ex) {
			log("Error encountered while uploading file",ex);
		}
		if (filename != null) {
			request.getSession().setAttribute("mapping_name", filename);
		}

        if (mode.compareTo("batch") == 0 || mode.compareTo("interactive") == 0) {
			if (action.compareTo("upload_data") == 0) {
				if (type.compareTo("codingscheme") == 0) {
					response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/pages/codingscheme_data.jsf"));
				} else if (type.compareTo("ncimeta") == 0) {
					response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/pages/ncimeta_data.jsf"));
				} else if (type.compareTo("valueset") == 0) {
					response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/pages/valueset_data.jsf"));
				}		} else {
				response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/pages/home.jsf"));
			}
		} else {
			if (action.compareTo("upload_data") == 0) {
				System.out.println("*** redirect to enter_mapping_data.jsf");
				if (msg != null) {
				    request.getSession().setAttribute("msg", msg);
				}
				response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/pages/enter_mapping_data.jsf"));

			} else {
				try {
					if (msg == null) {
						Vector<String> v = parseData(s, '\n');
						List entries = new ArrayList();
						int match_knt = 0;
						int size = 0;
						for (int i=1; i<v.size(); i++) {
							String t = (String) v.elementAt(i);
							t = t.trim();
							if (t.length() > 0) {
								size++;
								List<String> values = Arrays.asList(t.split("\\s*,\\s*"));
								String source_code = values.get(0);
								source_code = source_code.substring(1, source_code.length()-1);

								String source_term = values.get(1);
								source_term = source_term.substring(1, source_term.length()-1);

								String target_code = values.get(2);
								target_code = target_code.substring(1, target_code.length()-1);

								String target_term = values.get(3);
								target_term = target_term.substring(1, target_term.length()-1);

								if (target_term.startsWith("\"")) {
									target_term = target_term.substring(1, target_term.length());
								}
								if (target_term.endsWith("\"")) {
									target_term = target_term.substring(0, target_term.length()-1);
								}
								gov.nih.nci.evs.mapping.bean.MappingEntry m = new gov.nih.nci.evs.mapping.bean.MappingEntry(source_code, source_term, target_code, target_term);
								if (target_code != null && target_code.length() > 0) {
									match_knt++;
								}
								entries.add(m);
							}
						}

						gov.nih.nci.evs.mapping.bean.Mapping mapping = new gov.nih.nci.evs.mapping.bean.Mapping(size, match_knt, entries);
						request.getSession().removeAttribute("mapping_data");
						request.getSession().setAttribute("mapping", mapping);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					msg = "WARNING: Invalid file format.";
				}

				if (msg == null) {
				    response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/pages/auto_mapping_results.jsf"));
				} else {
					request.getSession().setAttribute("msg", msg);
					response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/pages/home_alt.jsf"));
				}

			}
		}

    }
}
