import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class LineCounter {
	private int[] countPerLine = new int[200];
	private int totalCount = 0;
	private int totalLineCount = 0;
	String filePath = "profprovided.txt";
	
	public LineCounter(){
		try {
			readFileToCharArray();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	//returns char array of file read
	public char[] readFileToCharArray() throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		
		String line;
		
		int i = 0;
		while ((line = reader.readLine()) != null) {
			countPerLine[i] = line.length() + 2;
			totalCount += line.length() + 2;
			i++;
		}
		fileData.append("  ");
		totalCount += 2;
		totalLineCount = i + 1;
		
		reader.close();
		return  fileData.toString().toCharArray();	
		
	}
	
	public int getLineNumber(int n){
		int counter = 0;
		for (int i = 0; i < totalLineCount; i++){
			counter += countPerLine[i];
			if (n <= counter){
				return i + 1;
			}
		}
		return totalLineCount;
	}
	
}
