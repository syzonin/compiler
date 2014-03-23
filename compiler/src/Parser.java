import java.util.ArrayList;
public class Parser {

	private int currentLocation = 0;
	private String errorMessages="";
	private String parseInfo="";
	private ArrayList<Token> tokenArray = new ArrayList<Token>(); 
	private NonTerminalCollection nt = new NonTerminalCollection();
	private int tokenArrayLocation = 0;
	private Token lookAhead;

	
	public Parser(Lexer l){
		
		/*for (int i = 0; i< tokenArray.size(); i++){
			System.out.println(i);
			System.out.println(tokenArray.get(i));
		} */
		
		//call parser, returns true or false
		
		//INSERT PARSER CODE HERE 
		while (!l.fileComplete()){
			Token t = l.getNextToken();
			
			if(t != null && !t.getType().equals("error")){
				tokenArray.add(t);
			}
		}
		
		tokenArray.add(new Token("eof", "eof", 0, 0));
		
		System.out.println("first lookAhead token: " + tokenArray.get(0));
		
		boolean parsed = parser();
		

		if (parsed)
			System.out.println("successful parse!");
		else
			System.out.println("parse failed.");
	}

	public String getParsedContent(){
		return parseInfo;
	}
	
	//syntactic analyzer
	private boolean parser(){
		
		lookAhead = nextTokenParser();
		System.out.println("This is the lookAhead token: " + lookAhead);
		boolean success;
		if (Prog() && match("eof"))
			success = true;
		else
			success = false;
		return success;
	}
	
	//returns next parser token
	private Token nextTokenParser(){
		Token t = null;
		if (tokenArrayLocation < tokenArray.size() ){
			t = tokenArray.get(tokenArrayLocation);
			tokenArrayLocation++;

			System.out.println("");
			System.out.println("This is the current token: " + t);
		}
		return t;
	}
	
	//Error Handling
	private boolean match(String t) {
		//System.out.println("match lookahead: " + lookAhead.getType() +
		//		", terminal: " + t.getName());
		if (t.equals("num")){
			if (lookAhead.equals(t) || lookAhead.equals("INT") ){
				System.out.println("Terminal Matched! " + t );
				//move to next token
				lookAhead = nextTokenParser();
				return true;
			}
		}
		if (lookAhead.equals(t)){
			System.out.println("Terminal Matched! " + t);
			//move to next token
			lookAhead = nextTokenParser();
			return true;
		}
		else {
			//write ("syntax error at" lookahead.location. "expected" token)
			System.out.println("Syntax error at: " + lookAhead.getLexeme() + ". Expected: " + t);
			lookAhead = nextTokenParser();
			return false;
		}
	}
	
	//Error Handling
	private boolean skipErrors(NonTerminal nt){
		
		//case no error detected:
		if (lookAhead.inUnion(nt)){
			return true;
				
		}
		//case error detected:
		//else
			//write ("syntax error at " lookahead.location)
			//while (lookahead not in nt.first or nt.follow)
				//lookahead = nextToken
			//write ("parsing resumed at " lookahead.location)
			//return false
		else {
			System.out.println("Syntax error at nonterminal: " + lookAhead.getCount() + ". ");
			System.out.println("Expected: " + nt + ", Got: " + lookAhead);
			
			while (!lookAhead.inUnion(nt) && tokenArrayLocation < tokenArray.size() ){
				lookAhead = nextTokenParser();
				System.out.println("Skipping: " + lookAhead.getType() + " and " + lookAhead.getLexeme() + " is not in the first of follow set of " + nt.getSymbol());
			}
			System.out.println("Parsing resumed at: " + lookAhead.getCount() + ".");
			return false;
		} 
	}
	
	//1. Prog -> ClassDeclList ProgBody 
	private boolean Prog(){
		System.out.println("Calling: Prog -> ClassDeclList ProgBody");
		boolean success = skipErrors(nt.Prog);
		if (lookAhead.inRHS1(nt.Prog)){
			if (ClassDeclList() && ProgBody()){
				System.out.println("Prog -> ClassDeclList ProgBody");
				parseInfo += "Prog -> ClassDeclList ProgBody" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Prog(), did not pass: Prog -> ClassDeclList ProgBody");
			}
		}
		else {
			success = false;
			System.out.println("In Prog(), did not pass: lookAhead.inFirst(nt.ClassDeclList)");
		}
		return success;
	}
	
	
	//2. ClassDeclList -> ClassDecl ClassDeclList | epsilon
	private boolean ClassDeclList(){
		System.out.println("Calling: ClassDeclList -> ClassDecl ClassDeclList | epsilon");
		boolean success = skipErrors(nt.ClassDeclList);
		if (lookAhead.inRHS1(nt.ClassDeclList)){
			if (ClassDecl() && ClassDeclList())
			{
				System.out.println("ClassDeclList -> ClassDecl ClassDeclList");
				parseInfo += "ClassDeclList -> ClassDecl ClassDeclList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ClassDeclList(), did not pass: ClassDeclList -> ClassDecl ClassDeclList");
			}
		}
		else if (lookAhead.inFollow(nt.ClassDeclList)){
			System.out.println("ClassDeclList -> epsilon");
			parseInfo += "ClassDeclList -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In ClassDeclList(), did not pass: infirst or infollow");
		}
		return success;
	}
	
	//3. ClassDecl -> class id { ClassMemberDeclList } ; 
	private boolean ClassDecl(){
		System.out.println("Calling: ClassDecl -> class id { ClassMemberDeclList } ; ");
		boolean success = skipErrors(nt.ClassDecl);
		if (lookAhead.inRHS1(nt.ClassDecl)){
			if (match("class") && match("id") && match("opencur") && 
				ClassMemberDeclList() && match("closecur") && match("semi"))
			{
				System.out.println("ClassDecl -> class id { ClassMemberDeclList } ; ");
				parseInfo += "ClassDecl -> class id { ClassMemberDeclList } ; " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ClassDecl(), did not pass: ClassDecl -> class id { ClassMemberDeclList } ; ");
			}
		}
		else {
			success = false;
			System.out.println("In ClassDecl(), did not pass: lookAhead.equals(\"class\")");
		}
		return success;
	}
	
	//4. ClassMemberDecl -> Type id ClassMemberDecl2
	private boolean ClassMemberDecl(){
		System.out.println("Calling: ClassMemberDecl -> Type id ClassMemberDecl2");
		boolean success = skipErrors(nt.ClassMemberDecl);
		if (lookAhead.inRHS1(nt.ClassMemberDecl)){
			if (Type() && match("id") && ClassMemberDecl2())
			{
				System.out.println("ClassMemberDecl -> Type id ClassMemberDecl2");
				parseInfo += "ClassMemberDecl -> Type id ClassMemberDecl2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ClassMemberDecl(), did not pass: ClassMemberDecl -> Type id ClassMemberDecl2");
			}
		}
		else {
			success = false;
			System.out.println("In ClassMemberDecl(), did not pass: lookAhead.inFirst(nt.Type)");
		}
		return success;
	}
	
	//5. ClassMemberDecl2 -> ArraySizeList ; | ( FParams ) FuncBody ;
	private boolean ClassMemberDecl2(){
		System.out.println("Calling: ClassMemberDecl2 -> ArraySizeList ; | ( FParams ) FuncBody ;");
		boolean success = skipErrors(nt.ClassMemberDecl2);
		if (lookAhead.inRHS1(nt.ClassMemberDecl2)){
			if (ArraySizeList() && match("semi"))
			{
				System.out.println("ClassMemberDecl2 -> ArraySizeList ;");
				parseInfo += "ClassMemberDecl2 -> ArraySizeList ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ClassMemberDecl2(), did not pass: ClassMemberDecl2 -> ArraySizeList ;");
			}
		}
		else if (lookAhead.inRHS2(nt.ClassMemberDecl2)){
			if (match("openpar") && FParams() && match("closepar") && FuncBody() && match("semi")){
				System.out.println("ClassMemberDecl2 -> ( FParams ) FuncBody ;");
				parseInfo += "ClassMemberDecl2 -> ( FParams ) FuncBody ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ClassMemberDecl2(), did not pass: ClassMemberDecl2 -> ( FParams ) FuncBody ;");
			}
		}
		else {
			success = false;
			System.out.println("In ClassMemberDecl2(), did not pass: lookAhead.inFirst");
		}
		return success;
	}
	
	//6. ClassMemberDeclList -> ClassMemberDecl ClassMemberDeclList | epsilon
	private boolean ClassMemberDeclList(){
		System.out.println("Calling: ClassMemberDeclList -> ClassMemberDecl ClassMemberDeclList | epsilon");
		boolean success = skipErrors(nt.ClassMemberDeclList);
		if (lookAhead.inRHS1(nt.ClassMemberDeclList)){
			if (ClassMemberDecl() && ClassMemberDeclList())
			{
				System.out.println("ClassMemberDeclList -> ClassMemberDecl ClassMemberDeclList");
				parseInfo += "ClassMemberDeclList -> ClassMemberDecl ClassMemberDeclList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ClassMemberDeclList(), did not pass: ClassMemberDeclList -> ClassMemberDecl ClassMemberDeclList");
			}
		}
		else if (lookAhead.inFollow(nt.ClassMemberDeclList)){
			System.out.println("ClassMemberDeclList -> epsilon");
			parseInfo += "ClassMemberDeclList -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In ClassMemberDeclList(), did not pass: infirst or infollow");
		}
		return success;
	}
	
	//7. FuncDefList -> FuncDef FuncDefList | epsilon
	private boolean FuncDefList(){
		System.out.println("Calling: FuncDefList -> FuncDef FuncDefList | epsilon");
		boolean success = skipErrors(nt.FuncDefList);
		if (lookAhead.inRHS1(nt.FuncDefList)){
			if (FuncDef() && FuncDefList())
			{
				System.out.println("FuncDefList -> FuncDef FuncDefList");
				parseInfo += "FuncDefList -> FuncDef FuncDefList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncDefList(), did not pass: FuncDefList -> FuncDef FuncDefList");
			}
		}
		else if (lookAhead.inFollow(nt.FuncDefList)){
			System.out.println("FuncDefList -> epsilon");
			parseInfo += "FuncDefList -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In FuncDefList(), did not pass: infirst or infollow");
		}
		return success;
	}
	
	//8. ProgBody -> program FuncBody ; FuncDefList
	private boolean ProgBody(){
		System.out.println("Calling: ProgBody -> program FuncBody ; FuncDefList");
		boolean success = skipErrors(nt.ProgBody);
		if (lookAhead.inRHS1(nt.ProgBody)){
			if (match("program") && FuncBody() && match("semi") && FuncDefList())
			{
				System.out.println("ProgBody -> program FuncBody ; FuncDefList");
				parseInfo += "ProgBody -> program FuncBody ; FuncDefList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ProgBody(), did not pass: ProgBody -> program FuncBody ; FuncDefList ");
			}
		}
		else {
			success = false;
			System.out.println("In ProgBody(), did not pass: lookAhead.equals(\"program\") ");
		}
		return success;
	}

	//9. FuncHead -> Type id ( FParams )
	private boolean FuncHead(){
		System.out.println("Calling: FuncHead -> Type id ( FParams )");
		boolean success = skipErrors(nt.FuncHead);
		if (lookAhead.inRHS1(nt.FuncHead)){
			if (Type() && match("id") && match("openpar") && FParams() && match("closepar"))
			{
				System.out.println("FuncHead -> Type id ( FParams )");
				parseInfo += "FuncHead -> Type id ( FParams )" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncHead(), did not pass: FuncHead -> Type id ( FParams ) ");
			}
		}
		else {
			success = false;
			System.out.println("In FuncHead(), did not pass: lookAhead.inFirst(nt.Type) ");
		}
		return success;
	}
	
	//10. FuncDef -> FuncHead FuncBody ; 
	private boolean FuncDef(){
		System.out.println("Calling: FuncDef -> FuncHead FuncBody ;");
		boolean success = skipErrors(nt.FuncDef);
		if (lookAhead.inRHS1(nt.FuncDef)){
			if (FuncHead() && FuncBody() && match("semi")){
				System.out.println("FuncDef -> FuncHead FuncBody ;");
				parseInfo += "FuncDef -> FuncHead FuncBody ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncDef(), did not pass: FuncDef -> FuncHead FuncBody ;");
			}
		}
		else {
			success = false;
			System.out.println("In FuncDef(), did not pass: infirst or infollow");
		}
		return success;
	}
	
	//11. FuncBody -> { FuncBodyMemberList }
	private boolean FuncBody(){
		System.out.println("Calling: FuncBody -> { FuncBodyMemberList }");
		boolean success = skipErrors(nt.FuncBody);
		if (lookAhead.inRHS1(nt.FuncBody)){
			if (match("opencur") && FuncBodyMemberList() && match("closecur"))
			{
				System.out.println("FuncBody -> { FuncBodyMemberList}");
				parseInfo += "FuncBody -> { FuncBodyMemberList }" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBody(), did not pass: FuncBody -> { FuncBodyMemberList }");
			}
		}
		else {
			success = false;
			System.out.println("In FuncBody(), did not pass: lookAhead.equals(\"opencur\")");
		}
		return success;
	}
	
	//12. FuncBodyMember -> int id ArraySizeList ; | float id ArraySizeList ; | id FuncBodyMember2 | Statement2
	private boolean FuncBodyMember(){
		System.out.println("Calling: FuncBodyMember -> int id ArraySizeList ; | float id ArraySizeList ; | id FuncBodyMember2 | Statement2");
		boolean success = skipErrors(nt.FuncBodyMember);
		if (lookAhead.inRHS1(nt.FuncBodyMember)){
			//case: FuncBodyMember -> Statement2
			if (Statement2()){
				System.out.println("FuncBodyMember -> Statement2");
				parseInfo += "FuncBodyMember -> Statement2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember(), did not pass: FuncBodyMember -> Statement2");
			}
		}
		//TEST CASE
		else if (lookAhead.inRHS2(nt.FuncBodyMember)){
			//case: FuncBodyMember -> id FuncBodyMember2
			if (match("id") && FuncBodyMember2()){
				System.out.println("FuncBodyMember -> id FuncBodyMember2");
				parseInfo += "FuncBodyMember -> id FuncBodyMember2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember(), did not pass: FuncBodyMember -> id FuncBodyMember2");
			}
			//case: FuncBodyMember -> FuncBodyMember2
			/*if (FuncBodyMember2()){
				System.out.println("FuncBodyMember -> FuncBodyMember2");
				parseInfo += "FuncBodyMember -> FuncBodyMember2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember(), did not pass: FuncBodyMember -> id FuncBodyMember2");
			}*/
		}
		else if (lookAhead.inRHS3(nt.FuncBodyMember)){
			//case: FuncBodyMember -> float id ArraySizeList ; 
			if (match("float") && match("id") && ArraySizeList() && match("semi")){
				System.out.println("FuncBodyMember -> float id ArraySizeList ;");
				parseInfo += "FuncBodyMember -> float id ArraySizeList ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember(), did not pass: FuncBodyMember -> float id ArraySizeList ;");
			}
		}
		else if (lookAhead.inRHS4(nt.FuncBodyMember)){
			//case: FuncBodyMember -> int id ArraySizeList ; 
			if (match("int") && match("id") && ArraySizeList() && match("semi")){
				System.out.println("FuncBodyMember -> int id ArraySizeList ;");
				parseInfo += "FuncBodyMember -> int id ArraySizeList ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember(), did not pass: FuncBodyMember -> int id ArraySizeList ;");
			}
		}
		else {
			success = false;
			System.out.println("In FuncBodyMember(), did not pass: inFirst");
		}
		return success;
	}
	
	//13. FuncBodyMember2 -> id ArraySizeList ; | IndiceList FuncBodyMember3
	private boolean FuncBodyMember2(){
		System.out.println("Calling: FuncBodyMember2 -> id ArraySizeList ; | IndiceList FuncBodyMember3");
		boolean success = skipErrors(nt.FuncBodyMember2);
		if (lookAhead.inRHS1(nt.FuncBodyMember2)){
			//case: FuncBodyMember2 -> IndiceList FuncBodyMember3
			if (IndiceList() && FuncBodyMember3()){
				System.out.println("IndiceList FuncBodyMember3");
				parseInfo += "IndiceList FuncBodyMember3" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember2(), did not pass: IndiceList FuncBodyMember3");
			}
		}
		else if (lookAhead.inRHS2(nt.FuncBodyMember2)){
			//case: FuncBodyMember2 -> id ArraySizeList ; 
			if (match("id") && ArraySizeList() && match("semi")){
				System.out.println("FuncBodyMember2 -> id ArraySizeList ; ");
				parseInfo += "FuncBodyMember2 -> id ArraySizeList ; " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember2(), did not pass: FuncBodyMember2 -> id ArraySizeList ; ");
			}
		}
		else {
			success = false;
			System.out.println("In FuncBodyMember2(), did not pass: inFirst");
		}
		return success;
	}
	
	//14. FuncBodyMember3 -> . Variable = Expr ; | = Expr ;
	private boolean FuncBodyMember3(){
		System.out.println("Calling: FuncBodyMember3 -> . Variable = Expr ; | = Expr ;");
		boolean success = skipErrors(nt.FuncBodyMember3);
		if (lookAhead.inRHS1(nt.FuncBodyMember3)){
			//case: FuncBodyMember3 -> = Expr ;
			if (match("assignop") && Expr() && match("semi")){
				System.out.println("FuncBodyMember3 -> = Expr ;");
				parseInfo += "FuncBodyMember3 -> = Expr ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember3(), did not pass: FuncBodyMember3 -> = Expr ;");
			}
		}
		else if (lookAhead.inRHS2(nt.FuncBodyMember3)){
			//case: FuncBodyMember3 -> . Variable = Expr ;
			if (match("dot") && Variable() && match("assignop") && Expr() && match("semi")){
				System.out.println("FuncBodyMember3 -> . Variable = Expr ; ");
				parseInfo += "FuncBodyMember3 -> . Variable = Expr ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMember2(), did not pass:FuncBodyMember3 -> . Variable = Expr ;");
			}
		}
		else {
			success = false;
			System.out.println("In FuncBodyMember3(), did not pass: inFirst");
		}
		return success;
	}
	
	//15. FuncBodyMemberList -> FuncBodyMember FuncBodyMemberList | epsilon
	private boolean FuncBodyMemberList(){
		System.out.println("Calling: FuncBodyMemberList -> FuncBodyMember FuncBodyMemberList | epsilon");
		boolean success = skipErrors(nt.FuncBodyMemberList);
		if (lookAhead.inRHS1(nt.FuncBodyMemberList)){
			if (FuncBodyMember() && FuncBodyMemberList())
			{
				System.out.println("FuncBodyMemberList -> FuncBodyMember FuncBodyMemberList");
				parseInfo += "FuncBodyMemberList -> FuncBodyMember FuncBodyMemberList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FuncBodyMemberList(), did not pass: FuncBodyMemberList -> FuncBodyMember FuncBodyMemberList");
			}
		}
		else if (lookAhead.inFollow(nt.FuncBodyMemberList)){
			System.out.println("FuncBodyMemberList -> epsilon");
			parseInfo += "FuncBodyMemberList -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In FuncBodyMemberList(), did not pass: infirst or infollow");
		}
		return success;
	}
	
	//16. Type -> int | float | id
	private boolean Type(){
		System.out.println("Calling: Type -> int | float | id");
		boolean success = skipErrors(nt.Type);
		if (lookAhead.inRHS1(nt.Type)){
			if (match("float"))
			{
				System.out.println("Type -> float");
				parseInfo += "Type -> float" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Type(), did not pass: Type -> float");
			}
		}
		else if (lookAhead.inRHS2(nt.Type)){
			if (match("int"))
			{
				System.out.println("Type -> int");
				parseInfo += "Type -> int" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Type(), did not pass: Type -> int");
			}
		}
		else if (lookAhead.inRHS3(nt.Type)){
			if (match("id"))
			{
				System.out.println("Type -> id");
				parseInfo += "Type -> id" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Type(), did not pass: Type -> id");
			}
		}
		else {
			success = false;
			System.out.println("In Type(), did not pass: match(int, float, id)");
		}
		return success;
	}
	
	//17. ArraySizeList -> ArraySize ArraySizeList | epsilon
	private boolean ArraySizeList(){
		System.out.println("Calling: ArraySizeList ->  ArraySize ArraySizeList | epsilon");
		boolean success = skipErrors(nt.ArraySizeList);
		if (lookAhead.inRHS1(nt.ArraySizeList)){
			if (ArraySize() && ArraySizeList())
			{
				System.out.println("ArraySizeList ->  ArraySize ArraySizeList");
				parseInfo += "ArraySizeList ->  ArraySize ArraySizeList " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ArraySize(), did not pass: ArraySizeList ->  ArraySize ArraySizeList ");
			}
		}
		else if (lookAhead.inFollow(nt.ArraySizeList)){
			System.out.println("ArraySize -> epsilon");
			parseInfo += "ArraySize -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In ArraySizeList(), did not pass: infirst or infollow");
		}
		
		return success;
	}
	
	//18. Statement -> Variable = Expr ; | Statement2
	private boolean Statement(){
		System.out.println("Calling: Statement -> Variable = Expr ; | Statement2");
		boolean success = skipErrors(nt.Statement);
		if (lookAhead.inRHS1(nt.Statement)){
			//case: Statement -> Statement2
			if (Statement2()){
				System.out.println("Statement -> Statement2");
				parseInfo += "Statement -> Statement2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Statement(), did not pass: Statement -> Statement2");
			}
		}
		else if (lookAhead.inRHS2(nt.Statement)){
			//case: Statement -> Variable = Expr ; 
			if (Variable() && match("assignop") && Expr() && match("semi")){
				System.out.println("Statement -> Variable = Expr ; ");
				parseInfo += "Statement -> Variable = Expr ; " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Statement(), did not pass: Statement -> Variable = Expr ; ");
			}
		}
		else {
			success = false;
			System.out.println("In Statement(), did not pass: inFirst");
		}
		return success;
	}
	
	//19. Statement2 ->  if ( Expr ) then StatBlock else StatBlock ; 
	//					| for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; Variable = Expr ) StatBlock ; 
	//					| get ( Variable ) ; 
	//					| put ( Expr ) ; 
	//					| return ( Expr ) ;
	private boolean Statement2(){
		System.out.println("Calling: Statement2 -> ...");
		boolean success = skipErrors(nt.Statement2);
		// | if ( Expr ) then StatBlock else StatBlock ;
		if (lookAhead.inRHS1(nt.Statement2)){
			if (match("if") && match("openpar") && Expr() && match("closepar") 
					&& match("then") && StatBlock() && match("else") && StatBlock() && match("semi"))
			{
				System.out.println("Statement ->  if ( Expr ) then StatBlock else StatBlock ;");
				parseInfo += "Statement ->  if ( Expr ) then StatBlock else StatBlock ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		// | for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; Variable = Expr ) StatBlock ; 
		else if (lookAhead.inRHS2(nt.Statement2)){
			if (match("for") && match("openpar") && Type() && match("id") && 
					match("assignop") && Expr() && match("semi") && ArithExpr() && RelOp() 
					&& ArithExpr() && match("semi") && Variable() && match("assignop") 
					&& Expr() && match("closepar") && StatBlock() && match("semi") )
			{
				System.out.println(" Statement -> for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; " +
						"Variable = Expr ) StatBlock ;");
				parseInfo += "Statement -> for ( Type id = Expr ; ArithExpr RelOp ArithExpr ; " +
						"Variable = Expr ) StatBlock ;" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| get ( Variable ) ; 
		else if (lookAhead.inRHS3(nt.Statement2)){
			if (match("get") && match("openpar") && Variable() && 
					match("closepar") && match("semi"))
			{
				System.out.println("Statement -> get ( Variable ) ;");
				parseInfo += "Statement -> get ( Variable ) ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Statement(), did not pass: get ( Variable ) ; ");
			}
		}
		//| put ( Expr ) ;
		else if (lookAhead.inRHS4(nt.Statement2)){
			if (match("put") && match("openpar") && Expr() && match("closepar") && match("semi"))
			{
				System.out.println("Statement -> put ( Expr ) ; ");
				parseInfo += "Statement -> put ( Expr ) ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Statement(), did not pass: put ( Expr ) ; ");
			}
		}
		//| return ( Expr ) ; 
		else if (lookAhead.inRHS5(nt.Statement2)){
			if (match("return") && match("openpar") && Expr() && match("closepar") && match("semi"))
			{
				System.out.println("Statement -> return ( Expr ) ; ");
				parseInfo += "Statement -> return ( Expr ) ;" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Statement(), did not pass: return ( Expr ) ; ");
			}
		}
		return success;
	}
	
	//20. StatBlock -> { StatementList } | Statement | epsilon
	private boolean StatBlock(){
		System.out.println("Calling: StatBlock -> { StatementList } | Statement | epsilon");
		boolean success = skipErrors(nt.StatBlock);
		if (lookAhead.inRHS1(nt.StatBlock)){
			if (Statement())
			{
				System.out.println("StatBlock -> Statement");
				parseInfo += "StatBlock -> Statement" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In StatBlock(), did not pass:  StatBlock -> Statement");
			}
		}
		else if (lookAhead.inRHS2(nt.StatBlock)){
			if (match("opencur") && StatementList() && match("closecur"))
			{
				System.out.println("StatBlock -> { StatementList }");
				parseInfo += "StatBlock -> { StatementList }" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In StatBlock(), did not pass:  StatBlock -> { StatementList }");
			}
		}
		else if (lookAhead.inFollow(nt.StatBlock)){
				System.out.println("Statblock -> epsilon");
				parseInfo += "Statblock -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In StatBlock(), did not pass:  isInFirst && isInFollow");
		}
		return success;
	}
	
	//21. StatementList -> Statement StatementList | epsilon
	private boolean StatementList(){
		System.out.println("Calling: StatementList -> Statement StatementList | epsilon");
		boolean success = skipErrors(nt.StatementList);
		if (lookAhead.inRHS1(nt.StatementList)){
			if (Statement() && StatementList())
			{
				System.out.println("StatementList -> Statement StatementList");
				parseInfo += "StatementList -> Statement StatementList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In StatementList(), did not pass: Statem" +
						"entList -> Statement StatementList");
			}
		}
		else if (lookAhead.inFollow(nt.StatementList)){
			System.out.println("StatementList -> epsilon");
			parseInfo += "StatementList -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In StatementList(), did not pass: infirst or infollow");
		}
		return success;
	}

	//22. Expr -> ArithExpr Expr2
	private boolean Expr(){
		System.out.println("Calling: Expr -> ArithExpr Expr2");
		boolean success = skipErrors(nt.Expr);
		if (lookAhead.inRHS1(nt.Expr)){
			if (ArithExpr() && Expr2())
			{
				System.out.println("Expr -> ArithExpr Expr2");
				parseInfo += "Expr -> ArithExpr Expr2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Expr(), did not pass:  Expr -> ArithExpr Expr2");
			}
		}
		else {
			success = false;
			System.out.println("In Expr(), did not pass:  isInFirst");
		}
		return success;
	}
	
	//23. Expr2 -> RelOp ArithExpr | epsilon
	private boolean Expr2(){
		System.out.println("Calling: Expr2 -> RelOp ArithExpr | epsilon");
		boolean success = skipErrors(nt.Expr2);
		if (lookAhead.inRHS1(nt.Expr2)){
			if (RelOp() && ArithExpr())
			{
				System.out.println("Expr2 -> RelOp ArithExpr");
				parseInfo += "Expr2 -> RelOp ArithExpr" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Expr2(), did not pass:  Expr2 -> RelOp ArithExpr");
			}
		}
		else if (lookAhead.inFollow(nt.Expr2)){
				System.out.println("Expr2 -> epsilon");
				parseInfo += "Expr2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In Expr2(), did not pass:  isInFirst && isInFollow");
		}
		return success;
	}

	//24. ArithExpr -> Term ArithExpr2
	private boolean ArithExpr(){
		System.out.println("Calling: ArithExpr -> Term ArithExpr2");
		boolean success = skipErrors(nt.ArithExpr);
		if (lookAhead.inRHS1(nt.ArithExpr)){
			if (Term() && ArithExpr2()){
				System.out.println("ArithExpr -> Term ArithExpr2");
				parseInfo += "ArithExpr -> Term ArithExpr2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ArithExpr(), did not pass:  ArithExpr -> Term ArithExpr2");
			}
		}
		else {
			success = false;
			System.out.println("In ArithExpr(), did not pass: lookAhead.inFirst(nt.Term)");
		}
		return success;
	}
	
	//25. ArithExpr2 -> AddOp Term ArithExpr2 | epsilon
	private boolean ArithExpr2(){
		System.out.println("Calling: ArithExpr2 -> AddOp Term ArithExpr2 | epsilon");
		boolean success = skipErrors(nt.ArithExpr2);
		if (lookAhead.inRHS1(nt.ArithExpr2)){
			if (AddOp() && Term() && ArithExpr2()){
				System.out.println("ArithExpr -> Term ArithExpr2");
				parseInfo += "ArithExpr -> Term ArithExpr2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ArithExpr2(), did not pass: ArithExpr2 -> AddOp Term ArithExpr2");
			}
		}
		else if (lookAhead.inFollow(nt.ArithExpr2)){
			System.out.println("ArithExpr2 -> epsilon");
			parseInfo += "ArithExpr2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In ArithExpr2(), did not pass: infirst or infollow");
		}
		return success;
	}

	//26. Sign -> + | -
	private boolean Sign(){
		System.out.println("Calling: Sign -> + | -");
		boolean success = skipErrors(nt.Sign);
		if (lookAhead.inRHS1(nt.Sign)){
			if (match("add"))
			{
				System.out.println("Sign -> + ");
				parseInfo += "Sign -> + " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Sign(), did not pass: Sign -> + ");
			}
		}
		else if (lookAhead.inRHS2(nt.Sign)){
			if (match("sub"))
			{
				System.out.println("Sign -> - ");
				parseInfo += "Sign -> - " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Sign(), did not pass: Sign -> - ");
			}
		}
		else {
			success = false;
			System.out.println("In Sign(), did not pass: match(add) or match(sub)");
		}
		return success;
	}
	
	
	//27. Term -> Factor Term2
	private boolean Term(){
		System.out.println("Calling: Term -> Factor Term2");
		boolean success = skipErrors(nt.Term);
		if (lookAhead.inRHS1(nt.Term)){
			if (Factor() && Term2()){
				System.out.println("Term -> Factor Term2");
				parseInfo += "Term -> Factor Term2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Term(), did not pass: Term -> Factor Term2");
			}
		}
		else {
			success = false;
			System.out.println("In Term(), did not pass:lookAhead.inFirst(nt.Factor)");
		}
		return success;
	}

	//28. Term2 -> MultOp Factor Term2 | epsilon
	private boolean Term2(){
		System.out.println("Calling: Term2 -> MultOp Factor Term2 | epsilon");
		boolean success = skipErrors(nt.Term2);
		if (lookAhead.inRHS1(nt.Term2)){
			if (MultOp() && Factor() && Term2()){
				System.out.println("Term2 -> MultOp Factor Term2 ");
				parseInfo += "Term2 -> MultOp Factor Term2 " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Term2(), did not pass:  Term2 -> MultOp Factor Term2");
			}
		}
		else if (lookAhead.inFollow(nt.Term2)){
			System.out.println("Term2 -> epsilon");
			parseInfo += "Term2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In Term2(), did not pass:  infirst or infollow");
		}
		return success;
	}
	
	//29. Factor -> id IdnestList Factor2 | num | ( ArithExpr ) | not Factor | Sign Factor
	private boolean Factor(){
		System.out.println("Calling: Factor -> id Factor3 Factor2| num | ( ArithExpr ) | not Factor | Sign Factor");
		boolean success = skipErrors(nt.Factor);
		//| ( ArithExpr )
		if (lookAhead.inRHS1(nt.Factor)){
			if (match("openpar") && ArithExpr() && match("closepar"))
			{
				System.out.println("Factor -> ( ArithExpr )");
				parseInfo += "Factor -> ( ArithExpr )" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Factor(), did not pass:  Factor -> ( ArithExpr ) ");
			}
		}
		//Factor -> id IdnestList Factor2
		else if (lookAhead.inRHS2(nt.Factor)){
			if (match("id") && IdnestList() && Factor2())
			{
				System.out.println("Factor -> id IdnestList Factor2");
				parseInfo += "Factor -> id IdnestList Factor2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Factor(), did not pass:  Factor -> id IdnestList Factor2");
			}
		}
		//Factor -> id IndiceList Factor2
		/*else if (lookAhead.inRHS2(nt.Factor)){
			if (match("id") && IndiceList() && Factor2())
			{
				System.out.println("Factor -> id IndiceList Factor2");
				parseInfo += "Factor -> id IndiceList Factor2" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Factor(), did not pass:  Factor -> id IdnestList Factor2");
			}
		}*/
		//| num or INT
		else if (lookAhead.inRHS3(nt.Factor)){
			if (match("num"))
			{
				System.out.println("Factor -> num");
				parseInfo += "Factor -> num" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Factor(), did not pass:  Factor -> num ");
			}

		}
		//| not Factor
		else if (lookAhead.inRHS4(nt.Factor)){
			if (match("t_not") && Factor())
			{
				System.out.println("Factor -> not Factor");
				parseInfo += "Factor -> not Factor" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Factor(), did not pass:  Factor -> not Factor ");
			}
		}
		//| Sign Factor
		else if (lookAhead.inRHS5(nt.Factor)){
			if (Sign() && Factor())
			{
				System.out.println("Factor -> Sign Factor ");
				parseInfo += "Factor -> Sign Factor " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Factor(), did not pass:  Factor -> Sign Factor ");
			}
		}
		else {
			success = false;
			System.out.println("In Factor(), did not pass:  match() and isInFirst() ");
		}
		return success;
	}

	
	//30. Factor2 -> ( AParams ) | epsilon
	private boolean Factor2(){
		System.out.println("Calling: Factor2 -> ( AParams ) ");
		boolean success = skipErrors(nt.Factor2);
		if (lookAhead.inRHS1(nt.Factor2)){
			if (match("openpar") && AParams() && match("closepar")){
				System.out.println("Factor2 -> ( AParams )");
				parseInfo += "Factor2 -> ( AParams )" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Factor2(), did not pass:  Factor2 -> ( AParams )");
			}
		}
		else if (lookAhead.inFollow(nt.Factor2)){
			System.out.println("Factor2 -> epsilon");
			parseInfo += "Factor2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In Factor2(), did not pass: infirst && infollow ");
		}
		return success;
	}
	
	//31. IdnestList -> . id IndiceList IdnestList | IndiceList | epsilon
	private boolean IdnestList(){
		System.out.println("Calling: IdnestList -> . id IndiceList IdnestList | IndiceList | epsilon");
		boolean success = skipErrors(nt.IdnestList);
		if (lookAhead.inRHS1(nt.IdnestList)){
			if (match("dot") && match("id") && IndiceList() && IdnestList()){
				System.out.println("IdnestList -> . id IndiceList IdnestList ");
				parseInfo += "IdnestList -> . id IndiceList IdnestList " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In IdnestList(), did not pass: IdnestList -> . id IndiceList IdnestList ");
			}
		}
		else if (lookAhead.inFirst(nt.IndiceList)){
			if (IndiceList()){
				System.out.println("IdnestList -> IndiceList");
				parseInfo += "IdnestList -> IndiceList " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In IdnestList(), did not pass: IdnestList -> IndiceList ");
			}
		}
		else if (lookAhead.inFollow(nt.IdnestList)){
			System.out.println("IdnestList -> epsilon");
			parseInfo += "IdnestList -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In IdnestList(), did not pass:  infirst or infollow");
		}
		return success;
	}
	
	//32. Variable -> id IdnestList
	private boolean Variable(){
		System.out.println("Calling: Variable -> id IdnestList");
		boolean success = skipErrors(nt.Variable);
		/*
		if (lookAhead.inRHS1(nt.Variable)){
			if (match("id") && IdnestList()){
				System.out.println("Variable -> id IdnestList");
				parseInfo += "Variable -> id IdnestList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Variable(), did not pass:  Variable -> id IdnestList");
			}
		}*/
		if (lookAhead.inRHS1(nt.Variable)){
			if (match("id") && IndiceList()){
				System.out.println("Variable -> id IndiceList");
				parseInfo += "Variable -> id IndiceList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Variable(), did not pass:  Variable -> id IndiceList");
			}
		}
		else {
			success = false;
			System.out.println("In Variable(), did not pass: lookahead.equals(id) ");
		}
		return success;
	}
	
	//33. IndiceList ->  Indice IndiceList | epsilon
	private boolean IndiceList(){
		System.out.println("Calling: IndiceList ->  Indice IndiceList | epsilon");
		boolean success = skipErrors(nt.IndiceList);
		if (lookAhead.inRHS1(nt.IndiceList)){
			if (Indice() && IndiceList())
			{
				System.out.println("IndiceList ->  Indice IndiceList");
				parseInfo += "IndiceList ->  Indice IndiceList " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Indice(), did not pass: IndiceList ->  Indice IndiceList ");
			}
		}
		else if (lookAhead.inFollow(nt.IndiceList)){
			System.out.println("Indice -> epsilon");
			parseInfo += "Indice -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In IndiceList(), did not pass: infirst or infollow");
		}
		
		return success;
	}
	
	//34. Indice -> [ ArithExpr ] 
	private boolean Indice(){
		System.out.println("Calling: Indice -> [ ArithExpr ] Indice | epsilon");
		boolean success = skipErrors(nt.Indice);
		if (lookAhead.inRHS1(nt.Indice)){
			if (match("opensq") && ArithExpr() && match("closesq"))
			{
				System.out.println("Indice -> [ ArithExpr ]");
				parseInfo += "Indice -> [ ArithExpr ]" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Indice(), did not pass: Indice -> [ ArithExpr ] ");
			}
		}
		else {
			success = false;
			System.out.println("In Indice(), did not pass: lookAhead.equals(\"opensq\") ");
		}
		return success;
	}
	
	//35. ArraySize -> [ INT ] 
	private boolean ArraySize(){
		System.out.println("Calling: ArraySize -> [ INT ] ");
		boolean success = skipErrors(nt.ArraySize);
		if (lookAhead.inRHS1(nt.ArraySize)){
			if (match("opensq") && match("INT") && match("closesq"))
			{
				System.out.println("ArraySize -> [ INT ] ");
				parseInfo += "ArraySize -> [ INT ] " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In ArraySize(), did not pass: ArraySize -> [ INT ] ");
			}
		}
		else {
			success = false;
			System.out.println("In ArraySize(), did not pass: lookAhead.equals(\"opensq\")");
		}
		return success;
	}
	
	//36. FParams -> Type id ArraySizeList FParamsTailList | epsilon
	private boolean FParams(){
		System.out.println("Calling: FParams -> Type id ArraySizeList FParamsTailList | epsilon");
		boolean success = skipErrors(nt.FParams);
		if (lookAhead.inRHS1(nt.FParams)){
			if (Type() && match("id") && ArraySizeList() && FParamsTailList()){
				System.out.println("FParams -> Type id ArraySizeList FParamsTailList ");
				parseInfo += "FParams -> Type id ArraySizeList FParamsTailList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FParams(), did not pass: FParams -> Type id ArraySizeList FParamsTailList");
			}
		}
		else if (lookAhead.inFollow(nt.FParams)){
			System.out.println("FParams -> epsilon");
			parseInfo += "FParams -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In FParams(), did not pass: infirst or infollow ");
		}
		return success;
	}

	//37. FParamsTailList -> FParamsTail FParamsTailList | epsilon
	private boolean FParamsTailList(){
		System.out.println("Calling: FParamsTailList ->  FParamsTail FParamsTailList | epsilon");
		boolean success = skipErrors(nt.FParamsTailList);
		if (lookAhead.inRHS1(nt.FParamsTailList)){
			if (FParamsTail() && FParamsTailList())
			{
				System.out.println("FParamsTailList ->  FParamsTail FParamsTailList");
				parseInfo += "FParamsTailList ->  FParamsTail FParamsTailList " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FParamsTail(), did not pass: FParamsTailList ->  FParamsTail FParamsTailList ");
			}
		}
		else if (lookAhead.inFollow(nt.FParamsTailList)){
			System.out.println("FParamsTail -> epsilon");
			parseInfo += "FParamsTail -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In FParamsTailList(), did not pass: infirst or infollow");
		}
		
		return success;
	}
	
	//38. AParams -> Expr AParamsTailList | epsilon
	private boolean AParams(){
		System.out.println("Calling: AParams -> Expr AParamsTailList | epsilon");
		boolean success = skipErrors(nt.AParams);
		if (lookAhead.inRHS1(nt.AParams)){
			if (Expr() && AParamsTailList()){
				System.out.println("AParams -> Expr AParamsTailList");
				parseInfo += "AParams -> Expr AParamsTailList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In Aparams(), did not pass: AParams -> Expr AParamsTailList");
			}
		}
		else if (lookAhead.inFollow(nt.AParams)){
			System.out.println("AParams -> epsilon");
			parseInfo += "AParams -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In AParams(), did not pass: infirst or infollow");
		}
		return success;
	}
	
	//39. AParamsTailList -> AParamsTail AParamsTailList | epsilon
	private boolean AParamsTailList(){
		System.out.println("Calling: AParamsTailList ->  AParamsTail AParamsTailList | epsilon");
		boolean success = skipErrors(nt.AParamsTailList);
		if (lookAhead.inRHS1(nt.AParamsTailList)){
			if (AParamsTail() && AParamsTailList())
			{
				System.out.println("AParamsTailList -> AParamsTail AParamsTailList");
				parseInfo += "AParamsTailList -> AParamsTail AParamsTailList " + "\r\n";
			}
			else {
				success = false;
				System.out.println("In AParamsTail(), did not pass: AParamsTailList -> AParamsTail AParamsTailList ");
			}
		}
		else if (lookAhead.inFollow(nt.AParamsTailList)){
			System.out.println("AParamsTail -> epsilon");
			parseInfo += "AParamsTail -> epsilon" + "\r\n";
		}
		else {
			success = false;
			System.out.println("In AParamsTailList(), did not pass: infirst or infollow");
		}
		
		return success;
	}
	
	//40. FParamsTail -> , Type id ArraySizeList
	private boolean FParamsTail(){
		System.out.println("Calling: FParamsTail -> , Type id ArraySizeList");
		boolean success = skipErrors(nt.FParamsTail);
		if (lookAhead.inRHS1(nt.FParamsTail)){
			if (match("comma") && Type() && match("id") && ArraySizeList())
			{
				System.out.println("FParamsTail -> , Type id ArraySizeList");
				parseInfo += "FParamsTail -> , Type id ArraySizeList" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In FParamsTail(), did not pass: FParamsTail -> , Type id ArraySizeList");
			}
		}
		else {
			success = false;
			System.out.println("In FParamsTail(), did not pass: lookAhead.equals(\"comma\")");
		}
		return success;
	}


	
	//41. AParamsTail -> , Expr 
	private boolean AParamsTail(){
		System.out.println("Calling: AParamsTail -> , Expr ");
		boolean success = skipErrors(nt.AParamsTail);
		if (lookAhead.inRHS1(nt.AParamsTail)){
			if (match("comma") && Expr())
			{
				System.out.println("AParamsTail -> , Expr");
				parseInfo += "AParamsTail -> , Expr" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In AParamsTail(), did not pass: AParamsTail -> , Expr");
			}
		}
		else {
			success = false;
			System.out.println("In AParamsTail(), did not pass: lookAhead.equals(\"comma\")");
		}
		return success;
	}
	
	//42. RelOp -> == | <> | < | > | <= | >=
	private boolean RelOp(){
		System.out.println("Calling: RelOp -> == | <> | < | > | <= | >=");
		boolean success = skipErrors(nt.RelOp);
		if (lookAhead.inRHS1(nt.RelOp)){
			if (match("relop_e"))
			{
				System.out.println("RelOp -> ==");
				parseInfo += "RelOp -> ==" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In RelOp(), did not pass: RelOp -> ==");
			}
		}
		else if (lookAhead.inRHS2(nt.RelOp)){
			if (match("lege"))
			{
				System.out.println("RelOp -> <>");
				parseInfo += "RelOp -> <>" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In RelOp(), did not pass: RelOp -> <>");
			}
		}
		else if (lookAhead.inRHS3(nt.RelOp)){
			if (match("relop_l"))
			{
				System.out.println("RelOp -> <");
				parseInfo += "RelOp -> <" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In RelOp(), did not pass: RelOp -> < ");
			}
		}
		else if (lookAhead.inRHS4(nt.RelOp)){
			if (match("relop_g"))
			{
				System.out.println("RelOp -> >");
				parseInfo += "RelOp -> >" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In RelOp(), did not pass: RelOp -> >");
			}
		}
		else if (lookAhead.inRHS5(nt.RelOp)){
			if (match("relop_le"))
			{
				System.out.println("RelOp -> <=");
				parseInfo += "RelOp -> <=" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In RelOp(), did not pass: RelOp -> <=");
			}
		}
		else if (lookAhead.inRHS6(nt.RelOp)){
			if (match("relop_ge"))
			{
				System.out.println("RelOp -> >=");
				parseInfo += "RelOp -> >=" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In RelOp(), did not pass: RelOp -> >=");
			}
		}
		else {
			success = false;
			System.out.println("In RelOp(), did not pass: lookAhead.equals(...)");
		}
		return success;
	}
	
	//43. AddOp -> + | - | or
	private boolean AddOp(){
		System.out.println("Calling: AddOp -> + | - | or");
		boolean success = skipErrors(nt.AddOp);
		if (lookAhead.inRHS1(nt.AddOp)){
			if (match("or"))
			{
				System.out.println("AddOp -> or");
				parseInfo += "AddOp -> or" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In AddOp(), did not pass: AddOp -> or");
			}
		}
		else if (lookAhead.inRHS2(nt.AddOp)){
			if (match("sub"))
			{
				System.out.println("AddOp -> -");
				parseInfo += "AddOp -> -" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In AddOp(), did not pass: AddOp -> -");
			}
		}
		else if (lookAhead.inRHS3(nt.AddOp)){
			if (match("add"))
			{
				System.out.println("AddOp -> +");
				parseInfo += "AddOp -> +" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In AddOp(), did not pass: AddOp -> +");
			}
		}
		else {
			success = false;
			System.out.println("In AddOp(), did not pass: lookAhead.equals(...)");
		}
		return success;
	}

	//44. MultOp -> * | / | and
	private boolean MultOp(){
		System.out.println("Calling: MultOp -> * | / | and");
		boolean success = skipErrors(nt.MultOp);
		if (lookAhead.inRHS1(nt.MultOp)){
			if (match("and"))
			{
				System.out.println("MultOp -> and");
				parseInfo += "MultOp -> and" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In MultOp(), did not pass: MultOp -> and");
			}
		}
		else if (lookAhead.inRHS2(nt.MultOp)){
			if (match("div"))
			{
				System.out.println("MultOp -> /");
				parseInfo += "MultOp -> /" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In MultOp(), did not pass: MultOp -> /");
			}
		}
		else if (lookAhead.inRHS3(nt.MultOp)){
			if (match("mul"))
			{
				System.out.println("MultOp -> *");
				parseInfo += "MultOp -> *" + "\r\n";
			}
			else {
				success = false;
				System.out.println("In MultOp(), did not pass: MultOp -> *");
			}
		}
		else {
			success = false;
			System.out.println("In MultOp(), did not pass: lookAhead.getType().equals(...)");
		}
		return success;
	}
}