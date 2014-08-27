/*
 * Copyright 2014 SAP SE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.comvantage.dataintegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import eu.comvantage.dataintegration.data.DistributedRequest;
import eu.comvantage.dataintegration.data.QueryResult;
import eu.comvantage.dataintegration.data.QueryResultSet;
import eu.comvantage.dataintegration.utils.SystemParameterManager;
import eu.comvantage.nextel.sparql.SparqlRewriter;
import eu.comvantage.nextel.sparql.SparqlRewriterFactory;
import eu.comvantage.nextel.sparul.SparulComposer;
import eu.comvantage.nextel.sparul.SparulComposerFactory;

/**
 * Servlet implementation class QueryDistributionServiceImpl
 */
public class QueryDistributionServiceImpl extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryDistributionServiceImpl() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
		
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		
		String[] roles = new String[]{};
		//example... String[] roles = new String[]{"http://comvantage.eu/ontologies/pec/examplefactory/cv_wp6_fiat_employee"};
		//example... String[] roles = new String[]{"http://comvantage.eu/ontologies/ac-schema/cv_operator", "http://comvantage.eu/ontologies/ac-schema/cv_manager"};
		
		//handling of optional header "role"
		if(request.getHeader("role") != null) {
			String tempheaderstring = "";
			tempheaderstring = request.getHeader("role");
			
			if(tempheaderstring.contains(";")) {
				roles = tempheaderstring.split(";");
			} else {
				roles = new String[]{tempheaderstring};
			}
		} else if (request.getParameterMap().containsKey("role") && !request.getParameter("role").equals("")) {
			//user simulated user authentication (for debug)
			roles = new String[]{request.getParameter("role")};
		}
		
		//handling SPARQL request
		if (uri.endsWith("/DataIntegration/QueryDistributionServiceImpl/SPARQL")) {
			//optional request parameters and default values
			int limit = SystemParameterManager.getDefaultQueryResultLimit();
			int offset = 0;
			String format = "application/json";
			//mandatory request parameters
			String query = "";
			
			//handling of mandatory request parameters
			if(request.getParameterMap().containsKey("query")) query = request.getParameter("query");
			
			//Verify mandatory parameters
			if(query.equals("")) {
				response.setStatus(500);
				print("QueryDistributionServiceImpl: Mandatory parameters missing (query)",response);
				return;
			}
			
			//handling parameters in SPARQL query
			if(query.contains("LIMIT")) {
				String[] parts = query.split("LIMIT");
				String rest_contains_value = parts[parts.length-1];
				String[] parts_of_value = rest_contains_value.split(" ");
				limit = Integer.valueOf(parts_of_value[1]);
			}
			if(query.contains("OFFSET")) {
				String[] parts = query.split("OFFSET");
				String rest_contains_value = parts[parts.length-1];
				String[] parts_of_value = rest_contains_value.split(" ");
				offset = Integer.valueOf(parts_of_value[1]);
			}
			
			//handling of optional request parameters
			if(request.getParameterMap().containsKey("limit") && !request.getParameter("limit").equals("")) limit = Integer.valueOf(request.getParameter("limit"));
			if(request.getParameterMap().containsKey("offset") && !request.getParameter("offset").equals("")) offset = Integer.valueOf(request.getParameter("offset"));
			if(request.getParameterMap().containsKey("format")) format = request.getParameter("format");
			
			
			System.out.println("QueryDistributionService: handleSPARQLrequest called!");
			System.out.println("QueryDistributionService: SPARQL query: " + query);
			
			handleSPARQLrequest(query,limit,offset,format,roles,response);
		}
		
		//handling SPARUL request
		else if (uri.endsWith("/DataIntegration/QueryDistributionServiceImpl/SPARUL")) {
			
			//security features enabled?
			if(SystemParameterManager.useAccessControlFeatures()) {
				//get message body
				String body = getBody(request);
				if(body.equals("")) {
					response.setStatus(500);
					print("QueryDistributionServiceImpl: JSON body empty",response);
					return;
				}
				
				JSONObject updateCommand = new JSONObject();
				try {
					updateCommand = new JSONObject(body);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setStatus(500);
					print("QueryDistributionServiceImpl: JSON body malformed",response);
					return;
				}
				
				//parse message body for paramters
				Long commandId = new Long(0);
				Long clientId = new Long(0);
				JSONArray jarr = new JSONArray();
				try {
					commandId = updateCommand.getLong("Template");
					jarr = updateCommand.getJSONArray("Params");
					
					if(updateCommand.has("Client")){
						clientId = updateCommand.getLong("Client");
					} else {
						clientId = SystemParameterManager.getDefaultClientId();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setStatus(500);
					print("QueryDistributionServiceImpl: Mandatory parameters missing (Template, Params)",response);
					return;
				}
				
				//get parameters
				HashMap<String, String> parameters = new HashMap<String, String>();

				for(int i = 0; i < jarr.length(); i++){
					try {
						parameters.put(((JSONObject)jarr.get(i)).getString("name"),((JSONObject)jarr.get(i)).getString("value"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						response.setStatus(500);
						print("QueryDistributionServiceImpl: Mandatory parameters malformed (Params)",response);
						return;
					}
				}
				
				System.out.println("QueryDistributionService: handleSPARULrequest called!");
				System.out.	println("QueryDistributionService: SPARUL template selected: " + commandId +", current client: " + clientId);
				
				handleSPARULrequest(commandId, clientId, parameters, roles ,response);
			} else {
				//security features disabled
				
				//mandatory request parameters
				String update = "";
				
				//handling of mandatory request parameters
				if(request.getParameterMap().containsKey("query")) update = request.getParameter("query");
				
				System.out.println("QueryDistributionService: handleSPARULrequest called! (ac disabled)");
				System.out.	println("QueryDistributionService: SPARUL command: " + update);
				handleSPARULrequest(update, response);
			}
		}
		
		//handling DHM request
		else if (uri.endsWith("/DataIntegration/QueryDistributionServiceImpl/DHM")) {
			//optional request parameters and default values
			String dir = "";
			//mandatory request parameters
			String query = "";
			String file = "";
			String arguments = "";
			String host = "";
			
			//handling of mandatory request parameters
			if(request.getParameterMap().containsKey("query")) query = request.getParameter("query");
			if(request.getParameterMap().containsKey("file")) file = request.getParameter("file");
			if(request.getParameterMap().containsKey("arguments")) arguments = request.getParameter("arguments");
			if(request.getParameterMap().containsKey("host")) host = request.getParameter("host");
			
			//Verify mandatory parameters
			if(file.equals("") && arguments.equals("")) {
				response.setStatus(500);
				print("QueryDistributionServiceImpl: Mandatory parameters missing (file, arguments)",response);
				return;
			}
			
			//handling of optional request parameters
			if(request.getParameterMap().containsKey("dir")) dir = request.getParameter("dir");
						
			System.out.println("QueryDistributionService: handleDHMrequest called!");
			System.out.println("QueryDistributionService: DHM request: " + file + "?" + arguments);
			
			handleDHMrequest(host,dir,file,arguments,response);
		}
	}
	
	private void handleSPARQLrequest(String query, int limit, int offset, String format, String[] roles, HttpServletResponse response) throws IOException {			
		//locale variables
		String outstream = "";
		boolean debugModeEnabled = SystemParameterManager.debugModeEnabled();
		boolean acModeEnabled = SystemParameterManager.useAccessControlFeatures();
		boolean bypassRoleMatched = false;
		
		//Very if one of the received roles matches the ac bypass role
		String acBypassRole = SystemParameterManager.getACBypassRole();
		for(int i=0;i<roles.length;i++) {
			if(acBypassRole.equalsIgnoreCase(roles[i])) bypassRoleMatched = true;
		}
		
		//TODO:Remove debug mode output
		if(debugModeEnabled) {
			outstream += "<h1>!!! ATTENTION !!! DEBUG MODE ENABLED !!! </h1>(remove param 'debug_mode = true' from domain configuration)</br></br>";
			outstream += "Query: " + StringEscapeUtils.escapeHtml(query) + "</br>";
			outstream += "Access control mode enabled: " + acModeEnabled + "</br>";
			outstream += "Received roles (" + roles.length + "):</br>";
			for(int i=0;i<roles.length;i++) {
				outstream += "Role "+ String.valueOf(i+1) + ": " + roles[i] + "</br>";
			}
			if (bypassRoleMatched) outstream += "</br>Bypass role (" + acBypassRole + ") matched!</br>";
			else outstream += "</br>Bypass role (" + acBypassRole + ") NOT matched!</br>";
		}
		
		//Rewrite query for access authorization
		SparqlRewriter rewriter;
		String rewrittenSparqlQuery = "";
		try {
			rewriter = SparqlRewriterFactory.getSparqlRewriter();
			
			if(acModeEnabled && roles.length > 0 && !bypassRoleMatched) {
				//TODO:Remove debug mode output
				if(debugModeEnabled) outstream += "QueryDistributionService: Rewriting SPARQL request performed!</br>";
				rewrittenSparqlQuery = rewriter.rewrite(query, roles);
				
				//TODO:Remove debug mode output
				if(debugModeEnabled) outstream += "QueryDistributionService: Rewritten query = "+ StringEscapeUtils.escapeHtml(rewrittenSparqlQuery) + "</br></br>";
			}
			else {
				//TODO:Remove debug mode output
				if(debugModeEnabled) outstream += "QueryDistributionService: Rewriting SPARQL request skipped!</br></br>";
				rewrittenSparqlQuery = query;
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		//Create a distributed request for all (relevant|connected) data sources
		DistributedRequestFactory drfactory = new DistributedRequestFactory();
		DistributedRequest disRequest = drfactory.createSPARQLRequestForAllSources(rewrittenSparqlQuery);
		
		if(disRequest.getRequestList().size() == 0) {
			response.setStatus(500);
			print("QueryDistributionServiceImpl.handleSPARQLrequest: No data sources are registered",response);
			return;
		}
		
		//Execute the query
		QueryResultSet results = new QueryResultSet();
		DistributedRequestExecutionManager dreManager = new DistributedRequestExecutionManager();
		results = dreManager.handleSPARQLRequest(disRequest, limit, offset);
		
		//create data transfer object
		Map<String,Object> dto = new HashMap<String, Object>();
		Map<String,Object> head = new HashMap<String, Object>();
		head.put("vars", results.getVariables());
		dto.put("head", head);
		dto.put("results", results);
		
		//Handle query result for different format options
		if(format.equals("application/json")){
			// format JSON
			Gson gson = new Gson();
			outstream += gson.toJson(dto);
			response.setStatus(200);
		} else if (format.equals("application/xml")){
			// format XML
			outstream += "Content type application/xml not supported, please use 'application/json' instead";
			response.setStatus(500);
		} else if (format.equals("text/plain")){
			// format plain text
			outstream += "Content type text/plain not supported, please use 'application/json' instead";
			response.setStatus(500);
		} else {
			// unknown format used
			outstream += "The selected content type '" + format +"' is unknown, please use 'application/json' instead";
			response.setStatus(500);
		}
		
		//Print result to servlet output stream
		print(outstream, response);
	}
	
	private void handleSPARULrequest(Long commandId, Long clientId, HashMap<String, String> params, String[] roles, HttpServletResponse response) throws IOException{
		
		//run SPARULComposer to check, if user is allowed to access template and to complete template and viewactions (insertion of params)
		SparulComposer composer = null;
		try {
			composer = SparulComposerFactory.getSparulComposer();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SPARULComposerDataConnector dataConnector = new SPARULComposerDataConnector();
		composer.setDataInterface(dataConnector);
		String[] commands = composer.compose(clientId, commandId, params, roles);
		
		if(commands.length==0){
			response.setStatus(500);
			print("QueryDistributionServiceImpl.handleSPARULrequest: No commands have been created (cause: false clientId or commandId, no role specified, malformed or missing template parameters",response);
			return;
		}
		
		//execute template and viewactions via SPARQLConnector (data source url is stored with the template in the repository)
		DistributedRequestFactory drfactory = new DistributedRequestFactory();
		
		String updateEndpoint = dataConnector.getUpdateEndpoint(clientId, commandId);
		if (updateEndpoint==null) {
			response.setStatus(500);
			print("QueryDistributionServiceImpl.handleSPARULrequest: Invalid template or client id!",response);
			return;
		}
		
		DistributedRequest disRequest = drfactory.createSPARULRequestForDedicatedSources(commands, updateEndpoint);
		
		if(disRequest == null || disRequest.getRequestList().size() == 0) {
			response.setStatus(500);
			print("QueryDistributionServiceImpl.handleSPARULrequest: No requests have been created!",response);
			return;
		}
		
		QueryResultSet result = new QueryResultSet();
		DistributedRequestExecutionManager dreManager = new DistributedRequestExecutionManager();
		result = dreManager.handleSPARULRequest(disRequest);
		
		String outstream = "QueryDistributionServiceImpl.handleSPARULrequest: Update executed!";
		response.setStatus(200);
		print(outstream, response);
	}
	
	private void handleSPARULrequest(String update, HttpServletResponse response) throws IOException{
		DistributedRequestFactory drfactory = new DistributedRequestFactory();
		DistributedRequest disRequest = drfactory.createSPARULRequestForUnknownSource(update);
		
		if(disRequest == null || disRequest.getRequestList().size() == 0) {
			response.setStatus(500);
			print("QueryDistributionServiceImpl.handleSPARULrequest: No requests have been created!",response);
			return;
		}
		
		QueryResultSet result = new QueryResultSet();
		DistributedRequestExecutionManager dreManager = new DistributedRequestExecutionManager();
		result = dreManager.handleSPARULRequest(disRequest);
		
		response.setStatus(200);
		print("QueryDistributionServiceImpl.handleSPARULrequest: Update executed!", response);
	}
	
	private void handleDHMrequest(String host, String dir, String file, String arguments, HttpServletResponse response) throws IOException{
		DistributedRequestFactory drfactory = new DistributedRequestFactory();
		DistributedRequest disRequest;
		
		//handle optional parameter "host"
		if(host.equals("")){
			disRequest = drfactory.createDHMRequestForSpecificSource(host, dir, file, arguments);
		} else {
			disRequest = drfactory.createDHMRequestForAllSources(dir, file, arguments);
		}
				
		if(disRequest.getRequestList().size() == 0) {
			response.setStatus(500);
			print("QueryDistributionServiceImpl.handleDHMrequest: No data sources are registered",response);
			return;
		}
		
		DistributedRequestExecutionManager dreManager = new DistributedRequestExecutionManager();
		HttpResponse r = dreManager.handleDHMRequest(disRequest);
		if(r==null) {
			response.setStatus(500);
			print("Internal error occured, maybe </br>", response);
			print("(1) data sources are not accessible </br>", response);
			print("(2) data sources are not configured correctly </br>", response);
			print("(3) command arguments are badly formatted </br>", response);
			print("(4) proxy settings are missing </br>", response);
			return;
		} else {
			response.setStatus(200);
			print(r,response);
		}
	}
	
	private void print(String outstring, HttpServletResponse response) throws IOException {
		//parallel console output
		String[] strings = outstring.split("</br>");
		for(String s:strings) System.out.println(s);
		
		//writing character response stream
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.write(outstring);
	}
	
	private void print(HttpResponse in, HttpServletResponse out) throws IOException {
		//writing binary response stream
		for (Header h : in.getAllHeaders()){
			out.addHeader(h.getName(), h.getValue());
		}
		out.setStatus(in.getStatusLine().getStatusCode());
		out.setLocale(in.getLocale());
		out.setContentType("text/html");
		
		ServletOutputStream outstream = out.getOutputStream();
		int c;
		while ((c = in.getEntity().getContent().read()) != -1) {
			outstream.write(c);
		}
		outstream.flush();
	}
	
	private static String getBody(HttpServletRequest request) throws IOException {
	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}
}