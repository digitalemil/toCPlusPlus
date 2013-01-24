package de.digitalemil.tocplusplus;

import java.util.HashSet;
import java.util.Set;

public abstract class Clazz {
	protected String superClazz;
	protected Set<String> interfaces;
	protected String name;
	protected Set<String> references;
	protected String [] fieldNames;
	protected String[] fieldTypes;
	protected String[] fieldValues;
	protected boolean[] staticFields;
	protected boolean[] finalFields;
	protected Method[] methods= new Method[1024];
	protected int nmethods= 0;
	
	protected int fields;
	
	public Clazz() {
		references= new HashSet<String>();
		interfaces= new HashSet<String>();
		fieldNames= new String[1024];
		fieldTypes= new String[1024];
		fieldValues= new String[1024];
		staticFields= new boolean[1024];
		finalFields= new boolean[1024];
		fields= 0;
	}
	
	public Clazz(String name, String superClazz) {
		this();
		this.name= name;
		this.superClazz= superClazz;
		addReference(superClazz);
	}
	
	public void setSuperClazz(String sc) {
		superClazz= sc;
		addReference(superClazz);
	}
	
	public Method addMethod(String name, String retType, int n) {
		methods[nmethods]= new Method(this, name, typeConversion(retType), n);
		return methods[nmethods++];
	}
	
	public void addField(String name, String type, String value, boolean isStatic, boolean isFinal) {
	//	System.out.println("Adding Field: "+name+" "+type+" "+value);
		fieldNames[fields]= name;
		fieldTypes[fields]= typeConversion(type);
		fieldValues[fields]= valueConversion(value);
		staticFields[fields]= isStatic;
		finalFields[fields]= isFinal;
		addReference(type);
		fields++;
	}
	
	public void addInterface(String i) {
		addReference(i);
		interfaces.add(typeConversion(i));
	}
	
	public abstract void addReference(String ref);

	protected abstract String typeConversion(String type);
	
	protected abstract String valueConversion(String value);
	
	public String toImpl() {
		StringBuffer ret= new StringBuffer();	
		return ret.toString();
	}
	
	public String toString() {
		StringBuffer ret= new StringBuffer();	
		return ret.toString();
	}
}
