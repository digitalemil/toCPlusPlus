package de.digitalemil.tocplusplus;

public class Method {
	protected String name;
	protected String retType;
	protected String[] paramTypes;
	protected String[] paramNames;
	protected String[] constrExprs;

	protected String body;
	protected int p;
	protected int constrexpr;
	protected boolean isAbstract, isConstructor, isStatic;
	protected Clazz clazz;

	protected Method(Clazz clazz, String name, String retType, int n) {
		this.clazz = clazz;
		this.name = name;
		this.retType = retType;
		p = 0;
		paramTypes = new String[n];
		paramNames = new String[n];
		constrExprs = new String[64];

		isAbstract = false;
		isConstructor = false;
		isStatic = false;
	}

	public void addSuperConstrExpr(String e) {
		if (Character.isUpperCase(e.charAt(0))) {
			e = e.replace(".", "::");
		}
		if (e.startsWith("\"")) {
			e = "(" + ToCPlusPlusClazz.STRINGTYPE + ")" + e;
		}

		constrExprs[constrexpr++] = e;
	}

	public void addParam(String t, String n) {
		paramNames[p] = n;
		paramTypes[p] = clazz.typeConversion(t);
		// System.out.println("Method: "+name+" "+n+" "+paramTypes[p]);
		p++;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String b) {
		body = bodyConversion(b);
	}

	private String replaceDot(String in) {
		String ret = in;
		int i = -1;

		do {
			i = ret.indexOf(".", i + 1);
			int j = i;
			if (i> 0 && Character.isDigit(ret.charAt(i + 1)) && Character.isDigit(ret.charAt(i - 1))){
				continue;
			}
			boolean isStatic = false;
			
			while (j > 0) {
				j--;
				if (!Character.isJavaIdentifierPart(ret.charAt(j)) && Character.isUpperCase(ret.charAt(j + 1))) {
					isStatic = true;
					ret = ret.substring(0, i) + "::" + ret.substring(i + 1);
					break;
				}
				if (!Character.isJavaIdentifierPart(ret.charAt(j)))
					break;
			}

			if (!isStatic && i> 0) {
				ret = ret.substring(0, i) + "->" + ret.substring(i + 1);
			}
		} while (i > -1);

		return ret;
	}

	private String bodyConversion(String body) {
		StringBuffer ret = new StringBuffer(body);
		if (!ret.toString().startsWith("{")) {
			ret.insert(0, "{\n");
			ret.append("}\n");
		}
		String b = ret.toString();
		b = b.replaceAll("super\\(", "//super(");
		b = b.replaceAll("super.", clazz.superClazz + "::");
		b = b.replaceAll("String\\[\\]", ToCPlusPlusClazz.STRINGTYPE + "*");
		b = b.replaceAll("String", ToCPlusPlusClazz.STRINGTYPE);
		b = b.replaceAll("Types\\.", "Types::");
		b = b.replace("boolean", "bool");
		b = b.replace("Math.", "");
		b = b.replace("this.", "this->");
		b = b.replaceAll("System\\.", "//System.");
		b = b.replaceAll("]\\.", "]->");
		b = b.replaceAll("\\)\\.", ")->");
		b = b.replaceAll("\\[\\]", "*");
		// b= b.replaceAll("(\\s*)(\\w*\\s*=new\\s*)([A-Z]\\w*)", "$1$2$3*");
		b = b.replaceAll("\\.TRIANGLE", "::TRIANGLE");
		b = b.replaceAll("(\\w\\s*)( \\w*\\s*=\\s*new)", "$1*$2");
		b = b.replaceAll("(\"[^\"]*\")", "(" + ToCPlusPlusClazz.STRINGTYPE
				+ ")$1");
		b = b.replace("null", "0");
		b = b.replaceAll("\\(([A-Z]\\w*)\\)", "($1*)");

		b = replaceDot(b);

		return b;
	}
}
