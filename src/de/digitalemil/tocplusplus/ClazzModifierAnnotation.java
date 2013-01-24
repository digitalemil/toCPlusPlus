package de.digitalemil.tocplusplus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface ClazzModifierAnnotation {
	public final static String ADDFIELD= "AF";
	String[] value();
}