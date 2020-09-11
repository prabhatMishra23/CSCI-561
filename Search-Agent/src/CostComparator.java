import java.util.Comparator;

public class CostComparator implements Comparator<Node> {

	@Override
	public int compare(Node node1, Node node2) {
		 if(node1.pathCost<node2.pathCost) {
			 return -1;
		 }else if(node1.pathCost>node2.pathCost){
			 return 1;
		 }else {
			 return 0;
		 }
	}

}
