package de.digitalemil.tocplusplus;

import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

public class AnnoValueAndLocation {
	public String value;
	public String loc;
	
	public AnnoValueAndLocation(String value, String loc) {
		this.value= value;
		this.loc= loc.toString();
		System.out.println("Location: "+loc.toString());
	}
}
