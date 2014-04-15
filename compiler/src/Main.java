import java.io.IOException;


public class Main {

	public static void main(String args[]){
			
		//read from file: retrieve file from stream into char array
		FileIO f = new FileIO();
		char[] fileContent = null;
		try {
			fileContent = f.readFileToCharArray();
		} catch (IOException e) {
			System.out.println("File path not found. Terminating program.");
			System.exit(0);
		}
			
		//call lexical analyzer
		Lexer lex = new Lexer(fileContent);
		
		//call parser
		Parser par = new Parser(lex);
		
		//write to file
		f.write(lex.getTokens(), lex.getErrorMessages(), par.getParsedContent(),
				par.getParserErrors(), par.getTableInfo(), par.getTableErrors());

			
	}
}
