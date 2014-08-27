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

import eu.comvantage.dataintegration.utils.SystemParameterManager;
import eu.comvantage.domainconfiguration.DomainConfigurationServiceImpl;
import eu.comvantage.domainconfiguration.DomainSPARULTemplate;
import eu.comvantage.domainconfiguration.DomainViewAction;
import eu.comvantage.nextel.sparul.SparulComposerDataInterface;

public class SPARULComposerDataConnector implements SparulComposerDataInterface {

	@Override
	public String getTemplate(Long domainId, Long templateId) {		
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImpl client = SystemParameterManager.getDatabaseWebServiceClient();
		
		DomainSPARULTemplate result = client.getDomainSPARULTemplateById(templateId, domainId);
		return result.getStatement();
	}

	@Override
	public boolean hasUseRigths(Long domainId, Long templateId, String role) {
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImpl client = SystemParameterManager.getDatabaseWebServiceClient();
		
		return client.hasUseRightsForSPARULTemplate(role, templateId, domainId);
	}

	@Override
	public String[] getActions(Long domainId, Long templateId) {
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImpl client = SystemParameterManager.getDatabaseWebServiceClient();
		
		DomainSPARULTemplate result = client.getDomainSPARULTemplateById(templateId, domainId);
		String[] actions = new String[result.getViewactions().size()];
		
		for(int i=0; i<result.getViewactions().size();i++){
			actions[i] = result.getViewactions().get(i).getStatement();
		}
		
		return actions;
	}
	
	public String getUpdateEndpoint(Long domainId, Long templateId){
		SystemParameterManager.disableProxy();
		DomainConfigurationServiceImpl client = SystemParameterManager.getDatabaseWebServiceClient();
		
		DomainSPARULTemplate result = client.getDomainSPARULTemplateById(templateId, domainId);
		
		if(result==null) return null;
		return result.getSource();
	}
}