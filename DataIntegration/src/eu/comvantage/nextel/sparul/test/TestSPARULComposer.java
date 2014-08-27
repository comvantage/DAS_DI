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

import java.util.HashMap;

import eu.comvantage.nextel.sparul.*;

/**
 * @author Manuel Carnerero (Nextel)
 * @version 0.1
 * @updated 23/04/2014
 */

public class TestSPARULComposer
{

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Constants -------------------------- * ---
 // ---------------------------------------------------------------------------

 // Characters or special strings
 //
 //		CR	 -> Carriage Return
 //		NL	 -> New Line
 //		CL	 -> Colon
 //		SP	 -> Space
 //		TB	 -> Tab
 //		LD	 -> Line Separator
 //		LSTB -> Line Separator + Tab
 //		DLS	 -> Double Line Separator

	public static final char CR			 = '\r';
	public static final char NL			 = '\n';
	public static final String CL		 = ":";
	public static final String SP		 = " ";
	public static final String TB		 = "\t";
	public static final String LS		 = System.getProperty("line.separator");
	public static final int LSL			 = LS.length();
	public static final String LSTB	 = LS + TB;
	public static final String LS2TB = LS + LS + TB;
	public static final String DLS	 = LS + LS;

	public static void main(String[] args)
	{
		try
		{

		// Initializing data interface and SPARUL composer

			SparulComposerDataInterface dataInterface = new TestDataInterface();
			SparulComposer composer = SparulComposerFactory.getSparulComposer();
			composer.setDataInterface(dataInterface);

		// Initializing composer parameters

			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("ticket", "ex:Ticket122819273420swd");
			parameters.put("person", "ex:nn273824");
			parameters.put("status", "\"true\"^^xsd:boolean");
			String[] roles = {"<http://www.comvantage.eu/ontologies/ac-schema/cv_wp6_comau>", "<http://www.comvantage.eu/ontologies/ac-schema/cv_wp6_fiat>"};

		// Testing

			Long domainId = 0L;
			Long[] templates = new Long[]{TestDataInterface.TMPL_WITH_PARAMS, TestDataInterface.TMPL_WITHOUT_PARAMS};
			for (Long templateId : templates)
			{
				System.out.printf("Composing template with id '%d':%s", templateId, DLS);
				System.out.printf("%s%s", dataInterface.getTemplate(domainId, templateId), DLS);
				String[] actions = dataInterface.getActions(domainId, templateId);
				if (actions.length > 0)
				{
					System.out.printf("and with the associated actions:%s", DLS);
					for (String action : dataInterface.getActions(0L, templateId)) System.out.printf("%s%s----------------%s", action, LS, LS);
				}
				String[] statements = composer.compose(0L, TestDataInterface.TMPL_WITH_PARAMS, parameters, roles);
				System.out.printf("After the composing proccess the resulting statements are:%s", DLS);
				for (String statement : statements)
					System.out.printf("%s%s----------------%s", statement, LS, LS);
				System.out.println("");
			}

			System.out.println("That's all folks!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}

}