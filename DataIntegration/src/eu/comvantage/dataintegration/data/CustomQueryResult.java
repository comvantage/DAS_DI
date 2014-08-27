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
import java.util.List;

public class CustomQueryResult {
	private List<String> variables;
	private List<List> results;
	
	public CustomQueryResult() {
		this.variables = new ArrayList<String>();
		this.results = new ArrayList<List>();
	}
	
	public CustomQueryResult(List<String> variables){
		this.variables = variables;
		this.results = new ArrayList<List>();
	}
	
	public void addResult(List<String> result) {
		if(this.results.contains(result)) return;
		this.results.add(result);
	}
	
	public void addResults(List<List> results) {
		for(List l : results){
			addResult(l);
		}
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public List<List> getResults() {
		return results;
	}

	public void setResults(List<List> results) {
		this.results = results;
	}
}