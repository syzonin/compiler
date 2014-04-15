import java.util.ArrayList;
public class TypeChecker {

	private String errorMessages="";
	private String tableInfo = "";
	private String parseInfo="";
	private ArrayList<Token> tokenArray = new ArrayList<Token>(); 
	private NonTerminalCollection nt = new NonTerminalCollection();
	private int tokenArrayLocation = 0;
	private Token lookAhead;
	private Token lookBehind;
	private Table table;
	public boolean parsed;
	private Record lastIdnest;
	private int numParams;
	private Table lastClassScope;
	private int arrayDim = 0;
	private Type variable = new Type();
	private Type functionReturn = new Type();
	private String functionName;
	private ArrayList<Type> functionCallParams = new ArrayList<Type>();
	private int lineNum;
	
	public TypeChecker(Table t, ArrayList<Token> tArray){
		
		
		table = t;
		tokenArray = tArray;
		parsed = parser();
	}

	public Type checkType(Type t1, Type t2, int line){
		if (t1.getType().equals(t2.getType())){
			return t1;
		}
		else if (t1.getType().equals("ERROR") || t2.getType().equals("ERROR")){
			return new Type("ERROR", false);
		}
		String e = "Error on line " + line + "" +
				": types " + t1.getType() + " and " + t2.getType() + " do not match." + "\r\n";
		Table.addError(e);
		return new Type("ERROR", false);
	}
	
	public void functionReturnCheck(Type t){
		if (t.equals(functionReturn)){
		}
		else {
			String e = "Error on line " + lineNum + "" +
					": return type " + t + " does not match function " +
						functionName + "'s return type, " + functionReturn.getType() + ".\r\n";
			Table.addError(e);
		}
	}
	
	private String getPString(ArrayList<Type> p){
		String e = p.get(0).toString();
		for(int i = 1; i < p.size(); i++){
			e += ", " + p.get(i).toString();
		}
		return e;
	}
	
	public void checkParameters(Record r, int line){
		if (r.getParams().size() != functionCallParams.size()){
			String e = "Error on line " + line + ": Function call " + r.getName() + " has an incorrect number of parameters ";
			e += "(Expected: " + r.getParams().size() + ", Got: " + functionCallParams.size() + ").\r\n";
			Table.addError(e);
		}
		else {
			boolean matched = true;
			for (int i = 0; i < r.getParams().size(); i++){
				if(!r.getParams().get(i).equals(functionCallParams.get(i))){
					matched = false;
				}
			}
			if (!matched){
				String p1 = getPString(r.getParams());
				String p2 = getPString(functionCallParams);
				String e = "Error on line " + line + ": Function call " + r.getName() + " has the wrong parameter types ";
				e += "(Expected: " + p1 + ", Got: " + p2 + ").\r\n";
				Table.addError(e);
			}
		}
	}
	
	Type getType(Record record, Table table, int line){
		if(record != null){
			Record found = table.searchRecord(record, line);
			if (found != null){
				Type t = new Type(found.getType(), false);
				if (found.getVarStructure().equals("array")){
					t.setArray(true);
				}
				return t;
			}
			 
			if (record.getType() != null){
				String e = "Error: record " + record.getName() + 
						" with type " + record.getType() + " not found in scope." + "\r\n";
				Table.addError(e);
			}
		}
		return new Type("ERROR", false);
	}
	
	public String getParsedContent(){
		return parseInfo;
	}
	
	public String getTableInfo(){
		return tableInfo;
	}
	
	public String getTableErrors(){
		return Table.getTableErrors();
	}
	
	public String getParserErrors(){
		return errorMessages;
	}
	
	//Parser
	private boolean parser(){
		
		lookAhead = nextTokenParser();
		lookBehind = lookAhead;
		
		boolean success;
		
		if (Prog(table) && match("eof"))
			success = true;
		else
			success = false;

		return success;
	}
	
	//Returns next parser token
	private Token nextTokenParser(){
		Token t = null;
		if (tokenArrayLocation < tokenArray.size() ){
			t = tokenArray.get(tokenArrayLocation);
			tokenArrayLocation++;
		}
		return t;
	}
	
	//Returns whether or not lookAhead matched with terminal, advances lookAhead
	private boolean match(String t) {
		if (t.equals("num")){
			if (lookAhead.equals(t) || lookAhead.equals("INT") ){
				//move to next token
				lookBehind = lookAhead;
				lookAhead = nextTokenParser();
				return true;
			}
		}
		if (lookAhead.equals(t)){
			//move to next token
			lookBehind = lookAhead;
			lookAhead = nextTokenParser();
			return true;
		}
		else {
			lookBehind = lookAhead;
			lookAhead = nextTokenParser();
			return false;
		}
	}
	
	//Skips tokens if they are not in the first or follow sets of nonterminal
	private boolean skipErrors(NonTerminal nt){
		
		//case no error detected:
		if (lookAhead.inUnion(nt)){
			return true;
		}
		//case error detected:
		else {
			while (!lookAhead.inUnion(nt) && tokenArrayLocation < tokenArray.size() ){
				lookBehind = lookAhead;
				lookAhead = nextTokenParser();
				errorMessages += "Skipping token: " + lookAhead + "\r\n";
			}
			errorMessages += "Parsing resumed at: " + lookAhead.getCount() + "." + "\r\n";
			return false;
		} 
	}
	
	//1. Prog -> ClassDeclList ProgBody 
	private boolean Prog(Table table){
		
		boolean success = skipErrors(nt.Prog);
		
		if (lookAhead.inRHS1(nt.Prog)){
			if (ClassDeclList(table) && ProgBody(table)){
				parseInfo += "*Prog -> ClassDeclList ProgBody" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	
	//2. ClassDeclList -> ClassDecl ClassDeclList | epsilon
	private boolean ClassDeclList(Table table){
		
		boolean success = skipErrors(nt.ClassDeclList);
		
		if (lookAhead.inRHS1(nt.ClassDeclList)){
			if (ClassDecl(table) && ClassDeclList(table))
			{
				parseInfo += "*ClassDeclList -> ClassDecl ClassDeclList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.ClassDeclList)){
			parseInfo += "*ClassDeclList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//3. ClassDecl -> class id { ClassMemberDeclList } ; 
	private boolean ClassDecl(Table table){
		
		boolean success = skipErrors(nt.ClassDecl);
		
		if (lookAhead.inRHS1(nt.ClassDecl)){
			
			Record record = new Record();
			
			boolean b1 = match("class");
			boolean b2 = match("id");
			record.setStructure("class");
			record.setName(lookBehind.getLexeme());
			lineNum = lookBehind.getLine();
			Table newScope = table.find(record, true, lineNum).getLocal();
			
			boolean b3 = match("opencur");
			boolean b4 = ClassMemberDeclList(newScope);
			boolean b5 = match("closecur");
			boolean b6 = match("semi");
					
			if (b1 && b2 && b3 && b4 && b5 && b6)
			{
				parseInfo += "*ClassDecl -> class id { ClassMemberDeclList } ; " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//4. ClassMemberDecl -> Type id ClassMemberDecl2
	private boolean ClassMemberDecl(Table table){
		
		boolean success = skipErrors(nt.ClassMemberDecl);
		
		if (lookAhead.inRHS1(nt.ClassMemberDecl)){
			
			Record record = new Record();
			
			boolean b1 = Type(new Type());
			record.setType(lookBehind.getLexeme());
			functionReturn.setType(lookBehind.getLexeme());
			boolean b2 = match("id");
			functionName = lookBehind.getLexeme();
			record.setName(lookBehind.getLexeme());
			
			boolean b3 = ClassMemberDecl2(record, table);
			
			if (b1 && b2 && b3)
			{
				parseInfo += "*ClassMemberDecl -> Type id ClassMemberDecl2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//5. ClassMemberDecl2 -> ArraySizeList ; | ( FParams ) FuncBody ;
	private boolean ClassMemberDecl2(Record record, Table table){
		
		boolean success = skipErrors(nt.ClassMemberDecl2);
		
		if (lookAhead.inRHS1(nt.ClassMemberDecl2)){
			if (ArraySizeList() && match("semi"))
			{
				parseInfo += "*ClassMemberDecl2 -> ArraySizeList ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.ClassMemberDecl2)){
			
			record.setStructure("function");
			
			boolean b1 = match("openpar");
			boolean b2 = FParams(record);
			lineNum = lookBehind.getLine();
			Table newScope = table.find(record, true, lineNum).getLocal();
			
			boolean b3 = match("closepar");
			boolean b4 = FuncBody(newScope);
			boolean b5 = match("semi");
			
			if (b1 && b2 && b3 && b4 && b5 ){
				parseInfo += "*ClassMemberDecl2 -> ( FParams ) FuncBody ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//6. ClassMemberDeclList -> ClassMemberDecl ClassMemberDeclList | epsilon
	private boolean ClassMemberDeclList(Table table){
		
		boolean success = skipErrors(nt.ClassMemberDeclList);
		
		if (lookAhead.inRHS1(nt.ClassMemberDeclList)){
			if (ClassMemberDecl(table) && ClassMemberDeclList(table))
			{
				parseInfo += "*ClassMemberDeclList -> ClassMemberDecl ClassMemberDeclList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.ClassMemberDeclList)){
			parseInfo += "*ClassMemberDeclList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//7. FuncDefList -> FuncDef FuncDefList | epsilon
	private boolean FuncDefList(Table table){
		
		boolean success = skipErrors(nt.FuncDefList);
		
		if (lookAhead.inRHS1(nt.FuncDefList)){
			if (FuncDef(table) && FuncDefList(table))
			{
				parseInfo += "*FuncDefList -> FuncDef FuncDefList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.FuncDefList)){
			parseInfo += "*FuncDefList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//8. ProgBody -> program FuncBody ; FuncDefList
	private boolean ProgBody(Table table){
		
		boolean success = skipErrors(nt.ProgBody);
		
		if (lookAhead.inRHS1(nt.ProgBody)){
			
			Record program = new Record();
			
			boolean b1 = match("program");
			program.setName("program");
			program.setStructure("function");
			lineNum = lookBehind.getLine();
			Table programScope = table.find(program, true, lineNum).getLocal();
			
			boolean b2 = FuncBody(programScope);
			boolean b3 = match("semi");
			boolean b4 = FuncDefList(table);
			
			if (b1 && b2 && b3 && b4)
			{
				parseInfo += "ProgBody -> program FuncBody ; FuncDefList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}

	//9. FuncHead -> Type id ( FParams )
	private boolean FuncHead(Record record){
		
		boolean success = skipErrors(nt.FuncHead);
		
		if (lookAhead.inRHS1(nt.FuncHead)){
			
			boolean b1 = Type(new Type());
			functionReturn.setType(lookBehind.getLexeme());
			
			boolean b2 = match("id");
			record.setName(lookBehind.getLexeme());
			functionName = lookBehind.getLexeme();
			
			record.setStructure("function");
			
			boolean b3 = match("openpar");
			boolean b4 = FParams(record);
			boolean b5 = match("closepar");
			
			if (b1 && b2 && b3 && b4 && b5)
			{
				parseInfo += "*FuncHead -> Type id ( FParams )" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//10. FuncDef -> FuncHead FuncBody ; 
	private boolean FuncDef(Table table){
		
		boolean success = skipErrors(nt.FuncDef);
		
		if (lookAhead.inRHS1(nt.FuncDef)){
			
			Record record = new Record();
			
			boolean b1 = FuncHead(record);
			lineNum = lookBehind.getLine();
			Table newScope = table.find(record, true, lineNum).getLocal();
			
			boolean b2 = FuncBody(newScope);
			boolean b3 = match("semi");
			
			if (b1 && b2 && b3){
				parseInfo += "*FuncDef -> FuncHead FuncBody ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//11. FuncBody -> { FuncBodyMemberList }
	private boolean FuncBody(Table table){
		
		boolean success = skipErrors(nt.FuncBody);
		
		if (lookAhead.inRHS1(nt.FuncBody)){
			if (match("opencur") && FuncBodyMemberList(table) && match("closecur"))
			{
				parseInfo += "*FuncBody -> { FuncBodyMemberList }" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//12. FuncBodyMember -> int id ArraySizeList ; | float id ArraySizeList ; | id FuncBodyMember2 | Statement2
	private boolean FuncBodyMember(Table table){
		
		boolean success = skipErrors(nt.FuncBodyMember);
		
		if (lookAhead.inRHS1(nt.FuncBodyMember)){
			//case: FuncBodyMember -> Statement2
			if (Statement2(table)){
				parseInfo += "*FuncBodyMember -> Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		
		//unsure if call or declaration
		else if (lookAhead.inRHS2(nt.FuncBodyMember)){
			//case: FuncBodyMember -> id FuncBodyMember2
			
			
			Record record = new Record();
			Type funcBodyMember2 = new Type();
			
			boolean b1 = match("id");
			record.setName(lookBehind.getLexeme()); //assume call
			lineNum = lookBehind.getLine();
			funcBodyMember2.setTo(getType(record, table, lineNum));

			boolean b2 = FuncBodyMember2(record, table, funcBodyMember2);
			
			if (b1 && b2) {
				
				parseInfo += "*FuncBodyMember -> id FuncBodyMember2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.FuncBodyMember)){
			//case: FuncBodyMember -> float id ArraySizeList ; 
			
			boolean b1 = match("float");
			boolean b2 = match("id");
			boolean b3 = ArraySizeList();
			boolean b4 = match("semi");
			
			if (b1 && b2 && b3 && b4){
				parseInfo += "*FuncBodyMember -> float id ArraySizeList ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS4(nt.FuncBodyMember)){
			//case: FuncBodyMember -> int id ArraySizeList ; 
			
			boolean b1 = match("int");
			boolean b2 = match("id");
			boolean b3 = ArraySizeList();
			boolean b4 = match("semi");
			
			if (b1 && b2 && b3 && b4){
				parseInfo += "*FuncBodyMember -> int id ArraySizeList ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//13. FuncBodyMember2 -> id ArraySizeList ; | IndiceList FuncBodyMember3
	private boolean FuncBodyMember2(Record record, Table table, Type funcBodyMember2){
		
		boolean success = skipErrors(nt.FuncBodyMember2);
		
		if (lookAhead.inRHS1(nt.FuncBodyMember2)){
			//case: FuncBodyMember2 -> IndiceList FuncBodyMember3
			
			Type indiceList = new Type();
			Type funcBodyMember3 = new Type();
			
			//TESTING MODIFICATION
			boolean b1 = IndiceList(record, table, funcBodyMember2, indiceList);
			
			boolean b2 = FuncBodyMember3(record, table, indiceList, funcBodyMember3);
			
			if (b1 && b2){
				parseInfo += "*FuncBodyMember2 -> IndiceList FuncBodyMember3" + "\r\n";
				
				//below set may not be necessary
				funcBodyMember2.setTo(funcBodyMember3);
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.FuncBodyMember2)){
			//case: FuncBodyMember2 -> id ArraySizeList ; 
			
			boolean b1 = match("id");		
			boolean b2 = ArraySizeList();
			boolean b3 = match("semi");
			
			if (b1 && b2 && b3){
				parseInfo += "*FuncBodyMember2 -> id ArraySizeList ; " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//14. FuncBodyMember3 -> . Variable = Expr ; | = Expr ;
	private boolean FuncBodyMember3(Record record, Table table, Type indiceList, Type funcBodyMember3){
		
		boolean success = skipErrors(nt.FuncBodyMember3);
		lineNum = lookAhead.getLine();
		Type expr = new Type();
		
		if (lookAhead.inRHS1(nt.FuncBodyMember3)){
			//case: FuncBodyMember3 -> = Expr ;
			
			//find maxValue
			lineNum = lookBehind.getLine();
			Record r = table.find(record, true, lineNum);
			
			indiceList = getType(r, table, lineNum);
			
			

			//still at program table
			if (match("assignop") && Expr(table, expr) && match("semi")){
				parseInfo += "*FuncBodyMember3 -> = Expr ;" + "\r\n";
				lineNum = lookBehind.getLine();
				checkType(indiceList, expr, lineNum);
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.FuncBodyMember3)){
			//case: FuncBodyMember3 -> . Variable = Expr ;
			
			Type variable1 = new Type();
			boolean b1 = match("dot");
			Table newScope = table.findScope(record).getLocal();

			//variable not given a type yet in funcbodymember3
			boolean b2 = Variable(newScope, variable1);
			boolean b3 = match("assignop");
			boolean b4 = Expr(table, expr);
			boolean b5 = match("semi");
			
			
			if (b1 && b2 && b3 && b4 && b5){
				parseInfo += "*FuncBodyMember3 -> . Variable = Expr ;" + "\r\n";
				lineNum = lookBehind.getLine();
				checkType(variable, expr, lineNum);
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//15. FuncBodyMemberList -> FuncBodyMember FuncBodyMemberList | epsilon
	private boolean FuncBodyMemberList(Table table){
		
		boolean success = skipErrors(nt.FuncBodyMemberList);
		
		if (lookAhead.inRHS1(nt.FuncBodyMemberList)){
			if (FuncBodyMember(table) && FuncBodyMemberList(table))
			{
				parseInfo += "*FuncBodyMemberList -> FuncBodyMember FuncBodyMemberList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.FuncBodyMemberList)){
			parseInfo += "*FuncBodyMemberList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//16. Type -> int | float | id
	private boolean Type(Type type){
		
		boolean success = skipErrors(nt.Type);
		
		if (lookAhead.inRHS1(nt.Type)){
			if (match("float"))
			{
				parseInfo += "*Type -> float" + "\r\n";
				type.setType("float");
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.Type)){
			if (match("int"))
			{
				parseInfo += "*Type -> int" + "\r\n";
				type.setType("int");
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.Type)){
			if (match("id"))
			{
				parseInfo += "*Type -> id" + "\r\n";
				type.setType(lookBehind.getLexeme());
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//17. ArraySizeList -> ArraySize ArraySizeList | epsilon
	private boolean ArraySizeList(){
		
		boolean success = skipErrors(nt.ArraySizeList);
		
		if (lookAhead.inRHS1(nt.ArraySizeList)){
			
			if (ArraySize() && ArraySizeList())
			{
				parseInfo += "*ArraySizeList ->  ArraySize ArraySizeList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.ArraySizeList)){
			parseInfo += "*ArraySize -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	//18. Statement -> Variable = Expr ; | Statement2
	private boolean Statement(Table table){
		
		boolean success = skipErrors(nt.Statement);
		Type variable1 = new Type();
		Type expr = new Type();
		
		if (lookAhead.inRHS1(nt.Statement)){
			//case: Statement -> Statement2
			if (Statement2(table)){
				parseInfo += "*Statement -> Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.Statement)){
			//case: Statement -> Variable = Expr ; 
			lineNum = lookAhead.getLine();
			if (Variable(table, variable1) && match("assignop") && Expr(table, expr) && match("semi")){
				parseInfo += "*Statement -> Variable = Expr ; " + "\r\n";
				
				checkType(variable, expr, lineNum);

			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//19. Statement2 ->  if ( Expr ) then StatBlock else StatBlock ; 
	//					| for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; Variable = Expr ) StatBlock ; 
	//					| get ( Variable ) ; 
	//					| put ( Expr ) ; 
	//					| return ( Expr ) ;
	private boolean Statement2(Table table){
		
		boolean success = skipErrors(nt.Statement2);
		Type expr = new Type();
		
		
		// if ( Expr ) then StatBlock else StatBlock ;
		if (lookAhead.inRHS1(nt.Statement2)){
			if (match("if") && match("openpar") && Expr(table, expr) && match("closepar") 
					&& match("then") && StatBlock(table) && match("else") && StatBlock(table) && match("semi"))
			{
				parseInfo += "*Statement ->  if ( Expr ) then StatBlock else StatBlock ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		// | for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; Variable = Expr ) StatBlock ; 
		else if (lookAhead.inRHS2(nt.Statement2)){
			
			//Record record = new Record();
			
			boolean b1 = match("for");
			boolean b2 = match("openpar");
			//must also check type here
			Type type = new Type();
			boolean b3 = Type(type);
			boolean b4 = match("id");
			
			
			boolean b5 = match("assignop");
			boolean b6 = Expr(table, expr);
			boolean b7 = match("semi");
			Type variable1 = new Type();
			
			//TESTING MODIFICATIONS ONLY
			Type arithExpr = new Type();
			Type arithExpr_2 = new Type();
			
			boolean b8 = ArithExpr(table, arithExpr);
			boolean b9 = RelOp();
			//TESTING MODIFICATIONS ONLY
			boolean b10 = ArithExpr(table, arithExpr_2);
			lineNum = lookBehind.getLine();
			Type t = checkType(arithExpr, arithExpr_2, lineNum);
			
			boolean b11 = match("semi");
			boolean b12 = Variable(table, variable1);
			boolean b13 = match("assignop");
			boolean b14 = Expr(table, expr);
			lineNum = lookBehind.getLine();
			Type t1 = checkType(variable, expr, lineNum);
			
			boolean b15 = match("closepar");
			boolean b16 = StatBlock(table);
			boolean b17 = match("semi");
			
			if (b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9 && b10
					&& b11 && b12 && b13 && b14 && b15 && b16 && b17)
			{
				parseInfo += "*Statement -> for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; " +
						"Variable = Expr ) StatBlock ;" + "\r\n";
				

				
				if(t.getType().equals("ERROR")){
					
				}
			}
			else {
				success = false;
			}
		}
		//| get ( Variable ) ; 
		else if (lookAhead.inRHS3(nt.Statement2)){
			Type variable1 = new Type();
			if (match("get") && match("openpar") && Variable(table, variable1) && match("closepar") && match("semi"))
			{
				parseInfo += "*Statement -> get ( Variable ) ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		
		//| put ( Expr ) ;
		else if (lookAhead.inRHS4(nt.Statement2)){
			if (match("put") && match("openpar") && Expr(table, expr) && match("closepar") && match("semi"))
			{
				parseInfo += "*Statement -> put ( Expr ) ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		
		//| return ( Expr ) ; 
		else if (lookAhead.inRHS5(nt.Statement2)){
			if (match("return") && match("openpar") && Expr(table, expr) && match("closepar") && match("semi"))
			{
				//check type here
				lineNum = lookBehind.getLine();
				// FUNCTION RETURN TYPE MATCH
				functionReturnCheck(expr);
				
				parseInfo += "*Statement -> return ( Expr ) ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		return success;
	}
	
	//20. StatBlock -> { StatementList } | Statement | epsilon
	private boolean StatBlock(Table table){

		boolean success = skipErrors(nt.StatBlock);
		
		if (lookAhead.inRHS1(nt.StatBlock)){
			if (Statement(table))
			{
				parseInfo += "*StatBlock -> Statement" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.StatBlock)){
			if (match("opencur") && StatementList(table) && match("closecur"))
			{
				parseInfo += "*StatBlock -> { StatementList }" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.StatBlock)){
				parseInfo += "*Statblock -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//21. StatementList -> Statement StatementList | epsilon
	private boolean StatementList(Table table){
		
		boolean success = skipErrors(nt.StatementList);
		
		if (lookAhead.inRHS1(nt.StatementList)){
			if (Statement(table) && StatementList(table))
			{
				parseInfo += "*StatementList -> Statement StatementList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.StatementList)){
			parseInfo += "*StatementList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}

	//22. Expr -> ArithExpr Expr2
	private boolean Expr(Table table, Type expr){
		
		boolean success = skipErrors(nt.Expr);
		Type arithExpr = new Type();
		Type expr2 = new Type();
		
		if (lookAhead.inRHS1(nt.Expr)){
			if (ArithExpr(table, arithExpr) && Expr2(table, arithExpr, expr2))
			{
				parseInfo += "*Expr -> ArithExpr Expr2" + "\r\n";
				expr.setTo(expr2);
				
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//23. Expr2 -> RelOp ArithExpr | epsilon
	private boolean Expr2(Table table, Type arithExpr, Type expr2){
		
		boolean success = skipErrors(nt.Expr2);
		Type arithExpr_2 = new Type();
		
		if (lookAhead.inRHS1(nt.Expr2)){
			if (RelOp() && ArithExpr(table, arithExpr_2))
			{
				parseInfo += "*Expr2 -> RelOp ArithExpr" + "\r\n";
				
				lineNum = lookBehind.getLine();
				checkType(arithExpr, arithExpr_2, lineNum);
				expr2.setTo(arithExpr_2);
				
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.Expr2)){
			parseInfo += "*Expr2 -> epsilon" + "\r\n";
			expr2.setTo(arithExpr);	
			
		}
		else {
			success = false;
		}
		return success;
	}

	//24. ArithExpr -> Term ArithExpr2
	private boolean ArithExpr(Table table, Type arithExpr){
		
		Type term = new Type();
		Type arithExpr2 = new Type();
		
		boolean success = skipErrors(nt.ArithExpr);
		
		if (lookAhead.inRHS1(nt.ArithExpr)){
			boolean b1 = Term(table, term);
			boolean b2 = ArithExpr2(table, term, arithExpr2);
			
			if (b1 && b2){
				parseInfo += "*ArithExpr -> Term ArithExpr2" + "\r\n";
				arithExpr.setTo(arithExpr2);
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//25. ArithExpr2 -> AddOp Term ArithExpr2 | epsilon
	private boolean ArithExpr2(Table table, Type term, Type arithExpr2){

		boolean success = skipErrors(nt.ArithExpr2);
		Type term_2 = new Type();
		Type arithExpr2_2 = new Type();
		
		if (lookAhead.inRHS1(nt.ArithExpr2)){
			if (AddOp() && Term(table, term_2) && ArithExpr2(table, term_2, arithExpr2_2)){
				parseInfo += "*ArithExpr2 -> AddOp Term ArithExpr2" + "\r\n";
				
				lineNum = lookBehind.getLine();
				Type t = checkType(term, arithExpr2_2, lineNum);
				//TEST
				arithExpr2.setTo(term_2);
				
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.ArithExpr2)){
			parseInfo += "*ArithExpr2 -> epsilon" + "\r\n";
			arithExpr2.setTo(term);
		}
		else {
			success = false;
		}
		return success;
	}

	//26. Sign -> + | -
	private boolean Sign(){
		
		boolean success = skipErrors(nt.Sign);
		if (lookAhead.inRHS1(nt.Sign)){
			if (match("add"))
			{
				parseInfo += "*Sign -> + " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.Sign)){
			if (match("sub"))
			{
				parseInfo += "*Sign -> - " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	
	//27. Term -> Factor Term2
	private boolean Term(Table table, Type term){
		
		boolean success = skipErrors(nt.Term);
		Type factor = new Type();
		Type term2 = new Type();
		
		if (lookAhead.inRHS1(nt.Term)){
			if (Factor(table, factor) && Term2(table, factor, term2)){
				parseInfo += "*Term -> Factor Term2" + "\r\n";
				term.setTo(term2);
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}

	//28. Term2 -> MultOp Factor Term2 | epsilon
	private boolean Term2(Table table, Type factor, Type term2){
		
		boolean success = skipErrors(nt.Term2);
		Type factor_2 = new Type();
		Type term2_2 = new Type();

		if (lookAhead.inRHS1(nt.Term2)){
			if (MultOp() && Factor(table, factor_2) && Term2(table, factor_2, term2_2)){
				parseInfo += "*Term2 -> MultOp Factor Term2 " + "\r\n";
				lineNum = lookBehind.getLine();
				Type t = checkType(factor, term2_2, lineNum);
				//TEST
				term2.setTo(factor_2);
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.Term2)){
			parseInfo += "*Term2 -> epsilon" + "\r\n";
			term2.setTo(factor);		
		}
		else {
			success = false;
		}
		return success;
	}
	
	//29. Factor -> id IndiceList IdnestList Factor2 | num | ( ArithExpr ) | not Factor | Sign Factor
	private boolean Factor(Table table, Type factor){

		Table tempTable = table;
		boolean success = skipErrors(nt.Factor);
		Type indiceList = new Type();
		Type idnestList = new Type();
		Type factor2 = new Type();
		Type arithExpr = new Type();
		//arithExpr.setTo(factor);
		Type factor_2 = new Type();
		
		//| ( ArithExpr )
		if (lookAhead.inRHS1(nt.Factor)){
			if (match("openpar") && ArithExpr(table, arithExpr) && match("closepar"))
			{
				parseInfo += "*Factor -> ( ArithExpr )" + "\r\n";
				factor.setTo(arithExpr);
				
			}
			else {
				success = false;
			}
		}

		//Factor -> id IndiceList IdnestList Factor2
		else if (lookAhead.inRHS2(nt.Factor)){
			
			//COME BACK
			Record call = new Record();
			
			boolean b1 = match("id");
			
			call.setName(lookBehind.getLexeme());
			lineNum = lookBehind.getLine();
			factor.setTo(getType(call, table, lineNum));
			
			
			boolean b2 = IndiceList(call, table, factor, indiceList);
			
			//Utility
			lastIdnest = call;

			boolean b3 = IdnestList(call, tempTable, indiceList, idnestList);
			boolean b4 = Factor2(table, idnestList, factor2);
			
			if (b1 && b2 && b3 && b4)
			{
				parseInfo += "*Factor -> id IndiceList IdnestList Factor2" + "\r\n";
				factor.setTo(factor2);
			}
			else {
				success = false;
			}
		}
		//| num or INT
		else if (lookAhead.inRHS3(nt.Factor)){
			if (match("num"))
			{
				parseInfo += "*Factor -> num" + "\r\n";
				
				if (lookBehind.getType().equals("INT")){
					factor.setType("int");
				}
				else {
					factor.setType("float");
				}
			}
			else {
				success = false;
			}

		}
		//| not Factor
		else if (lookAhead.inRHS4(nt.Factor)){
			if (match("t_not") && Factor(table, factor_2))
			{
				parseInfo += "*Factor -> not Factor" + "\r\n";
				factor.setTo(factor_2);
			}
			else {
				success = false;
			}
		}
		//| Sign Factor
		else if (lookAhead.inRHS5(nt.Factor)){
			if (Sign() && Factor(table, factor_2))
			{
				parseInfo += "*Factor -> Sign Factor " + "\r\n";
				factor.setTo(factor_2);
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}

	//TYPE CHECKING
	//30. Factor2 -> ( AParams ) | epsilon
	private boolean Factor2(Table table, Type idnestList, Type factor2){

		boolean success = skipErrors(nt.Factor2);
		//Type aparams = new Type();
		
		if (lookAhead.inRHS1(nt.Factor2)){
			
			lastIdnest.setStructure("function");

			
			Record tempLastIdnest = lastIdnest;
			
			//get AParams to return a set number of parameters
			if (match("openpar") && AParams(lastIdnest, table) && match("closepar")){
				Record r;
				//re-evaluate type
				lineNum = lookBehind.getLine();
				if(lastClassScope != null){
					
					r = lastClassScope.find(tempLastIdnest, true, lineNum);
					factor2.setTo(getType(tempLastIdnest, lastClassScope, lineNum));
				}
				else {
					r = table.find(lastIdnest, true, lineNum);
					factor2.setTo(getType(lastIdnest, table, lineNum));
				}
				
				idnestList.setTo(factor2);
				if(r != null){
					lineNum = lookBehind.getLine();
					checkParameters(r, lineNum);
				}
				functionCallParams.clear();
				
				parseInfo += "*Factor2 -> ( AParams )" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.Factor2)){
			lineNum = lookBehind.getLine();
			if(table.find(lastIdnest, true, lineNum) != null){
				
			}
			
			parseInfo += "*Factor2 -> epsilon" + "\r\n";
			factor2.setTo(idnestList);
			
		}
		else {
			success = false;
		}
		return success;
	}
	
	//31. IdnestList -> . id IndiceList IdnestList | epsilon
	private boolean IdnestList(Record call, Table scope, Type indiceList, Type idnestList){
		
		//call: variable utility
		boolean success = skipErrors(nt.IdnestList);
		Type indiceList_2 = new Type();
		Type idnestList_2 = new Type();
		
		if (lookAhead.inRHS1(nt.IdnestList)){
			
			boolean b1 = match("dot");
			
			Record newCall = new Record();
			
			//get table for class
			lineNum = lookBehind.getLine();
			scope = scope.find(call, true, lineNum).getClassLocal();
			lastClassScope = scope;
			
			//new record: function
			boolean b2 = match("id");
			newCall.setName(lookBehind.getLexeme());
			newCall.setStructure("function");
			
			//get previously stored record for function
			lineNum = lookBehind.getLine();
			
			call = scope.find(newCall, false, lineNum);
			
			idnestList.setTo(getType(call, lastClassScope, lineNum));
			
			boolean b3 = IndiceList(call, scope, idnestList, indiceList_2);	
			
			lastIdnest = newCall;
			
			boolean b4 = IdnestList(call, scope, indiceList_2, idnestList_2);
			
			if (b1 && b2 && b3 && b4){
				parseInfo += "*IdnestList -> . id IndiceList IdnestList " + "\r\n";
				idnestList.setTo(idnestList_2);
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.IdnestList)){
			parseInfo += "*IdnestList -> epsilon" + "\r\n";
			idnestList.setTo(indiceList);
		}
		else {
			success = false;
		}
		return success;
	}
	
	// IdnestList2 -> . id IndiceList IdnestList2 | epsilon
	private boolean IdnestList2(Record call, Table scope, Type indiceList, Type idnestList2){
		
		boolean success = skipErrors(nt.IdnestList);
		Type indiceList_2 = new Type();
		Type idnestList2_2 = new Type();
		
		if (lookAhead.inRHS1(nt.IdnestList)){
			
			Record newCall = new Record();
			
			boolean b1 = match("dot");
			lineNum = lookBehind.getLine();
			scope = scope.find(call, true, lineNum).getClassLocal();
			
			boolean b2 = match("id");
			newCall.setName(lookBehind.getLexeme());
			lineNum = lookBehind.getLine();
			call = scope.find(newCall, true, lineNum);
			
			idnestList2.setTo(getType(call, scope, lineNum));
			
			//TESTING MODIFICATION
			boolean b3 = IndiceList(call, scope, idnestList2, indiceList_2);			
			boolean b4 = IdnestList2(call, scope, indiceList_2, idnestList2_2);
			
			if (b1 && b2 && b3 && b4){
				parseInfo += "*IdnestList -> . id IndiceList IdnestList " + "\r\n";
				idnestList2.setTo(idnestList2_2);
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.IdnestList)){
			scope.findScope(call); //insert array dimension check
			parseInfo += "*IdnestList -> epsilon" + "\r\n";
			idnestList2.setTo(indiceList);
		}
		else {
			success = false;
		}
		return success;
	}
	
	//32. Variable -> id IndiceList IdnestList2
	private boolean Variable(Table table, Type variable1){

		boolean success = skipErrors(nt.Variable);
		Type indiceList = new Type();
		Type idnestList2 = new Type();
		
		if (lookAhead.inRHS1(nt.Variable)){
			
			Record call = new Record();
			boolean b1 = match("id");

			call.setName(lookBehind.getLexeme());
			lineNum = lookBehind.getLine();
			variable1 = getType(call, table, lineNum);

			
			//TESTING MODIFICATION
			boolean b2 = IndiceList(call, table, variable1, indiceList);
			
			boolean b3 = IdnestList2(call, table, indiceList, idnestList2);
			lineNum = lookBehind.getLine();
			table.find(call, true, lineNum);
			lineNum = lookAhead.getLine();
			
			if (b1 && b2 && b3){
				parseInfo += "*Variable -> id IndiceList IdnestList" + "\r\n";
				variable1.setTo(idnestList2);
				variable.setTo(variable1);
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//33. IndiceList ->  Indice IndiceList | epsilon
	private boolean IndiceList(Record call, Table table, Type factor, Type indiceList){

		boolean success = skipErrors(nt.IndiceList);
		Type indice = new Type();
		indice.setTo(factor);
		Type indiceList_2 = new Type();
		if (lookAhead.inRHS1(nt.IndiceList)){
			
			call.setVarStructure("array");
			
			if (Indice(call, table, indice) && IndiceList(call, table, indice, indiceList_2))
			{
				parseInfo += "*IndiceList ->  Indice IndiceList " + "\r\n";
				//not sure
				indiceList.setTo(indiceList_2);
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.IndiceList)){
			
			//TEST
			if(call != null){
				if(call.getVarStructure().equals("array")){
					lineNum = lookBehind.getLine();
					table.checkArrayDim(call, lineNum);
				}
				parseInfo += "*IndiceList -> epsilon" + "\r\n";
			}
			parseInfo += "*IndiceList -> epsilon" + "\r\n";
			indiceList.setTo(factor);
			
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	//34. Indice -> [ ArithExpr ] 
	private boolean Indice(Record record, Table table, Type indice){
		
		boolean success = skipErrors(nt.Indice);
		
		Type arithExpr = new Type();
		Type int_type = new Type("int", false);
		
		if (lookAhead.inRHS1(nt.Indice)){
			
			if (match("opensq") && ArithExpr(table, arithExpr) && match("closesq"))
			{
				record.getDimension().add(arrayDim);
				arrayDim = 0;
				indice.setArray(true);
				indice.addDimension('0');
				parseInfo += "*Indice -> [ ArithExpr ]" + "\r\n";
				lineNum = lookBehind.getLine();
				checkType(arithExpr, int_type, lineNum);

			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//35. ArraySize -> [ INT ] 
	private boolean ArraySize(){
		
		boolean success = skipErrors(nt.ArraySize);
		
		if (lookAhead.inRHS1(nt.ArraySize)){
			
			boolean b1 = match("opensq");
			boolean b2 = match("INT");	
			boolean b3 = match("closesq");
			
			if (b1 && b2 && b3)
			{
				parseInfo += "*ArraySize -> [ INT ] " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//36. FParams -> Type id ArraySizeList FParamsTailList | epsilon
	private boolean FParams(Record record){
		
		boolean success = skipErrors(nt.FParams);
		
		if (lookAhead.inRHS1(nt.FParams)){
			
			Record parameter = new Record();
			parameter.setVarKind("parameter");
			
			boolean b1 = Type(new Type());
			parameter.setType(lookBehind.getLexeme());
			
			boolean b2 = match("id");
			boolean b3 = ArraySizeList();

			if (parameter.getVarStructure().equals("array")){
				record.getParams().add(new Type(parameter.getType(), true));
			}
			else {
				record.getParams().add(new Type(parameter.getType(), false));
			}
			
			boolean b4 = FParamsTailList(record);
			
			if (b1 && b2 && b3 && b4){
				parseInfo += "*FParams -> Type id ArraySizeList FParamsTailList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.FParams)){
			parseInfo += "*FParams -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}

	//37. FParamsTailList -> FParamsTail FParamsTailList | epsilon
	private boolean FParamsTailList(Record record){
		
		boolean success = skipErrors(nt.FParamsTailList);
		
		if (lookAhead.inRHS1(nt.FParamsTailList)){
			if (FParamsTail(record) && FParamsTailList(record))
			{
				parseInfo += "*FParamsTailList ->  FParamsTail FParamsTailList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.FParamsTailList)){
			parseInfo += "*FParamsTail -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	//38. AParams -> Expr AParamsTailList | epsilon
	private boolean AParams(Record call, Table table){
		
		boolean success = skipErrors(nt.AParams);
		Type expr = new Type();
		
		if (lookAhead.inRHS1(nt.AParams)){
			
			call.getParams().add(new Type("", false));
			
			boolean b1 = Expr(table, expr);
			if(expr != null){
				functionCallParams.add(expr);
			}
			boolean b2 = AParamsTailList(call, table);
			numParams = call.getParams().size();
			//boolean b2 = AParamsTailList(call, table);
			
			if (b1 && b2){
				parseInfo += "*AParams -> Expr AParamsTailList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.AParams)){
			parseInfo += "*AParams -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//39. AParamsTailList -> AParamsTail AParamsTailList | epsilon
	private boolean AParamsTailList(Record call, Table table){
		
		boolean success = skipErrors(nt.AParamsTailList);
		
		if (lookAhead.inRHS1(nt.AParamsTailList)){
			
			boolean b1 = AParamsTail(call, table);
			boolean b2 = AParamsTailList(call, table);
			
			if (b1 && b2)
			{
				parseInfo += "*AParamsTailList -> AParamsTail AParamsTailList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.AParamsTailList)){
			parseInfo += "*AParamsTailList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	//40. FParamsTail -> , Type id ArraySizeList
	private boolean FParamsTail(Record record){
		
		boolean success = skipErrors(nt.FParamsTail);
		
		if (lookAhead.inRHS1(nt.FParamsTail)){
			
			boolean b1 = match("comma");
			Record parameter = new Record();
			
			boolean b2 = Type(new Type());
			parameter.setType(lookBehind.getLexeme());
			
			boolean b3 = match("id");
			boolean b4 = ArraySizeList();
			
			if (parameter.getVarStructure().equals("array")){
				record.getParams().add(new Type(parameter.getType(), true));
			}
			else {
				record.getParams().add(new Type(parameter.getType(), false));
			}
			
			if (b1 && b2 && b3 && b4)
			{
				parseInfo += "*FParamsTail -> , Type id ArraySizeList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//41. AParamsTail -> , Expr 
	private boolean AParamsTail(Record call, Table table){
		
		boolean success = skipErrors(nt.AParamsTail);
		Type expr = new Type();
		
		if (lookAhead.inRHS1(nt.AParamsTail)){
			
			call.getParams().add(new Type("", false));
			if (match("comma") && Expr(table, expr))
			{
				if(expr != null){
					functionCallParams.add(expr);
				}
				parseInfo += "*AParamsTail -> , Expr" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//42. RelOp -> == | <> | < | > | <= | >=
	private boolean RelOp(){
		
		boolean success = skipErrors(nt.RelOp);
		
		if (lookAhead.inRHS1(nt.RelOp)){
			if (match("relop_e"))
			{
				parseInfo += "*RelOp -> ==" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.RelOp)){
			if (match("lege"))
			{
				parseInfo += "*RelOp -> <>" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.RelOp)){
			if (match("relop_l"))
			{
				parseInfo += "*RelOp -> <" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS4(nt.RelOp)){
			if (match("relop_g"))
			{
				parseInfo += "*RelOp -> >" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS5(nt.RelOp)){
			if (match("relop_le"))
			{
				parseInfo += "*RelOp -> <=" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS6(nt.RelOp)){
			if (match("relop_ge"))
			{
				parseInfo += "*RelOp -> >=" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
	
	//43. AddOp -> + | - | or
	private boolean AddOp(){

		boolean success = skipErrors(nt.AddOp);
		
		if (lookAhead.inRHS1(nt.AddOp)){
			if (match("or"))
			{
				parseInfo += "*AddOp -> or" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.AddOp)){
			if (match("sub"))
			{
				parseInfo += "*AddOp -> -" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.AddOp)){
			if (match("add"))
			{
				parseInfo += "*AddOp -> +" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}

	//44. MultOp -> * | / | and
	private boolean MultOp(){

		boolean success = skipErrors(nt.MultOp);
		
		if (lookAhead.inRHS1(nt.MultOp)){
			if (match("and"))
			{
				parseInfo += "*MultOp -> and" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.MultOp)){
			if (match("div"))
			{
				parseInfo += "*MultOp -> /" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.MultOp)){
			if (match("mul"))
			{
				parseInfo += "*MultOp -> *" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}
}