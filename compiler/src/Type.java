import java.util.ArrayList;


public class Type {
	
	private String type;
	private boolean isArray;
	private ArrayList<Integer> arrayDimensions;
	
	public Type(){
		type = "";
		isArray = false;
		arrayDimensions = new ArrayList<Integer>();
	}
	
	public Type(String t, boolean b){
		setType(t);
		setArray(b);
		arrayDimensions = new ArrayList<Integer>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isArray() {
		return isArray;
	}

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}
	
	public boolean equals(Type t){
		if (type.equals(t.type) && isArray == t.isArray &&
				arrayDimensions.size() == t.arrayDimensions.size())
			return true;
		return false;
	}
	
	public void setTo(Type t){
		if(t != null){
			type = t.type;
			isArray = t.isArray;
		}
	}
	public String toString(){
		String s = type;
		for (int i = 0; i < arrayDimensions.size(); i++){
			s += "[]";
		}
		return s;
	}

	public ArrayList<Integer> getArrayDimensions() {
		return arrayDimensions;
	}

	public void setArrayDimensions(ArrayList<Integer> arrayDimensions) {
		this.arrayDimensions = arrayDimensions;
	}
	
	public void addDimension(int i){
		arrayDimensions.add(i);
	}
	

	
}
