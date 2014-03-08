
public class Token {
	private String type;
	private String lexeme;
	private int count;
	private int length;
	
	
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
	
	public String toString(){
		String ret = "Token type: " + type + ", lexeme: " + lexeme + ", count: " + count +", length: " + length + "\r\n";
		return ret;
	}
}
