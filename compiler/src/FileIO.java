import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//Class handles all reading to and writing from files
public class FileIO {
	
	String tokenFilePath = "tokens.txt";
	String lexErrorFilePath = "lexErrors.txt";
	String parserFilePath = "parseInfo.txt";
	String parseErrorFilePath = "parseErrors.txt";
	String tableFilePath = "tableInfo.txt";
	String tableErrorFilePath = "tableErrors.txt";
	String filePath = "profprovided.txt";
	
	//returns char array of file read
	public char[] readFileToCharArray() throws IOException {
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
	
	
	//writes strings to specified file paths
	public void write(String tokens, String lexError, String parse, 
			String parseError, String table, String tableError){
		writeToFile(tokenFilePath, tokens);
		writeToFile(lexErrorFilePath, lexError);
		writeToFile(parserFilePath, parse);
		writeToFile(parseErrorFilePath, parseError);
		writeToFile(tableFilePath, table);
		writeToFile(tableErrorFilePath, tableError);
	}
	
	//writes string to file
	private void writeToFile(String filePath, String c){
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
