package org.ECS193.TripleDataProcessor;

public abstract class TripleElement {
	private String name;
	
	public TripleElement(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
