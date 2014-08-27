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

import eu.comvantage.dataintegration.data.Credentials;
import eu.comvantage.dataintegration.data.DedicatedRequest;
import eu.comvantage.dataintegration.data.DistributedRequest;
import eu.comvantage.dataintegration.utils.SystemParameterManager;
import eu.comvantage.domainconfiguration.DomainConfigurationServiceImpl;
import eu.comvantage.domainconfiguration.DomainSourceDetail;

public class DistributedRequestFactory {
	public DistributedRequestFactory(){
		
	}
	public DistributedRequest createSPARQLRequestForAllSources(String query){
		DistributedRequest disRequest = new DistributedRequest();
		
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImpl client = SystemParameterManager.getDatabaseWebServiceClient();
		
		List<DomainSourceDetail> result = client.getDomainSourcesByType("sparql");
		
		//No source was found
		if(result.isEmpty()) {
			System.out.println("DistributedRequestFactory: no source found for type 'sparql'");
			return null;
		}
		
		for (DomainSourceDetail source : result) {
			DedicatedRequest dedrequest = new DedicatedRequest(source.getQueryEndpointURL(), new Credentials(source.getUsername(), source.getPassword()), query);
			disRequest.addRequest(dedrequest);
		}	
		return disRequest;
	}
	
	public DistributedRequest createSPARULRequestForDedicatedSources(String[] commands, String endpointId){
		DistributedRequest disRequest = new DistributedRequest();
		
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImpl client = SystemParameterManager.getDatabaseWebServiceClient();
		
		DomainSourceDetail result = client.getDomainSourcesByName(endpointId);
		
		//No source is known for specified endpoint
		if(result==null) {
			System.out.println("DistributedRequestFactory: no source found for specified endpoint = " + endpointId);
			return null;
		}
		
		//Resolve update endpoint
		String updateEndpointUrl = "";
		if(result.getUpdateEndpointURL()!=null && result.getUpdateEndpointURL().startsWith("http")) updateEndpointUrl = result.getUpdateEndpointURL();
		else {
			//if update endpoint url is not specified, use query endpoint instead
			updateEndpointUrl = result.getQueryEndpointURL();
			System.out.println("DistributedRequestFactory: no update endpoint specified for source " + endpointId +", query endpoint will be used ("+updateEndpointUrl+").");
		}
		
		//Resolve privileges for update
		Credentials credentials;
		if(result.getUsername()!=null && !result.getUsername().isEmpty() && result.getPassword()!=null && !result.getPassword().isEmpty()) {
			credentials = new Credentials(result.getUsername(),result.getPassword());
		} else {
			credentials = SystemParameterManager.getSPARQLSystemAccountCredentials();
		}
		
		for (String command:commands) {
			DedicatedRequest dedrequest = new DedicatedRequest(updateEndpointUrl, credentials, command);
			disRequest.addRequest(dedrequest);
		}
		return disRequest;
	}
	
	public DistributedRequest createSPARULRequestForUnknownSource(String command){
		DistributedRequest disRequest = new DistributedRequest();
		
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImpl client = SystemParameterManager.getDatabaseWebServiceClient();
		
		List<DomainSourceDetail> result = client.getDomainSourcesByType("sparql");
		
		//No source was found
		if(result.isEmpty()) {
			System.out.println("DistributedRequestFactory: no source found for type 'sparql'");
			return null;
		}
		
		for (DomainSourceDetail source : result) {
			//Resolve update endpoint
			String updateEndpointUrl = "";
			if(source.getUpdateEndpointURL()!=null && source.getUpdateEndpointURL().startsWith("http")) updateEndpointUrl = source.getUpdateEndpointURL();
			else {
				//if update endpoint url is not specified, use query endpoint instead
				updateEndpointUrl = source.getQueryEndpointURL();
				System.out.println("DistributedRequestFactory: no update endpoint specified for source " + source.getName() +", query endpoint will be used ("+updateEndpointUrl+").");
			}
			
			//Resolve update privilegies for specific source
			Credentials credentials;
			if(source.getUsername()!=null && !source.getUsername().isEmpty() && source.getPassword()!=null && !source.getPassword().isEmpty()) {
				credentials = new Credentials(source.getUsername(),source.getPassword());
			} else {
				credentials = SystemParameterManager.getSPARQLSystemAccountCredentials();
			}
			
			DedicatedRequest dedrequest = new DedicatedRequest(updateEndpointUrl, credentials, command);
			disRequest.addRequest(dedrequest);
		}
		return disRequest;
	}
	
	public DistributedRequest createDHMRequestForAllSources(String dir, String file, String arguments){
		DistributedRequest disRequest = new DistributedRequest();
		
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImpl client = SystemParameterManager.getDatabaseWebServiceClient();
		
		List<DomainSourceDetail> result = client.getDomainSourcesByType("dhm");
		
		//assemble query string from parts
		String location = "";
		//append optional dir parameter
		if (!dir.equals("")) location += dir+"/";
		location += file;
		
		for (DomainSourceDetail source : result) {
			DedicatedRequest dedrequest = new DedicatedRequest(source.getQueryEndpointURL(), new Credentials(source.getUsername(), source.getPassword()), location, arguments);
			disRequest.addRequest(dedrequest);
		}	
		return disRequest;
	}
	
	public DistributedRequest createDHMRequestForSpecificSource(String host, String dir, String file, String arguments){
		DistributedRequest disRequest = new DistributedRequest();

		//assemble query string from parts
		String location = "";
		//append optional dir parameter
		if (!dir.equals("")) location += dir+"/";
		location += file;
		
		DedicatedRequest dedrequest = new DedicatedRequest(host, new Credentials(), location, arguments);
		disRequest.addRequest(dedrequest);

		return disRequest;
	}
}