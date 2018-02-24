package org.ECS193.TripleDataProcessor;

public class Triple {
	private TripleElement subject;
	private TripleElement predicate;
	private TripleElement object;
	
	public Triple(String subject, String predicate, String object) {
		this.subject = new Subject(subject);
		this.predicate = new Predicate(predicate);
		this.object = new ObjectElement(object);
	}

	public TripleElement getSubject() {
		return subject;
	}

	public void setSubject(TripleElement subject) {
		this.subject = subject;
	}

	public TripleElement getPredicate() {
		return predicate;
	}

	public void setPredicate(TripleElement predicate) {
		this.predicate = predicate;
	}

	public TripleElement getObject() {
		return object;
	}

	public void setObject(TripleElement object) {
		this.object = object;
	}


}
