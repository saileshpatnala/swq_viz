package org.ECS193.TripleDataProcessor.data;

import org.ECS193.TripleDataProcessor.data.TripleElement.TYPE;

public class Triple {
	private TripleElement subject;
	private TripleElement predicate;
	private TripleElement object;
	
	public Triple(String subject, TYPE subjectType, String predicate, TYPE predicateType, String object, TYPE objectType) {
		this.subject = new SubjectElement(subject, subjectType);
		this.predicate = new PredicateElement(predicate, predicateType);
		this.object = new ObjectElement(object, objectType);
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
