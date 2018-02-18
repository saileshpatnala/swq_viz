import java.util.ArrayList;

public class Triple {
	private ArrayList<TripleElement> triple;
	
	public Triple() {
		this.setTriple(new ArrayList<TripleElement>());
	}

	public ArrayList<TripleElement> getTriple() {
		return triple;
	}

	public void setTriple(ArrayList<TripleElement> triple) {
		this.triple = triple;
	}
}
