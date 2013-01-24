package de.digitalemil.tocplusplus;

import java.util.Arrays;

public class ToCPlusPlusClazz extends Clazz {

	public final static String STRINGTYPE = "unsigned char*";
	public boolean hasFinalizer= false;
	
	public Method addMethod(String name, String retType, int n) {
		if(name.trim().equals("finalize")) {
			hasFinalizer= true;
		}
		
		return super.addMethod(name, retType, n);
	}
	
	public void addReference(String ref) {
		ref = typeConversion(ref);
		ref = ref.replace("*", "");
		if (STRINGTYPE.startsWith(ref))
			return;
		if ("RuntimeException".equals(ref))
			return;
		if ("Integer".equals(ref) || "Integer*".equals(ref))
			return;
		if ("Integer".equals(ref) || "Integer*".equals(ref))
			return;
		if ("int".equals(ref) || "int*".equals(ref))
			return;
		if ("float".equals(ref) || "float*".equals(ref))
			return;
		if ("Float".equals(ref) || "Float*".equals(ref))
			return;
		if ("Double".equals(ref) || "Double*".equals(ref))
			return;
		if ("double".equals(ref) || "double*".equals(ref))
			return;
		if ("Boolean".equals(ref) || "Boolean*".equals(ref))
			return;
		if ("bool".equals(ref) || "bool*".equals(ref))
			return;
		if ("long".equals(ref) || "long*".equals(ref))
			return;
		if ("Long".equals(ref) || "Long*".equals(ref))
			return;
		if ("short".equals(ref) || "short*".equals(ref))
			return;
		if ("Short".equals(ref) || "Short*".equals(ref))
			return;
		if ("char".equals(ref) || "char*".equals(ref))
			return;

		references.add(ref);
	}

	protected String typeConversion(String type) {
		String in = type;
		type = type.trim();
		if (type.contains("*"))
			return type;

		boolean isPrimitive = false;
		if (type.startsWith("void") || type.startsWith("int")
				|| type.startsWith("float") || type.startsWith("short")
				|| type.startsWith("long") || type.startsWith("double")
				|| type.startsWith("boolean") || type.startsWith("char"))
			isPrimitive = true;
		// System.out.print("typeConversion: " + type);

		boolean isString = false;
		if (type.startsWith("String")) {
			type = type.replace("String", STRINGTYPE);
			isString = true;
		}
		if ("Boolean".equals(type) || "boolean".equals(type))
			type = "bool";
		if ("Boolean[]".equals(type) || "boolean[]".equals(type))
			type = "bool*";
		boolean isArray = type.contains("[]");
		// if(isArray)
		// System.out.println("\nArray: "+type+"\n");
		type = type.replace("[]", "*");
		// if(isArray)
		// System.out.println("\nArray1: "+type+"\n");

		if (!isPrimitive && !isString)
			type += "*";

		// if(isArray)
		// System.out.println("\nArray2: "+type+" "+isPrimitive+"\n");

		// System.out.println(in+" : " + type);

		return type;
	}

	protected String valueConversion(String value) {
		if (value == null)
			return value;
		if ("null".equals(value))
			return "0";
		return value;
	}

	public String toImpl() {
		StringBuffer ret = new StringBuffer();

		ret.append("#include \"all.h\"\n");
		ret.append("\n");

		ret.append("#include \"" + name.toLowerCase() + ".h\"\n\n");
		ret.append(name + "::~" + name + "() {\n");
		if(hasFinalizer)
			ret.append("\tfinalize();\n");
		ret.append("\n}\n\n");
	
		for (int i = 0; i < nmethods; i++) {
			if (methods[i].isAbstract)
				continue;

			ret.append("");

			if (!methods[i].isConstructor)
				ret.append(methods[i].retType + " ");

			ret.append(name + "::" + methods[i].name + "(");
			for (int p = 0; p < methods[i].paramNames.length; p++) {
				ret.append(methods[i].paramTypes[p] + " "
						+ methods[i].paramNames[p]);
				if (p < methods[i].paramNames.length - 1)
					ret.append(", ");
			}
			ret.append(")");

			if (methods[i].isConstructor && methods[i].constrexpr > 0) {
				ret.append(" : " + superClazz + "(");
				for (int c = 0; c < methods[i].constrexpr; c++) {
					ret.append(methods[i].constrExprs[c]);
					if (c < methods[i].constrexpr - 1)
						ret.append(", ");
				}
				ret.append(")");
			}
			methods[i].body = methods[i].body.substring(1,
					methods[i].body.length() - 2);
			ret.append(" {\n");
			if (methods[i].isConstructor) {
				for (int f = 0; f < fields; f++) {
					
					if (!staticFields[f] && !finalFields[f]
							&& fieldValues[f] == null
							/*&& (fieldTypes[f].contains("*") || fieldNames[f].contains("*"))*/) {
						ret.append("\t" + fieldNames[f] + " = 0;\n");
					}
					if (!staticFields[f] && !finalFields[f]
							&& fieldValues[f] != null
							&& !fieldValues[f].startsWith("{")) {
						ret.append("\t"
								+ fieldNames[f]
								+ ((fieldValues[f] == null) ? ""
										: (" = " + fieldValues[f])) + ";\n");
					}
				}
			}
			ret.append(methods[i].body + "\n}\n\n\n");
		}
		for (int i = 0; i < fields; i++) {
			if (staticFields[i]
					&& (!finalFields[i] || fieldValues[i].startsWith("{"))) {
				ret.append(fieldTypes[i] + " " + name + "::" + fieldNames[i]
						+ " = " + fieldValues[i] + ";\n");
			}
		}
		return ret.toString();
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();

		ret.append("#ifndef _" + name.toUpperCase() + "_\n#define _"
				+ name.toUpperCase() + "_\n\n");

		ret.append("#include <string.h>\n#include <math.h>\n#include <cstdlib>\n#include \"types.h\"\n");

		if (superClazz != null)
			ret.append("#include \"" + superClazz.toLowerCase() + ".h\"\n");

		for (String ref : interfaces) {
			ret.append("#include \"" + ref.toLowerCase().replace("*", "")
					+ ".h\"\n");
		}
		ret.append("\n#define null 0\n\n");

		for (String ref : references) {
			ret.append("class " + ref + ";\n");
		}

		ret.append("\nclass " + name);

		if (superClazz != null) {
			ret.append(" : public " + superClazz);
			if (interfaces.size() > 0)
				ret.append(", ");
		} else {
			if (interfaces.size() > 0)
				ret.append(" : public ");
		}
		Object ios[] = interfaces.toArray();
		for (int i = 0; i < ios.length; i++) {
			String iface = (String) ios[i];
			ret.append(iface.replace("*", ""));
			if (i < ios.length - 1)
				ret.append(", ");
		}
		ret.append(" {\npublic:");
		for (int i = 0; i < fields; i++) {
			ret.append("\n\t");
			if (staticFields[i]) {
				ret.append("static ");
			}
			if (!staticFields[i] && finalFields[i]
					&& !fieldValues[i].startsWith("{")) {
				ret.append("const "
						+ fieldTypes[i]
						+ " "
						+ fieldNames[i]
						+ ((fieldValues[i] == null) ? ""
								: (" = " + fieldValues[i])) + ";");
			} else {
				if (fieldValues[i] != null && fieldValues[i].startsWith("{")) {
					fieldTypes[i] = fieldTypes[i].replace("*", "");
					fieldNames[i] += "[]";
				}
				if (!finalFields[i] || fieldValues[i].startsWith("{"))
					ret.append(fieldTypes[i] + " " + fieldNames[i] + ";");
				else
					ret.append("const " + fieldTypes[i] + " " + fieldNames[i]
							+ " = " + fieldValues[i] + ";");
			}
		}
		ret.append("\n\n");
		for (int i = 0; i < nmethods; i++) {
			ret.append("\t");
			if (!methods[i].isConstructor && !methods[i].isStatic)
				ret.append("virtual ");
			if (methods[i].isStatic)
				ret.append("static ");
			if (!methods[i].isConstructor)
				ret.append(methods[i].retType + " ");
			ret.append(methods[i].name + "(");
			for (int p = 0; p < methods[i].paramNames.length; p++) {
				ret.append(methods[i].paramTypes[p] + " "
						+ methods[i].paramNames[p]);
				if (p < methods[i].paramNames.length - 1)
					ret.append(", ");
			}
			ret.append(")");
			if (methods[i].isAbstract)
				ret.append(" = 0");
			ret.append(";\n");

		}
		ret.append("\tvirtual ~" + name + "();\n};\n\n");

		ret.append("\n#endif\n");

		return ret.toString() + "\n";
	}
}
