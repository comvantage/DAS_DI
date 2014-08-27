/*
 * Copyright 2014 Nextel S.A.
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

package eu.comvantage.nextel.sparul;

/**
 * @author Manuel Carnerero (Nextel)
 * @version 0.1
 * @updated 11/04/2014
 */

public interface SparulComposerDataInterface
{

	String getTemplate(Long domainId,
										 Long templateId);

	boolean hasUseRigths(Long domainId,
											 Long templateId,
											 String role);

	String[] getActions(Long domainId,
											Long templateId);

}