import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Lex {
	
	//lookup table with height 28, width 25
	static String[][] lookupTable = new String[28][25];
	static char[] fileContent;
	static int currentLocation = 0;
	static String lexeme = "";
	static String state = "1";
	static boolean fileComplete = false;
	static String tokens="";
	static String errorMessages="";
	
	public static void main(String args[]){
		
		//get file from stream into char array
		String filePath = "C:/Users/Si/workspace/lexical_analyzer/test.txt";
		String tokenFilePath = "C:/Users/Si/workspace/lexical_analyzer/tokens.txt";
		String errorFilePath = "C:/Users/Si/workspace/lexical_analyzer/errorMessages.txt";
		try {
			fileContent = ReadFileToCharArray(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		printFileContent();
		System.out.println("number of characters: " + fileContent.length);
		// create lookup table
		createLookupTable();
		
		//call next token until reach end of file
		while(!fileComplete){
			Token t = nextToken();
			if (t != null){
				if (!t.getType().equals("error")){
					System.out.println(t.toString());
					tokens += t.toString();
				}
			}
			else{
				System.out.println("Reached end of file.");
			}
			//if token type is an error, do not write to file
		}
		WriteToFile(tokenFilePath, tokens);
		WriteToFile(errorFilePath, errorMessages);
	}
	
	//returns next Token
	static Token nextToken(){
		
		Token newToken = null;
		boolean errorFound = false;
		while (newToken == null && !fileComplete && !errorFound){
			String nextCharacter = nextChar();
			//System.out.println("next character: " + nextCharacter + ", state: " + state);
			
			String lookup = nextCharacter;
			if (nextCharacter != null){
				lexeme += nextCharacter;
				//System.out.println("current lexeme: " + lexeme);
				int xIndex = convertColumnToXIndex(lookup);
				int yIndex = convertStateToYIndex(state);
				//System.out.println("xIndex: " + xIndex);
				
				if (xIndex == 99){
					yIndex = 12;
					xIndex = 11;
					//handle error of something not under the declared alphabet
					lexeme = lexeme.trim();
					String error = "Error occurred with the following set of characters: " + lexeme 
							+ " at location " + (currentLocation-lexeme.length()) + "\r\n";
					errorMessages += error;
					System.out.println(error);
					System.out.println("Commencing panic mode token recovery...");
					state = "1";
					errorFound = true;
					newToken = new Token("error", lexeme, currentLocation-lexeme.length(), lexeme.length());
					lexeme = "";
				}
				else{
					String oldState = state;
					state = lookupTable[yIndex][xIndex];
					
					//handle state 12
					if (isFinalState(yIndex)){
						if(isBackup(yIndex)){
							currentLocation--;
							currentLocation--;
							lexeme = lexeme.substring(0, lexeme.length()-2);
							lexeme = lexeme.trim();
							newToken = createToken(oldState);
						}
						else{
							currentLocation--;
							lexeme = lexeme.substring(0, lexeme.length()-1);
							lexeme = lexeme.trim();
							newToken = createToken(oldState);
						}
	
					}
				}
			}
			else {
				fileComplete = true;
				return null;
			}
		}
	return newToken;	
	}
	
	 //returns nextChar;
	static String nextChar(){
		if (currentLocation == fileContent.length){
			return null;
		}
		else {
			String c = Character.toString(fileContent[currentLocation]);
			currentLocation++;
			return c;
		}
	}
	
	//converts string to index location on the x-axis
	static int convertColumnToXIndex(String s){
		int i = 99;
		if (s.equals(""))
			i = 0;
		else if (s.matches("[1-9]"))
			i = 1;
		else if (s.equals("0"))
			i = 2;
		else if (s.matches("[a-zA-Z]"))
			i = 3;
		else if (s.equals("."))
			i = 4;
		else if (s.equals("/"))
			i = 5;
		else if (s.equals("*"))
			i = 6;
		else if (s.equals("<"))
			i = 7;
		else if (s.equals(">"))
			i = 8;
		else if (s.equals("="))
			i = 9;
		else if (s.equals("_"))
			i = 10;
		else if (s.equals(";"))
			i = 11;
		else if (s.equals(","))
			i = 12;
		else if (s.equals("+"))
			i = 13;
		else if (s.equals("-"))
			i = 14;
		else if (s.equals("{"))
			i = 15;
		else if (s.equals("}"))
			i = 16;
		else if (s.equals("("))
			i = 17;
		else if (s.equals(")"))
			i = 18;
		else if (s.equals("["))
			i = 19;
		else if (s.equals("]"))
			i = 20;
		else if (s.equals("\r") || s.equals("\n") || s.equals("\r\n") || s.equals(null))
			i = 21;
		else if (s.equals(" "))
			i = 22;
		else if (s.equals("final"))
			i = 23;
		else if (s.equals("backup"))
			i = 24;
		return i;
	}

	//converts string to index location on the y-axis
	static int convertStateToYIndex(String s){
		int i = 0;
		if (!s.equals(""))
			i = Integer.parseInt(s);
		return i;
	}
	
	//checks if state is a final state
	static boolean isFinalState(int yIndex){
		boolean isFinal = false;
		int xIndex = 23;
		// if s is one of the final states, return true
		if (!lookupTable[yIndex][xIndex].equals("no")){
			isFinal = true;
		}
		return isFinal;
	}
	
	//checks if state needs to back up
	static boolean isBackup(int yIndex){
		boolean isBackup = false;
		int xIndex = 24;
		// if s is one of the final states, return true
		if (lookupTable[yIndex][xIndex].equals("yes")){
			isBackup = true;
		}
		return isBackup;
	}
	
	static Token createToken(String s){
		// check final state to see Token type
		Token t;
		if(s.equals("5"))
			t = new Token("num", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("11"))
			if (lexeme.equals("and")){
				t = new Token("and", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("not")){
				t = new Token("not", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("or")){
				t = new Token("or", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("if")){
				t = new Token("if", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("then")){
				t = new Token("then", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("else")){
				t = new Token("else", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("for")){
				t = new Token("for", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("class")){
				t = new Token("class", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("int")){
				t = new Token("int", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("float")){
				t = new Token("float", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("get")){
				t = new Token("get", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("put")){
				t = new Token("put", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("return")){
				t = new Token("return", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else{
				t = new Token("id", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
		else if(s.equals("12")){
			t = new Token("error", lexeme, currentLocation-lexeme.length(), lexeme.length());
			String error = "Error occurred with the following set of characters: " + lexeme 
					+ " at location " + (currentLocation-lexeme.length()) + "\r\n";
			errorMessages += error;
			System.out.println(error);
			System.out.println("Commencing panic mode token recovery...");
		}
		else if(s.equals("16"))
			t = new Token("comment_long", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("18"))
			t = new Token("comment", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("20"))
			t = new Token("relop_ge", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("21"))
			t = new Token("relop_gl", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("23"))
			t = new Token("relop_le", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("25"))
			t = new Token("relop_e", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("26")){
			if (lexeme.equals("/")){
				t = new Token("div", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("<")){
				t = new Token("relop_l", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals(">")){
				t = new Token("relop_g", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("=")){
				t = new Token("assignop", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else {
				t = new Token("error at 26", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
		}
		else if(s.equals("27")){
			if (lexeme.equals(";")){
				t = new Token("semi", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals(",")){
				t = new Token("comma", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals(".")){
				t = new Token("dot", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("+")){
				t = new Token("add", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("-")){
				t = new Token("sub", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("*")){
				t = new Token("mul", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("(")){
				t = new Token("openpar", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals(")")){
				t = new Token("closepar", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("[")){
				t = new Token("opensq", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("]")){
				t = new Token("closesq", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("{")){
				t = new Token("opencur", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("}")){
				t = new Token("closecur", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else {
				t = new Token("error at 27", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
		}
		else
			t = new Token("error out of loop", lexeme, currentLocation-lexeme.length(), lexeme.length());
		
		lexeme = "";
		return t;
	}

	//returns corresponding entry in 2D array
	static String tableLookup(int state, int column){
		String s = lookupTable[state][column];
		System.out.println("tableLookup: " + s);
		return s;
	}
	
	//prints the lookup table
	static void printLookupTable(){
		for(int i = 0; i<lookupTable.length; i++){
			for(int j = 0; j <lookupTable[i].length; j++){
				System.out.print(lookupTable[i][j] + ",");
			}
			System.out.println();
		}
	}
	
	//creates the lookup table
	static void createLookupTable(){

		String tempArray0[] = {"","[1-9]", "0", "[a-zA-Z]", ".", "/", "*", "<", ">", "=", "_", ";", ",", "+", "-", "{", "}", "(", ")", "[", "]", "\n || null", " ", "final", "backup"};
		String tempArray1[] = {"1", "2", "9", "10", "27", "13", "27", "19", "22", "24", "12", "27", "27", "27", "27", "27", "27", "27", "27", "27", "27", "1", "1", "no", "no"};
		String tempArray2[] = {"2", "2", "2", "5", "3", "5", "5", "5", "5", "5", "12", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "no", "no"};
		String tempArray3[] = {"3", "6", "4", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "no", "no"};
		String tempArray4[] = {"4", "6", "7", "5", "5", "5", "5", "5", "5", "5", "12", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "no", "no"};
		String tempArray5[] = {"5", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[num]", "yes"};
		String tempArray6[] = {"6", "6", "8", "5", "5", "5", "5", "5", "5", "5", "12", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "no", "no"};
		String tempArray7[] = {"7", "6", "8", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "no", "no"};
		String tempArray8[] = {"8", "6", "8", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "no", "no"};
		String tempArray9[] = {"9", "5", "5", "5", "3", "5", "5", "5", "5", "5", "12", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "no", "no"};
		String tempArray10[] = {"10", "10", "10", "10", "11", "11", "11", "11", "11", "11", "10", "11", "11", "11", "11", "11", "11", "11", "11", "11", "11", "11", "11", "no", "no"};
		String tempArray11[] = {"11", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[id]", "yes"};
		String tempArray12[] = {"12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[error]", "no"};
		String tempArray13[] = {"13", "26", "26", "26", "26", "17", "14", "26", "26", "26", "12", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "no", "no"};
		String tempArray14[] = {"14", "14", "14", "14", "14", "14", "15", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "no", "no"};
		String tempArray15[] = {"15", "14", "14", "14", "14", "16", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "no", "no"};
		String tempArray16[] = {"16", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[/*..*/]", "no"};
		String tempArray17[] = {"17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "18", "17", "no", "no"};
		String tempArray18[] = {"18", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[//..]", "no"};
		String tempArray19[] = {"19", "26", "26", "26", "26", "26", "26", "26", "21", "20", "12", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "no", "no"};
		String tempArray20[] = {"20", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[relop_ge]", "no"};
		String tempArray21[] = {"21", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[relop_gl]", "no"};
		String tempArray22[] = {"22", "26", "26", "26", "26", "26", "26", "26", "26", "23", "12", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "no", "no"};
		String tempArray23[] = {"23", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[relop_le]", "no"};
		String tempArray24[] = {"24", "26", "26", "26", "26", "26", "26", "26", "26", "25", "12", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "no", "no"};
		String tempArray25[] = {"25", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[relop_e]", "no"};
		String tempArray26[] = {"26", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[various]", "yes"};
		String tempArray27[] = {"27", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[various]", "no"};
		
		for (int i = 0; i< tempArray0.length; i++){
			lookupTable[0][i] = tempArray0[i];
			lookupTable[1][i] = tempArray1[i];
			lookupTable[2][i] = tempArray2[i];
			lookupTable[3][i] = tempArray3[i];
			lookupTable[4][i] = tempArray4[i];
			lookupTable[5][i] = tempArray5[i];
			lookupTable[6][i] = tempArray6[i];
			lookupTable[7][i] = tempArray7[i];
			lookupTable[8][i] = tempArray8[i];
			lookupTable[9][i] = tempArray9[i];
			lookupTable[10][i] = tempArray10[i];
			lookupTable[11][i] = tempArray11[i];
			lookupTable[12][i] = tempArray12[i];
			lookupTable[13][i] = tempArray13[i];
			lookupTable[14][i] = tempArray14[i];
			lookupTable[15][i] = tempArray15[i];
			lookupTable[16][i] = tempArray16[i];
			lookupTable[17][i] = tempArray17[i];
			lookupTable[18][i] = tempArray18[i];
			lookupTable[19][i] = tempArray19[i];
			lookupTable[20][i] = tempArray20[i];
			lookupTable[21][i] = tempArray21[i];
			lookupTable[22][i] = tempArray22[i];
			lookupTable[23][i] = tempArray23[i];
			lookupTable[24][i] = tempArray24[i];
			lookupTable[25][i] = tempArray25[i];
			lookupTable[26][i] = tempArray26[i];
			lookupTable[27][i] = tempArray27[i];
		}
	}
	
	//prints file content
	static void printFileContent(){
		for (int i = 0; i<fileContent.length; i++)
			System.out.print(fileContent[i]);
		System.out.println();
	}
	
	//returns char array of file read
	static char[] ReadFileToCharArray(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		fileData.append("  ");
		reader.close();
		return  fileData.toString().toCharArray();	
	}
	
	static void WriteToFile(String filePath, String c){
		try {
			 
			String content = c;
 
			File file = new File(filePath);
 
			// if file doesnt exist, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
 
			System.out.println("Done Writing to " + filePath);
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
