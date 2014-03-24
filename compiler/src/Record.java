/*import java.util.ArrayList;


public class Record {
	
	private String name;
	private String type;
	private String varKind;
	private String structure;
	private int numParams;
	private ArrayList<String> paramTypes;
	private ArrayList<Integer> dimension;
	private float address;
	private Table local;
	
	public Record(){
		varKind = "normal";
		
	}

} */

import java.util.ArrayList;

public class Record {

	private String name, type, varKind, structure, varStructure;
	private Table local;
	private ArrayList<Integer> dimension = new ArrayList<Integer>();
	private ArrayList<String> params = new ArrayList<String>();
	
	private Table classLocal;
	
	private static long unique = 0;
	private long address;
	
	public Record() {
		
		//address = unique++;
		varKind = "normal";
		structure = "variable";
		varStructure = "simple";
		local = new Table();
		classLocal = new Table();
		
	}

	/*
	public Record(Record record) {
		
		name = record.getName();
		type = record.getType();
		varKind = record.getVarKind();
		varStructure = record.getVarStructure();
		local = new Table(record.getLocal());
		dimension = record.getDimension();
		params = record.getParams();
		classLocal = record.getClassLocal();
		address = record.getAddress();
		
	}
	*/
	public Table getClassLocal() {
		return classLocal;
	}

	public void setClassLocal(Table classLocal) {
		this.classLocal = classLocal;
	}

	public String getVarStructure() {
		return varStructure;
	}

	public void setVarStructure(String varStructure) {
		this.varStructure = varStructure;
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
	
	public String getVarKind() {
		return varKind;
	}

	public void setVarKind(String varKind) {
		this.varKind = varKind;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Table getLocal() {
		return local;
	}

	public void setLocal(Table local) {
		this.local = local;
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

	public ArrayList<String> getParams() {
		return params;
	}

	public void setParams(ArrayList<String> params) {
		this.params = params;
	}
	
	public long getAddress() {
		return address;
	}

	public void setAddress(long address) {
		this.address = address;
	}

	public boolean equalsR(Record record, boolean call) {
		
		if (structure.equals(record.structure) && name.equals(record.name)) {
		
			if (structure.equals("class"))
				return true;
			
			else if(call && structure.equals("function") && params.size() == record.params.size())
				return true;
			
			else if (!call && structure.equals("function") && this.hasSameParams(record))
				return true;
			
			else if (structure.equals("variable"))
				return true;
			
			else
				return false;
			
		}
		
		else
			return false;
		
	}
	
	public boolean hasSameParams(Record record) {
		
		for (int i=0; i < params.size(); i++) {
			
			if (!params.get(i).equals(record.params.get(i)))
					return false;
			
		}
		
		return true;
		
	}
	
	public boolean isValidType(ArrayList<String> validTypes) {
		
		for (int i=0; i < validTypes.size(); i++) {
			
			if (type.equals(validTypes.get(i)))
				return true;
			
		}
		
		return false;
		
	}

	@Override
	public String toString() {
		return "Record [name=" + name + ", type=" + type + ", varKind="
				+ varKind + ", structure=" + structure + ", varStructure="
				+ varStructure + ", local=" + local + ", dimension="
				+ dimension + ", params=" + params + ", address=" + address
				+ "]";
	}
	
	public void giveAddress() {
		
		address = unique++;
		
		for(int i=0; i<classLocal.getRecords().size(); i++) {
			
			classLocal.getRecords().get(i).address = unique++;
			
		}
		
	}
	
}