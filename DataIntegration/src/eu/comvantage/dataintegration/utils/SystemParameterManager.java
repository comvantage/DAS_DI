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

package eu.comvantage.dataintegration.utils;

import java.util.List;

import org.apache.ws.security.validate.Credential;

import eu.comvantage.dataintegration.data.Credentials;
import eu.comvantage.domainconfiguration.DomainConfigurationServiceImpl;
import eu.comvantage.domainconfiguration.DomainConfigurationServiceImplService;
import eu.comvantage.domainconfiguration.DomainSystemParameter;

public final class SystemParameterManager {
	
	//Singelton access to database web service
	private static DomainConfigurationServiceImplService service;
	private static DomainConfigurationServiceImpl client;

	static public String[] getProxySettings(){
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImplService service = new DomainConfigurationServiceImplService();
		DomainConfigurationServiceImpl client = service.getDomainConfigurationServiceImplPort();
		
		List<DomainSystemParameter> r1 = client.getDomainSystemParametersByKey("proxy_host");
		List<DomainSystemParameter> r2 = client.getDomainSystemParametersByKey("proxy_port");
		
		if(r1.isEmpty() && r2.isEmpty()) return new String[]{"",""};
		return new String[] {r1.get(0).getValue(),r2.get(0).getValue()};
	}
	
	static public void enableProxy() {	
		String[] params = getProxySettings();
		//skip if no proxy is defined
		if (!params[0].equals("")) System.setProperty("http.proxyHost", params[0]);
		if (!params[1].equals("")) System.setProperty("http.proxyPort", params[1]);
		System.out.println("SystemParameterManager: Proxy enabled at " + params[0] + ":" + params[1]);
	}
	
	static public void disableProxy() {
		System.clearProperty("http.proxyHost");
		System.clearProperty("http.proxyPort");
		System.out.println("SystemParameterManager: Proxy disabled");
	}
	
	static public boolean useAccessControlFeatures(){
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImplService service = new DomainConfigurationServiceImplService();
		DomainConfigurationServiceImpl client = service.getDomainConfigurationServiceImplPort();
		
		List<DomainSystemParameter> r1 = client.getDomainSystemParametersByKey("ac_enabled");
		
		if(!r1.isEmpty() && r1.get(0).getValue().equalsIgnoreCase("true")) {
			System.out.println("SystemParameterManager: Access control features enabled");
			return true;
		}
		else {
			System.out.println("SystemParameterManager: Access control features disabled");
			return false;
		}
	}
	
	static public int getDefaultQueryResultLimit(){
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImplService service = new DomainConfigurationServiceImplService();
		DomainConfigurationServiceImpl client = service.getDomainConfigurationServiceImplPort();
		
		List<DomainSystemParameter> r1 = client.getDomainSystemParametersByKey("default_result_limit");
				
		if(r1.isEmpty()) {
			System.out.println("SystemParameterManager: Default query result limit parameter not customized, set default limit = 10.");
			return 10;
		}
		else {
			System.out.println("SystemParameterManager: Default query result limit parameter set to "+r1.get(0).getValue());
			return Integer.valueOf(r1.get(0).getValue());
		}
	}
	
	static public String getACBypassRole(){
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImplService service = new DomainConfigurationServiceImplService();
		DomainConfigurationServiceImpl client = service.getDomainConfigurationServiceImplPort();
		
		List<DomainSystemParameter> r1 = client.getDomainSystemParametersByKey("ac_bypass_role");
				
		if(r1.isEmpty()) {
			System.out.println("SystemParameterManager: AC bypass role parameter not specified");
			return "";
		}
		else {
			System.out.println("SystemParameterManager: AC bypass role parameter set to "+r1.get(0).getValue());
			return r1.get(0).getValue();
		}
	}
	
	static public boolean debugModeEnabled(){
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImplService service = new DomainConfigurationServiceImplService();
		DomainConfigurationServiceImpl client = service.getDomainConfigurationServiceImplPort();
		
		List<DomainSystemParameter> r1 = client.getDomainSystemParametersByKey("debug_mode");
		
		if(!r1.isEmpty() && r1.get(0).getValue().equalsIgnoreCase("true")) {
			System.out.println("SystemParameterManager: Debug mode enabled");
			return true;
		}
		else {
			System.out.println("SystemParameterManager: Debug mode disabled");
			return false;
		}
	}
	
	static public Long getDefaultClientId(){
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImplService service = new DomainConfigurationServiceImplService();
		DomainConfigurationServiceImpl client = service.getDomainConfigurationServiceImplPort();
		
		List<DomainSystemParameter> r1 = client.getDomainSystemParametersByKey("default_client_id");
				
		if(r1.isEmpty()) {
			System.out.println("SystemParameterManager: Default client id not customized, set default client id = 1.");
			return new Long(1);
		}
		else {
			System.out.println("SystemParameterManager: Default client id set to "+r1.get(0).getValue());
			return Long.valueOf(r1.get(0).getValue());
		}
	}
	
	static public Credentials getSPARQLSystemAccountCredentials(){
		return new Credentials("comvantage","284928"); //TODO, replace with: "comvantage / 284928"
	}
	
	static public DomainConfigurationServiceImpl getDatabaseWebServiceClient() {
		
		if(service == null) {
			service = new DomainConfigurationServiceImplService();
		}

		if(client == null) {
			client = service.getDomainConfigurationServiceImplPort();
		}
		return client;
	}
}