import java.util.ArrayList;

public class EndPoint {
	private ArrayList<Record> endPoint;
	
	public EndPoint() {
		this.setEndPoint(new ArrayList<Record>());
	}

	public ArrayList<Record> getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(ArrayList<Record> endPoint) {
		this.endPoint = endPoint;
	}
}
