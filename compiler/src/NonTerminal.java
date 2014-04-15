public class NonTerminal {
	private String symbol;
	private String[] firstSet;
	private String[] followSet;
	private String[] RHS1;
	private String[] RHS2;
	private String[] RHS3;
	private String[] RHS4;
	private String[] RHS5;
	private String[] RHS6;
	private String type;
	
	public NonTerminal(String s, String[] first, String[] follow, 
			String[] r1, String[] r2, String[] r3, String[] r4, String[] r5, String[] r6){
		symbol = s;
		firstSet = first;
		followSet = follow;
		RHS1 = r1;
		RHS2 = r2;
		RHS3 = r3;
		RHS4 = r4;
		RHS5 = r5;
		RHS6 = r6;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public String[] getFirstSet() {
		return firstSet;
	}

	public String[] getFollowSet() {
		return followSet;
	}
	
	//Tests if string is in first set of nonterminal
	public boolean isInFirst(String s){
		for (int i = 0; i < firstSet.length; i++){
			if (firstSet[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	//Tests if string is in follow set of nonterminal
	public boolean isInFollow(String s){
		for (int i = 0; i < followSet.length; i++){
			if (followSet[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInRHS1(String s){
		for (int i = 0; i < RHS1.length; i++){
			if (RHS1[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInRHS2(String s){
		for (int i = 0; i < RHS2.length; i++){
			if (RHS2[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInRHS3(String s){
		for (int i = 0; i < RHS3.length; i++){
			if (RHS3[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInRHS4(String s){
		for (int i = 0; i < RHS4.length; i++){
			if (RHS4[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInRHS5(String s){
		for (int i = 0; i < RHS5.length; i++){
			if (RHS5[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInRHS6(String s){
		for (int i = 0; i < RHS6.length; i++){
			if (RHS6[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	public String toString(){
		String first="";
		String follow="";
		for (int i = 0; i < firstSet.length; i++){
			first += " " + firstSet[i];
		} 
		for (int i = 0; i < followSet.length; i++){
			follow += " " + followSet[i];
		} 
		String s = "Symbol: " + symbol + " , first set: " + first + " , follow set: " + follow; 
		return s;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
