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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryResultSet {
	private List<String> vars;
	private List<Map> bindings;
	
	public QueryResultSet() {
		this.vars = new ArrayList<String>();
		this.bindings = new ArrayList<Map>();
	}
	
	public QueryResultSet(List<String> vars){
		this.vars = vars;
		this.bindings = new ArrayList<Map>();
	}
	
	public void addResult(List<String[]> bindings) {
	
		//TODO: remove duplicates!!!!
		//if(this.bindings.contains(result)) return;
		
		Map<String, QueryResult> result = new HashMap<String, QueryResult>();
		for(String[] b : bindings) {
			QueryResult binding = new QueryResult(b[1], b[2]);
			result.put(b[0], binding);
		}
		this.bindings.add(result);
	}
	
	public void addResults(List<Map> results) {
		for(Map<String, QueryResult> m : results){
			List<String[]> temp = new ArrayList<String[]>();
			
			for(String var : m.keySet()) {
				temp.add(new String[]{var,m.get(var).getType(),m.get(var).getValue()});
			}
			addResult(temp);
		}
	}
	
	public List<String> getVariables() {
		return vars;
	}

	public void setVariables(List<String> vars) {
		this.vars = vars;
	}

	public List<Map> getResults() {
		return bindings;
	}

	public void setResults(List<Map> results) {
		this.bindings = results;
	}
}