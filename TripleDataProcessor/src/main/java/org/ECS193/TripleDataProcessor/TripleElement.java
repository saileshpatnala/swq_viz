package org.ECS193.TripleDataProcessor;

public abstract class TripleElement {
	private String name;
	private TYPE type;
	
	enum TYPE
	{
	    literal, uri;
	}
	
	public TripleElement(String name, TYPE type) {
		this.setName(name);
		this.setType(type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
	
	
	
}
