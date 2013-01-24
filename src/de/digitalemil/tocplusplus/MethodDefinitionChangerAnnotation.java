package de.digitalemil.tocplusplus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MethodDefinitionChangerAnnotation {
	public final static String SUPERCONSTRUCTOR= "SC", METHODPARAMETERNAME= "MPN", METHODPARAMETERTYPE= "MPT", INBODY= "BY";
	String[] value();
}