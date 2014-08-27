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


import static es.nextel.libs.Definitions.PROPERTIES_EXT;

import java.io.InputStream;
import java.util.Properties;

import es.nextel.libs.reflection.Reflection;

/**
 * @author Manuel Carnerero (Nextel)
 * @version 0.1
 * @updated 11/04/2014
 */

public class SparulComposerFactory
{

 //	---------------------------------------------------------------------------
 //	--- * --------------------------- Constants ------------------------- * ---
 //	---------------------------------------------------------------------------

	public static final long serialVersionUID = 1L;

 // Related to properties

	private static final String P_SPARUL_COMPOSER = "SPARULComposer";

 // Related to this class

	private static final String SIMPLE_CLASS_NAME = SparulComposerFactory.class.getSimpleName();

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Attributes ------------------------- * ---
 // ---------------------------------------------------------------------------

 // Related to rewriter

	private static SparulComposer sparulComposer;

 // Related to be a 'Singleton' class

	private static SparulComposerFactory instance = new SparulComposerFactory();

 // ---------------------------------------------------------------------------
 // --- * ---------------------- Static Initializer --------------------- * ---
 // ---------------------------------------------------------------------------

	static
	{
		try
		{

		// Obtaining properties
		// ====================

			Properties properties = new Properties();
			InputStream input = SparulComposerFactory.class.getResourceAsStream(SIMPLE_CLASS_NAME + PROPERTIES_EXT);
			properties.load(input);

			sparulComposer = Reflection.instantiate(properties.getProperty(P_SPARUL_COMPOSER));

			input.close();
		}
		catch (Exception e)
		{
			System.err.println(e.getStackTrace());
		}
	}

 // ---------------------------------------------------------------------------
 // --- * ------------------------- Constructor ------------------------- * ---
 // ---------------------------------------------------------------------------

	private SparulComposerFactory()
	{
	}

 // ---------------------------------------------------------------------------
 // --- * ----------------------- Getters/Setters ----------------------- * ---
 // ---------------------------------------------------------------------------

	public static synchronized SparulComposerFactory getInstance()
	{
		return instance;
	}

	public static SparulComposer getSparulComposer()
																					throws IllegalAccessException
	{
		return sparulComposer;
	}

}