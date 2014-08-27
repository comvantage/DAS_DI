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

package es.nextel.libs;

/**
 * @author Manuel Carnerero
 * @version 1.1
 */

public class Definitions
{

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Constants -------------------------- * ---
 // ---------------------------------------------------------------------------

	public static final String PROPERTIES_EXT = ".properties";

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Attributes ------------------------- * ---
 // ---------------------------------------------------------------------------

	private static String fs;
	private static String ls;

 // ---------------------------------------------------------------------------
 // --- * ---------------------- Static Initializer --------------------- * ---
 // ---------------------------------------------------------------------------

	static
	{
		fs = System.getProperty("file.separator");
		ls = System.getProperty("line.separator");
	}

 // ---------------------------------------------------------------------------
 // --- * ----------------------- Getters/Setters ----------------------- * ---
 // ---------------------------------------------------------------------------

	public static String getFS()
	{
		return fs;
	}

	public static String getLS()
	{
		return ls;
	}

}