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

package eu.comvantage.nextel.sparql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryFactory;

/**
 * @author Manuel Carnerero (Nextel)
 * @version 0.1
 * @updated 10/01/2013
 */

public class TestSparqlRewriter
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
 //		SSC	 -> Special Separator Character
 //		ES	 -> Special Separator
 //		SE2	 -> Double Special Separator

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
	public static final char SDC		 = '|';
	public static final String SS		 = new String(new char[] {SDC});
	public static final String DSS	 = new String(new char[] {SDC, SDC});

	public static final int CLUSTER = 4096;

	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 

	public static void main(String[] args)
	{
		try
		{
			System.out.printf("%sSPARQL QUERY REWRITING DEMO%s%s", LS, LS, LS);
			do
			{
				try
				{
					//read roles from shell
					System.out.println("How many roles has the user?");
					String input = br.readLine();
					if (input.isEmpty()) break;	
					int nRoles = Integer.parseInt(input);
					String[] roles = new String[nRoles];
					for (int i = nRoles; (--i) >= 0;)
					{
						System.out.printf("Input role #%s:%s", nRoles - i - 1, LS);
						roles[i] = br.readLine();
					}
					
					//read query from shell
					System.out.printf("Input query file:%s", LS);
					String queryFile = br.readLine();
					BufferedReader sparqlReader = new BufferedReader(new FileReader(queryFile));

					int readed;

					StringBuilder sparqlQuery = new StringBuilder(CLUSTER);
					char[] buffer = new char[CLUSTER];
					while ((readed = sparqlReader.read(buffer)) > -1) sparqlQuery.append(buffer, 0, readed);
					sparqlReader.close();

					//rewrite query
					SparqlRewriter rewriter = SparqlRewriterFactory.getSparqlRewriter();
					String rewritedSparqlQuery = rewriter.rewrite(sparqlQuery.toString(), roles);
					System.out.printf("The rewritten query is:%s%s%s", LS, rewritedSparqlQuery, LS, LS);
					try
					{
						@SuppressWarnings("unused")
						Query query = QueryFactory.create(rewritedSparqlQuery);
						System.out.println("The rewritten query is syntactically correct!");
					}
					catch (QueryException e)
					{
						System.out.println("The rewritten query is NOT syntactically correct!");
					}
					if (finish()) break;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.out.printf("There is an UNEXPECTED error!%s%s", LS, e.toString());
					cleanInput();
					if (finish()) break;
				}
			}
			while (true);

			System.out.println("That's all folks!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}

	private static void cleanInput()
	{
		try
		{
			while (br.readLine() != null);
		}
		catch (IOException e)
		{
		}
	}

	private static boolean finish()
												 throws IOException
	{
		System.out.printf("Do you want restart continue?%s", LS);
		String input = br.readLine().toLowerCase();
		return !input.matches("^y(e(s)?)?$");
	}

}