package de.digitalemil.tocplusplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class Main {

	public static Clazz parse(String str) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit cu = null;

		cu = (CompilationUnit) parser.createAST(null);

		final Clazz clazz = new ToCPlusPlusClazz();

		cu.accept(new ASTVisitor() {
			List superConstrExprs;
			String replacevalues, clazzvalues;
			String[] typerep = new String[1024];
			String[] searchrep = new String[1024];
			String[] replacerep = new String[1024];
			
			String[] clazztyperep = new String[1024];
			String[] clazzsearchrep = new String[1024];
			String[] clazzreplacerep = new String[1024];
			String[] clazzvaluerep = new String[1024];
			
			int replacements, clazzreplacements;

			public String replace(String type, String in) {
				String ret = in;
				for (int i = 0; i < replacements; i++) {
					// System.out.println("replace: "+typerep[i]+" search for: "+searchrep[i]+
					// " in: "+in+ " "+in.contains(searchrep[i]));

					if (type.equals(typerep[i]) && ret.contains(searchrep[i])) {
						ret = ret.replace(searchrep[i], replacerep[i]);
					}
				}
				return ret;
			}
			
			public void clazzModification(Clazz clazz) {
			//	System.out.println("\n\nClazzModification: "+clazzreplacements);
				for(int i= 0; i< clazzreplacements; i++) {
					System.out.println(clazztyperep[i]);
					if(clazztyperep[i].equals("AF")) {
					//	System.out.println("\n\nClazzModification: "+clazzsearchrep[i]+" "+clazzreplacerep[i]+" "+clazzvaluerep[i]);
						
						clazz.addField(clazzsearchrep[i], clazzreplacerep[i],clazzvaluerep[i], false, false);
					}
				}
				
			}

			public void prepareReplacements(String in) {
				String patternstr = "\"[^\"]*\"";
				// String patternstr = "*";

				Pattern pattern = Pattern.compile(patternstr);
				Matcher matcher = pattern.matcher(in);
				// Check all occurance
				int n = 0;

				while (matcher.find()) {
					if (n % 3 == 0) {
						typerep[replacements] = matcher.group().substring(1,
								matcher.group().length() - 1);
					}
					if (n % 3 == 1) {
						searchrep[replacements] = matcher.group().substring(1,
								matcher.group().length() - 1);
						searchrep[replacements] = searchrep[replacements]
								.replace("@EMPTYSTRING", "\"\"").replace('\'',
										'\"');

					}
					if (n % 3 == 2) {
						replacerep[replacements++] = matcher.group().substring(
								1, matcher.group().length() - 1);
						replacerep[replacements - 1] = replacerep[replacements - 1]
								.replace("@EMPTYSTRING", "\"\"").replace('\'',
										'\"');
						// System.out.println("Replacement: "+typerep[replacements-1]+" "+searchrep[replacements-1]+" "+replacerep[replacements-1]);

					}
					n++;
				}
			}

			public void prepareClazzReplacements(String in) {
				String patternstr = "\"[^\"]*\"";
				// String patternstr = "*";

				Pattern pattern = Pattern.compile(patternstr);
				Matcher matcher = pattern.matcher(in);
				// Check all occurance
				int n = 0;
				//System.out.println("PRECLAZZ: "+in);
				
				while (matcher.find()) {
					if (n % 4 == 0) {
						clazztyperep[clazzreplacements] = matcher.group().substring(1,
								matcher.group().length() - 1);
					//	System.out.println("PRECLAZZ: "+clazztyperep[clazzreplacements]);
					}
					if (n % 4 == 1) {
						clazzsearchrep[clazzreplacements] = matcher.group().substring(1,
								matcher.group().length() - 1);
						clazzsearchrep[clazzreplacements] = clazzsearchrep[clazzreplacements]
								.replace("@EMPTYSTRING", "\"\"").replace('\'',
										'\"');
					//	System.out.println("PRECLAZZ: "+clazzsearchrep[clazzreplacements]);
						
					}
					if (n % 4 == 2) {
						clazzreplacerep[clazzreplacements] = matcher.group().substring(
								1, matcher.group().length() - 1);
						clazzreplacerep[clazzreplacements] = clazzreplacerep[clazzreplacements]
								.replace("@EMPTYSTRING", "\"\"").replace('\'',
										'\"');
					//	System.out.println("PRECLAZZ: "+clazzreplacerep[clazzreplacements]);
						
					}
					if (n % 4 == 3) {
						clazzvaluerep[clazzreplacements++] = matcher.group().substring(
								1, matcher.group().length() - 1);
						clazzvaluerep[clazzreplacements - 1] = clazzvaluerep[clazzreplacements - 1]
								.replace("@EMPTYSTRING", "\"\"").replace('\'',
										'\"');
					//	System.out.println("PRECLAZZ: "+clazzvaluerep[clazzreplacements-1]);
						
					}
					n++;
				}
			}
			public void endVisit(MethodDeclaration node) {
				if (replacevalues != null) {
					prepareReplacements(replacevalues);
				}
				
				String retv = "void";
				if (node.getReturnType2() != null)
					retv = node.getReturnType2().toString();
				Method m = clazz.addMethod(node.getName().toString(), retv,
						node.parameters().size());

				Object params[] = node.parameters().toArray();
				for (int i = 0; i < params.length; i++) {
					SingleVariableDeclaration param = (SingleVariableDeclaration) params[i];
					// param.
					String type = param.getType().toString();

					if (!param.getType().isArrayType()
							&& param.toString().contains("[]"))
						type += "[]";
					// System.out.println(type+" "+param.toString());
					// if(!param.getType().isPrimitiveType())
					// type+="*";
					m.addParam(replace("MPT", type),
							replace("MPN", param.getName().toString()));
				}

				if ((node.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
					m.isAbstract = true;
				}
				if ((node.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
					m.isStatic = true;
				}
				if (node.isConstructor()) {
					m.isConstructor = true;
					if (superConstrExprs != null) {
						Object exprs[] = superConstrExprs.toArray();
						for (int i = 0; i < exprs.length; i++) {
							Expression expr = (Expression) exprs[i];
							m.addSuperConstrExpr(replace("SC", expr.toString()));
						}
					}
				}
				String body;
				if (node.getBody() == null)
					body = "";
				else
					body = replace("BY", node.getBody().toString());
				m.setBody(body);

				for (Object obj : node.parameters()) {
					SingleVariableDeclaration var = (SingleVariableDeclaration) obj;
					clazz.addReference(var.getType().toString());
				}
				replacevalues = null;
				replacements = 0;
			}

			public boolean visit(ExpressionStatement expr) {
				return true;
			}

			public boolean visit(AnnotationTypeDeclaration node) {
				// System.out.println("Annotation: " + node.getName());
				return true;
			}

			public boolean visit(SingleMemberAnnotation node) {
				if (node.getTypeName().toString()
						.equals("MethodDefinitionChangerAnnotation")) {
				
					replacevalues = node.getValue().toString();
				}
				if (node.getTypeName().toString()
						.equals("ClazzModifierAnnotation") && clazzvalues== null) {
				
					clazzvalues = node.getValue().toString();
				}
				return true;
			}

			public boolean visit(SuperConstructorInvocation node) {
				superConstrExprs = node.arguments();
				return true;
			}

			public boolean visit(MethodInvocation node) {
				if (node.resolveMethodBinding() == null) {
					return true;
				}
				clazz.addReference(node.resolveMethodBinding()
						.getDeclaringClass().getName());
				return true;
			}

			public boolean visit(VariableDeclarationStatement node) {
				clazz.addReference(node.getType().toString());
				return true;
			}

			public boolean visit(ClassInstanceCreation node) {
				clazz.addReference(node.getType().toString());
				return true;
			}

			public void endVisit(TypeDeclaration node) {
				if (clazzvalues != null) {
					prepareClazzReplacements(clazzvalues);
				}
				clazzModification(clazz); 
			}
			
			public boolean visit(TypeDeclaration node) {
				clazz.name = node.getName().toString();
				
				if (node.getSuperclassType() != null)
					clazz.setSuperClazz(node.getSuperclassType().toString());

				List ifs = node.superInterfaceTypes();
				for (Object obj : ifs) {
					Type type = (Type) obj;
					clazz.addInterface(type.toString());
				}

				FieldDeclaration fs[] = node.getFields();
				for (int i = 0; i < fs.length; i++) {
					boolean sta = false;
					if ((fs[i].getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
						sta = true;
					}
					boolean isfinal = false;
					if ((fs[i].getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
						isfinal = true;
					}

					String type = fs[i].getType().toString();
					// if (!fs[i].getType().isPrimitiveType())
					// type += "*";

					for (int l = 0; l < fs[i].fragments().size(); l++) {
						String field = fs[i].fragments().get(l).toString();
						String value = null;
						int e = field.length();
						if (field.contains("=")) {
							e = field.indexOf('=');
							value = field.substring(e + 1);
						}
						String name = field.substring(0, e);
						if (name.endsWith("[]")) {
							type += "[]";
							name = name.substring(0, name.length() - 2);
						}
						clazz.addField(replace("FN", name),
								replace("FT", type), replace("FV", value), sta,
								isfinal);
					}
				}
				return true;
			}
		});
		return clazz;
	}

	// read file content into a string
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();
		// System.out.println()
		return fileData.toString();
	}

	public static void ParseFile(String dir, String jname) throws IOException {

		File f = new File(jname);
		String filePath = f.getAbsolutePath();
		if (!f.isFile())
			return;
		System.out.println("File: " + filePath);
		String in = readFileToString(filePath);

		Clazz clazz = parse(in);

		if (clazz.name == null)
			return;

		File fh = new File(dir + "/include/" + clazz.name.toLowerCase() + ".h");
		FileWriter hw = new FileWriter(fh);
		hw.write(clazz.toString());
		hw.close();

		File fi = new File(dir + "/src/" + clazz.name.toLowerCase() + ".cpp");
		FileWriter iw = new FileWriter(fi);
		iw.write(clazz.toImpl());
		iw.close();

	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// ParseFilesInDir(args[0]);
		Process p = Runtime.getRuntime().exec("mkdir -p " + args[0] + "/src");
		p.waitFor();
		p = Runtime.getRuntime().exec("mkdir -p " + args[0] + "/include");
		p.waitFor();

		System.out.println("Folders created: " + args[0] + "/src" + " & "
				+ args[0] + "/include");

		p = Runtime.getRuntime().exec("cp helper/os.h " + args[0] + "/include");
		p.waitFor();

		p = Runtime.getRuntime().exec("cp helper/os.cpp " + args[0] + "/src");
		p.waitFor();

		for (int i = 1; i < args.length; i++) {
			try {
				ParseFile(args[0], args[i]);
			} catch (Exception e) {
				System.err.println("Error processing: " + args[i]);
				e.printStackTrace();
			}

		}

		File fa = new File(args[0] + "/include/all.h");
		FileWriter aw = new FileWriter(fa);

		aw.write("#ifndef _ALL_\n#define _ALL_\n\n");

		for (int i = 1; i < args.length; i++) {
			String file = args[i].replace(".java", "").toLowerCase();
			file = file.substring(file.lastIndexOf("/") + 1);
			if (file.contains("annotation"))
				continue;
			aw.write("#include \"" + file + ".h\"\n");
		}
		aw.write("#include \"os.h\"\n\n");
		aw.write("#include <stdio.h>\n");
		aw.write("#include <string.h>\n");

		aw.write("#define min(X,Y) ((X) < (Y) ? (X) : (Y))\n");
		aw.write("#define PI " + Math.PI + "\n");
		aw.write("extern unsigned char* tmptextbuffer;\n");
		aw.write("#endif\n\n");

		aw.close();

		File fm = new File(args[0] + "/Makefile");
		FileWriter mw = new FileWriter(fm);
		mw.write("\nINCLUDE= -Iinclude -I/Developer/SDKs/MacOSX10.6.sdk/System/Library/Frameworks/JavaVM.framework/Headers\n\n");
		mw.write("TARGET=$(shell uname)\nDISTDIR= ${TARGET}\nLD=$(CC)\nMKDIR=mkdir\n\nOBJECTFILES= \\\n");

		for (int i = 1; i < args.length; i++) {
			String file = args[i].replace(".java", "").toLowerCase();
			file = file.substring(file.lastIndexOf("/") + 1);
			if (file.contains("annotation"))
				continue;
			mw.write("\tbuild/${DISTDIR}/" + file + ".o \\\n");
		}
		mw.write("\tbuild/${DISTDIR}/os.o \\\n");

		mw.write("\nifeq ($(TARGET), Darwin)\n\tCC=g++\n\tCFLAGS =  -O2 -c -fno-common -pipe  ${INCLUDE} -D${TARGET} -DMACOSX\n\tTARGETOBJECTFILES= ${OBJECTFILES} \n\tLDFLAGS=-dynamiclib -fno-common -O2 -pipe -flat_namespace\n\tLIBNAME=tatanka.lib\n\tLDDISTDIR=build/${DISTDIR}\nendif\n");
		mw.write("\nifeq ($(TARGET),Linux)\n\tCC=g++\n\tCFLAGS = -Wall ${INCLUDE} -D${TARGET} -c $(DEBUG) -m32 -shared -fPIC -MMD -MP \n\tTARGETOBJECTFILES= ${OBJECTFILES}\n\tLDFLAGS=-shared -m32 -lc -lgcc -lstdc++ -lm\n\tLDDISTDIR=build/${DISTDIR}\n\tLIBNAME=libtatanka.so\nendif\n");
		mw.write("\nbuild/tatanka: ${TARGETOBJECTFILES} \n\t${MKDIR} -p build/${DISTDIR} ${LDDISTDIR}\n\t${LD} -fPIC -Wl -o ${LDDISTDIR}/${LIBNAME} $(LDFLAGS) ${OBJECTFILES} ${LDLIBSOPTIONS} \nifeq ($(TARGET), Darwin)\n\t$(CP) ${LDDISTDIR}/${LIBNAME} dist/${TARGET}/${LIBNAME}\nendif\n\n");

		for (int i = 1; i < args.length; i++) {
			String file = args[i].replace(".java", "").toLowerCase();
			file = file.substring(file.lastIndexOf("/") + 1);
			if (file.contains("annotation"))
				continue;
			mw.write("build/${DISTDIR}/"
					+ file
					+ ".o: src/"
					+ file
					+ ".cpp\n\t${MKDIR} -p build/${DISTDIR}\n\t$(CC) -I${INCLUDE} ${CFLAGS} -o build/${DISTDIR}/"
					+ file + ".o src/" + file + ".cpp\n\n");
		}
		mw.write("build/${DISTDIR}/"
				+ "os"
				+ ".o: src/"
				+ "os"
				+ ".cpp\n\t${MKDIR} -p build/${DISTDIR}\n\t$(CC) -I${INCLUDE} ${CFLAGS} -o build/${DISTDIR}/"
				+ "os" + ".o src/" + "os" + ".cpp\n\n");

		mw.write("clean:\n\t${RM} -rf build/${DISTDIR}/* build\n\n");

		mw.close();

	}
}