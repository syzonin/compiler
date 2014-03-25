
import java.util.ArrayList;

public class Table {

	private ArrayList<Record> records;
	private ArrayList<Record> buffer;
	private ArrayList<String> validTypes;
	private Table parentTable;
	private static String errors = "";
	public static boolean hasErrors = false;
	private String tableInfo = "";
	
	//Empty Constructor
	public Table() {
		records = new ArrayList<Record>();
		buffer = new ArrayList<Record>();
		validTypes = new ArrayList<String>();
		validTypes.add("int");
		validTypes.add("float");
	}
	
	//Constructor with Table inheriting parent Table
	public Table(Table parent) {
		records = new ArrayList<Record>();
		buffer = new ArrayList<Record>();
		validTypes = parent.validTypes;
		parentTable = parent;
	}

	public String getSymbolTable(){
		ArrayList<Record> classArray = new ArrayList<Record>();
		ArrayList<Record> functionArray = new ArrayList<Record>();
		
		tableInfo += "Table: Global Scope" + "\r\n";
		for (int i = 0; i < records.size(); i++){
			tableInfo += records.get(i) + "\r\n";
		}
		tableInfo += "\r\n";
		
		for (int i = 0; i < records.size(); i++){
			if (records.get(i).getStructure().equals("function")){
				functionArray.add(records.get(i));
			}
			else if (records.get(i).getStructure().equals("class")){
				classArray.add(records.get(i));
				ArrayList<Record> temp = records.get(i).getLocal().getRecords();
				for(int j = 0; j < temp.size(); j++){
					if (temp.get(j).getStructure().equals("function")){
						functionArray.add(temp.get(j));
					}
				}
			}
		}
		
		for (int i = 0; i < classArray.size(); i++){
			tableInfo +="Table: Class "+ classArray.get(i).getName() + "\r\n";
			ArrayList<Record> temp = classArray.get(i).getLocal().getRecords();
			for(int j = 0; j < temp.size(); j++){
				tableInfo += temp.get(j) + "\r\n";
			}
			tableInfo += "\r\n";
		}
		
		for (int i = 0; i < functionArray.size(); i++){
			tableInfo += "Table: Function "+ functionArray.get(i).getName() + "\r\n";
			ArrayList<Record> temp = functionArray.get(i).getLocal().getRecords();
			for(int j = 0; j < temp.size(); j++){
					tableInfo += temp.get(j) + "\r\n";
			}
			tableInfo += "\r\n";
		}
		
		return tableInfo;
		
	}
	public static String getTableErrors(){
		return errors;
	}
	public ArrayList<String> getValidTypes() {
		return validTypes;
	}

	public void setValidTypes(ArrayList<String> v) {
		validTypes = v;
	}

	public ArrayList<Record> getbuffer() {
		return buffer;
	}

	public void setbuffer(ArrayList<Record> b) {
		buffer = b;
	}

	public ArrayList<Record> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<Record> r) {
		records = r;
	}

	public Table getParentTable() {
		return parentTable;
	}

	public void setParentTable(Table p) {
		parentTable = p;
	}
	
	//Insert record
	public void insert(Record record, boolean parsedComplete, boolean flush) {
		
		if(parsedComplete)
			return;
		
		//add program record
		if (record.getName().equals("program")) {
			records.add(record);
			return;
		}
		//add class to buffer
		if (!record.getStructure().equals("class") && !record.isValidType(validTypes)){
			buffer.add(record);
		}
		//add record to records
		else if (!alreadyDeclared(record)) {
			records.add(record);
			if (record.getStructure().equals("variable")){
				record.setClassLocal(getScope(record.getType()).getLocal());			
			}
			else if (record.getStructure().equals("function")){
				record.setClassLocal(getScope(record.getName()).getLocal());
				if (flush) {
					//destroy function table
					record.setLocal(null);
				}
			}
			else if (record.getStructure().equals("class")){
				validTypes.add(record.getName());
			}
		}
		//Error: Already declared
		else {
			//For Functions
			if (record.getStructure().equals("function")) {
				errors += "Error: Function " + record.getName() + "(";
				for (int i=0; i<record.getParams().size(); i++) {
					if(i == record.getParams().size() - 1){
						errors += record.getParams().get(i);
					}
					else{
						errors += record.getParams().get(i) + ", ";
					}
				}
				errors += ") already declared." + "\r\n";
			}
			//For Classes
			else if(record.getStructure().equals("class")){
				errors += "Error: Class " + record.getName() + " already declared." + "\r\n";
			}
			//For Variables
			else {
				errors += "Error: Variable " + record.getName() + " already declared." + "\r\n";
			}
			hasErrors = true;
		}
		
	}
	
	//Helper method: checks if record is already declared
	private boolean alreadyDeclared(Record record) {
		
		boolean declared = false;
		for (int i=0; i<records.size(); i++) {
			if(record.equalsRecord(records.get(i), false)){
				declared = true;
			}
		}
		return declared;
	}
	
	//Flush buffer
	public void flushBuffer(boolean flush) {
		
		for (int i=0; i<buffer.size(); i++) {
			if (buffer.get(i).getLocal() != null){
				buffer.get(i).getLocal().flushBuffer(flush);
			}
			if (buffer.get(i).isValidType(validTypes)){
				insert(buffer.get(i), false, flush);
			}
			else {
				errors += "Error: Invalid type " + buffer.get(i).getType() + " for " + buffer.get(i).getStructure()
						+ " " + buffer.get(i).getName() + "\r\n";
				hasErrors = true;
			}
		}
		for (int i=0; i<records.size(); i++) {
			if (records.get(i).getLocal() != null){
				records.get(i).getLocal().flushBuffer(flush);
			}
		}
		
	}
	
	//Search table for record
	public boolean search(Record record) {
		
		//Handling Function
		if (record.getStructure().equals("function")) {
			for (int i=0; i<records.size(); i++) {
				if (record.getName().equals(records.get(i).getName()) 
						&& record.getParams().size() == records.get(i).getParams().size()){
					return true;
				}
			}
			if (parentTable!=null){
				return parentTable.search(record);
			}
			errors += "Error: undefined function " + record.getName() + "(";
			
			for (int i=0; i<record.getParams().size(); i++) {
				if(i == record.getParams().size() - 1){
					errors += record.getParams().get(i);
				}
				else{
					errors += record.getParams().get(i) + ", ";
				}
			}
			errors += "\r\n";
			hasErrors = true;
			return false;
			
		}
		//Handling Variable
		else if(record.getStructure().equals("variable")) {
			//Array
			if (record.getVarStructure().equals("array")) {
				for (int i=0; i<records.size(); i++) {
					if (record.getName().equals(records.get(i).getName()) && 
							record.getDimension().size() == records.get(i).getDimension().size()){
						return true;
					}
				}
				if (parentTable!=null){
					return parentTable.search(record);
				}
				errors += "Error: undefined array " + record.getName();
				for (int i=0; i<record.getDimension().size(); i++) {
					errors += "[]";
				}
				errors += "\r\n";
				hasErrors = true;
				return false;
			}
			//Simple
			else {
				for (int i=0; i<records.size(); i++) {
					if (record.getName().equals(records.get(i).getName()))
						return true;
				}
				if (parentTable!=null){
					return parentTable.search(record);
				}
				errors += "Errors: undefined variable" + record.getName() + "\r\n";
				hasErrors = true;
				return false;
			}
		}
		return false;
	}
	
	//Checks if record is defined in scope
	public Record find(Record record) {
		
		for (int i=0; i<records.size(); i++) {
			if (records.get(i).equalsRecord(record, true)){
				return records.get(i);
			}
		}
		if (parentTable!=null){
			return parentTable.find(record);
		}
		errors += "Error: element " + record.getName() + " is undefined in scope. " + "\r\n";
		hasErrors = true;
		return new Record();		
	}
	
	//get scope of record
	public Record getScope(String type) {
		
		for (int i=0; i<records.size(); i++) {
			if (records.get(i).getStructure().equals("class") && records.get(i).getName().equals(type)){
				return records.get(i);
			}
		}
		if (parentTable!=null){
			return parentTable.getScope(type);
		}
		return new Record();
	}
	
	//return Type of Variable
	public String getVariableType(Record record) {
		
		for (int i=0; i<records.size(); i++) {
			if (records.get(i).getStructure().equals("variable") && 
					records.get(i).getName().equals(record.getName())){
				return records.get(i).getType();
			}
		}
		if (parentTable!=null){
			return parentTable.getVariableType(record);
		}
		return null;	
	}
}