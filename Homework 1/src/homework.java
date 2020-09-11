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
			long startTime = System.nanoTime();
			List<String> lines = Files.readAllLines(Paths.get("src/shagun/input.txt"), StandardCharsets.UTF_8);
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
				line = lines.get(i + p).split(" ");
				for (int j = 0; j < elevation[0].length; j++) {
					elevation[p][j] = Integer.parseInt(line[j]);
				}
			}
			switch (algo) {
			case "BFS":
				Queue<Node> openBFS = new LinkedList<Node>();
				List<Node> nodesBFS = new ArrayList<Node>();
				for (int[] target : targets) {
				Node nodeBFS = doTraversal(landing, target, elevation, threshhold, algo, targets, openBFS);
				nodesBFS.add(nodeBFS);
				}
				generateAnswer(nodesBFS);
				break;
			case "UCS":
				Queue<Node> openUCS = new PriorityQueue<Node>(new CostComparator());
				List<Node> nodesUCS = new ArrayList<Node>();
				for (int[] target : targets) {
					Node nodesAStar = doTraversal(landing, target, elevation, threshhold, algo, targets,
							openUCS);
					nodesUCS.add(nodesAStar);
				}
				generateAnswer(nodesUCS);
				break;
			case "A*":
				Queue<Node> openAStar = new PriorityQueue<Node>(new CostComparator());
				List<Node> solutionAStar = new ArrayList<Node>();
				for (int[] target : targets) {
					Node nodesAStar = doAstarTraversal(landing, target, elevation, threshhold, algo, targets,
							openAStar);
					solutionAStar.add(nodesAStar);
				}
				generateAnswer(solutionAStar);
			default:
				break;
			}
			long endTime = System.nanoTime();
			System.out.println((endTime - startTime) / 1000000000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateAnswer(List<Node> nodes) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter("cost.txt"));
		int count = nodes.size();
		for (Node node : nodes) {
			String s = "";
			String costS = "";
			if (node == null) {
				s = "FAIL";
				costS = "0";
			} else {
				s = "";
				String old = "";
				costS = "" + node.actualCost;
				while (node != null) {
					s = node.x + "," + node.y + " " + old;
					old = s;
					node = node.previous;
				}
			}
			count--;
			if (count > 0) {
				writer.write(s.trim() + "\n");
				writer2.write(costS + "\n");
			} else {
				writer.write(s.trim());
				writer2.write(costS.trim());
			}
		}
		writer.close();
		writer2.close();
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
		return (int) (Math.hypot(Math.abs(node.x - i), Math.abs(node.y - j)));
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
