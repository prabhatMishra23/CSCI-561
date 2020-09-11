import java.util.Arrays;

public class Literal implements Cloneable{
	String name;
	String[] args;
	boolean negated;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
	public boolean isNegated() {
		return negated;
	}
	public void setNegated(boolean negated) {
		this.negated = negated;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (negated ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Literal other = (Literal) obj;
		if (!Arrays.equals(args, other.args))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (negated != other.negated)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "[name=" + name + ", args=" + Arrays.toString(args) + ", negated=" + negated + "]"+"\n";
	}


}
