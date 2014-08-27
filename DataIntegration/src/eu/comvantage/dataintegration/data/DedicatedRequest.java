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

package eu.comvantage.dataintegration.data;

public class DedicatedRequest {
	private Endpoint endpoint;
	private Credentials credentials;
	private Query query;
	private String location;
	private String arguments;
	
	public DedicatedRequest(){
		
	}

	public DedicatedRequest(String endpointURL, Credentials credentials, String query){
		this.endpoint = new Endpoint(endpointURL);
		this.credentials = credentials;
		this.query = new Query(query);
	}
	public DedicatedRequest(String endpointURL, Credentials credentials, String location, String arguments){
		this.endpoint = new Endpoint(endpointURL);
		this.credentials = credentials;
		this.location = location;
		this.arguments = arguments;
	}
	
	public Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}	
}