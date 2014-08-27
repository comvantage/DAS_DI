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

package es.nextel.libs.reflection;

import java.lang.reflect.*;
/**
 * @author mcarnerero
 * @version 1.1
 */
public class Reflection
{

 // Related to be a 'Singleton' class

	private static Reflection instance = new Reflection();

 // ---------------------------------------------------------------------------
 // --- * ------------------------- Constructor ------------------------- * ---
 // ---------------------------------------------------------------------------

	private Reflection()
	{
	}

 // ---------------------------------------------------------------------------
 // --- * ----------------------- Getters/Setters ----------------------- * ---
 // ---------------------------------------------------------------------------

	public static synchronized Reflection getInstance()
	{
		return instance;
	}

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Auxiliary -------------------------- * ---
 // ---------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public static <T> T instantiate(String className,
																	Object... parameters)
													 throws InstantiationException, IllegalAccessException, ClassNotFoundException,
																	SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		Class<?> clazz = Class.forName(className);
		if (parameters == null)
			return (T)clazz.newInstance();
		else
		{
			Class<?>[] neededParameterTypes = new Class<?>[parameters.length];
			for (int i = parameters.length; (--i) >= 0;)
				neededParameterTypes[i] = parameters[i].getClass();
			Constructor<?> constructor = null;
			try
			{
				constructor = clazz.getConstructor(neededParameterTypes);
			}
			catch (NoSuchMethodException e)
			{
				for (Constructor<?> c : clazz.getConstructors())
				{
					Class<?>[] constructorParameterTypes = c.getParameterTypes();
					if (constructorParameterTypes.length == parameters.length)
					{
						boolean match = true;
						for (int i = constructorParameterTypes.length; match && ((--i) >= 0);)
							if (!constructorParameterTypes[i].isAssignableFrom(neededParameterTypes[i])) match = false;
						if (match)
							return (T)c.newInstance(parameters);
					}
				}
				throw new NoSuchMethodException();
			}
			return (T)constructor.newInstance(parameters);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstance(String className)
													 throws SecurityException, NoSuchMethodException, ClassNotFoundException,
																	IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Method method = Class.forName(className).getMethod("getInstance");
		return (T)method.invoke(null, new Object[0]);
	}

	public static String getSTMethodName(int level)
	{
		return Thread.currentThread().getStackTrace()[2 + level].getMethodName();
	}

	public static String getSTClassName(int level)
	{
		return Thread.currentThread().getStackTrace()[2 + level].getClassName();
	}

}