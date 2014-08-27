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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;

/**
 * Servlet implementation class SimulationServlet
 */
public class SparulSimulationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SparulSimulationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String eventID = "";
		if(request.getParameterMap().containsKey("eventID")) eventID = request.getParameter("eventID");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String uri = request.getRequestURI();
		ServletContext context = getServletContext();
		
		String serverName = "";
		if(request.getLocalName().startsWith("0")) {
			serverName = "localhost";
		} else {
			serverName = request.getLocalName();
		}
		
		simulateEvent(eventID, new String("http://" + serverName +":"+request.getLocalPort()+request.getContextPath()+"/QueryDistributionServiceImpl/SPARUL"));		
		out.write("'"+eventID+"' simulated. ");
	}
	
	private void simulateEvent(String eventID, String endpoint) {
		//container for the final SPARUL update command including header information
		String body="";
		//additional body information for the SPARUL update command

		//try to encode the SPARUL update command string with UTF-8
		String temp = "";
		Gson gson = new Gson();
		
		//simulation of a correct request
		if(eventID.equalsIgnoreCase("sparul1")) {
			temp = "{ "
					+ "\"Template\" : 1, "
					+ "\"Client\" : 1, "
					+ "\"Params\" : [{\"name\":\"ticket\", \"value\":\"ex:Ticket0070071239swd\"}, " 
					+ "{\"name\":\"person\", \"value\":\"ex:nn00110011\"}]"
					+ "}";
		}
		//simulation of invalid template id for client
		else if(eventID.equalsIgnoreCase("sparul2")) {
			temp = "{ "
					+ "\"Template\" : 8, "
					+ "\"Client\" : 1, "
					+ "\"Params\" : [{\"name\":\"ticket\", \"value\":\"ex:Ticket008008123swd\"}, " 
					+ "{\"name\":\"person\", \"value\":\"ex:nn1234567\"}]"
					+ "}";
		}
		//simulation of invalid client id
		else if(eventID.equalsIgnoreCase("sparul3")) {
			temp = "{ "
					+ "\"Template\" : 1, "
					+ "\"Client\" : 3, "
					+ "\"Params\" : [{\"name\":\"ticket\", \"value\":\"ex:Ticket000000000swd\"}, " 
					+ "{\"name\":\"person\", \"value\":\"ex:nn55555\"}]"
					+ "}";
		}
		//simulation of invalid parameter for specified template
		else if(eventID.equalsIgnoreCase("sparul4")) {
			temp = "{ "
					+ "\"Template\" : 1, "
					+ "\"Client\" : 1, "
					+ "\"Params\" : [{\"name\":\"bla\", \"value\":\"ex:Ticket98761234swd\"}, " 
					+ "{\"name\":\"person\", \"value\":\"ex:nn223344\"}]"
					+ "}";
		}
		//simulation of invalid parameter for specified template
		else if(eventID.equalsIgnoreCase("sparul5")) {
			temp = "{ "
					+ "\"Templates\" : 1, "
					+ "\"Clients\" : 1, "
					+ "\"Param\" : [{\"name\":\"bla\", \"value\":\"ex:Ticket98761234swd\"}, " 
					+ "{\"name\":\"person\", \"value\":\"ex:nn223344\"}]"
					+ "}";
		}
		//malformed json
		else if(eventID.equalsIgnoreCase("sparul6")) {
			temp = "blabla";
		} 
		//simulation of a correct request
		else if(eventID.equalsIgnoreCase("sparul7")) {
			temp = "{ "
					+ "\"Template\" : 1, "
					+ "\"Client\" : 1, "
					+ "\"Params\" : [{\"name\":\"templateId\", \"value\":\"tee:Ticket0070071239swd\"}], "
					+ "}";
		//test of the long statement parameters of file client1_test0
		} else if(eventID.equalsIgnoreCase("sparul8")) {
			temp = "{ "
					+ "\"Template\" : 1, "
					+ "\"Client\" : 1, "
					+ "\"Params\" : [{\"name\":\"templateId\", \"value\":\"tee:test1\"}, " 
					+ "{\"name\":\"reportId\", \"value\":\"1\"},"
					+ "{\"name\":\"device1\", \"value\":\"tee:test2\"},"
					+ "{\"name\":\"device2\", \"value\":\"tee:test3\"},"
					+ "{\"name\":\"device3\", \"value\":\"tee:test4\"}]"
					+ "}";
			
		}
		//body = gson.toJson(temp);
		body = temp;
		

		//try to execute the SPARUL update command
		try {
			//insertion is done by a manual HTTP post
			HttpPost httpPost = new HttpPost(endpoint) ;

			//put SPARUL update command to output stream
			ByteArrayOutputStream b_out = new ByteArrayOutputStream() ;
			OutputStreamWriter wr = new OutputStreamWriter(b_out);
			wr.write(body);
			wr.flush();

			//transform output stream and modify header information for HTTP post			    
			byte[] bytes = b_out.toByteArray() ;
			AbstractHttpEntity reqEntity = new ByteArrayEntity(bytes) ;
			reqEntity.setContentType("application/x-www-form-urlencoded") ;
			reqEntity.setContentEncoding(HTTP.UTF_8) ;
			httpPost.setEntity(reqEntity);
			httpPost.setHeader("role", "http://www.comvantage.eu/ontologies/ac-schema/cv_wp6_comau_employee");

			HttpClient httpclient = new DefaultHttpClient() ;
			
//			 //set proxy if defined
//            if(System.getProperty("http.proxyHost") != null) {
//            	HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.valueOf(System.getProperty("http.proxyPort")), "http");
//                httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//            }
            
			try {
				//execute the HTTP put
				System.out.println("SparulSimulationServlet: Event '"+eventID+"' simulated at endpoint "+endpoint);
				HttpResponse response = httpclient.execute(httpPost) ;
				
				//handle different server responses and failures
				int responseCode = response.getStatusLine().getStatusCode() ;
				String responseMessage = response.getStatusLine().getReasonPhrase() ;
				System.out.println("SparulSimulationServlet: Response = "+responseCode+", "+responseMessage);
				//close the output stream
				wr.close();
			} catch (IOException ex)
			{
				throw new Exception(ex) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}