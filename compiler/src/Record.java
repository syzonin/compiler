import java.util.ArrayList;

public class Record {

	private String name; 
	private String type; 
	private String varKind; 
	private String structure; 
	private String varStructure;
	private Table local;
	private Table classLocal;
	private ArrayList<Integer> dimension = new ArrayList<Integer>();
	private ArrayList<Type> params = new ArrayList<Type>();
	private int address;
	private static int increment = 0;
	
	public Record() {
		address = increment++;
		varKind = "normal";
		structure = "variable";
		varStructure = "simple";
		local = new Table();
		classLocal = new Table();
	}

	public Record(Record r) {
		name = r.getName();
		type = r.getType();
		varKind = r.getVarKind();
		varStructure = r.getVarStructure();
		local = new Table(r.getLocal());
		dimension = r.getDimension();
		params = r.getParams();
		classLocal = r.getClassLocal();
		address = r.getAddress();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getVarKind() {
		return varKind;
	}

	public void setVarKind(String varKind) {
		this.varKind = varKind;
	}
	
	public String getVarStructure() {
		return varStructure;
	}

	public void setVarStructure(String varStructure) {
		this.varStructure = varStructure;
	}
	
	public Table getLocal() {
		return local;
	}

	public void setLocal(Table local) {
		this.local = local;
	}
	
	public Table getClassLocal() {
		return classLocal;
	}

	public void setClassLocal(Table classLocal) {
		this.classLocal = classLocal;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public ArrayList<Integer> getDimension() {
		return dimension;
	}

	public void setDimension(ArrayList<Integer> dimension) {
		this.dimension = dimension;
	}

	public ArrayList<Type> getParams() {
		return params;
	}

	public void setParams(ArrayList<Type> params) {
		this.params = params;
	}
	
	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}
	
	//increments address
	public void incrementAddress() {
		address = increment++;
		for(int i=0; i<classLocal.getRecords().size(); i++) {
			classLocal.getRecords().get(i).address = increment++;
		}
	}

	//checks if parameter equals current record
	public boolean equalsRecord(Record r, boolean call, boolean complete) {
		
		boolean equals = false;
		if (name.equals(r.name) && structure.equals(r.structure)) {
			if (structure.equals("class")){
				equals = true;
			}
			else if(call && structure.equals("function") && complete){
				equals = true;
			}
			else if(call && structure.equals("function") && !complete){
				equals = true;
			}
			else if (!call && structure.equals("function") && this.paramIsRepeated(r)){
				equals = true;
			}
			else if (structure.equals("variable")){
				equals = true;
			}
		}
		return equals;
		
	}
	
	//Checks if parameter already exists
	public boolean paramIsRepeated(Record r) {
		boolean isRepeated = true;
		for (int i=0; i < params.size(); i++) {
			if (!params.get(i).equals(r.params.get(i))){
				isRepeated = false;
			}
		}
		return isRepeated;
	}
	
	//Checks if type is valid within scope
	public boolean isValidType(ArrayList<String> validTypes) {
		boolean isValid = false;;
		for (int i=0; i < validTypes.size(); i++) {
			if (type.equals(validTypes.get(i))){
				isValid = true;
			}
		}
		return isValid;
	}
	
	//Helper method for toString()
	public ArrayList<Integer> printDimension(){
		if (dimension.isEmpty()){
			return null;
		}
		else {
			return dimension;
		}
	}
	
	//Returns records as String
	public String toString() {
		String s;
		//toString for Variables
		if (!structure.equals("class") && !structure.equals("function") ){
			s = "{Record name: " + name + ", structure: " + structure + ", type: " + type 
				+  ", variable kind: " + varKind + ", variable structure: " + varStructure 
				+  ", dimension: " + printDimension() + ", address: " + address + "}";
		}
		//toString for Functions and Classes
		else {
			s = "{Record name: " + name +  ", structure: " + structure + ", type: " + type
				+ ", number of parameters: " + params.size() + ", parameters: " + !params.isEmpty() 
				+ ", local table: " + !local.getRecords().isEmpty() + ", address: " + address + "}";
		}
		return s;
	}


}