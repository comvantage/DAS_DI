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

import java.util.*;
import java.util.regex.*;

/**
 * @author Manuel Carnerero (Nextel)
 * @version 0.2
 * @updated 15/04/2014
 */

public class DefaultSPARULComposer implements SparulComposer
{

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Constants -------------------------- * ---
 // ---------------------------------------------------------------------------

 // Regular Expressions Templates

	private static String NON_CAPTURING = "(?:%s)";
	private static String ONE_OR_NOT_AT_ALL	 = NON_CAPTURING + "?";
	private static String ZERO_OR_MORE_TIMES = NON_CAPTURING + "*";

 // Related with SPARQL 1.1 Syntax
 // ------------------------------

 // PN_CHARS_BASE ::= [A-Z] | [a-z] | [#x00C0-#x00D6] | [#x00D8-#x00F6] | [#x00F8-#x02FF] | [#x0370-#x037D] | [#x037F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
	private static String RE_T_PN_CHARS_BASE = "[a-zA-Z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD\\u10000-\\uEFFFF]";
 // PN_CHARS_U ::= PN_CHARS_BASE | '_'
	private static String RE_T_PN_CHARS_U = or(RE_T_PN_CHARS_BASE, "_");
 // PN_CHARS ::= PN_CHARS_U | '-' | [0-9] | #x00B7 | [#x0300-#x036F] | [#x203F-#x2040]
	private static String RE_T_PN_CHARS = or(RE_T_PN_CHARS_U, "[\\-0-9\u00B7\u0300-\u036F\u203F-\u2040]");
 // PN_LOCAL ::= ( PN_CHARS_U | [0-9] ) ((PN_CHARS|'.')* PN_CHARS)?
	private static String RE_T_PN_LOCAL = cc(or(RE_T_PN_CHARS, "[0-9]"), zeroOrOne(cc(zeroOrMore(or(RE_T_PN_CHARS, "\\.")), RE_T_PN_CHARS)));
 // PN_PREFIX ::= PN_CHARS_BASE ((PN_CHARS|'.')* PN_CHARS)?
	private static String RE_T_PN_PREFIX = RE_T_PN_CHARS_BASE + "((" + RE_T_PN_CHARS + "|\\.)*" + RE_T_PN_CHARS + ")?";
 // PNAME_NS ::= PN_PREFIX? ':'
	private static String RE_T_PNAME_NS = cc(zeroOrOne(RE_T_PN_PREFIX), ":");
 // PNAME_LN ::= PNAME_NS PN_LOCAL
	private static String RE_T_PNAME_LN = cc(RE_T_PNAME_NS, RE_T_PN_LOCAL); 
 // IRI_REF ::= '<' ([^<>"{}|^`\]-[#x00-#x20])* '>'
	private static String RE_T_IRI_REF = "<([^<>\"{}|^`\\x00-\\x20])*>";
 // ECHAR ::= '\' [tbnrf\"']
	private static String RE_T_ECHAR = "\\[tbnrf\"']";
 // STRING_LITERAL1 ::= "'" ( ([^#x27#x5C#xA#xD]) | ECHAR )* "'"
	private static String RE_T_STRING_LITERAL1 = cc("'", zeroOrMore(or("[^\\x27\\x5C\\x0A\\x0D]", RE_T_ECHAR)), "'");
 // STRING_LITERAL2 ::= '"' ( ([^#x22#x5C#xA#xD]) | ECHAR )* '"'
	private static String RE_T_STRING_LITERAL2 = cc("\"", zeroOrMore(or("[^\\x22\\x5C\\x0A\\x0D]", RE_T_ECHAR)), "\"");
 // STRING_LITERAL_LONG1 ::= "'''" ( ( "'" | "''" )? ( [^'\] | ECHAR ) )* "'''"
	private static String RE_T_STRING_LITERAL_LONG1 = cc("'''", zeroOrMore(cc(zeroOrOne(or("'", "''")), or("[^'\\]", RE_T_ECHAR))), "'''");
 // STRING_LITERAL_LONG2 ::= '"""' ( ( '"' | '""' )? ( [^"\] | ECHAR ) )* '"""'
	private static String RE_T_STRING_LITERAL_LONG2 = cc("\"\"\"", zeroOrMore(cc(zeroOrOne(or("\"", "\"\"")), or("[^\"\\]", RE_T_ECHAR))), "\"\"\"");
 // LANGTAG ::= '@' [a-zA-Z]+ ('-' [a-zA-Z0-9]+)*
	private static String RE_T_LANG_TAG = "@[a-zA-Z](\\-[a-zA-Z0-9]+)*";
 // VARNAME ::= ( PN_CHARS_U | [0-9] ) ( PN_CHARS_U | [0-9] | #x00B7 | [#x0300-#x036F] | [#x203F-#x2040] )*
	private static String RE_T_VARNAME = cc(or(RE_T_PN_CHARS, "[0-9]"), zeroOrMore(or(RE_T_PN_CHARS_U, "[0-9\\u00B7\\u0300-\\u036F\\u203F-\\u2040]")));

 // PrefixedName ::= PNAME_LN | PNAME_NS
	private static String RE_PREFIXED_NAME = or(RE_T_PNAME_LN, RE_T_PNAME_NS);
 // IRIref ::= IRI_REF | PrefixedName
	private static String RE_IRI_REF = or(RE_T_IRI_REF, RE_PREFIXED_NAME); 
 // String ::= STRING_LITERAL1 | STRING_LITERAL2 | STRING_LITERAL_LONG1 | STRING_LITERAL_LONG2
	private static String RE_STRING = or(RE_T_STRING_LITERAL1, RE_T_STRING_LITERAL2, RE_T_STRING_LITERAL_LONG1, RE_T_STRING_LITERAL_LONG2);
 // RDFLiteral ::= String ( LANGTAG | ( '^^' IRIref ) )?
	private static String RE_RDF_LITERAL = cc(RE_STRING, zeroOrOne(or(RE_T_LANG_TAG, cc("\\^\\^", RE_IRI_REF))));

	private static Pattern PT_IRI_REF			= Pattern.compile(RE_IRI_REF);
	private static Pattern PT_RDF_LITERAL = Pattern.compile(RE_RDF_LITERAL);

 // Related with template parameters

	private static String RE_TEMPLATE_PARAM	 = cc("\\$\\{(", RE_T_VARNAME, "),([il])\\}");
	private static String RE_TEMPLATE_PARAM_2 = "\\$\\(([^,]*),(i|ir|iri|l|li|lit|lite|liter|litera|literal)\\)";

	private static Pattern PT_TEMPLATE_PARAM = Pattern.compile(RE_TEMPLATE_PARAM_2, Pattern.CASE_INSENSITIVE);

	private SparulComposerDataInterface dataInterface;

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Operators -------------------------- * ---
 // ---------------------------------------------------------------------------

	@Override
	public String[] compose(Long domainId,
													Long templateId,
													HashMap<String, String> parameters,
													String... roles)
	{
		String[] statements = null;
		for (String role : roles)
		{
		// If the role has the use rights...
			if (dataInterface.hasUseRigths(domainId, templateId, role))
			{
				ArrayList<String> unfilled = new ArrayList<String>();
				HashMap<String, String> paramsTypes = new HashMap<String, String>();
				unfilled.add(dataInterface.getTemplate(domainId, templateId));
				unfilled.addAll(Arrays.asList(dataInterface.getActions(domainId, templateId)));
				statements = new String[unfilled.size()];
				boolean error = false;
				for (int i = statements.length; (--i) >= 0;)
				{
					String statement = unfilled.get(i);
					StringBuilder composed = new StringBuilder(statement);
					Matcher matcher = PT_TEMPLATE_PARAM.matcher(statement);
					int displacement = 0;
					while (matcher.find())
					{
						String name = matcher.group(1);
						String value = parameters.get(name);
					// If the parameter is in the map of received parameters...
						if (value != null)
						{
							String type = normalizeType(matcher.group(2));
							String previousType = paramsTypes.get(name);
						// If it's the first time it appears...
							if (previousType == null)
							{
							// If it's a valid parameter value according the specified type...
								if (isValidParamValue(type, value)) paramsTypes.put(name, type);
							// If it isn't...
								else error = true;
							}
						// If it isn't the first time it appears and the actual type doesn't match with the previous one...
							else if (!previousType.equals(type)) error = true;
						}
					// If there isn't a value for the specified parameter...
						else
							error = true;
					// If there isn't an error...
						if (!error)
						{
							composed.replace(matcher.start(0) + displacement, matcher.end(0) + displacement, value);
							displacement += value.length() - matcher.group().length();
						}
					}
				// If there's an error...
					if (error)
					{
						statements = null;
						break;
					}
				// If there isn't an error...
					else
						statements[i] = composed.toString();
				}
				break;
			}
		}

		return statements;
	}

	@Override
	public void setDataInterface(SparulComposerDataInterface dataInterface)
	{
		this.dataInterface = dataInterface;
	}

 // ---------------------------------------------------------------------------
 // --- * -------------------------- Auxiliary -------------------------- * ---
 // ---------------------------------------------------------------------------

	private static String or(String... expressions)
	{
		
		int nExpressions = expressions.length;
		if (nExpressions > 1)
		{
			StringBuilder result = new StringBuilder(1024);
			result.append(String.format(NON_CAPTURING, expressions[0]));
			for (int i = 1; i < nExpressions; i++)
				result.append("|").append(String.format(NON_CAPTURING, expressions[i]));
			return result.toString();
		}
		else if (nExpressions == 1)
		{
			return expressions[0];
		}
		else
			return "";
	}

	private static String zeroOrOne(String expression)
	{
		return String.format(ONE_OR_NOT_AT_ALL, expression);
	}

	private static String zeroOrMore(String expression)
	{
		return String.format(ZERO_OR_MORE_TIMES, expression);
	}

	private static String cc(String... texts)
	{
		StringBuilder result = new StringBuilder(1024);
		for (String text : texts) result.append(text);
		return result.toString();
	}

	private static String normalizeType(String type)
	{
		return (type.toLowerCase().startsWith("i") ? "iri" : "literal");
	}

	private static boolean isValidParamValue(String type,
																					 String value)
	{
		return true;
	}

	private static boolean isValidIRI(String value)
	{
		Matcher matcher = PT_IRI_REF.matcher(value);
		return matcher.find();
	}

	private static boolean isValidLiteral(String value)
	{
		Matcher matcher = PT_RDF_LITERAL.matcher(value);
		return matcher.find();
	}

}