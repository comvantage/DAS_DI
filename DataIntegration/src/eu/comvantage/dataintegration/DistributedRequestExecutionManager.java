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

import java.util.List;

import org.apache.http.HttpResponse;

import eu.comvantage.dataintegration.data.Credentials;
import eu.comvantage.dataintegration.data.DedicatedRequest;
import eu.comvantage.dataintegration.data.DistributedRequest;
import eu.comvantage.dataintegration.data.QueryResultSet;
import eu.comvantage.dataintegration.utils.SystemParameterManager;

public class DistributedRequestExecutionManager {
	
	
	public DistributedRequestExecutionManager(){
		
	}
	
	public QueryResultSet handleSPARQLRequest(DistributedRequest disRequest, int limit, int offset) {
		QueryResultSet result = new QueryResultSet(), temp = new QueryResultSet();
		
		SystemParameterManager.enableProxy();	
		for (DedicatedRequest request : disRequest.getRequestList()) {
			SPARQLConnector connector = new SPARQLConnector(request.getEndpoint());
			temp = connector.query(request.getQuery(), limit, offset);
			result.addResults(temp.getResults());
		}
		SystemParameterManager.disableProxy();
		
		result.setVariables(temp.getVariables());
		return result;
	}
	
	public QueryResultSet handleSPARULRequest(DistributedRequest disRequest) {
		QueryResultSet result = new QueryResultSet(), temp = new QueryResultSet();
		
		SystemParameterManager.enableProxy();	
		for (DedicatedRequest request : disRequest.getRequestList()) {
			SPARQLConnector connector = new SPARQLConnector(request.getEndpoint(), request.getCredentials());
			temp = connector.update(request.getQuery());
			result.addResults(temp.getResults());
		}
		SystemParameterManager.disableProxy();
		
		result.setVariables(temp.getVariables());
		return result;
	}
	
	public HttpResponse handleDHMRequest(DistributedRequest disRequest) {
		HttpResponse response =  null;
		
		SystemParameterManager.enableProxy();	
		for (DedicatedRequest request : disRequest.getRequestList()) {
			DHMConnector connector = new DHMConnector(request.getEndpoint());
			response = connector.query(request.getLocation(), request.getArguments());
		}
		SystemParameterManager.disableProxy();
		
		return response;
	}
}