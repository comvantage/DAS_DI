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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.update.UpdateException;

import eu.comvantage.dataintegration.data.Credentials;
import eu.comvantage.dataintegration.data.Endpoint;
import eu.comvantage.dataintegration.data.QueryResult;

public class DHMConnector {
	private Endpoint endpoint;
	private Credentials credentials;
	
	public DHMConnector(Endpoint endpoint){
		this.endpoint = endpoint;
	}
	
	public DHMConnector(Endpoint endpoint, Credentials credentials){
		this.endpoint = endpoint;
		this.credentials = credentials;
	}
	

	public HttpResponse query(String location, String arguments){
		HttpResponse response = null;
		String queryString=location+"?"+arguments;
		queryString = endpoint.getEndpointURL() + "/" + queryString;
		
		try {
			HttpGet httpGet = new HttpGet(queryString) ;
            
//		ByteArrayOutputStream b_out = new ByteArrayOutputStream();
//      OutputStreamWriter wr = new OutputStreamWriter(b_out);
//		wr.write(queryString);
//		wr.flush();
//		byte[] bytes = b_out.toByteArray() ;
//		AbstractHttpEntity reqEntity = new ByteArrayEntity(bytes) ;
//      eqEntity.setContentType("application/x-www-form-urlencoded") ;
//      reqEntity.setContentEncoding(HTTP.UTF_8) ;
//      httpPost.setEntity(reqEntity) ;
            HttpClient httpclient = new DefaultHttpClient();
            
            //set proxy if defined
            if(System.getProperty("http.proxyHost") != null) {
            	HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.valueOf(System.getProperty("http.proxyPort")), "http");
                httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }
            
            try
            {
            	System.out.println("DHMConnector, Command: "+queryString);
                response = httpclient.execute(httpGet) ;
                int responseCode = response.getStatusLine().getStatusCode() ;
                String responseMessage = response.getStatusLine().getReasonPhrase() ;
                
	            if ( responseCode == 204 )
	            	System.out.println("DHMConnector response: NO_CONTENT_204");
	            if ( responseCode == 200 )
	            	System.out.println("DHMConnector response: OK_200");
	            if ( responseCode == 404 )
	            	System.out.println("DHMConnector response: NOT_FOUND_404");
	            
	            //wr.close();
            } catch (IOException ex)
            {
            	response = null;
            	//throw new UpdateException(ex);
            }
        
		} catch (Exception e) {
			System.out.println("DHMConnector: Can't build HTTP GET object, arguments string is corrupted");
			//e.printStackTrace();
    	}
		return response;
	}
}