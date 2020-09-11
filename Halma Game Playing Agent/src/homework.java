import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class homework {
	public static int depth = 0;
	public static Grid[][] halma;
	public static String playerMax;
	public static int pruning;

	public static void main(String[] args) throws IOException, InterruptedException {
		List<String> lines = Files.readAllLines(Paths.get("input.txt"), StandardCharsets.UTF_8);
		String type = lines.get(0);
		String player = lines.get(1);
		if (player.equals("WHITE")) {
			player = "W";
			playerMax = "W";
		} else {
			player = "B";
			playerMax = "B";
		}
		Double time = Double.parseDouble(lines.get(2));
		halma = new Grid[16][16];
		int i = 3;
		for (int p = 0; p < 16; p++) {
			String[] line = lines.get(i + p).split("");
			for (int j = 0; j < 16; j++) {
				if (!line[j].equals(".")) {
					halma[j][p] = new Grid(line[j], p, j);
				} else {
					halma[j][p] = new Grid("E", p, j);
				}
				if (p < 5 && j < 5 && p + j <= 5) {
					halma[j][p].region = Grid.BLACK;
				}
				if (p > 10 && j > 10 && p + j >= 25) {
					halma[j][p].region = Grid.WHITE;
				}
			}
		}

		if (type.equals("SINGLE")) {
			depth = Grid.DEPTH_SINGLE_MAX;
			Action action = allPiecesAreOut();
			if (action != null) {
				printAction(action);
			} else {
				MinMaxValues result = minimaxAlphaBeta(time, player, -(Double.MAX_VALUE-100), Double.MAX_VALUE, true, depth);
				//System.out.println("Stop");
				printAction(result.getAction());
			}
		} else {
			int countB = 0;
			int countW=0;
			int count = 0;
          while(true) {
			depth = Grid.DEPTH_GAME_MAX;
			Action action=allPiecesAreOut();
			if(action == null) {
			//Thread.sleep(10000);
			MinMaxValues result = minimaxAlphaBeta(time, player, -(Double.MAX_VALUE-100), Double.MAX_VALUE, true, depth);
			 action = result.getAction();
			}
			 printAction(action);
			  int col = action.from.col;
			  int row = action.from.row;
			  int colTo = action.to.col;
			  int rowTo = action.to.row;
			  halma[col][row].pawn = "E";
			  halma[colTo][rowTo].pawn = player;
			  if (player.equals("W")) {
					player = "B";
					playerMax = "B";
					System.out.println(++countW);
				} else {
					player = "W";
					playerMax = "W";
					System.out.println(++countB);
				}
			  System.out.println("---------------------------------------");
			  printHalma();
			  System.out.println(++count);
			  System.out.println("---------------------------------------");
			  //Thread.sleep(1000);
			}
		}
		//}

	}

	private static void printHalma() {
		for(int i=0;i<16;i++) {
			for(int j=0;j<16;j++) {
				if(halma[j][i].pawn.equals("E")) {
					System.out.print("."+" ");
					continue;
				}
				System.out.print(halma[j][i].pawn+" ");
			}
			System.out.println();
		}
		
	}

	private static void printAction(Action action) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		String ans = "";
		if (action.jumpSequence != null) {
			for (int i = 0; i < action.jumpSequence.size() - 1; i++) {
				ans += "J " + action.jumpSequence.get(i).col + "," + action.jumpSequence.get(i).row + " "
						+ action.jumpSequence.get(i + 1).col + "," + action.jumpSequence.get(i + 1).row;
				ans += "\n";
			}
		} else {
			ans = "E " + action.getFrom().col + "," + action.getFrom().row + " " + action.getTo().col + ","
					+ action.getTo().row;
			ans += "\n";
		}
		writer.write(ans.substring(0, ans.lastIndexOf("\n")));
		writer.close();
	}

	private static Action allPiecesAreOut() {
		int[][] blackCamp = { { 2, 2 }, { 0, 0 }, { 1, 3 }, { 1, 1 }, { 0, 3 }, { 0, 1 }, { 0, 2 }, { 1, 2 }, { 3, 2 },
				{ 1, 0 }, { 4, 1 }, { 2, 1 }, { 4, 0 }, { 2, 0 }, { 3, 0 }, { 3, 1 }, { 1, 4 }, { 0, 4 }, { 2, 3 } };
		int[][] blackDest = { { 3, 3 }, { 4, 4 }, { 2, 4 }, { 3, 5 }, { 4, 5 }, { 2, 5 }, { 4, 2 }, { 5, 6 }, { 4, 3 },
				{ 5, 4 }, { 5, 2 }, { 6, 3 }, { 5, 1 }, { 6, 4 }, { 3, 6 }, { 7, 5 }, { 1, 5 }, { 7, 6 }, { 3, 4 } };
		int[][] whiteCamp = { { 13, 13 }, { 15, 15 }, { 14, 12 }, { 14, 14 }, { 15, 12 }, { 15, 14 }, { 15, 13 },
				{ 14, 13 }, { 12, 13 }, { 14, 15 }, { 11, 14 }, { 13, 14 }, { 11, 15 }, { 13, 15 }, { 12, 15 },
				{ 12, 14 }, { 14, 11 }, { 15, 11 }, { 13, 12 } };
		int[][] whiteDest = { { 12, 12 }, { 11, 11 }, { 13, 11 }, { 12, 10 }, { 11, 10 }, { 13, 10 }, { 11, 13 },
				{ 10, 9 }, { 11, 12 }, { 10, 11 }, { 10, 13 }, { 9, 12 }, { 10, 14 }, { 9, 11 }, { 12, 9 }, { 8, 10 },
				{ 14, 10 }, { 9, 9 }, { 12, 11 } };
		int[][] list = null;
		int[][] dest = null;
		if (playerMax.equals("W")) {
			list = whiteCamp;
			dest = whiteDest;
		} else {
			list = blackCamp;
			dest = blackDest;
		}
		Action action = null;
		for (int i = 0; i < list.length; i++) {
			if (halma[list[i][0]][list[i][1]].region.equals(playerMax)
					&& halma[list[i][0]][list[i][1]].pawn.equals(playerMax)) {
				List<Move> actions = getActionsFromPlayer(playerMax);
				for (Move move : actions) {
					Grid next = null;
					if (move.getFrom().col == list[i][0] && move.getFrom().row == list[i][1]
							&& move.to.contains(halma[dest[i][0]][dest[i][1]])) {
						next = halma[dest[i][0]][dest[i][1]];
						action = new Action();
						action.setFrom(move.getFrom());
						action.setTo(next);
						if (move.getJumpSequence() != null) {
							action.setJumpSequence(move.getJumpSequence().get(next));
						}else {
							action.setJumpSequence(null);
						}
						return action;
					}
					for (Grid next_an : move.to) {
						if(playerMax.equals("W")) {
							if(!(move.getFrom().col - next_an.col >=0 && move.getFrom().row - next_an.row>=0)) {
								continue;
							}
						}else {
							if(!(next_an.col-move.getFrom().col >=0 && next_an.row-move.getFrom().row>=0)) {
								continue;
							}
						}
						if (move.getFrom().col == list[i][0] && move.getFrom().row == list[i][1]) {
							action = new Action();
							action.setFrom(move.getFrom());
							action.setTo(next_an);
							if (move.getJumpSequence() != null) {
								action.setJumpSequence(move.getJumpSequence().get(next_an));
							}else {
								action.setJumpSequence(null);
							}
							return action;
						}
					}
				}
			}
		}
		return action;
	}

	private static MinMaxValues minimaxAlphaBeta(Double time, String player, double alpha, double beta,
			boolean maxDecision, int depth) {
		List<Move> actions = null;
		MinMaxValues value = new MinMaxValues(null, null);
		Double bestVal = 0.0;
		if (depth == 0) {
			double valueUt = utilityCalculate(player);
			value.setValue(valueUt);
			return value;
		}
		if (maxDecision) {
			player=playerMax;
			bestVal = -(Double.MAX_VALUE-100);
			actions = getActionsFromPlayer(player);
		} else {
			if (player.equals("W")) {
				player = "B";
			} else {
				player = "W";
			}
			bestVal = Double.MAX_VALUE;
			actions = getActionsFromPlayer(player);
		}
		Action action = new Action();
		for (Move move : actions) {
			for (Grid next : move.getTo()) {
				next.pawn = move.getFrom().pawn;
				String temp = move.getFrom().pawn;
				move.getFrom().pawn = "E";
				value = minimaxAlphaBeta(time, player, alpha, beta, !maxDecision, depth - 1);
				if (!maxDecision && value.getValue() < bestVal) {
					action.setFrom(move.getFrom());
					action.setTo(next);
					if (move.getJumpSequence() != null) {
						action.setJumpSequence(move.getJumpSequence().get(next));
					}
					else {
						action.setJumpSequence(null);
					}
					bestVal = value.getValue();
					beta = Math.min(beta, value.getValue());
				}
				if (maxDecision && value.getValue() > bestVal) {
					action.setFrom(move.getFrom());
					action.setTo(next);
					if (move.getJumpSequence() != null) {
						action.setJumpSequence(move.getJumpSequence().get(next));
					}
					else {
						action.setJumpSequence(null);
					}
					bestVal = value.getValue();
					alpha = Math.max(alpha, value.getValue());
				}
				next.pawn = "E";
				move.getFrom().pawn = temp;
				if (alpha >= beta) {
					value.setAction(action);
					value.setValue(bestVal);
					pruning++;
					return value;
				}

			}
		}
		value.setAction(action);
		value.setValue(bestVal);
		return value;
	}

	private static boolean terminatingCondition() {
		int[][] blackCamp = { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 }, { 3, 1 },
				{ 4, 1 }, { 5, 1 }, { 0, 2 }, { 1, 2 }, { 2, 2 }, { 3, 2 }, { 4, 2 }, { 0, 3 }, { 1, 3 }, { 2, 3 },
				{ 0, 4 } };
		int[][] whiteCamp = { { 11, 15 }, { 12, 15 }, { 13, 15 }, { 14, 15 }, { 15, 15 }, { 11, 14 }, { 12, 14 },
				{ 13, 14 }, { 14, 14 }, { 15, 14 }, { 12, 13 }, { 13, 13 }, { 14, 13 }, { 15, 13 }, { 13, 12 },
				{ 14, 12 }, { 15, 12 }, { 14, 11 }, { 15, 11 }, { 15, 10 } };
		int count = 0;
		for(int[]x : blackCamp) {
			if (!halma[x[0]][x[1]].pawn.equals("W")) {
				break;
			}else {
				count++;
			}
		}
		if(count == 19) {
			return true;
		}
		count=0;
		for(int[]x : whiteCamp) {
			if (!halma[x[0]][x[1]].pawn.equals("B")) {
				break;
			}else {
				count++;
			}
		}
		if(count == 19) {
			return true;
		}
		return false;
	}

	private static Double utilityCalculate(String player) {
//		int[][] blackCamp = { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 }, { 3, 1 },
//				{ 4, 1 }, { 5, 1 }, { 0, 2 }, { 1, 2 }, { 2, 2 }, { 3, 2 }, { 4, 2 }, { 0, 3 }, { 1, 3 }, { 2, 3 },
//				{ 0, 4 } };
//		int[][] whiteCamp = { { 11, 15 }, { 12, 15 }, { 13, 15 }, { 14, 15 }, { 15, 15 }, { 11, 14 }, { 12, 14 },
//				{ 13, 14 }, { 14, 14 }, { 15, 14 }, { 12, 13 }, { 13, 13 }, { 14, 13 }, { 15, 13 }, { 13, 12 },
//				{ 14, 12 }, { 15, 12 }, { 14, 11 }, { 15, 11 }, { 15, 10 } };
		
		double valueBlack = 0.0;
		double valueWhite = 0.0;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (halma[j][i].pawn.equals("W")) {
						valueWhite += (2*Math.hypot(j, i)+1.5*Math.hypot(j, i-4)+1.6*Math.hypot(4-j, i));
				}
				if (halma[j][i].pawn.equals("B")) {
					valueBlack += (2*Math.hypot(15-j, 15-i)+1.5*Math.hypot(15-j, 11-i)+1.6*Math.hypot(11-j, 15-i));
				}
			}	
		}
		if (playerMax.equals("W")) {
			return (-valueWhite);
		} else {
			return (-valueBlack);
		}	
	}

	private static List<Move> getActionsFromPlayer(String player) {
		List<Move> actions = new ArrayList<Move>();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				Grid grid = halma[i][j];
				Move move = null;
				if (grid.getPawn().equals(player)) {
					move = new Move();
					move.setFrom(grid);
					getMovesAtGrid(move, grid, false, player);
				}
				if (move != null) {
					actions.add(move);
				}
			}
		}
		return actions;
	}

	private static void getMovesAtGrid(Move move, Grid grid, boolean jumpB, String player) {
		List<String> destination = new ArrayList<String>();
		destination.add(Grid.BLACK);
		destination.add(Grid.WHITE);
		destination.add(Grid.EMPTY);
		if (move.to == null) {
			move.setTo(new ArrayList<Grid>());
		}
		if (move.jumps == null) {
			move.setJumps(new ArrayList<Grid>());
		}
		if (!Grid.EMPTY.equals(grid.region) && !grid.region.equals(player)) { // should not move from opposite camp to
			// empty region if reached
			destination.remove(Grid.EMPTY);
		}
		int currRow = grid.getRow();
		int currCol = grid.getCol();
		int nextRow = 0, nextCol = 0;
		
		if (!grid.region.equals(player)) { // should not move it to it's own camp
			destination.remove(player);
		}
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				nextRow = currRow + i;
				nextCol = currCol + j;
				if (nextRow < 0 || nextCol < 0)
					continue;
				if (nextRow > 15 || nextCol > 15)
					continue;
				if (nextRow == currRow && nextCol == currCol)
					continue;
				Grid nextGrid = halma[nextCol][nextRow];
				if (!destination.contains(nextGrid.region)) {
					continue;
				}
				if (nextGrid.pawn.equals("E")) {
					if (!jumpB) {
						move.getTo().add(nextGrid);
					}
					continue;
				}
				nextRow = nextRow + i;
				nextCol = nextCol + j;
				if (nextRow < 0 || nextCol < 0) {
					continue;
				}
				if (nextRow > 15 || nextCol > 15) {
					continue;
				}
				nextGrid = halma[nextCol][nextRow];
				if (!destination.contains(nextGrid.region) || move.getTo().contains(nextGrid)) {
					continue;
				}
				if (nextGrid.pawn.equals("E")) {
					move.getJumps().add(nextGrid);
					move.getTo().add(0, nextGrid);
					getMovesAtGrid(move, nextGrid, true, player);
					if (move.getJumpSequence() == null) {
						move.setJumpSequence(new HashMap<Grid, List<Grid>>());
					}
					List<Grid> jumpCopy = new ArrayList<Grid>(move.getJumps());
					if (jumpCopy.size() > 0) {
						jumpCopy.add(0, move.getFrom());
						while (jumpCopy.size() > 1) {
							move.getJumpSequence().put(jumpCopy.get(jumpCopy.size() - 1),
									new ArrayList<Grid>(jumpCopy));
							jumpCopy.remove(jumpCopy.size() - 1);
						}
					}
					move.getJumps().remove(move.getJumps().size() - 1);
				}

			}

		}
	}

}
