import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class homework {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("input.txt"), StandardCharsets.UTF_8);
		int numberOfQueries = Integer.parseInt(lines.get(0));
		List<Literal> queries = new ArrayList<Literal>();
		Queue<KB> kbList = new LinkedList<KB>();
		int i = 0;
		for (; i < numberOfQueries; i++) {
			queries.add(parseQuery(lines.get(1 + i)));
			kbList.add(new KB());
		}
		int numberOfSentences = Integer.parseInt(lines.get(i + 1));
		i++;
		for (int p = 0; p < numberOfQueries; p++) {
			KB kb = kbList.remove();
			for (int j = 0; j < numberOfSentences; j++) {
				Clause cl = parseSentence(lines.get(j + i + 1).replaceAll("\\s+", ""), j + 1);
				kb.tell(kb, cl);
			}
			kbList.add(kb);
		}
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < queries.size(); j++) {
			KB kb = kbList.remove();
			if (kb.ask(queries.get(j), kb)) {
				sb.append("TRUE\n");
			} else {
				sb.append("FALSE\n");
			}
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		writer.write(sb.substring(0, sb.lastIndexOf("\n")));
		writer.close();
	}

	private static Clause parseSentence(String sentence, int i) {
		String[] division = sentence.split("=>");
		String premise = division[0];
		String consequent = "";
		if (division.length > 1)
			consequent = division[1];
		String[] premPredicates = premise.split("&");
		Literal literal = null;
		Clause clause = new Clause();
		for (String predicate : premPredicates) {
			// make all values of clauses negating them
			if (sentence.contains("=>")) {
				if (predicate.startsWith("~")) {
					literal = new Literal();
					String predicateName = predicate.substring(1, predicate.indexOf("("));
					String[] args = predicate.substring(predicate.indexOf("(") + 1, predicate.indexOf(")")).split(",");
					for (int p = 0; p < args.length; p++) {
						if (Character.isLowerCase(args[p].charAt(0))) {
							args[p] += i;
						}
					}
					literal.setName(predicateName);
					literal.setArgs(args);
					literal.setNegated(false);
				} else {
					literal = new Literal();
					String predicateName = predicate.substring(0, predicate.indexOf("("));
					String[] args = predicate.substring(predicate.indexOf("(") + 1, predicate.indexOf(")")).split(",");
					for (int p = 0; p < args.length; p++) {
						if (Character.isLowerCase(args[p].charAt(0))) {
							args[p] += i;
						}
					}
					literal.setName(predicateName);
					literal.setArgs(args);
					literal.setNegated(true);
				}
			} else {
				if (predicate.startsWith("~")) {
					literal = new Literal();
					String predicateName = predicate.substring(1, predicate.indexOf("("));
					String[] args = predicate.substring(predicate.indexOf("(") + 1, predicate.indexOf(")")).split(",");
					for (int p = 0; p < args.length; p++) {
						if (Character.isLowerCase(args[p].charAt(0))) {
							args[p] += i;
						}
					}
					literal.setName(predicateName);
					literal.setArgs(args);
					literal.setNegated(true);
				} else {
					literal = new Literal();
					String predicateName = predicate.substring(0, predicate.indexOf("("));
					String[] args = predicate.substring(predicate.indexOf("(") + 1, predicate.indexOf(")")).split(",");
					for (int p = 0; p < args.length; p++) {
						if (Character.isLowerCase(args[p].charAt(0))) {
							args[p] += i;
						}
					}
					literal.setName(predicateName);
					literal.setArgs(args);
				}
			}
			clause.getSentence().add(literal);
		}
		if (!consequent.equals("")) {
			if (consequent.startsWith("~")) {
				literal = new Literal();
				String predicateName = consequent.substring(1, consequent.indexOf("("));
				String[] args = consequent.substring(consequent.indexOf("(") + 1, consequent.indexOf(")")).split(",");
				for (int p = 0; p < args.length; p++) {
					if (Character.isLowerCase(args[p].charAt(0))) {
						args[p] += i;
					}
				}
				literal.setName(predicateName);
				literal.setArgs(args);
				literal.setNegated(true);
			} else {
				literal = new Literal();
				String predicateName = consequent.substring(0, consequent.indexOf("("));
				String[] args = consequent.substring(consequent.indexOf("(") + 1, consequent.indexOf(")")).split(",");
				for (int p = 0; p < args.length; p++) {
					if (Character.isLowerCase(args[p].charAt(0))) {
						args[p] += i;
					}
				}
				literal.setName(predicateName);
				literal.setArgs(args);
			}
			clause.getSentence().add(literal);
		}
		return clause;

	}

	private static Literal parseQuery(String query) {
		Literal literal = null;
		if (query.startsWith("~")) {
			literal = new Literal();
			String predicateName = query.substring(1, query.indexOf("("));
			String[] args = query.substring(query.indexOf("(") + 1, query.indexOf(")")).split(",");
			literal.setName(predicateName);
			literal.setArgs(args);
			literal.setNegated(false);
		} else {
			literal = new Literal();
			String predicateName = query.substring(0, query.indexOf("("));
			String[] args = query.substring(query.indexOf("(") + 1, query.indexOf(")")).split(",");
			literal.setName(predicateName);
			literal.setArgs(args);
			literal.setNegated(true);
		}
		return literal;

	}

	public static Map<String, String> unify(Object args1, Object args2, Map<String, String> subst) {
		if (subst == null) {
			return null;
		} else if (args1.toString().equals(args2.toString())) {
			return subst;
		} else if (Character.isLowerCase(args1.toString().charAt(0))) {
			return unifyVar(args1.toString(), args2.toString(), subst);
		} else if (Character.isLowerCase(args2.toString().charAt(0))) {
			return unifyVar(args2.toString(), args1.toString(), subst);
		} else if (args1 instanceof String[] && args2 instanceof String[]
				&& ((String[]) args1).length == ((String[]) args2).length) {
			for (int i = 0; i < ((String[]) args1).length; i++) {
				subst = unify(((String[]) args1)[i], ((String[]) args2)[i], subst);
			}
			return subst;
		} else {
			return null;
		}
	}

	private static Map<String, String> unifyVar(String args1, String args2, Map<String, String> subst) {
		if (subst.containsKey(args1)) {
			return unify(subst.get(args1), args2, subst);
		} else if (subst.containsKey(args2)) {
			return unify(args1, subst.get(args2), subst);
		} else {
			subst.put(args1, args2);
		}
		return subst;
	}

}
