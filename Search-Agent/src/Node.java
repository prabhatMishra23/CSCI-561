import java.util.Map;
import java.util.Queue;

public class Node {
	int x;
	int y;
	int pathCost;
	int zElevation;
	int actualCost;
	Node previous;
	boolean targetReached;

	public Node(int x, int y, int pathCost, int zElevation, Node previous) {
		this.y = y;
		this.x = x;
		this.pathCost = pathCost;
		this.zElevation = zElevation;
		this.previous = previous;
		this.targetReached = false;
	}

	public Node() {
		this.y = 0;
		this.x = 0;
		this.pathCost = 0;
	}

	public Node move(Node currentState, int xNew, int yNew, int[][] elevation, String algo, int threshhold,
			Queue<Node> open, int[] target, Map<String, Node> openHash) {
		Node nextState = null;
		if (Math.abs(elevation[yNew][xNew] - currentState.zElevation) > threshhold) {// change 0 to threshhold
			return null;
		} else {
			nextState = new Node(xNew, yNew, 0, elevation[yNew][xNew], currentState);
			if ("BFS".equals(algo)) {
				nextState.actualCost = currentState.actualCost + 1;
			} else if ("UCS".equals(algo)) {
				nextState = updateUCSCost(nextState, currentState, open,openHash);
			} else if ("A*".equals(algo)) {
				nextState = updateAstarCost(nextState, currentState, open, target,openHash);
			}
		}
		return nextState;
	}

	private Node updateUCSCost(Node nextState, Node currentState, Queue<Node> open, Map<String, Node> openHash) {
		if (Math.abs(nextState.x - currentState.x) == 1 && Math.abs(nextState.y - currentState.y) == 1) {
			nextState.pathCost = currentState.pathCost + 14;
		} else {
			nextState.pathCost = currentState.pathCost + 10;
		}
		nextState.actualCost = nextState.pathCost;
		if (!open.isEmpty()) {
			nextState = updateOpen(nextState, open,openHash);
		}
		return nextState;
	}

	private Node updateOpen(Node nextState, Queue<Node> open, Map<String, Node> openHash) {
		//for (Node n : open) {
		String key = nextState.x+","+nextState.y;
		if (openHash.containsKey(key)){
			Node n = openHash.get(key);
				if (n.pathCost <= nextState.pathCost) {
					return null;
				} else {
					open.remove(n);
					openHash.remove(key);
				}
			}
		return nextState;
	}

	private Node updateOpenAstar(Node nextState, Queue<Node> open, Map<String, Node> openHash) {
		//for (Node n : open) {
			String key = nextState.x+","+nextState.y;
			if (openHash.containsKey(key)){
				Node n = openHash.get(key);
				if (n.pathCost < nextState.pathCost) {
					return null;
				} else {
					if(n.pathCost == nextState.pathCost) {
						if(n.actualCost < nextState.actualCost) {
							return null;
						}
					}
					open.remove(n);
					openHash.remove(key);
				}
				//break;
			//}
		}
		return nextState;
	}

	private Node updateAstarCost(Node nextState, Node currentState, Queue<Node> open, int[] target, Map<String, Node> openHash) {
		if (Math.abs(nextState.x - currentState.x) == 1 && Math.abs(nextState.y - currentState.y) == 1) {
			nextState.actualCost = currentState.actualCost + 14;
		} else {
			nextState.actualCost = currentState.actualCost + 10;
		}
		nextState.actualCost += Math.abs(currentState.zElevation - nextState.zElevation);
		nextState.pathCost = nextState.actualCost+homework.getHeuristic(nextState, target[0], target[1]);
		if (!open.isEmpty()) {
			nextState = updateOpenAstar(nextState, open, openHash);
		}
		return nextState;
	}
}