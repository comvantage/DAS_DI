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

/**
 * @author Manuel Carnerero (Nextel)
 * @version 1.0
 * @updated 30/06/2014
 */

import com.hp.hpl.jena.query.QueryException;

public interface SparqlRewriter
{

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Operators -------------------------- * ---
 // ---------------------------------------------------------------------------

	public void init(String propertiesName);

	public String rewrite(String query,
												String... roles)
								 throws QueryException;

	public String rewriteQuery(String query,
														 String... roles)
										  throws QueryException;
	

}