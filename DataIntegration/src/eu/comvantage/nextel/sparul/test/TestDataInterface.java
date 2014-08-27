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

package eu.comvantage.nextel.sparul.test;

import eu.comvantage.nextel.sparul.SparulComposerDataInterface;

/**
 * @author Manuel Carnerero (Nextel)
 * @version 0.1
 * @updated 20/04/2014
 */

public class TestDataInterface implements SparulComposerDataInterface
{

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Constants -------------------------- * ---
 // ---------------------------------------------------------------------------

	private static final String LS = System.getProperty("line.separator");

	public static Long TMPL_WITH_PARAMS		 = 0L;
	public static Long TMPL_WITHOUT_PARAMS = 1 + TMPL_WITH_PARAMS;

	private static String[] TEMPLATES =
	{
	// 000
		"PREFIX ex: <http://examplefactory.exampleservicerequest/>" + LS +
		"PREFIX rppm: <http://www.comvantage.eu/ontologies/mma/rppm/>" + LS + LS +
		"" + LS +
		"DELETE FROM  <http://examplefactory.exampleservicerequest/>" + LS +
		"{" + LS +
		"  $(ticket,iri) rppm:isAssignedTo $(person,iri)." + LS + 
		"  $(ticket,iri) ex:isClosed $(status,literal)" + LS +
		"}",
	// 001
		"PREFIX ex: <http://examplefactory.exampleservicerequest/>" + LS +
		"PREFIX rppm: <http://www.comvantage.eu/ontologies/mma/rppm/>" + LS + LS +
		"" + LS +
		"DELETE FROM  <http://examplefactory.exampleservicerequest/>" + LS +
		"{" + LS +
		"  ex:Ticket122819273420swd rppm:isAssignedTo ex:nn273824." + LS + 
		"  ex:Ticket122819273420swd ex:isClosed \"true\"^^xsd:boolean" + LS +
		"}"
	};

	private static String[] ACTIONS =
	{
		"PREFIX ac: <http://www.comvantage.eu/ontologies/ac-schema/>" + LS +
		"PREFIX ex: <http://examplefactory.exampleservicerequest/>" + LS +
		"PREFIX rppm: <http://www.comvantage.eu/ontologies/mma/rppm/>" + LS +
		"" + LS +
		"DELETE FROM <http://examplefactory.exampleservicerequest/>" + LS +
		"{" + LS +
		"  _:t1 a ac:Tuple;" + LS +
		"    ac:subject $(ticket,iri);" + LS +
		"    ac:predicate rppm:isAssignedTo;" + LS +
		"    ac:object $(person,iri)" + LS +
		"}" + LS +
		"WHERE" + LS +
		"{" + LS +
		"    ?v a ac:View;" + LS +
		"      ac:hasTuple _:t1." + LS +
		"    _:t1 a ac:Tuple" + LS +
		" }"
	};

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Operators -------------------------- * ---
 // ---------------------------------------------------------------------------

	@Override
	public String getTemplate(Long domainId,
														Long templateId)
	{
		return TEMPLATES[templateId.intValue()];
	}

	@Override
	public boolean hasUseRigths(Long domainId,
															Long templateId,
															String role)
	{
		return true;
	}

	@Override
	public String[] getActions(Long domainId,
														 Long templateId)
	{
		return ACTIONS;
	}

}