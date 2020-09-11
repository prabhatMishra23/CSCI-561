
public class Grid {
	public final static String WHITE= "W";
	public final static String BLACK = "B";
	public final static String EMPTY = "E";
	public final static int DEPTH_SINGLE_MAX = 1;
	public final static int DEPTH_GAME_MAX = 2;
	String pawn;
	int row;
	int col;
	String region = Grid.EMPTY;

	
	public Grid(String pawn, int row, int col) {
		super();
		this.pawn = pawn;
		this.row = row;
		this.col = col;
	}

	public String getPawn() {
		return pawn;
	}

	public void setPawn(String pawn) {
		this.pawn = pawn;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

}
