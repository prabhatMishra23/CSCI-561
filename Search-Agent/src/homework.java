import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class homework {

	public static void main(String[] args) {
		try {
			List<String> lines = Files.readAllLines(Paths.get("input2.txt"), StandardCharsets.UTF_8);
			String algo = lines.get(0);
			String[] line = lines.get(1).split(" ");
			int columns = Integer.parseInt(line[0]);
			int length = Integer.parseInt(line[1]);
			line = lines.get(2).split(" ");
			int[] landing = new int[line.length];
			landing[0] = Integer.parseInt(line[0]);
			landing[1] = Integer.parseInt(line[1]);
			int threshhold = Integer.parseInt(lines.get(3));
			int numTargets = Integer.parseInt(lines.get(4));
			int[][] targets = new int[numTargets][2];
			int i = 5;
			int count = 0;
			while (numTargets > 0) {
				line = lines.get(i).split(" ");
				targets[count][0] = Integer.parseInt(line[0]);
				targets[count][1] = Integer.parseInt(line[1]);
				i++;
				count++;
				numTargets--;
			}
			int[][] elevation = new int[length][columns];
			for (int p = 0; p < elevation.length; p++) {
				line = lines.get(i + p).split("\\s+");
				for (int j = 0; j < elevation[0].length; j++) {
					elevation[p][j] = Integer.parseInt(line[j]);
				}
			}
			switch (algo) {
			case "BFS":
				List<Node> nodesBFS = new ArrayList<Node>();
				for (int[] target : targets) {
				Queue<Node> openBFS = new LinkedList<Node>();
				Node nodeBFS = doBFSTraversal(landing, target, elevation, threshhold, algo, targets, openBFS);
				nodesBFS.add(nodeBFS);
				}
				generateAnswer(nodesBFS);
				break;
			case "UCS":
				List<Node> nodesUCS = new ArrayList<Node>();
				for (int[] target : targets) {
					Queue<Node> openUCS = new PriorityQueue<Node>(new CostComparator());
					Node nodesAStar = doTraversal(landing, target, elevation, threshhold, algo, targets,
							openUCS);
					nodesUCS.add(nodesAStar);
				}
				generateAnswer(nodesUCS);
				break;
			case "A*":
				List<Node> solutionAStar = new ArrayList<Node>();
				for (int[] target : targets) {
					Queue<Node> openAStar = new PriorityQueue<Node>(new CostComparator());
					Node nodesAStar = doAstarTraversal(landing, target, elevation, threshhold, algo, targets,
							openAStar);
					solutionAStar.add(nodesAStar);
				}
				generateAnswer(solutionAStar);
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateAnswer(List<Node> nodes) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		int count = nodes.size();
		for (Node node : nodes) {
			String s = "";
			if (node == null) {
				s = "FAIL";
			} else {
				s = "";
				String old = "";
				while (node != null) {
					s = node.x + "," + node.y + " " + old;
					old = s;
					node = node.previous;
				}
			}
			count--;
			if (count > 0) {
				writer.write(s.trim() + "\n");
			} else {
				writer.write(s.trim());
			}
		}
		writer.close();
	}

	private static Node doTraversal(int[] landing, int[] target, int[][] elevation,
			int threshhold, String algo, int[][] targets, Queue<Node> open) {
		Map<String, Node> explored = new HashMap<String, Node>();
		Map<String,Node> openHash = new HashMap<String, Node>();
		Node start = new Node(landing[0], landing[1], 0, elevation[landing[1]][landing[0]], null);
		open.add(start);
		openHash.put(landing[0]+","+landing[1],start);
		while (true) {
			if (open.isEmpty()) {
				break;
			}
			Node current = open.remove();
			openHash.remove(current.x + "," + current.y, current);
			if (current.x == target[0] && current.y == target[1]) {
				current.targetReached = true;
				return current;
			}
			findChildren(elevation, current, algo, threshhold, explored, open, null,openHash);
			explored.put(current.x + "," + current.y, current);
		}
		return null;

	}
	private static Node doBFSTraversal(int[] landing, int[] target, int[][] elevation,
			int threshhold, String algo, int[][] targets, Queue<Node> open) {
		Map<String, Node> visited = new HashMap<String, Node>();
		Node start = new Node(landing[0], landing[1], 0, elevation[landing[1]][landing[0]], null);
		open.add(start);
		visited.put(landing[0]+","+landing[1],start);
		while (true) {
			if (open.isEmpty()) {
				break;
			}
			Node current = open.remove();
			visited.put(current.x + "," + current.y, current);
			if (current.x == target[0] && current.y == target[1]) {
				current.targetReached = true;
				return current;
			}
			findBFSChildren(elevation, current, algo, threshhold, visited, open, null,null);
		}
		return null;

	}

	private static void findBFSChildren(int[][] elevation, Node current, String algo, int threshhold,
			Map<String, Node> visited, Queue<Node> open, Object object, Object object2) {
		Node child = null;
		if (current.x + 1 < elevation[0].length && !visited.containsKey(current.x + 1 + "," + current.y)) {
			child = current.move(current, current.x + 1, current.y, elevation, algo, threshhold, open, null, null); // moveEast
			if (child != null) {
				open.add(child);
				visited.put(child.x + "," + child.y, child);
			}
		}
		if (current.x - 1 >= 0 && !visited.containsKey(current.x - 1 + "," + current.y)) {
			child = current.move(current, current.x - 1, current.y, elevation, algo, threshhold, open, null, null); // move
																												// West
			if (child != null) {
				open.add(child);
				visited.put(child.x + "," + child.y, child);
			}
		}
		if (current.y + 1 < elevation.length && !visited.containsKey(current.x + "," + current.y + 1)) {
			child = current.move(current, current.x, current.y + 1, elevation, algo, threshhold, open, null, null); // move
																												// South
			if (child != null) {
				open.add(child);
				visited.put(child.x + "," + child.y, child);
			}
		}
		if (current.y - 1 >= 0 && !visited.containsKey(current.x + "," + (current.y - 1))) {
			child = current.move(current, current.x, current.y - 1, elevation, algo, threshhold, open, null, null); // move
																												// North
			if (child != null) {
				open.add(child);
				visited.put(child.x + "," + child.y, child);
			}
		}
		if (current.x + 1 < elevation[0].length && current.y + 1 < elevation.length
				&& !visited.containsKey(current.x + 1 + "," + (current.y + 1))) {
			child = current.move(current, current.x + 1, current.y + 1, elevation, algo, threshhold, open, null, null); // move
			// south
			// east
			if (child != null) {
				open.add(child);
				visited.put(child.x + "," + child.y, child);
			}
		}
		if (current.x - 1 >= 0 && current.y - 1 >= 0 && !visited.containsKey(current.x - 1 + "," + (current.y - 1))) {
			child = current.move(current, current.x - 1, current.y - 1, elevation, algo, threshhold, open, null, null); // move
			// north
			// west
			if (child != null) {
				open.add(child);
				visited.put(child.x + "," + child.y, child);
			}
		}
		if (current.x - 1 >= 0 && current.y + 1 < elevation.length
				&& !visited.containsKey(current.x - 1 + "," + (current.y + 1))) {
			child = current.move(current, current.x - 1, current.y + 1, elevation, algo, threshhold, open, null, null);
			if (child != null) {
				open.add(child);
				visited.put(child.x + "," + child.y, child);
			}
		}
		if (current.x + 1 < elevation[0].length && current.y - 1 >= 0
				&& !visited.containsKey(current.x + 1 + "," + (current.y - 1))) {
			child = current.move(current, current.x + 1, current.y - 1, elevation, algo, threshhold, open, null, null);
			if (child != null) {
				open.add(child);
				visited.put(child.x + "," + child.y, child);
			}
		}
	}
		

	private static Node doAstarTraversal(int[] landing, int[] target, int[][] elevation, int threshhold, String algo,
			int[][] targets, Queue<Node> open) {
		Map<String, Node> explored = new HashMap<String, Node>();
		Map<String,Node> openHash = new HashMap<String, Node>();
		Node start = new Node(landing[0], landing[1], 0, elevation[landing[1]][landing[0]], null);
		start.pathCost = getHeuristic(start, target[0], target[1]);
		open.add(start);
		openHash.put(landing[0]+","+landing[1],start);
		while (true) {
			if (open.isEmpty()) {
				break;
			}
			Node current = open.remove();
			openHash.remove(current.x + "," + current.y, current);
			if (current.x == target[0] && current.y == target[1]) {
				current.targetReached = true;
				return current;
			}
			findChildren(elevation, current, algo, threshhold, explored, open, target,openHash);
			explored.put(current.x + "," + current.y, current);
		}
		return null;
	}

	public static int getHeuristic(Node node, int i, int j) {
		return (int)Math.hypot(Math.abs((node.x - i)*10), (Math.abs(node.y - j)*10));
	}

	private static void findChildren(int[][] elevation, Node current, String algo, int threshhold,
			Map<String, Node> explored, Queue<Node> open, int[] target, Map<String, Node> openHash) {
		Node child = null;
		if (current.x + 1 < elevation[0].length && !explored.containsKey(current.x + 1 + "," + current.y)) {
			child = current.move(current, current.x + 1, current.y, elevation, algo, threshhold, open, target, openHash); // moveEast
			if (child != null) {
				open.add(child);
				openHash.put(child.x + "," + child.y, child);
			}
		}
		if (current.x - 1 >= 0 && !explored.containsKey(current.x - 1 + "," + current.y)) {
			child = current.move(current, current.x - 1, current.y, elevation, algo, threshhold, open, target, openHash); // move
																												// West
			if (child != null) {
				open.add(child);
				openHash.put(child.x + "," + child.y, child);
			}
		}
		if (current.y + 1 < elevation.length && !explored.containsKey(current.x + "," + current.y + 1)) {
			child = current.move(current, current.x, current.y + 1, elevation, algo, threshhold, open, target, openHash); // move
																												// South
			if (child != null) {
				open.add(child);
				openHash.put(child.x + "," + child.y, child);
			}
		}
		if (current.y - 1 >= 0 && !explored.containsKey(current.x + "," + (current.y - 1))) {
			child = current.move(current, current.x, current.y - 1, elevation, algo, threshhold, open, target, openHash); // move
																												// North
			if (child != null) {
				open.add(child);
				openHash.put(child.x + "," + child.y, child);
			}
		}
		if (current.x + 1 < elevation[0].length && current.y + 1 < elevation.length
				&& !explored.containsKey(current.x + 1 + "," + (current.y + 1))) {
			child = current.move(current, current.x + 1, current.y + 1, elevation, algo, threshhold, open, target, openHash); // move
			// south
			// east
			if (child != null) {
				open.add(child);
				openHash.put(child.x + "," + child.y, child);
			}
		}
		if (current.x - 1 >= 0 && current.y - 1 >= 0 && !explored.containsKey(current.x - 1 + "," + (current.y - 1))) {
			child = current.move(current, current.x - 1, current.y - 1, elevation, algo, threshhold, open, target, openHash); // move
			// north
			// west
			if (child != null) {
				open.add(child);
				openHash.put(child.x + "," + child.y, child);
			}
		}
		if (current.x - 1 >= 0 && current.y + 1 < elevation.length
				&& !explored.containsKey(current.x - 1 + "," + (current.y + 1))) {
			child = current.move(current, current.x - 1, current.y + 1, elevation, algo, threshhold, open, target, openHash);
			if (child != null) {
				open.add(child);
				openHash.put(child.x + "," + child.y, child);
			}
		}
		if (current.x + 1 < elevation[0].length && current.y - 1 >= 0
				&& !explored.containsKey(current.x + 1 + "," + (current.y - 1))) {
			child = current.move(current, current.x + 1, current.y - 1, elevation, algo, threshhold, open, target, openHash);
			if (child != null) {
				open.add(child);
				openHash.put(child.x + "," + child.y, child);
			}
		}
	}

}
