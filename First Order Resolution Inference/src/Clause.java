import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Clause {
	Set<Literal> sentence;
	int index1;
	int index2;
	
	public Clause() {
		this.sentence = new HashSet<Literal>();
	}

	

	public Set<Literal> getSentence() {
		return sentence;
	}



	public void setSentence(Set<Literal> sentence) {
		this.sentence = sentence;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sentence == null) ? 0 : sentence.hashCode());
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
		Clause other = (Clause) obj;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if (!sentence.equals(other.sentence))
			return false;
		return true;
	}

	



	@Override
	public String toString() {
		return "Clause [sentence=" + sentence + ", index1=" + index1 + ", index2=" + index2 + "]";
	}



	public int getIndex1() {
		return index1;
	}



	public void setIndex1(int index1) {
		this.index1 = index1;
	}



	public int getIndex2() {
		return index2;
	}



	public void setIndex2(int index2) {
		this.index2 = index2;
	}

}
