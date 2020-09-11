import java.util.List;

public class Action {
 Grid from;
 Grid to;
 List<Grid> jumpSequence;

 public Grid getFrom() {
	return from;
}
public void setFrom(Grid from) {
	this.from = from;
}
public Grid getTo() {
	return to;
}
public void setTo(Grid to) {
	this.to = to;
}
public List<Grid> getJumpSequence() {
	return jumpSequence;
}
public void setJumpSequence(List<Grid> jumpSequence) {
	this.jumpSequence = jumpSequence;
}
 
 
}
