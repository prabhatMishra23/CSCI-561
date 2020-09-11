import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KB {

	Map<String, List<Clause>> kbMap;
	List<Clause> resolvedClauses;
	List<Clause> kbList;

	public KB() {
		this.kbMap = new HashMap<String, List<Clause>>();
		this.resolvedClauses = new ArrayList<Clause>();
		this.kbList = new ArrayList<Clause>();
	}

	public void tell(KB kb, Clause cl) {
		Map<String, List<Clause>> kbMap = kb.getKbMap();
		List<Clause> resolvedClauses = kb.getResolvedClauses();
		List<Clause> kbList = kb.getKbList();
		boolean resolved = true;
		for (Literal lit : cl.getSentence()) {
			String key = "";
			if (lit.negated) {
				key = "~" + lit.getName();
			} else {
				key = lit.getName();
			}
			if (!checkResolvedArgs(lit.args)) {
				resolved = false;
			}
			// standardiseVar(lit,kbList.size()+1);
			if (kbMap.get(key) == null) {
				kbMap.put(key, new ArrayList<Clause>());
				kbMap.get(key).add(cl);
			} else {
				if (!kbMap.get(key).contains(cl)) {
					kbMap.get(key).add(cl);
				}
			}
		}
		if (resolved) {
			resolvedClauses.add(cl);
		}
		kbList.add(cl);
	}

	private boolean checkResolvedArgs(String[] args) {
		boolean flag = true;
		for (String x : args) {
			if (!Character.isUpperCase(x.charAt(0))) {
				return false;
			}
		}
		return flag;
	}

	public boolean ask(Literal query, KB kb) {
		Set<Literal> queries = new HashSet<Literal>();
		queries.add(query);
		return resolution(queries, kb);

	}

	private boolean resolution(Set<Literal> queries, KB kb2) {
		Set<Clause> newClauses = new LinkedHashSet<Clause>();
		Map<String, Boolean> resolvedMap = new HashMap<String, Boolean>();
		Set<Clause> resolvents = null;
		Clause clause = new Clause();
		clause.setSentence(queries);
		List<Clause> kbList2 = kb2.getKbList();
		kb2.tell(kb2, clause);
		while (true) {
			for (int i = 0; i < kbList2.size(); i++) {
				for (int j = i + 1; j < kbList2.size(); j++) {
					if (canResolve(kbList2.get(i), kbList2.get(j), kb2)) {
						if (resolvedMap.containsKey(i + "#" + j)) {
							continue;
						}
						if(kbList2.get(i).sentence.size() == 1 && kbList2.get(j).sentence.size() ==1) {
							for(Literal l1 :  kbList2.get(i).getSentence()) {
								for(Literal l2 : kbList2.get(j).getSentence()) {
									if (l1.getName().equals(l2.getName()) && !(l1.negated == l2.negated)
											&& l1.getArgs().length == l2.getArgs().length) {
										Map<String, String> subst = new HashMap<String, String>();
										subst = homework.unify(l1.getArgs(), l2.getArgs(), subst);
										if(subst!=null) {
											return true;
										}
									   }
									}
								}
						}
						if (!(allGroundTerms(kbList2.get(i).getSentence())
								|| allGroundTerms(kbList2.get(j).getSentence()))) {
							continue;
						}
						resolvents = resolveQuerySent(kbList2.get(i).getSentence(), kbList2.get(j).getSentence(), i, j);
						if (resolvents.contains(null)) {
							return true;
						}
						checkTautology(resolvents);
						if (resolvents.size() == 0) {
							continue;
						}
						resolvedMap.put(i + "#" + j, true);
						Set<Clause> union = new LinkedHashSet<Clause>(newClauses);
						union.addAll(resolvents);
						newClauses = union;
					}
				}
			}
			for (Clause x : newClauses) {
				System.out.println(x.toString());
				}
			if (isSubset1(newClauses, kb2)) {
				return false;
			}
			for (Clause cl : newClauses) {
				kb2.tell(kb2, cl);
			}
			if(kb2.getKbList().size()>1000) {
				return false;
			}
		}

	}


	private void checkTautology(Set<Clause> resolvents) {
		Map<String, Literal> map = new HashMap<String, Literal>();
		Iterator<Clause> resolveIter = resolvents.iterator();
		// boolean breakFlag = false;
		while (resolveIter.hasNext()) {
			Clause cl = resolveIter.next();
			if (cl.getSentence().size() > 1) {
				for (Literal l1 : cl.getSentence()) {
					if (map.containsKey(l1.getName())) {
						Literal l2 = map.get(l1.getName());
						if (!l1.negated == l2.negated) {
							int i = 0;
							for (; i < l1.args.length; i++) {
								if (l1.args[i].equals(l2.args[i])) {
									continue;
								} else {
									break;
								}
							}
							if (l1.args.length == i) {
								resolveIter.remove();
								break;
//								if(resolvents.size()==0) {
//									return;
//								}
//								cl = resolveIter.next();
							}
						}

					} else {
						map.put(l1.getName(), l1);
					}
				}
			}
		}

	}

	private boolean allGroundTerms(Set<Literal> sentence) {
		for (Literal l : sentence) {
			int count = 0;
			for (int i = 0; i < l.args.length; i++) {
				String arg = l.args[i];
				while (i < l.args.length && Character.isUpperCase(arg.charAt(0))) {
					count++;
					i++;
					if (i < l.args.length)
						arg = l.args[i];
					continue;
				}
				if (count == l.args.length) {
					return true;
				}
			}
		}
		return false;

	}

	private boolean isSubset1(Set<Clause> newClauses, KB kb2) {
		Set<Clause> intersection = new LinkedHashSet<Clause>(newClauses);
		intersection.retainAll(kb2.getKbList());
		if (intersection.size() == newClauses.size()) {
			return true;
		}
		return false;
	}

	public List<Clause> getResolvedClauses() {
		return resolvedClauses;
	}

	public void setResolvedClauses(List<Clause> resolvedClauses) {
		this.resolvedClauses = resolvedClauses;
	}

	public List<Clause> getKbList() {
		return kbList;
	}

	public void setKbList(List<Clause> kbList) {
		this.kbList = kbList;
	}

	private boolean canResolve(Clause clause, Clause clause2, KB kb2) {
		String queryToFetch = "";
		Map<String, List<Clause>> kbMap2 = kb2.getKbMap();
		for (Literal query : clause.getSentence()) {
			if (query.isNegated()) { // i need to search positive
				queryToFetch = query.getName();
			} else {
				queryToFetch = "~" + query.getName();
			}
			if (!kbMap2.containsKey(queryToFetch)) {
				continue;
			} else {
				if (kbMap2.get(queryToFetch).contains(clause2)) {
					return true;
				}
			}
		}
		return false;
	}

	private Set<Clause> resolveQuerySent(Set<Literal> newSentence, Set<Literal> other, int i, int j) {
		Set<Clause> resolvents = new LinkedHashSet<Clause>();
		Set<Literal> resolvent = new LinkedHashSet<Literal>();
		Set<Literal> resolventCopy = null;
		Iterator<Literal> newSentIter = newSentence.iterator();
		boolean breakFlag = false;
		while (newSentIter.hasNext()) {
			Iterator<Literal> otherSentenceIter = other.iterator();
			Literal l = newSentIter.next();
			while (otherSentenceIter.hasNext()) {
				Literal query = otherSentenceIter.next();
				if (l.name.equals(query.name) && !(l.negated == query.negated)
						&& query.getArgs().length == l.getArgs().length) {
					Map<String, String> subst = new HashMap<String, String>();
					if (equalArgs(query.getArgs(), l.getArgs())) {
						Set<Literal> newSentenceCopy = cloneList(newSentence);
						Set<Literal> otherListCopy = cloneList(other);
						newSentenceCopy.remove(l);
						otherListCopy.remove(query);
						resolvent = merge(newSentenceCopy, otherListCopy, l, query);
						resolventCopy = new HashSet<Literal>(resolvent);
						if (resolvent.size() == 0) {
							resolvents.add(null);
							breakFlag = true;
							break;
						}
					} else {
						subst = homework.unify(query.getArgs(), l.getArgs(), subst);
						//boolean breakSubst = false;
						if (subst != null) {
//							for(Entry<String,String> val : subst.entrySet()) {
//								if(Character.isLowerCase(val.getKey().charAt(0)) && Character.isLowerCase(val.getValue().charAt(0))) {
//									breakSubst = true;
//									break;
//								}
//							}
//							if(breakSubst) {
//								continue;
//							}
							Set<Literal> newSentenceCopy = cloneList(newSentence);
							Set<Literal> otherListCopy = cloneList(other);
							newSentenceCopy.remove(l);
							otherListCopy.remove(query);
							applySubstitution(newSentenceCopy, subst);
							applySubstitution(otherListCopy, subst);
							resolvent = merge(newSentenceCopy, otherListCopy, l, query);
							resolventCopy = new HashSet<Literal>(resolvent);
							if (resolvent.size() == 0) {
								resolvents.add(null);
								breakFlag = true;
							}
						} else {
							continue;
						}
					}
					Clause clause = new Clause();
					clause.setSentence(resolventCopy);
					clause.setIndex1(i);
					clause.setIndex2(j);
					resolvents.add(clause);
				}
			}
			if (breakFlag) {
				break;
			}
		}
		return resolvents;
	}

	private Set<Literal> cloneList(Set<Literal> sentence) {
		Set<Literal> copySentence = new LinkedHashSet<Literal>();
		for (Literal x : sentence) {
			Literal l = new Literal();
			l.setName(x.getName());
			l.setArgs(x.getArgs().clone());
			l.setNegated(x.isNegated());
			copySentence.add(l);
		}
		return copySentence;
	}

	private boolean equalArgs(String[] args, String[] args2) {
		if (!Character.isUpperCase(args[0].charAt(0)) || !Character.isUpperCase(args2[0].charAt(0))) {
			return false;
		}
		for (int i = 0; i < args.length; i++) {
			if (!args[i].equals(args2[i])) {
				return false;
			}
		}
		return true;
	}

	private Set<Literal> merge(Set<Literal> newSentenceCopy, Set<Literal> otherListCopy, Literal l, Literal query) {
		if (otherListCopy.isEmpty()) {
			return newSentenceCopy;
		} else if (newSentenceCopy.isEmpty()) {
			return otherListCopy;
		} else {
			newSentenceCopy.addAll(otherListCopy);
		}
		return newSentenceCopy;
	}

	private void applySubstitution(Set<Literal> newSentence, Map<String, String> subst) {
		for (Literal l : newSentence) {
			for (int i = 0; i < l.args.length; i++) {
				if (subst.containsKey(l.args[i])) {
					l.args[i] = subst.get(l.args[i]);
				}
			}
		}

	}

	public void ask(Clause query) {
		resolve(kbMap, query);
	}

	private void resolve(Map<String, List<Clause>> kb, Clause query) {

	}

	public Map<String, List<Clause>> getKbMap() {
		return kbMap;
	}

	public void setKbMap(Map<String, List<Clause>> kbMap) {
		this.kbMap = kbMap;
	}

}
