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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.opensaml.ws.soap.client.http.HttpClientBuilder;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.update.UpdateException;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion.User;

import eu.comvantage.dataintegration.data.Credentials;
import eu.comvantage.dataintegration.data.Endpoint;
import eu.comvantage.dataintegration.data.QueryResultSet;
import eu.comvantage.dataintegration.utils.SystemParameterManager;

public class SPARQLConnector implements Connector{
	private Endpoint sparqlEndpoint;
	private Credentials credentials;
	
	public SPARQLConnector(Endpoint sparqlEndpoint){
		this.sparqlEndpoint = sparqlEndpoint;
		this.credentials = null;
	}
	
	public SPARQLConnector(Endpoint sparqlEndpoint, Credentials credentials){
		this.sparqlEndpoint = sparqlEndpoint;
		this.credentials = credentials;
	}
	
	@Override
	public QueryResultSet query(eu.comvantage.dataintegration.data.Query q, int limit, int offset){
		Query query = QueryFactory.create(q.getQueryString(), Syntax.syntaxARQ);
		if(limit!=-1) query.setLimit(limit); //size of result set
		if(offset!=-1) query.setOffset(offset); //position of first pointer

		QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlEndpoint.getEndpointURL(), query);
		System.out.println("SPARQLConnector, Query: "+query+"; Endpoint: "+sparqlEndpoint.getEndpointURL());
		ResultSet resultSet = qe.execSelect();
		
		QueryResultSet result = new QueryResultSet(resultSet.getResultVars());
		while (resultSet.hasNext()) {
		      //move the pointer forward for the next result
		      QuerySolution solution = resultSet.next();

		      List<String[]> bindings = new ArrayList<String[]>();
		      for (String var : resultSet.getResultVars()){
		    	  
		    	  if(solution.get(var).isAnon()){
		    		  //handling result type ANON
		    		  bindings.add(new String[] {var,"anonymous",solution.get(var).toString()});
		    	  } else if (solution.get(var).isLiteral()){
		    		  //handling result type LITERAL
		    		  
		    		  //TODO: Remove debug info
		    		  String languange = solution.get(var).asLiteral().getLanguage();
		    		  String lexicalForm = solution.get(var).asLiteral().getLexicalForm();
		    		  String string = solution.get(var).asLiteral().getString();
		    		  //System.out.println("SPARQLConnector, TYPE LITERAL language = " + languange);
		    		  //System.out.println("SPARQLConnector, TYPE LITERAL lexical form = " + lexicalForm);
		    		  System.out.println("SPARQLConnector, TYPE LITERAL string = " + string);
		    		  
		    		  bindings.add(new String[] {var,"literal",solution.get(var).asLiteral().getString()}); 
		    	  } else if (solution.get(var).isResource()){
		    		  //handling result type RESOURCE
		    		  
		    		  String localName = solution.get(var).asResource().getLocalName();
		    		  String uri = solution.get(var).asResource().getURI();
		    		  String string = solution.get(var).asResource().toString();
		    		  //System.out.println("SPARQLConnector, TYPE RESOURCE local name = " + localName);
		    		  System.out.println("SPARQLConnector, TYPE RESOURCE URI = " + uri);
		    		  //System.out.println("SPARQLConnector, TYPE RESOURCE string name = " + string);
		    		  
		    		  bindings.add(new String[] {var,"uri",solution.get(var).asResource().toString()}); 
		    	  } else if (solution.get(var).isURIResource()){
		    		  //handling result type URIRESOURCE
		    		  
		    		  String localName = solution.get(var).asResource().getLocalName();
		    		  String uri = solution.get(var).asResource().getURI();
		    		  String string = solution.get(var).asResource().toString();
		    		  //System.out.println("SPARQLConnector, TYPE URI RESOURCE local name = " + localName);
		    		  System.out.println("SPARQLConnector, TYPE URI RESOURCE URI = " + uri);
		    		  //System.out.println("SPARQLConnector, TYPE URI RESOURCE string name = " + string);
		    		  
		    		  bindings.add(new String[] {var,"uri",solution.get(var).asResource().toString()}); 
		    	  } else {
		    		  //handling other types
		    		  bindings.add(new String[] {var,"unknown",solution.get(var).toString()}); 
		    	  } 
		      }
		      result.addResult(bindings);
		}
		qe.close();
		return result;
	}

	@Override
	public QueryResultSet update(eu.comvantage.dataintegration.data.Query q){
		QueryResultSet result = new QueryResultSet();
		
		//try to execute the SPARUL update command
		try {
			//set authentication header
			UsernamePasswordCredentials userpasscred = new UsernamePasswordCredentials(this.credentials.getUsername(),this.credentials.getPassword());
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY,userpasscred);
								
			HttpPost httpPost = new HttpPost(this.sparqlEndpoint.getEndpointURL());
							
			//set up HTTP Post Request (look at http://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VOSSparqlProtocol for Protocol)
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			//nameValuePairs.add(new BasicNameValuePair("format",format));
			nameValuePairs.add(new BasicNameValuePair("update", q.getQueryString()));			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			//set proxy if defined
            if(System.getProperty("http.proxyHost") != null) {
            	HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.valueOf(System.getProperty("http.proxyPort")), "http");
                httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }
            
			try {
				//execute the HTTP post
				HttpResponse response = httpclient.execute(httpPost);	
				
				System.out.println("SPARQLConnector, Update command: "+q.getQueryString()+";");
				System.out.println("SPARQLConnector, Update executed on endpoint: "+sparqlEndpoint.getEndpointURL() + "; user credentials: "+this.credentials.getUsername()+":"+this.credentials.getPassword());
				
				//handle different server responses and failures
				int responseCode = response.getStatusLine().getStatusCode() ;
				String responseMessage = response.getStatusLine().getReasonPhrase() ;
				System.out.println("SPARQLConnector update response: "+responseCode+", "+responseMessage);
				
			} catch (IOException ex)
			{
				throw new UpdateException(ex) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}