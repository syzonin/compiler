import java.util.ArrayList;

public class NonTerminal {
	private String symbol;
	private ArrayList<String> firstSet;
	private ArrayList<String> followSet;
	
	public NonTerminal(String s){
		symbol = s;
		firstSet = new ArrayList<String>();
		followSet = new ArrayList<String>();
		firstSet.clear();
		followSet.clear();
	}
	
	public void addFirst(String s){
		firstSet.add(s);
	}
	
	public void addFollow(String s){
		followSet.add(s);
	}
	
	public String getSymbol() {
		return symbol;
	}

	public ArrayList<String> getFirstSet() {
		return firstSet;
	}

	public ArrayList<String> getFollowSet() {
		return followSet;
	}
	
	//Tests if string is in first set of nonterminal
	public boolean isInFirst(String s){
		for (int i = 0; i < firstSet.size(); i++){
			if (firstSet.get(i).equals(s)){
				return true;
			}
		}
		return false;
	}
	
	//Tests if string is in follow set of nonterminal
	public boolean isInFollow(String s){
		for (int i = 0; i < followSet.size(); i++){
			if (followSet.get(i).equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		String first="";
		String follow="";
		for (int i = 0; i < firstSet.size(); i++){
			first += " " + firstSet.get(i);
		} 
		for (int i = 0; i < followSet.size(); i++){
			follow += " " + followSet.get(i);
		} 
		String s = "Symbol: " + symbol + " , first set: " + first + " , follow set: " + follow; 
		return s;
	}
}
