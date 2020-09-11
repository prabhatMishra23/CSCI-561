import java.util.List;
import java.util.Map;

public class Move {
	Grid from;
	List<Grid> to;
	List<Grid> jumps;
	Map<Grid,List<Grid>>jumpSequence;

	public Grid getFrom() {
		return from;
	}

	public void setFrom(Grid from) {
		this.from = from;
	}

	public List<Grid> getTo() {
		return to;
	}

	public void setTo(List<Grid> to) {
		this.to = to;
	}

	public List<Grid> getJumps() {
		return jumps;
	}

	public void setJumps(List<Grid> jumps) {
		this.jumps = jumps;
	}

	public Map<Grid, List<Grid>> getJumpSequence() {
		return jumpSequence;
	}

	public void setJumpSequence(Map<Grid, List<Grid>> jumpSequence) {
		this.jumpSequence = jumpSequence;
	}

	
	
	

}
