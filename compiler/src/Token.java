
public class Token {
	private String type;
	private String lexeme;
	private int count;
	private int length;
	private int line;
	
	public Token(String type, String lexeme, int count, int length){
		this.type = type;
		this.lexeme = lexeme;
		this.count = count;
		this.length = length;
	}
	
	public String getType(){
		return type;
	}
	
	public String getLexeme(){
		return lexeme;
	}
	
	public int getCount(){
		return count;
	}
	
	public int getLength(){
		return length;
	}
	
	public boolean inFirst(NonTerminal t){
		if (t.isInFirst(type))
			return true;
		else
			return false;
	}
	
	public boolean inFollow(NonTerminal t){
		if (t.isInFollow(type))
			return true;
		else
			return false;
	}
	
	public boolean inRHS1(NonTerminal t){
		if (t.isInRHS1(type))
			return true;
		else
			return false;
	}
	
	public boolean inRHS2(NonTerminal t){
		if (t.isInRHS2(type))
			return true;
		else
			return false;
	}
	
	public boolean inRHS3(NonTerminal t){
		if (t.isInRHS3(type))
			return true;
		else
			return false;
	}
	
	public boolean inRHS4(NonTerminal t){
		if (t.isInRHS4(type))
			return true;
		else
			return false;
	}
	
	public boolean inRHS5(NonTerminal t){
		if (t.isInRHS5(type))
			return true;
		else
			return false;
	}
	
	public boolean inRHS6(NonTerminal t){
		if (t.isInRHS6(type))
			return true;
		else
			return false;
	}
	
	public boolean inUnion(NonTerminal t){
		if (t.isInFirst(type) || t.isInFollow(type))
			return true;
		else
			return false;
	}
	
	public boolean equals(String s){
		if (type.equals(s))
			return true;
		else
			return false;
	}
	
	public String toString(){
		String ret = "Token type: " + type + ", lexeme: " + lexeme + ", count: " + count +", length: " + length + "\r\n";
		return ret;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}
}
