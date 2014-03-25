import java.util.ArrayList;
public class Parser {

	private String errorMessages="";
	private String tableInfo = "";
	private String parseInfo="";
	private ArrayList<Token> tokenArray = new ArrayList<Token>(); 
	private NonTerminalCollection nt = new NonTerminalCollection();
	private int tokenArrayLocation = 0;
	private Token lookAhead;
	private Token lookBehind;
	private Record lastIdnest;
	private boolean parsedComplete = false;
	private boolean flush = false;

	
	public Parser(Lexer l){
		
		//call parser, returns true or false
	
		while (!l.fileComplete()){
			Token t = l.getNextToken();
			
			if(t != null && !t.getType().equals("error")){
				tokenArray.add(t);
			}
		}
		
		tokenArray.add(new Token("eof", "eof", 0, 0));
		
		boolean parsed = parser();
		
		if (parsed){
			System.out.println("Successful parse!");
			System.out.println("Check parseInfo.txt for derivations.");
			System.out.println();
			if (Table.hasErrors){
				System.out.println("Errors occured during symbol table creation. ");
				System.out.println("Check tableInfo.txt for symbol tables.");
				System.out.println("Check tableErrors.txt for errors.");
			}
			else {
				System.out.println("Successful symbol table creation!");
				System.out.println("Check tableInfo.txt for symbol tables.");
			}
			System.out.println();
		}
		else{
			System.out.println("Errors occured during parsing.");
			System.out.println("Check parseInfo.txt for existing derivations.");
			System.out.println("Check parseErrors.txt for errors.");
			System.out.println();
			if (Table.hasErrors){
				System.out.println("Errors occured during symbol table creation. ");
				System.out.println("Check tableInfo.txt for symbol tables.");
				System.out.println("Check tableErrors.txt for errors.");
			}
			else {
				System.out.println("Successful symbol table creation!");
				System.out.println("Check tableInfo.txt for symbol tables.");
			}
			System.out.println();	
		}
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
		
		Table table = new Table();
		
		lookAhead = nextTokenParser();
		lookBehind = lookAhead;
		
		boolean success;
		if (Prog(table) && match("eof"))
			success = true;
		else
			success = false;
		flush = true;
		tableInfo = table.getSymbolTable();
		table.flushBuffer(flush);
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
				parseInfo += "Prog -> ClassDeclList ProgBody" + "\r\n";
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
				parseInfo += "ClassDeclList -> ClassDecl ClassDeclList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.ClassDeclList)){
			parseInfo += "ClassDeclList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//3. ClassDecl -> class id { ClassMemberDeclList } ; 
	private boolean ClassDecl(Table table){
		
		Record record = new Record();
		
		boolean success = skipErrors(nt.ClassDecl);
		
		if (lookAhead.inRHS1(nt.ClassDecl)){
			
			boolean b1 = match("class");
			record.setType("class");
			record.setStructure("class");
			
			boolean b2 = match("id");
			record.setName(lookBehind.getLexeme());
			
			boolean b3 = match("opencur");
			Table table1 = new Table(table);
			
			boolean b4 = ClassMemberDeclList(table1);
			record.setLocal(table1);
			
			boolean b5 = match("closecur");
			boolean b6 = match("semi");
					
			if (b1 && b2 && b3 && b4 && b5 && b6)
			{
				table.insert(record, parsedComplete, false);
				parseInfo += "ClassDecl -> class id { ClassMemberDeclList } ; " + "\r\n";
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
		
		Record record = new Record();
		
		boolean success = skipErrors(nt.ClassMemberDecl);
		
		if (lookAhead.inRHS1(nt.ClassMemberDecl)){
			
			boolean b1 = Type(record);
			boolean b2 = match("id");
			record.setName(lookBehind.getLexeme());
			
			boolean b3 = ClassMemberDecl2(record, table);
			
			if (b1 && b2 && b3)
			{
				table.insert(record, false, false);
				parseInfo += "ClassMemberDecl -> Type id ClassMemberDecl2" + "\r\n";
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
			if (ArraySizeList(record) && match("semi"))
			{
				parseInfo += "ClassMemberDecl2 -> ArraySizeList ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.ClassMemberDecl2)){
			
			record.setStructure("function");
			Table table1 = new Table(table);
			
			boolean b1 = match("openpar");
			boolean b2 = FParams(record, table1);
			
			boolean b3 = match("closepar");
			boolean b4 = FuncBody(table1);
			
			boolean b5 = match("semi");
			
			record.setLocal(table1);
			
			if (b1 && b2 && b3 && b4 && b5 ){
				parseInfo += "ClassMemberDecl2 -> ( FParams ) FuncBody ;" + "\r\n";
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
				parseInfo += "ClassMemberDeclList -> ClassMemberDecl ClassMemberDeclList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.ClassMemberDeclList)){
			parseInfo += "ClassMemberDeclList -> epsilon" + "\r\n";
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
				parseInfo += "FuncDefList -> FuncDef FuncDefList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.FuncDefList)){
			parseInfo += "FuncDefList -> epsilon" + "\r\n";
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
			
			boolean b1 = match("program");
			Record program = new Record();
			program.setName("program");
			program.setType("function");
			program.setStructure("function");
			Table table1 = new Table(table);
			
			boolean b2 = FuncBody(table1);
			boolean b3 = match("semi");
			boolean b4 = FuncDefList(table);
			program.setLocal(table1);
			
			if (b1 && b2 && b3 && b4)
			{
				table.insert(program, parsedComplete, false);
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
	private boolean FuncHead(Record record, Table table){
		
		boolean success = skipErrors(nt.FuncHead);
		
		if (lookAhead.inRHS1(nt.FuncHead)){
			
			boolean b1 = Type(record);
			boolean b2 = match("id");
			record.setName(lookBehind.getLexeme());
			
			boolean b3 = match("openpar");
			boolean b4 = FParams(record, table);
			boolean b5 = match("closepar");
			
			if (b1 && b2 && b3 && b4 && b5)
			{
				parseInfo += "FuncHead -> Type id ( FParams )" + "\r\n";
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
		
		Record record = new Record();
		Table table1 = new Table(table);
		record.setStructure("function");
		
		boolean success = skipErrors(nt.FuncDef);
		
		if (lookAhead.inRHS1(nt.FuncDef)){
			if (FuncHead(record, table1) && FuncBody(table1) && match("semi")){
				
				record.setLocal(table1);
				table.insert(record, false, false);
				parseInfo += "FuncDef -> FuncHead FuncBody ;" + "\r\n";
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
				parseInfo += "FuncBody -> { FuncBodyMemberList }" + "\r\n";
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
		
		Record record = new Record();
		boolean success = skipErrors(nt.FuncBodyMember);
		
		if (lookAhead.inRHS1(nt.FuncBodyMember)){
			//case: FuncBodyMember -> Statement2
			if (Statement2(table)){
				parseInfo += "FuncBodyMember -> Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.FuncBodyMember)){
			//case: FuncBodyMember -> id FuncBodyMember2
		
			boolean b1 = match("id");
			record.setType(lookBehind.getLexeme());
			
			boolean b2 = FuncBodyMember2(record, table);
			
			if (b1 && b2) {
				parseInfo += "FuncBodyMember -> id FuncBodyMember2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.FuncBodyMember)){
			//case: FuncBodyMember -> float id ArraySizeList ; 
			
			boolean b1 = match("float");
			record.setType("float");
			
			boolean b2 = match("id");
			record.setName(lookBehind.getLexeme());
			
			boolean b3 = ArraySizeList(record);
			boolean b4 = match("semi");
			
			if (b1 && b2 && b3 && b4){
				table.insert(record, parsedComplete, false);
				parseInfo += "FuncBodyMember -> float id ArraySizeList ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS4(nt.FuncBodyMember)){
			//case: FuncBodyMember -> int id ArraySizeList ; 
			
			boolean b1 = match("int");
			record.setType("int");
			
			boolean b2 = match("id");
			record.setName(lookBehind.getLexeme());
			
			boolean b3 = ArraySizeList(record);
			boolean b4 = match("semi");
			
			if (b1 && b2 && b3 && b4){
				table.insert(record, parsedComplete, false);
				parseInfo += "FuncBodyMember -> int id ArraySizeList ;" + "\r\n";
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
	private boolean FuncBodyMember2(Record record, Table table){
		
		boolean success = skipErrors(nt.FuncBodyMember2);
		
		if (lookAhead.inRHS1(nt.FuncBodyMember2)){
			//case: FuncBodyMember2 -> IndiceList FuncBodyMember3
			
			Record call = new Record();
			call.setName(record.getType());
			call.setType(table.getVariableType(call));
			call.setClassLocal(table.getScope(call.getType()).getLocal());
			
			boolean b1 = IndiceList(call, table);
			
			if(parsedComplete){
				table.search(call);
			}
			
			boolean b2 = FuncBodyMember3(call, table);
			
			if (b1 && b2){
				parseInfo += "IndiceList FuncBodyMember3" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.FuncBodyMember2)){
			//case: FuncBodyMember2 -> id ArraySizeList ; 
			
			boolean b1 = match("id");
			record.setName(lookBehind.getLexeme());
			
			boolean b2 = ArraySizeList(record);
			boolean b3 = match("semi");
			
			if (b1 && b2 && b3){
				table.insert(record, parsedComplete, false);
				parseInfo += "FuncBodyMember2 -> id ArraySizeList ; " + "\r\n";
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
	private boolean FuncBodyMember3(Record call, Table table){
		
		boolean success = skipErrors(nt.FuncBodyMember3);
		
		if (lookAhead.inRHS1(nt.FuncBodyMember3)){
			//case: FuncBodyMember3 -> = Expr ;
			if (match("assignop") && Expr(table) && match("semi")){
				parseInfo += "FuncBodyMember3 -> = Expr ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.FuncBodyMember3)){
			//case: FuncBodyMember3 -> . Variable = Expr ;
			Table newScope = new Table();
			boolean b1 = match("dot");
			
			if (parsedComplete && table.search(call)){
				newScope = table.find(call).getLocal();
			}
			
			boolean b2 = Variable(newScope);
			boolean b3 = match("assignop");
			boolean b4 = Expr(table);
			boolean b5 = match("semi");
			
			if (b1 && b2 && b3 && b4 && b5){
				parseInfo += "FuncBodyMember3 -> . Variable = Expr ;" + "\r\n";
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
				parseInfo += "FuncBodyMemberList -> FuncBodyMember FuncBodyMemberList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.FuncBodyMemberList)){
			parseInfo += "FuncBodyMemberList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//16. Type -> int | float | id
	private boolean Type(Record record){
		
		boolean success = skipErrors(nt.Type);
		
		if (lookAhead.inRHS1(nt.Type)){
			if (match("float"))
			{
				record.setType("float");
				parseInfo += "Type -> float" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.Type)){
			if (match("int"))
			{
				record.setType("int");
				parseInfo += "Type -> int" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.Type)){
			if (match("id"))
			{
				record.setType(lookBehind.getLexeme());
				parseInfo += "Type -> id" + "\r\n";
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
	private boolean ArraySizeList(Record record){
		
		boolean success = skipErrors(nt.ArraySizeList);
		
		if (lookAhead.inRHS1(nt.ArraySizeList)){
			
			record.setVarStructure("array");
			
			if (ArraySize(record) && ArraySizeList(record))
			{
				parseInfo += "ArraySizeList ->  ArraySize ArraySizeList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.ArraySizeList)){
			parseInfo += "ArraySize -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	//18. Statement -> Variable = Expr ; | Statement2
	private boolean Statement(Table table){
		
		boolean success = skipErrors(nt.Statement);
		
		if (lookAhead.inRHS1(nt.Statement)){
			//case: Statement -> Statement2
			if (Statement2(table)){
				parseInfo += "Statement -> Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.Statement)){
			//case: Statement -> Variable = Expr ; 
			if (Variable(table) && match("assignop") && Expr(table) && match("semi")){
				parseInfo += "Statement -> Variable = Expr ; " + "\r\n";
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
		
		// if ( Expr ) then StatBlock else StatBlock ;
		if (lookAhead.inRHS1(nt.Statement2)){
			if (match("if") && match("openpar") && Expr(table) && match("closepar") 
					&& match("then") && StatBlock(table) && match("else") && StatBlock(table) && match("semi"))
			{
				parseInfo += "Statement ->  if ( Expr ) then StatBlock else StatBlock ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		// | for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; Variable = Expr ) StatBlock ; 
		else if (lookAhead.inRHS2(nt.Statement2)){
			
			Record record = new Record();
			
			boolean b1 = match("for");
			boolean b2 = match("openpar");
			boolean b3 = Type(record);
			boolean b4 = match("id");
			record.setName(lookBehind.getLexeme());
			table.insert(record, false, false);
			
			boolean b5 = match("assignop");
			boolean b6 = Expr(table);
			boolean b7 = match("semi");
			boolean b8 = ArithExpr(table);
			boolean b9 = RelOp();
			boolean b10 = ArithExpr(table);
			boolean b11 = match("semi");
			boolean b12 = Variable(table);
			boolean b13 = match("assignop");
			boolean b14 = Expr(table);
			boolean b15 = match("closepar");
			boolean b16 = StatBlock(table);
			boolean b17 = match("semi");
			
			if (b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9 && b10
					&& b11 && b12 && b13 && b14 && b15 && b16 && b17)
			{
				parseInfo += "Statement -> for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; " +
						"Variable = Expr ) StatBlock ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| get ( Variable ) ; 
		else if (lookAhead.inRHS3(nt.Statement2)){
			if (match("get") && match("openpar") && Variable(table) && match("closepar") && match("semi"))
			{
				parseInfo += "Statement -> get ( Variable ) ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| put ( Expr ) ;
		else if (lookAhead.inRHS4(nt.Statement2)){
			if (match("put") && match("openpar") && Expr(table) && match("closepar") && match("semi"))
			{
				parseInfo += "Statement -> put ( Expr ) ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| return ( Expr ) ; 
		else if (lookAhead.inRHS5(nt.Statement2)){
			if (match("return") && match("openpar") && Expr(table) && match("closepar") && match("semi"))
			{
				parseInfo += "Statement -> return ( Expr ) ;" + "\r\n";
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
				parseInfo += "StatBlock -> Statement" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.StatBlock)){
			if (match("opencur") && StatementList(table) && match("closecur"))
			{
				parseInfo += "StatBlock -> { StatementList }" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.StatBlock)){
				parseInfo += "Statblock -> epsilon" + "\r\n";
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
				parseInfo += "StatementList -> Statement StatementList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.StatementList)){
			parseInfo += "StatementList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}

	//22. Expr -> ArithExpr Expr2
	private boolean Expr(Table table){
		
		boolean success = skipErrors(nt.Expr);
		
		if (lookAhead.inRHS1(nt.Expr)){
			if (ArithExpr(table) && Expr2(table))
			{
				parseInfo += "Expr -> ArithExpr Expr2" + "\r\n";
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
	private boolean Expr2(Table table){
	
		boolean success = skipErrors(nt.Expr2);
		
		if (lookAhead.inRHS1(nt.Expr2)){
			if (RelOp() && ArithExpr(table))
			{
				parseInfo += "Expr2 -> RelOp ArithExpr" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.Expr2)){
				parseInfo += "Expr2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}

	//24. ArithExpr -> Term ArithExpr2
	private boolean ArithExpr(Table table){
		
		boolean success = skipErrors(nt.ArithExpr);
		
		if (lookAhead.inRHS1(nt.ArithExpr)){
			if (Term(table) && ArithExpr2(table)){
				parseInfo += "ArithExpr -> Term ArithExpr2" + "\r\n";
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
	private boolean ArithExpr2(Table table){

		boolean success = skipErrors(nt.ArithExpr2);
		
		if (lookAhead.inRHS1(nt.ArithExpr2)){
			if (AddOp() && Term(table) && ArithExpr2(table)){
				parseInfo += "ArithExpr -> Term ArithExpr2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.ArithExpr2)){
			parseInfo += "ArithExpr2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}

	//26. Sign -> + | -
	private boolean Sign(){
		//System.out.println("Calling: Sign -> + | -");
		boolean success = skipErrors(nt.Sign);
		if (lookAhead.inRHS1(nt.Sign)){
			if (match("add"))
			{
				parseInfo += "Sign -> + " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.Sign)){
			if (match("sub"))
			{
				parseInfo += "Sign -> - " + "\r\n";
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
	private boolean Term(Table table){
		
		boolean success = skipErrors(nt.Term);
		
		if (lookAhead.inRHS1(nt.Term)){
			if (Factor(table) && Term2(table)){
				parseInfo += "Term -> Factor Term2" + "\r\n";
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
	private boolean Term2(Table table){
		
		boolean success = skipErrors(nt.Term2);
		
		if (lookAhead.inRHS1(nt.Term2)){
			if (MultOp() && Factor(table) && Term2(table)){
				parseInfo += "Term2 -> MultOp Factor Term2 " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.Term2)){
			parseInfo += "Term2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//29. Factor -> id IndiceList IdnestList Factor2 | num | ( ArithExpr ) | not Factor | Sign Factor
	private boolean Factor(Table table){

		boolean success = skipErrors(nt.Factor);
		
		//| ( ArithExpr )
		if (lookAhead.inRHS1(nt.Factor)){
			if (match("openpar") && ArithExpr(table) && match("closepar"))
			{
				parseInfo += "Factor -> ( ArithExpr )" + "\r\n";
			}
			else {
				success = false;
			}
		}

		//Factor -> id IndiceList IdnestList Factor2
		else if (lookAhead.inRHS2(nt.Factor)){
			
			Record call = new Record();
			boolean b1 = match("id");
			call.setName(lookBehind.getLexeme());
			
			boolean b2 = IndiceList(call, table);
			lastIdnest = call;
			
			if(parsedComplete) {
			 	table.search(call);
			}
			
			boolean b3 = IdnestList(call, table);
			boolean b4 = Factor2(table);
			
			if (b1 && b2 && b3 && b4)
			{
				parseInfo += "Factor -> id IndiceList IdnestList Factor2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| num or INT
		else if (lookAhead.inRHS3(nt.Factor)){
			if (match("num"))
			{
				parseInfo += "Factor -> num" + "\r\n";
			}
			else {
				success = false;
			}

		}
		//| not Factor
		else if (lookAhead.inRHS4(nt.Factor)){
			if (match("t_not") && Factor(table))
			{
				parseInfo += "Factor -> not Factor" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| Sign Factor
		else if (lookAhead.inRHS5(nt.Factor)){
			if (Sign() && Factor(table))
			{
				parseInfo += "Factor -> Sign Factor " + "\r\n";
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

	
	//30. Factor2 -> ( AParams ) | epsilon
	private boolean Factor2(Table table){

		boolean success = skipErrors(nt.Factor2);
		
		if (lookAhead.inRHS1(nt.Factor2)){
			
			lastIdnest.setStructure("function");
			
			if(parsedComplete){
				table.search(lastIdnest);
			}
					
			if (match("openpar") && AParams(lastIdnest, table) && match("closepar")){
				parseInfo += "Factor2 -> ( AParams )" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.Factor2)){
			
			if(parsedComplete){
				table.search(lastIdnest);
			}
			
			lastIdnest = null;
			
			parseInfo += "Factor2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//31. IdnestList -> . id IndiceList IdnestList | epsilon
	private boolean IdnestList(Record call, Table table){
		Table newScope = new Table();
		//System.out.println("Calling: IdnestList -> . id IndiceList IdnestList | epsilon");
		boolean success = skipErrors(nt.IdnestList);
		if (lookAhead.inRHS1(nt.IdnestList)){
			
			boolean b1 = match("dot");
			
			if (parsedComplete && table.search(call)){
				newScope = table.find(call).getLocal();
			}
			
			Record newCall = new Record();
			
			boolean b2 = match("id");
			newCall.setName(lookBehind.getLexeme());
			
			boolean b3 = IndiceList(newCall, newScope);
			lastIdnest = newCall;
			
			boolean b4 = IdnestList(newCall, newScope);
			
			if (b1 && b2 && b3 && b4){
				parseInfo += "IdnestList -> . id IndiceList IdnestList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.IdnestList)){
			parseInfo += "IdnestList -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	private boolean IdnestList2(Record call, Table table){
		
		Table newScope = new Table();
		boolean success = skipErrors(nt.IdnestList);
		
		if (lookAhead.inRHS1(nt.IdnestList)){
			
			boolean b1 = match("dot");
			
			if (parsedComplete && table.search(call)){
				newScope = table.find(call).getLocal();
			}
			
			Record newCall = new Record();
			
			boolean b2 = match("id");
			newCall.setName(lookBehind.getLexeme());
			
			boolean b3 = IndiceList(newCall, newScope);
			
			if(parsedComplete){
				newScope.search(newCall);
			}
			
			boolean b4 = IdnestList2(newCall, newScope);
			
			if (b1 && b2 && b3 && b4){
				parseInfo += "IdnestList2 -> . id IndiceList IdnestList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.IdnestList)){
			parseInfo += "IdnestList2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
		
	}
	//32. Variable -> id IndiceList IdnestList2
	private boolean Variable(Table table){

		boolean success = skipErrors(nt.Variable);
		
		if (lookAhead.inRHS1(nt.Variable)){
			
			Record call = new Record();
			
			boolean b1 = match("id");
			call.setName(lookBehind.getLexeme());
			
			boolean b2 = IndiceList(call, table);
			boolean b3 = IdnestList2(call, table);
			
			if (b1 && b2 && b3){
				parseInfo += "Variable -> id IndiceList IdnestList2" + "\r\n";
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
	private boolean IndiceList(Record call, Table table){

		boolean success = skipErrors(nt.IndiceList);
		
		if (lookAhead.inRHS1(nt.IndiceList)){
			
			call.setVarStructure("array");
			
			if (Indice(call, table) && IndiceList(call, table))
			{
				parseInfo += "IndiceList ->  Indice IndiceList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.IndiceList)){
			parseInfo += "Indice -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	//34. Indice -> [ ArithExpr ] 
	private boolean Indice(Record call, Table table){
		
		boolean success = skipErrors(nt.Indice);
		
		if (lookAhead.inRHS1(nt.Indice)){
			
			if (match("opensq") && ArithExpr(table) && match("closesq"))
			{
				call.getDimension().add(1);
				parseInfo += "Indice -> [ ArithExpr ]" + "\r\n";
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
	private boolean ArraySize(Record record){
		
		boolean success = skipErrors(nt.ArraySize);
		
		if (lookAhead.inRHS1(nt.ArraySize)){
			
			boolean b1 = match("opensq");
			boolean b2 = match("INT");
			record.getDimension().add(Integer.parseInt(lookBehind.getLexeme()));
			
			boolean b3 = match("closesq");
			
			if (b1 && b2 && b3)
			{
				parseInfo += "ArraySize -> [ INT ] " + "\r\n";
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
	private boolean FParams(Record record, Table table){
		
		Record newRecord = new Record();
		newRecord.setVarKind("parameter");
		
		boolean success = skipErrors(nt.FParams);
		if (lookAhead.inRHS1(nt.FParams)){
			
			boolean b1 = Type(newRecord);
			boolean b2 = match("id");
			newRecord.setName(lookBehind.getLexeme());
			newRecord.setVarKind("parameter");
			table.insert(newRecord, parsedComplete, false);
			record.getParams().add(newRecord.getType());
			
			boolean b3 = ArraySizeList(newRecord);
			boolean b4 = FParamsTailList(record, table);
			
			if (b1 && b2 && b3 && b4){
				parseInfo += "FParams -> Type id ArraySizeList FParamsTailList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.FParams)){
			parseInfo += "FParams -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}

	//37. FParamsTailList -> FParamsTail FParamsTailList | epsilon
	private boolean FParamsTailList(Record record, Table table){
		
		boolean success = skipErrors(nt.FParamsTailList);
		
		if (lookAhead.inRHS1(nt.FParamsTailList)){
			if (FParamsTail(record, table) && FParamsTailList(record, table))
			{
				parseInfo += "FParamsTailList ->  FParamsTail FParamsTailList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.FParamsTailList)){
			parseInfo += "FParamsTail -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	//38. AParams -> Expr AParamsTailList | epsilon
	private boolean AParams(Record function, Table table){
		
		boolean success = skipErrors(nt.AParams);
		
		if (lookAhead.inRHS1(nt.AParams)){
			
			boolean b1 = Expr(table);
			function.getParams().add("");
			
			boolean b2 = AParamsTailList(function, table);
			
			if (b1 && b2){
				parseInfo += "AParams -> Expr AParamsTailList" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.AParams)){
			parseInfo += "AParams -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//39. AParamsTailList -> AParamsTail AParamsTailList | epsilon
	private boolean AParamsTailList(Record function, Table table){
		
		boolean success = skipErrors(nt.AParamsTailList);
		
		if (lookAhead.inRHS1(nt.AParamsTailList)){
			
			boolean b1 = AParamsTail(table);
			function.getParams().add("");
			
			boolean b2 = AParamsTailList(function, table);
			
			if (b1 && b2)
			{
				parseInfo += "AParamsTailList -> AParamsTail AParamsTailList " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inFollow(nt.AParamsTailList)){
			parseInfo += "AParamsTail -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	//40. FParamsTail -> , Type id ArraySizeList
	private boolean FParamsTail(Record record, Table table){
		
		Record newRecord = new Record();
		newRecord.setVarKind("parameter");
		
		boolean success = skipErrors(nt.FParamsTail);
		
		if (lookAhead.inRHS1(nt.FParamsTail)){
			
			boolean b1 = match("comma");
			boolean b2 = Type(newRecord);
			boolean b3 = match("id");
			newRecord.setName(lookBehind.getLexeme());
			record.getParams().add(newRecord.getType());
			
			boolean b4 = ArraySizeList(newRecord);
			
			if (b1 && b2 && b3 && b4)
			{
				table.insert(newRecord,  parsedComplete, false);
				parseInfo += "FParamsTail -> , Type id ArraySizeList" + "\r\n";
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
	private boolean AParamsTail(Table table){
		
		boolean success = skipErrors(nt.AParamsTail);
		
		if (lookAhead.inRHS1(nt.AParamsTail)){
			if (match("comma") && Expr(table))
			{
				parseInfo += "AParamsTail -> , Expr" + "\r\n";
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
				parseInfo += "RelOp -> ==" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.RelOp)){
			if (match("lege"))
			{
				parseInfo += "RelOp -> <>" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.RelOp)){
			if (match("relop_l"))
			{
				parseInfo += "RelOp -> <" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS4(nt.RelOp)){
			if (match("relop_g"))
			{
				parseInfo += "RelOp -> >" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS5(nt.RelOp)){
			if (match("relop_le"))
			{
				parseInfo += "RelOp -> <=" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS6(nt.RelOp)){
			if (match("relop_ge"))
			{
				parseInfo += "RelOp -> >=" + "\r\n";
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
				parseInfo += "AddOp -> or" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.AddOp)){
			if (match("sub"))
			{
				parseInfo += "AddOp -> -" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.AddOp)){
			if (match("add"))
			{
				parseInfo += "AddOp -> +" + "\r\n";
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
				parseInfo += "MultOp -> and" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS2(nt.MultOp)){
			if (match("div"))
			{
				parseInfo += "MultOp -> /" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.inRHS3(nt.MultOp)){
			if (match("mul"))
			{
				parseInfo += "MultOp -> *" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else {
			success = false;
			System.out.println("In MultOp(), did not pass: lookAhead.getType().equals(...)");
		}
		return success;
	}
}