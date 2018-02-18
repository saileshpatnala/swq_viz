import java.util.ArrayList;

public class Record {
	private ArrayList<Triple> record;
	
	public Record() {
		this.setRecord(new ArrayList<Triple>());
	}

	public ArrayList<Triple> getRecord() {
		return record;
	}

	public void setRecord(ArrayList<Triple> record) {
		this.record = record;
	}


}
