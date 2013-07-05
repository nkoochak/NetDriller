package ca.ucalgary.dblab.netdriller.data;

public class GraphEdge {
	double weight;
	String str;
	
	public GraphEdge(double wight){
		this.weight = wight;
	}
	
	public String getLabel(){
		return Double.toString(weight);
	}
	
	public String ToString(){
		return str;
	}

	public double getWeight() {
		return weight;
	}
}
