
/* MOON CODE INCOMPLET
import java.io.PrintWriter;

*/

import java.util.ArrayList;

public class Table {

	private ArrayList<Record> records, definitionBuffer;
	private ArrayList<String> validTypes;
	private Table parentTable;
	
	/* MOON CODE INCOMPLETE
	
	private ArrayList<String> moonCalls = new ArrayList<String>();
	
	private int parameterNum = 1;
	
	*/
	
	public Table() {
		
		records = new ArrayList<Record>();
		definitionBuffer = new ArrayList<Record>();
		validTypes = new ArrayList<String>();
		validTypes.add("integer");
		validTypes.add("real");
		
	}
	
	public Table(Table parentTable) {
		
		records = new ArrayList<Record>();
		definitionBuffer = new ArrayList<Record>();
		validTypes = parentTable.validTypes;
		this.parentTable = parentTable;
	
		
	}

	public ArrayList<String> getValidTypes() {
		return validTypes;
	}

	public void setValidTypes(ArrayList<String> validTypes) {
		this.validTypes = validTypes;
	}

	public ArrayList<Record> getDefinitionBuffer() {
		return definitionBuffer;
	}

	public void setDefinitionBuffer(ArrayList<Record> definitionBuffer) {
		this.definitionBuffer = definitionBuffer;
	}

	public ArrayList<Record> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<Record> records) {
		this.records = records;
	}

	public Table getParentTable() {
		return parentTable;
	}

	public void setParentTable(Table parentTable) {
		this.parentTable = parentTable;
	}
	
	public void insert(Record record, boolean parsedComplete, boolean flush) {
		
		if(parsedComplete)
			return;
		
		if (record.getName().equals("program")) {
			
			records.add(record);
			return;
			
		}
		
		if (!record.getStructure().equals("class") && !record.isValidType(validTypes))
			definitionBuffer.add(record);
		
		else if (!alreadyDefined(record)) {
			
			records.add(record);
			
			if (record.getStructure().equals("variable")) {
				
				record.setClassLocal(getScope(record.getType()).getLocal());
				
			}
			
			if (record.getStructure().equals("function")) {
				
				record.setClassLocal(getScope(record.getName()).getLocal());
				
				//Parser.pw2.println("Printing: Function " + record.getName());
				//Parser.pw2.println(record.getLocal().toString());
				
				if (flush) {
					record.setLocal(null);						//Destroy function table
				}
				
			}
			
			else if (record.getStructure().equals("class"))
				validTypes.add(record.getName());
			
		}
			
		else {
			
			if (record.getStructure().equals("function")) {
			
				System.err.print("Multiple declarations of " + record.getStructure() + " " + record.getName());
				System.err.print("(");
				
				for (int i=0; i<record.getParams().size(); i++) {
					
					if(i == record.getParams().size() - 1)
						System.err.print(record.getParams().get(i));
					else
						System.err.print(record.getParams().get(i) + ", ");
					
				}
				
				System.err.println(")");
				//Parser.tableError = true;
				
			}
			
			else
				System.err.println("Multiple declarations of " + record.getStructure() + " " + record.getName());
				//Parser.tableError = true;
		}
		
	}
	
	public boolean alreadyDefined(Record record) {
		
		for (int i=0; i<records.size(); i++) {
			
			if(record.equalsR(records.get(i), false))
				return true;
			
		}
		
		return false;
		
	}
	
	public void flushDefinitionBuffer(boolean flush) {
		
		for (int i=0; i<definitionBuffer.size(); i++) {
			
			if (definitionBuffer.get(i).getLocal() != null)
				definitionBuffer.get(i).getLocal().flushDefinitionBuffer(flush);
			
			if (definitionBuffer.get(i).isValidType(validTypes))
				insert(definitionBuffer.get(i), false, flush);
			
			else {
				System.err.println("Error: Invalid type " + definitionBuffer.get(i).getType() + " for " + definitionBuffer.get(i).getStructure()
						+ " " + definitionBuffer.get(i).getName());
				//Parser.tableError = true;
			}
		}
		
		for (int i=0; i<records.size(); i++) {
	
			if (records.get(i).getLocal() != null)
				records.get(i).getLocal().flushDefinitionBuffer(flush);
			
		}
		
	}
	
	public boolean search(Record record) {
		
		if(record.getStructure().equals("variable")) {
			
			if (record.getVarStructure().equals("array")) {
				
				for (int i=0; i<records.size(); i++) {
					
					if (record.getName().equals(records.get(i).getName()) && record.getDimension().size() == records.get(i).getDimension().size())
						return true;
					
				}
				
				if (parentTable!=null)
					return parentTable.search(record);
				
				System.err.print("Array " + record.getName());
				
				for (int i=0; i<record.getDimension().size(); i++) {
					
					System.err.print("[]");
					
				}
				
				System.err.println(" is undefined");
				
				//Parser.tableError = true;
				
				return false;
				
			}
			
			else {
				
				for (int i=0; i<records.size(); i++) {
					
					if (record.getName().equals(records.get(i).getName()))
						return true;
					
				}
				
				if (parentTable!=null)
					return parentTable.search(record);
				
				System.err.println("Error: Variable " + record.getName() + " is undefined");
				
				//Parser.tableError = true;
				
				return false;
				
			}
			
		}
		
		else if (record.getStructure().equals("function")) {
			
			for (int i=0; i<records.size(); i++) {
				
				if (record.getName().equals(records.get(i).getName()) && record.getParams().size() == records.get(i).getParams().size())
					return true;
				
			}
			
			if (parentTable!=null)
				return parentTable.search(record);
			
			System.err.print("Error: Function " + record.getName() + "(");
			
			for (int i=0; i<record.getParams().size(); i++) {
				
				if (i == record.getParams().size() - 1)
					System.err.print("parameter");
				
				else
					System.err.print("parameter, ");
				
			}
			
			System.err.println(") is undefined");
			
			//Parser.tableError = true;
			
			return false;
			
		}
		
		return false;
		
	}
	
	public Record findScope(Record record) {
		
		if(record.getStructure().equals("variable")) {
			
			if (record.getVarStructure().equals("array")) {
				
				for (int i=0; i<records.size(); i++) {
					
					if (record.getName().equals(records.get(i).getName()) && record.getDimension().size() == records.get(i).getDimension().size())
						return getScope(records.get(i).getType());
					
				}
				
				return new Record();
				
			}
			
			else {
				
				for (int i=0; i<records.size(); i++) {
					
					if (record.getName().equals(records.get(i).getName()))
						return getScope(records.get(i).getType());
					
				}
				
				return new Record();
				
			}
			
		}
		
		else if (record.getStructure().equals("function")) {
			
			for (int i=0; i<records.size(); i++) {
				
				if (record.getName().equals(records.get(i).getName()) && record.getParams().size() == records.get(i).getParams().size())
					return getScope(records.get(i).getType());
				
			}
			
			return null;
			
		}
		
		return null;
		
	}
	
	public Record getScope(String type) {
		
		for (int i=0; i<records.size(); i++) {
			
			if (records.get(i).getStructure().equals("class") && records.get(i).getName().equals(type))
				return records.get(i);
			
		}
		
		if (parentTable!=null)
			return parentTable.getScope(type);
		
		return new Record();
		
	}
	
	public String getVariableType(Record record) {
		
		for (int i=0; i<records.size(); i++) {
			
			if (records.get(i).getStructure().equals("variable") && records.get(i).getName().equals(record.getName()))
				return records.get(i).getType();
			
		}
		
		if (parentTable!=null)
			return parentTable.getVariableType(record);
		
		return null;
		
	}
	
	public Record find(Record record) {
		
		for (int i=0; i<records.size(); i++) {
			
			if (records.get(i).equalsR(record, true))
				return records.get(i);
			
		}
		
		if (parentTable!=null)
			return parentTable.find(record);
		
		System.err.println("Error: Element " + record.getName() + " is undefined in scope" );
		//Parser.tableError = true;
		return new Record();
		
	}
	
	public String toString() {
		
		String wholeTable = "";
		
		for (int i=0; i<records.size(); i++) {
			
			wholeTable = wholeTable + records.get(i).toString() + "\n";
			
		}
		
		return wholeTable;
		
	}
	
	/* MOON CODE INCOMPLETE
	
	public void writeMOON(PrintWriter moonCode) {
		
		for (int i=0; i<records.size(); i++) {
			
			if(records.get(i).getStructure().equals("variable") && records.get(i).getVarKind().equals("normal")) {
				
				//records.get(i).giveAddress();
				
				moonCode.println(records.get(i).getAddress() + "\tdw 0");
				
				records.get(i).getClassLocal().writeMOON(moonCode);
				
			}
			
			else if (records.get(i).getName().equals("program")) {
				
				records.get(i).getLocal().writeMOON(moonCode);
				
			}
			
			else if (records.get(i).getStructure().equals("function")) {
				
				//records.get(i).giveAddress();
				
				moonCode.println(records.get(i).getAddress() + "\tdw 0");
					
				for (int k=0; k < records.get(i).getLocal().records.size(); k++) {
						
					if (records.get(i).getLocal().records.get(k).getVarKind().equals("parameter")) {
						
						//records.get(i).getLocal().records.get(k).giveAddress();
						
						moonCode.println(records.get(i).getLocal().records.get(k).getAddress() + "\tdw 0");
						moonCode.println("\tsw " + records.get(i).getLocal().records.get(k).getAddress() + "(r0),r" + (++parameterNum));
							
					}
					
				}
				
				parameterNum = 1;
				
				records.get(i).getLocal().writeMOON(moonCode);
				
				records.get(i).getLocal().printMoonCalls(moonCode);
				
				for (int k=0; k < records.get(i).getLocal().records.size(); k++) {
					
					if (records.get(i).getLocal().records.get(k).getVarKind().equals("parameter")) {
						
						moonCode.println("\tlw r" + (++parameterNum) + "," + records.get(i).getLocal().records.get(k).getAddress() + "(r0)");
							
					}
					
				}
				
				moonCode.println("\tjr r15");
				
			}
			
		}
		
	}

	public ArrayList<String> getMoonCalls() {
		return moonCalls;
	}

	public void setMoonCalls(ArrayList<String> moonCalls) {
		this.moonCalls = moonCalls;
	}
	
	public void printMoonCalls(PrintWriter moonCode) {
		
		for(int i=0; i<moonCalls.size(); i++) {
			
			moonCode.println(moonCalls.get(i));
			
		}
		
	}
	
	*/
	
	public void giveAddresses() {
		
		for(int i=0; i<records.size(); i++) {
			
			if(records.get(i).getStructure().equals("function")) {
				
				records.get(i).giveAddress();
				
				records.get(i).getLocal().giveAddresses();
				
			}
			
			else if(records.get(i).getStructure().equals("variable")) {
				
				records.get(i).giveAddress();
				
				records.get(i).getClassLocal().giveAddresses();
				
			}
			
		}
		
	}
	
}