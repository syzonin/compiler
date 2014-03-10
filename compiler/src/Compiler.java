import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {
	
	//lookup table with height 28, width 25
	static String[][] lookupTable = new String[28][25];
	static char[] fileContent;
	static int currentLocation = 0;
	static String lexeme = "";
	static String state = "1";
	static boolean fileComplete = false;
	static String tokens="";
	static String errorMessages="";
	static String parseInfo="";
	static ArrayList<Token> tokenArray = new ArrayList<Token>(); 
	static NonTerminalCollection nonTerminals = new NonTerminalCollection();
	static TerminalCollection terminals = new TerminalCollection();
	static int tokenArrayLocation = 0;
	static Token lookAhead;
	
	public static void main(String args[]){
		
		//call lexical analyzer
		lexicalAnalyzer();
		
		/*for (int i = 0; i< tokenArray.size(); i++){
			System.out.println(i);
			System.out.println(tokenArray.get(i));
		} */
		
		//call parser, returns true or false
		boolean parsed = parser();
		String parseFilePath = "C:/Users/Si/git/compiler/compiler/parseInfo.txt";
		WriteToFile(parseFilePath, parseInfo);
		
		if (parsed)
			System.out.println("successful parse!");
		else
			System.out.println("parse failed.");
		
	}
	
	static boolean parser(){
		
		lookAhead = nextTokenParser();
		System.out.println(lookAhead);
		boolean success;
		if (Prog() && match(terminals.t_eof))
			success = true;
		else
			success = false;
		return success;
	}
	
	//1. Prog -> ClassDecl ProgBody 
	static boolean Prog(){
		boolean success = skipErrors(nonTerminals.Prog);
		//System.out.println("success: " + success);
		if (nonTerminals.ClassDecl.isInFirst(lookAhead.getType())){
			if (ClassDecl() && ProgBody()){
				System.out.println("Prog -> ClassDecl ProgBody");
				parseInfo += "Prog -> ClassDecl ProgBody" + "\r\n";
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
	
	//2. ClassDecl ->  class id { VarDecl FuncDef } ; ClassDecl2 | ClassDecl2
	static boolean ClassDecl(){
		boolean success = skipErrors(nonTerminals.ClassDecl);
		if (lookAhead.getType().equals("class")){
			if (match(terminals.t_class) && match(terminals.t_id) && match(terminals.t_opencur) && 
				VarDecl() && FuncDef() && match(terminals.t_closecur) && match(terminals.t_semi) &&
				ClassDecl2())
			{
				System.out.println("ClassDecl -> class id { VarDecl FuncDef } ; ClassDecl2");
				parseInfo += "ClassDecl -> class id { VarDecl FuncDef } ; ClassDecl2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.ClassDecl.isInFollow(lookAhead.getType())){
			System.out.println("ClassDecl -> epsilon");
			parseInfo += "ClassDecl -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//3. ClassDecl2 -> class id { VarDecl FuncDef } ; ClassDecl2 | epsilon
	static boolean ClassDecl2(){
		boolean success = skipErrors(nonTerminals.ClassDecl2);
		if (lookAhead.getType().equals("class")){
			if (match(terminals.t_class) && match(terminals.t_id) && match(terminals.t_opencur) && 
				VarDecl() && FuncDef() && match(terminals.t_closecur) && match(terminals.t_semi) &&
				ClassDecl2())
			{
				System.out.println("ClassDecl2 -> class id { VarDecl FuncDef } ; ClassDecl2");
				parseInfo += "ClassDecl2 -> class id { VarDecl FuncDef } ; ClassDecl2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.ClassDecl2.isInFollow(lookAhead.getType())){
				System.out.println("ClassDecl2 -> epsilon");
				parseInfo += "ClassDecl2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	
	//4. ProgBody -> id FuncBody ; FuncDef
	static boolean ProgBody(){
		boolean success = skipErrors(nonTerminals.ProgBody);
		if (lookAhead.getType().equals("id")){
			if (match(terminals.t_id) && FuncBody() && match(terminals.t_semi) && FuncDef())
			{
				System.out.println("ProgBody -> id FuncBody ; FuncDef");
				parseInfo += "ProgBody -> id FuncBody ; FuncDef" + "\r\n";
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
	
	//5. FuncHead -> Type id ( Fparams )
	static boolean FuncHead(){
		boolean success = skipErrors(nonTerminals.FuncHead);
		if (nonTerminals.Type.isInFirst(lookAhead.getType())){
			if (Type() && match(terminals.t_id) && match(terminals.t_openpar) && Fparams()
					&& match(terminals.t_closepar))
			{
				System.out.println("FuncHead -> Type id ( Fparams )");
				parseInfo += "FuncHead -> Type id ( Fparams )" + "\r\n";
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
	
	//6. FuncDef -> FuncHead FuncBody FuncDef2 | FuncDef2
	static boolean FuncDef(){
		boolean success = skipErrors(nonTerminals.FuncDef);
		if (nonTerminals.FuncHead.isInFirst(lookAhead.getType())){
			if (FuncHead() && FuncBody() && FuncDef2()){
				System.out.println("FuncDef -> FuncHead FuncBody FuncDef2");
				parseInfo += "FuncDef -> FuncHead FuncBody FuncDef2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.FuncDef.isInFollow(lookAhead.getType())){
			System.out.println("FuncDef -> epsilon");
			parseInfo += "FuncDef -> epsilon" + "\r\n";
	}
		else {
			success = false;
		}
		return success;
	}
	
	//7. FuncDef2 -> FuncHead FuncBody FuncDef2 | epsilon
	static boolean FuncDef2(){
		boolean success = skipErrors(nonTerminals.FuncDef2);
		if (nonTerminals.FuncHead.isInFirst(lookAhead.getType())){
			if (FuncHead() && FuncBody() && FuncDef2()){
				System.out.println("FuncDef2 -> FuncHead FuncBody FuncDef2");
				parseInfo += "FuncDef2 -> FuncHead FuncBody FuncDef2" + "\r\n";

			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.FuncDef2.isInFollow(lookAhead.getType())){
				System.out.println("FuncDef2 -> epsilon");
				parseInfo += "FuncDef2 -> epsilon" + "\r\n";

		}
		else {
			success = false;
		}
		return success;
	}
	
	//8. FuncBody -> { VarDecl Statement }
	static boolean FuncBody(){
		boolean success = skipErrors(nonTerminals.FuncBody);
		if (lookAhead.getType().equals("opencur")){
			if (match(terminals.t_opencur) && VarDecl() && Statement() && match(terminals.t_closecur))
			{
				System.out.println("FuncBody -> { VarDecl Statement }");
				parseInfo += "FuncBody -> { VarDecl Statement }" + "\r\n";
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
	
	//9. VarDecl -> Type id ArraySize ; VarDecl2 | VarDecl2
	static boolean VarDecl(){
		boolean success = skipErrors(nonTerminals.VarDecl);
		if (nonTerminals.Type.isInFirst(lookAhead.getType())){
			if (Type() && match(terminals.t_id) && ArraySize() && match(terminals.t_semi)
					&& VarDecl2()){
				System.out.println("VarDecl -> Type id ArraySize ; VarDecl2");
				parseInfo += "VarDecl -> Type id ArraySize ; VarDecl2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.VarDecl.isInFollow(lookAhead.getType())){
			System.out.println("VarDecl -> epsilon");
			parseInfo += "VarDecl -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//10. VarDecl2 -> Type id ArraySize ; VarDecl2 | epsilon
	static boolean VarDecl2(){
		boolean success = skipErrors(nonTerminals.VarDecl2);
		if (nonTerminals.Type.isInFirst(lookAhead.getType())){
			if (Type() && match(terminals.t_id) && ArraySize() && match(terminals.t_semi)
					&& VarDecl2()){
				System.out.println("VarDecl2 -> Type id ArraySize ; VarDecl2");
				parseInfo += "VarDecl2 -> Type id ArraySize ; VarDecl2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.VarDecl2.isInFollow(lookAhead.getType())){
			System.out.println("VarDecl2 -> epsilon");
			parseInfo += "VarDecl2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//11. Statement ->  AssignStat ; Statement2
	//		| if ( Expr ) then StatBlock else StatBlock ; Statement2
	//		| for ( Type id AssignOp Expr ; RelExpr ; AssignStat ) StatBlock ; Statement2
	//		| get ( Variable ) ; Statement2
	//		| put ( Expr ) ; Statement2
	//		| return ( Expr ) ; Statement2
	//		| Statement2
	static boolean Statement(){
		boolean success = skipErrors(nonTerminals.Statement);
		// AssignStat ; Statement2
		if (nonTerminals.AssignStat.isInFirst(lookAhead.getType())){
			if (AssignStat() && match(terminals.t_semi) && Statement2()){
				System.out.println("Statement ->  AssignStat ; Statement2");
				parseInfo += "Statement ->  AssignStat ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		// | if ( Expr ) then StatBlock else StatBlock ; Statement2
		else if (lookAhead.getType().equals("if")){
			if (match(terminals.t_if) && match(terminals.t_openpar) && Expr() &&
					match(terminals.t_closepar) && match(terminals.t_then) && StatBlock() &&
					match(terminals.t_else) && StatBlock() && match(terminals.t_semi) && Statement2() )
			{
				System.out.println("Statement ->  if ( Expr ) then StatBlock else StatBlock ; Statement2");
				parseInfo += "Statement ->  if ( Expr ) then StatBlock else StatBlock ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		// | for ( Type id AssignOp Expr ; RelExpr ; AssignStat ) StatBlock ; Statement2
		else if (lookAhead.getType().equals("for")){
			if (match(terminals.t_for) && match(terminals.t_openpar) && Type() &&
					match(terminals.t_id) && AssignOp() && Expr() && match(terminals.t_semi) && 
					RelExpr() && match(terminals.t_semi) && AssignStat() && match(terminals.t_closepar) &&
					StatBlock() &&	match(terminals.t_semi ) && Statement2() )
			{
				System.out.println("Statement ->  for ( Type id AssignOp Expr ; RelExpr " +
						"; AssignStat ) StatBlock ; Statement2");
				parseInfo += "Statement ->  for ( Type id AssignOp Expr ; RelExpr " +
						"; AssignStat ) StatBlock ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| get ( Variable ) ; Statement2
		else if (lookAhead.getType().equals("get")){
			if (match(terminals.t_get) && match(terminals.t_openpar) && Variable() &&
					match(terminals.t_closepar) && match(terminals.t_semi) && Statement2() )
			{
				System.out.println("Statement -> get ( Variable ) ; Statement2");
				parseInfo += "Statement -> get ( Variable ) ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| put ( Expr ) ; Statement2
		else if (lookAhead.getType().equals("put")){
			if (match(terminals.t_put) && match(terminals.t_openpar) && Expr() &&
					match(terminals.t_closepar) && match(terminals.t_semi) && Statement2() )
			{
				System.out.println("Statement -> put ( Expr ) ; Statement2");
				parseInfo += "Statement -> put ( Expr ) ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| return ( Expr ) ; Statement2
		else if (lookAhead.getType().equals("return")){
			if (match(terminals.t_return) && match(terminals.t_openpar) && Expr() &&
					match(terminals.t_closepar) && match(terminals.t_semi) && Statement2() )
			{
				System.out.println("Statement -> return ( Expr ) ; Statement2");
				parseInfo += "Statement -> return ( Expr ) ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Statement.isInFollow(lookAhead.getType())){
			System.out.println("Statement -> epsilon");
			parseInfo += "Statement -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//12. Statement2 -> AssignStat ; Statement2
	//		|  if ( Expr ) then StatBlock else StatBlock ; Statement2
	//		| for ( Type id AssignOp Expr ; RelExpr ; AssignStat ) StatBlock ; Statement2
	//		| get ( Variable ) ; Statement2
	//		| put ( Expr ) ; Statement2
	//		| return ( Expr ) ; Statement2
	//		| epsilon
	static boolean Statement2(){
		boolean success = skipErrors(nonTerminals.Statement2);
		// AssignStat ; Statement2
		if (nonTerminals.AssignStat.isInFirst(lookAhead.getType())){
			if (AssignStat() && match(terminals.t_semi) && Statement2()){
				System.out.println("Statement2 ->  AssignStat ; Statement2");
				parseInfo += "Statement2 ->  AssignStat ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		// | if ( Expr ) then StatBlock else StatBlock ; Statement2
		else if (lookAhead.getType().equals("if")){
			if (match(terminals.t_if) && match(terminals.t_openpar) && Expr() &&
					match(terminals.t_closepar) && match(terminals.t_then) && StatBlock() &&
					match(terminals.t_else) && StatBlock() && match(terminals.t_semi) && Statement2() )
			{
				System.out.println("Statement2 ->  if ( Expr ) then StatBlock else StatBlock ; Statement2");
				parseInfo += "Statement2 ->  if ( Expr ) then StatBlock else StatBlock ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		// | for ( Type id AssignOp Expr ; RelExpr ; AssignStat ) StatBlock ; Statement2
		else if (lookAhead.getType().equals("for")){
			if (match(terminals.t_for) && match(terminals.t_openpar) && Type() &&
					match(terminals.t_id) && AssignOp() && Expr() && match(terminals.t_semi) && 
					RelExpr() && match(terminals.t_semi) && AssignStat() && match(terminals.t_closepar) &&
					StatBlock() &&	match(terminals.t_semi ) && Statement2() )
			{
				System.out.println("Statement2 ->  for ( Type id AssignOp Expr ; RelExpr " +
						"; AssignStat ) StatBlock ; Statement2");
				parseInfo += "Statement2 ->  for ( Type id AssignOp Expr ; RelExpr " +
						"; AssignStat ) StatBlock ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| get ( Variable ) ; Statement2
		else if (lookAhead.getType().equals("get")){
			if (match(terminals.t_get) && match(terminals.t_openpar) && Variable() &&
					match(terminals.t_closepar) && match(terminals.t_semi) && Statement2() )
			{
				System.out.println("Statement2 -> get ( Variable ) ; Statement2");
				parseInfo += "Statement2 -> get ( Variable ) ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| put ( Expr ) ; Statement2
		else if (lookAhead.getType().equals("put")){
			if (match(terminals.t_put) && match(terminals.t_openpar) && Expr() &&
					match(terminals.t_closepar) && match(terminals.t_semi) && Statement2() )
			{
				System.out.println("Statement2 -> put ( Expr ) ; Statement2");
				parseInfo += "Statement2 -> put ( Expr ) ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| return ( Expr ) ; Statement2
		else if (lookAhead.getType().equals("return")){
			if (match(terminals.t_return) && match(terminals.t_openpar) && Expr() &&
					match(terminals.t_closepar) && match(terminals.t_semi) && Statement2() )
			{
				System.out.println("Statement2 -> return ( Expr ) ; Statement2");
				parseInfo += "Statement2 -> return ( Expr ) ; Statement2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		// epsilon
		else if (nonTerminals.Statement2.isInFollow(lookAhead.getType())){
			System.out.println("Statement2 -> epsilon");
			parseInfo += "Statement2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//13. AssignStat -> Variable AssignOp Expr
	static boolean AssignStat(){
		boolean success = skipErrors(nonTerminals.AssignStat);
		if (nonTerminals.Variable.isInFirst(lookAhead.getType())){
			if (Variable() && AssignOp() && Expr()){
				System.out.println("AssignStat -> Variable AssignOp Expr");
				parseInfo += "AssignStat -> Variable AssignOp Expr" + "\r\n";
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
	
	//14. StatBlock -> { Statement } | Statement | epsilon
	static boolean StatBlock(){
		boolean success = skipErrors(nonTerminals.StatBlock);
		if (lookAhead.getType().equals("opencur")){
			if (match(terminals.t_opencur) && Statement() && match(terminals.t_closecur))
			{
				System.out.println("StatBlock -> { Statement }");
				parseInfo += "StatBlock -> { Statement }" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Statement.isInFirst(lookAhead.getType())){
			if (Statement())
			{
				System.out.println("StatBlock -> Statement");
				parseInfo += "StatBlock -> Statement" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.StatBlock.isInFollow(lookAhead.getType())){
				System.out.println("Statblock -> epsilon");
				parseInfo += "Statblock -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//15. Expr -> ArithExpr | RelExpr 
	static boolean Expr(){
		boolean success = skipErrors(nonTerminals.Expr);
		if (nonTerminals.ArithExpr.isInFirst(lookAhead.getType())){
			if (ArithExpr())
			{
				System.out.println("Expr -> ArithExpr");
				parseInfo += "Expr -> ArithExpr" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.RelExpr.isInFirst(lookAhead.getType())){
			if (RelExpr())
			{
				System.out.println("Expr -> RelExpr");
				parseInfo += "Expr -> RelExpr" + "\r\n";
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

	//16. RelExpr -> ArithExpr RelOp ArithExpr
	static boolean RelExpr(){
		boolean success = skipErrors(nonTerminals.RelExpr);
		if (nonTerminals.ArithExpr.isInFirst(lookAhead.getType())){
			if (ArithExpr() && RelOp() && ArithExpr()){
				System.out.println("RelExpr -> ArithExpr RelOp ArithExpr");
				parseInfo += "RelExpr -> ArithExpr RelOp ArithExpr" + "\r\n";
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
	
	//17. ArithExpr -> Term ArithExpr2 | ArithExpr2
	static boolean ArithExpr(){
		boolean success = skipErrors(nonTerminals.ArithExpr);
		if (nonTerminals.Term.isInFirst(lookAhead.getType())){
			if (Term() && ArithExpr2()){
				System.out.println("ArithExpr -> Term ArithExpr2");
				parseInfo += "ArithExpr -> Term ArithExpr2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.ArithExpr.isInFollow(lookAhead.getType())){
			System.out.println("ArithExpr -> epsilon");
			parseInfo += "ArithExpr -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//18. ArithExpr2 -> AddOp Term ArithExpr2 | epsilon
	static boolean ArithExpr2(){
		boolean success = skipErrors(nonTerminals.ArithExpr2);
		if (nonTerminals.AddOp.isInFirst(lookAhead.getType())){
			if (AddOp() && Term() && ArithExpr2()){
				System.out.println("ArithExpr -> Term ArithExpr2");
				parseInfo += "ArithExpr -> Term ArithExpr2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.ArithExpr2.isInFollow(lookAhead.getType())){
			System.out.println("ArithExpr2 -> epsilon");
			parseInfo += "ArithExpr2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//19. Sign -> + | -
	static boolean Sign(){
		boolean success = skipErrors(nonTerminals.Sign);
		if (lookAhead.getType().equals("add")){
			if (match(terminals.t_add))
			{
				System.out.println("Sign -> + ");
				parseInfo += "Sign -> + " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("sub")){
			if (match(terminals.t_sub))
			{
				System.out.println("Sign -> - ");
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
	
	//20. Term -> Factor Term2 | Term2
	static boolean Term(){
		boolean success = skipErrors(nonTerminals.Term);
		if (nonTerminals.Factor.isInFirst(lookAhead.getType())){
			if (Factor() && Term2()){
				System.out.println("Term -> Factor Term2");
				parseInfo += "Term -> Factor Term2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Term.isInFollow(lookAhead.getType())){
			System.out.println("Term -> epsilon");
			parseInfo += "Term -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}

	//21. Term2 -> MultOp Factor Term2 | epsilon
	static boolean Term2(){
		boolean success = skipErrors(nonTerminals.Term2);
		if (nonTerminals.MultOp.isInFirst(lookAhead.getType())){
			if (MultOp() && Factor() && Term2()){
				System.out.println("Term2 -> MultOp Factor Term2 ");
				parseInfo += "Term2 -> MultOp Factor Term2 " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Term2.isInFollow(lookAhead.getType())){
			System.out.println("Term2 -> epsilon");
			parseInfo += "Term2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//22. Factor -> Variable | Idnest id ( Aparams ) | num | ( ArithExpr ) | not Factor | Sign Factor
	static boolean Factor(){
		boolean success = skipErrors(nonTerminals.Factor);
		//Variable
		if (nonTerminals.Variable.isInFirst(lookAhead.getType())){
			if (match(terminals.t_add))
			{
				System.out.println("Factor -> Variable ");
				parseInfo += "Factor -> Variable " + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| Idnest id ( Aparams ) 
		if (nonTerminals.Idnest.isInFirst(lookAhead.getType())){
			if (Idnest() && match(terminals.t_id) && match(terminals.t_openpar) &&
					Aparams() && match(terminals.t_closepar))
			{
				System.out.println("Factor -> Idnest id ( Aparams ) ");
				parseInfo += "Factor -> Idnest id ( Aparams ) " + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| num
		else if (lookAhead.getType().equals("num")){
			if (match(terminals.t_num))
			{
				System.out.println("Factor -> num");
				parseInfo += "Factor -> num" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| ( ArithExpr )
		else if (lookAhead.getType().equals("openpar")){
			if (match(terminals.t_openpar) && ArithExpr() && match(terminals.t_closepar))
			{
				System.out.println("Factor -> ( ArithExpr )");
				parseInfo += "Factor -> ( ArithExpr )" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| not Factor
		else if (lookAhead.getType().equals("not")){
			if (match(terminals.t_not) && Factor())
			{
				System.out.println("Factor -> not Factor");
				parseInfo += "Factor -> not Factor" + "\r\n";
			}
			else {
				success = false;
			}
		}
		//| Sign Factor
		if (nonTerminals.Sign.isInFirst(lookAhead.getType())){
			if (Sign() && Factor())
			{
				System.out.println("Factor -> Sign Factor ");
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
	
	//23. Variable -> Idnest id Indice
	static boolean Variable(){
		boolean success = skipErrors(nonTerminals.Variable);
		if (nonTerminals.Idnest.isInFirst(lookAhead.getType())){
			if (Idnest() && match(terminals.t_id) && Indice()){
				System.out.println("Variable -> Idnest id Indice");
				parseInfo += "Variable -> Idnest id Indice" + "\r\n";
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
	
	//24. Idnest -> id Indice . Idnest2 | Idnest2
	static boolean Idnest(){
		boolean success = skipErrors(nonTerminals.Idnest);
		if (lookAhead.getType().equals("id")){
			if (match(terminals.t_id) && Indice() && match(terminals.t_dot) && Idnest2())
			{
				System.out.println("Idnest -> id Indice . Idnest2");
				parseInfo += "Idnest -> id Indice . Idnest2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Idnest.isInFollow(lookAhead.getType())){
			System.out.println("Idnest -> epsilon");
			parseInfo += "Idnest -> epsilon" + "\r\n";
	}
		else {
			success = false;
		}
		return success;
	}
	
	//25. Idnest2 -> id Indice . Idnest2 | epsilon
	static boolean Idnest2(){
		boolean success = skipErrors(nonTerminals.Idnest2);
		if (lookAhead.getType().equals("id")){
			if (match(terminals.t_id) && Indice() && match(terminals.t_dot) && Idnest2())
			{
				System.out.println("Idnest2 -> id Indice . Idnest2");
				parseInfo += "Idnest2 -> id Indice . Idnest2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Idnest2.isInFollow(lookAhead.getType())){
				System.out.println("Idnest2 -> epsilon");
				parseInfo += "Idnest2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//26. Indice -> [ ArithExpr ] Indice2
	static boolean Indice(){
		boolean success = skipErrors(nonTerminals.Indice);
		if (lookAhead.getType().equals("opensq")){
			if (match(terminals.t_opensq) && ArithExpr() && match(terminals.t_closesq) && Indice2())
			{
				System.out.println("Indice -> [ ArithExpr ] Indice2");
				parseInfo += "Indice -> [ ArithExpr ] Indice2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Indice.isInFollow(lookAhead.getType())){
			System.out.println("Indice -> epsilon");
			parseInfo += "Indice -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//27. Indice2 -> [ ArithExpr ] Indice2 | epsilon
	static boolean Indice2(){
		boolean success = skipErrors(nonTerminals.Indice2);
		if (lookAhead.getType().equals("opensq")){
			if (match(terminals.t_opensq) && ArithExpr() && match(terminals.t_closesq) && Indice2())
			{
				System.out.println("Indice2 -> [ ArithExpr ] Indice2");
				parseInfo += "Indice2 -> [ ArithExpr ] Indice2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Indice2.isInFollow(lookAhead.getType())){
			System.out.println("Indice2 -> epsilon");
			parseInfo += "Indice2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//28. ArraySize -> [ num ] ArraySize2 | ArraySize2
	static boolean ArraySize(){
		boolean success = skipErrors(nonTerminals.ArraySize);
		if (lookAhead.getType().equals("opensq")){
			if (match(terminals.t_opensq) && match(terminals.t_num) &&
					match(terminals.t_closesq) && ArraySize2())
			{
				System.out.println("ArraySize -> [ num ] ArraySize2");
				parseInfo += "ArraySize -> [ num ] ArraySize2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.ArraySize.isInFollow(lookAhead.getType())){
			System.out.println("ArraySize -> epsilon");
			parseInfo += "ArraySize -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//29. ArraySize2 -> [ num ] ArraySize2 | epsilon
	static boolean ArraySize2(){
		boolean success = skipErrors(nonTerminals.ArraySize2);
		if (lookAhead.getType().equals("opensq")){
			if (match(terminals.t_opensq) && match(terminals.t_num) &&
					match(terminals.t_closesq) && ArraySize2())
			{
				System.out.println("ArraySize2 -> [ num ] ArraySize2");
				parseInfo += "ArraySize2 -> [ num ] ArraySize2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.ArraySize2.isInFollow(lookAhead.getType())){
			System.out.println("ArraySize2 -> epsilon");
			parseInfo += "ArraySize2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//30. Type -> int | float | id
	static boolean Type(){
		boolean success = skipErrors(nonTerminals.Type);
		if (lookAhead.getType().equals("int")){
			if (match(terminals.t_int))
			{
				System.out.println("Type -> int");
				parseInfo += "Type -> int" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("float")){
			if (match(terminals.t_float))
			{
				System.out.println("Type -> float");
				parseInfo += "Type -> float" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("id")){
			if (match(terminals.t_id))
			{
				System.out.println("Type -> id");
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

	//31. Fparams -> Type id ArraySize FParamsTail | epsilon
	static boolean Fparams(){
		boolean success = skipErrors(nonTerminals.Fparams);
		if (nonTerminals.Type.isInFirst(lookAhead.getType())){
			if (Type() && match(terminals.t_id) && ArraySize() && FParamsTail()){
				System.out.println("Fparams -> Type id ArraySize FParamsTail ");
				parseInfo += "Fparams -> Type id ArraySize FParamsTail " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Fparams.isInFollow(lookAhead.getType())){
			System.out.println("Fparams -> epsilon");
			parseInfo += "Fparams -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//32. Aparams -> Expr AParamsTail | epsilon
	static boolean Aparams(){
		boolean success = skipErrors(nonTerminals.Aparams);
		if (nonTerminals.Expr.isInFirst(lookAhead.getType())){
			if (Expr() && AParamsTail()){
				System.out.println("Aparams -> Expr AParamsTail ");
				parseInfo += "Aparams -> Expr AParamsTail " + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.Aparams.isInFollow(lookAhead.getType())){
			System.out.println("Aparams -> epsilon");
			parseInfo += "Aparams -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//33. FParamsTail -> , Type id ArraySize FParamsTail2 | FParamsTail2
	static boolean FParamsTail(){
		boolean success = skipErrors(nonTerminals.FParamsTail);
		if (lookAhead.getType().equals("comma")){
			if (match(terminals.t_comma) && Type() && match(terminals.t_id) &&
					ArraySize() && FParamsTail2())
			{
				System.out.println("FParamsTail -> , Type id ArraySize FParamsTail2");
				parseInfo += "FParamsTail -> , Type id ArraySize FParamsTail2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.FParamsTail.isInFollow(lookAhead.getType())){
			System.out.println("FParamsTail -> epsilon");
			parseInfo += "FParamsTail -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//34. FParamsTail2 -> , Type id ArraySize FParamsTail2 | epsilon
	static boolean FParamsTail2(){
		boolean success = skipErrors(nonTerminals.FParamsTail2);
		if (lookAhead.getType().equals("comma")){
			if (match(terminals.t_comma) && Type() && match(terminals.t_id) &&
					ArraySize() && FParamsTail2())
			{
				System.out.println("FParamsTail2 -> , Type id ArraySize FParamsTail2");
				parseInfo += "FParamsTail2 -> , Type id ArraySize FParamsTail2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.FParamsTail2.isInFollow(lookAhead.getType())){
			System.out.println("FParamsTail2 -> epsilon");
			parseInfo += "FParamsTail2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//35. AParamsTail -> , Expr AParamsTail2 | AParamsTail2
	static boolean AParamsTail(){
		boolean success = skipErrors(nonTerminals.AParamsTail);
		if (lookAhead.getType().equals("comma")){
			if (match(terminals.t_comma) && Expr() && AParamsTail2())
			{
				System.out.println("AParamsTail -> , Expr AParamsTail2");
				parseInfo += "AParamsTail -> , Expr AParamsTail2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.AParamsTail.isInFollow(lookAhead.getType())){
			System.out.println("AParamsTail -> epsilon");
			parseInfo += "AParamsTail -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}

	//36. AParamsTail2 -> , Expr AParamsTail2 | epsilon
	static boolean AParamsTail2(){
		boolean success = skipErrors(nonTerminals.AParamsTail2);
		if (lookAhead.getType().equals("comma")){
			if (match(terminals.t_comma) && Expr() && AParamsTail2())
			{
				System.out.println("AParamsTail2 -> , Expr AParamsTail2");
				parseInfo += "AParamsTail2 -> , Expr AParamsTail2" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (nonTerminals.AParamsTail2.isInFollow(lookAhead.getType())){
			System.out.println("AParamsTail2 -> epsilon");
			parseInfo += "AParamsTail2 -> epsilon" + "\r\n";
		}
		else {
			success = false;
		}
		return success;
	}
	
	//37. AssignOp -> =
	static boolean AssignOp(){
		boolean success = skipErrors(nonTerminals.AssignOp);
		if (lookAhead.getType().equals("assignop")){
			if (match(terminals.t_assignop))
			{
				System.out.println("AssignOp -> =");
				parseInfo += "AssignOp -> =" + "\r\n";
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
	
	//38. RelOp -> == | <> | < | > | <= | >=
	static boolean RelOp(){
		boolean success = skipErrors(nonTerminals.RelOp);
		if (lookAhead.getType().equals("relop_e")){
			if (match(terminals.t_relop_e))
			{
				System.out.println("RelOp -> ==");
				parseInfo += "RelOp -> ==" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("lege")){
			if (match(terminals.t_lege))
			{
				System.out.println("RelOp -> <>");
				parseInfo += "RelOp -> <>" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("relop_l")){
			if (match(terminals.t_relop_l))
			{
				System.out.println("RelOp -> <");
				parseInfo += "RelOp -> <" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("relop_g")){
			if (match(terminals.t_relop_g))
			{
				System.out.println("RelOp -> >");
				parseInfo += "RelOp -> >" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("relop_le")){
			if (match(terminals.t_relop_le))
			{
				System.out.println("RelOp -> <=");
				parseInfo += "RelOp -> <=" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("relop_ge")){
			if (match(terminals.t_relop_ge))
			{
				System.out.println("RelOp -> >=");
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
	
	//39. AddOp -> + | - | or
	static boolean AddOp(){
		boolean success = skipErrors(nonTerminals.AddOp);
		if (lookAhead.getType().equals("add")){
			if (match(terminals.t_add))
			{
				System.out.println("AddOp -> +");
				parseInfo += "AddOp -> +" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("sub")){
			if (match(terminals.t_sub))
			{
				System.out.println("AddOp -> -");
				parseInfo += "AddOp -> -" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("or")){
			if (match(terminals.t_or))
			{
				System.out.println("AddOp -> or");
				parseInfo += "AddOp -> or" + "\r\n";
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
	
	//40. MultOp -> * | / | and
	static boolean MultOp(){
		boolean success = skipErrors(nonTerminals.MultOp);
		if (lookAhead.getType().equals("mul")){
			if (match(terminals.t_mul))
			{
				System.out.println("MultOp -> *");
				parseInfo += "MultOp -> *" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("div")){
			if (match(terminals.t_div))
			{
				System.out.println("MultOp -> /");
				parseInfo += "MultOp -> /" + "\r\n";
			}
			else {
				success = false;
			}
		}
		else if (lookAhead.getType().equals("and")){
			if (match(terminals.t_and))
			{
				System.out.println("MultOp -> and");
				parseInfo += "MultOp -> and" + "\r\n";
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

	static Token nextTokenParser(){
		Token t = null;
		if (tokenArrayLocation < tokenArray.size() ){
			t = tokenArray.get(tokenArrayLocation);
			//System.out.println("tokenArrayLocation: " + tokenArrayLocation + ", token content: " + t);
			tokenArrayLocation++;
		}
		return t;
	}
	
	//Error Handling
	static boolean match(Terminal t) {
		//System.out.println("match lookahead: " + lookAhead.getType() +
		//		", terminal: " + t.getName());
		if (lookAhead.getType().equals(t.getName())){
			lookAhead = nextTokenParser();
			return true;
		}
		else {
			//write ("syntax error at" lookahead.location. "expected" token)
			System.out.println("Syntax error at: " + lookAhead.getLexeme() + ". Expected: " + t.getSymbol());
			
			lookAhead = nextTokenParser();
			return false;
		}
	}
	
	//Error Handling
	static boolean skipErrors(NonTerminal nt){
		
		//case no error detected:
		//if (lookahead is in nt.first or nt.follow
			//return true
		//System.out.print("In skipErrors: " + lookAhead.getType());
		//System.out.println(nt);
		//for(int i = 0; i < nt.getFirstSet().size(); i++){
			//System.out.println("nt first set:" + nt.getFirstSet().get(i));
		//}
		if (nt.isInFirst(lookAhead.getType()) || nt.isInFollow(lookAhead.getType())){
			//System.out.println("No errors for nonterminal " + nt.getSymbol());
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
			//System.out.println("Syntax error at: " + lookAhead.getCount() + ".");
			//System.out.println("nonterminal: " + nt.getSymbol());
			while (!nt.isInFirst(lookAhead.getType()) && !nt.isInFollow(lookAhead.getType()) && tokenArrayLocation < tokenArray.size() ){
				lookAhead = nextTokenParser();
			}
			System.out.println("Parsing resumed at: " + lookAhead.getCount() + ".");
			return false;
		}

	}
	

	////////////////////////////////////////////////LEXICAL ANALYZER CODE/////////////////////////////////////////////////
	static void lexicalAnalyzer(){
		//get file from stream into char array
		String filePath = "C:/Users/Si/git/compiler/compiler/profprovided.txt";
		String tokenFilePath = "C:/Users/Si/git/compiler/compiler/tokens.txt";
		String errorFilePath = "C:/Users/Si/git/compiler/compiler/errorMessages.txt";
		try {
			fileContent = ReadFileToCharArray(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		printFileContent();
		System.out.println("number of characters: " + fileContent.length);
		// create lookup table
		createLookupTable();
		
		//call next token until reach end of file
		while(!fileComplete){
			Token t = nextToken();
			if (t != null){
				if (!t.getType().equals("error")){
					System.out.println(t.toString());
					tokens += t.toString();
					//ignore comments for parser
					if (!t.getType().equals("comment") && !t.getType().equals("comment_long")){
						tokenArray.add(t);
					}
				}
			}
			else{
				//add EOF symbol $ for parser
				tokenArray.add(new Token("eof", "$", currentLocation + 1, 1));
				System.out.println("Reached end of file.");
			}
			//if token type is an error, do not write to file
		}
		WriteToFile(tokenFilePath, tokens);
		WriteToFile(errorFilePath, errorMessages);
		
	}
	
	//returns next Token
	static Token nextToken(){
		
		Token newToken = null;
		boolean errorFound = false;
		while (newToken == null && !fileComplete && !errorFound){
			String nextCharacter = nextChar();
			//System.out.println("next character: " + nextCharacter + ", state: " + state);
			
			String lookup = nextCharacter;
			if (nextCharacter != null){
				lexeme += nextCharacter;
				//System.out.println("current lexeme: " + lexeme);
				int xIndex = convertColumnToXIndex(lookup);
				int yIndex = convertStateToYIndex(state);
				//System.out.println("xIndex: " + xIndex);
				
				if (xIndex == 99){
					yIndex = 12;
					xIndex = 11;
					//handle error of something not under the declared alphabet
					lexeme = lexeme.trim();
					String error = "Error occurred with the following set of characters: " + lexeme 
							+ " at location " + (currentLocation-lexeme.length()) + "\r\n";
					errorMessages += error;
					System.out.println(error);
					System.out.println("Commencing panic mode token recovery...");
					state = "1";
					errorFound = true;
					newToken = new Token("error", lexeme, currentLocation-lexeme.length(), lexeme.length());
					lexeme = "";
				}
				else{
					String oldState = state;
					state = lookupTable[yIndex][xIndex];
					
					//handle state 12
					if (isFinalState(yIndex)){
						if(isBackup(yIndex)){
							currentLocation--;
							currentLocation--;
							lexeme = lexeme.substring(0, lexeme.length()-2);
							lexeme = lexeme.trim();
							newToken = createToken(oldState);
						}
						else{
							currentLocation--;
							lexeme = lexeme.substring(0, lexeme.length()-1);
							lexeme = lexeme.trim();
							newToken = createToken(oldState);
						}
	
					}
				}
			}
			else {
				fileComplete = true;
				return null;
			}
		}
	return newToken;	
	}
	
	//returns nextChar;
	static String nextChar(){
		if (currentLocation == fileContent.length){
			return null;
		}
		else {
			String c = Character.toString(fileContent[currentLocation]);
			currentLocation++;
			return c;
		}
	}
	
	//converts string to index location on the x-axis
	static int convertColumnToXIndex(String s){
		int i = 99;
		if (s.equals(""))
			i = 0;
		else if (s.matches("[1-9]"))
			i = 1;
		else if (s.equals("0"))
			i = 2;
		else if (s.matches("[a-zA-Z]"))
			i = 3;
		else if (s.equals("."))
			i = 4;
		else if (s.equals("/"))
			i = 5;
		else if (s.equals("*"))
			i = 6;
		else if (s.equals("<"))
			i = 7;
		else if (s.equals(">"))
			i = 8;
		else if (s.equals("="))
			i = 9;
		else if (s.equals("_"))
			i = 10;
		else if (s.equals(";"))
			i = 11;
		else if (s.equals(","))
			i = 12;
		else if (s.equals("+"))
			i = 13;
		else if (s.equals("-"))
			i = 14;
		else if (s.equals("{"))
			i = 15;
		else if (s.equals("}"))
			i = 16;
		else if (s.equals("("))
			i = 17;
		else if (s.equals(")"))
			i = 18;
		else if (s.equals("["))
			i = 19;
		else if (s.equals("]"))
			i = 20;
		else if (s.equals("\r") || s.equals("\n") || s.equals("\r\n") || s.equals(null))
			i = 21;
		else if (s.equals(" "))
			i = 22;
		else if (s.equals("final"))
			i = 23;
		else if (s.equals("backup"))
			i = 24;
		return i;
	}

	//converts string to index location on the y-axis
	static int convertStateToYIndex(String s){
		int i = 0;
		if (!s.equals(""))
			i = Integer.parseInt(s);
		return i;
	}
	
	//checks if state is a final state
	static boolean isFinalState(int yIndex){
		boolean isFinal = false;
		int xIndex = 23;
		// if s is one of the final states, return true
		if (!lookupTable[yIndex][xIndex].equals("no")){
			isFinal = true;
		}
		return isFinal;
	}
	
	//checks if state needs to back up
	static boolean isBackup(int yIndex){
		boolean isBackup = false;
		int xIndex = 24;
		// if s is one of the final states, return true
		if (lookupTable[yIndex][xIndex].equals("yes")){
			isBackup = true;
		}
		return isBackup;
	}
	
	//checks if string is an integer
	static boolean isInteger(String s) {
	    return isInteger(s,10);
	}
	
	//helper method for string is integer check
	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	//creates Token object
	static Token createToken(String s){
		// check final state to see Token type
		Token t;
		if(s.equals("5")){
			if (isInteger(lexeme)){
				if (Integer.parseInt(lexeme) > 0){
					t = new Token("INT", lexeme, currentLocation-lexeme.length(), lexeme.length());
				}
				else {
					t = new Token("num", lexeme, currentLocation-lexeme.length(), lexeme.length());
				}
			}
			else {
				t = new Token("num", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
		}
		else if(s.equals("11"))
			if (lexeme.equals("and")){
				t = new Token("and", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("not")){
				t = new Token("not", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("or")){
				t = new Token("or", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("if")){
				t = new Token("if", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("then")){
				t = new Token("then", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("else")){
				t = new Token("else", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("for")){
				t = new Token("for", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("class")){
				t = new Token("class", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("int")){
				t = new Token("int", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("float")){
				t = new Token("float", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("get")){
				t = new Token("get", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("put")){
				t = new Token("put", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("return")){
				t = new Token("return", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if (lexeme.equals("program")){
				t = new Token("program", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else{
				t = new Token("id", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
		else if(s.equals("12")){
			t = new Token("error", lexeme, currentLocation-lexeme.length(), lexeme.length());
			String error = "Error occurred with the following set of characters: " + lexeme 
					+ " at location " + (currentLocation-lexeme.length()) + "\r\n";
			errorMessages += error;
			System.out.println(error);
			System.out.println("Commencing panic mode token recovery...");
		}
		else if(s.equals("16"))
			t = new Token("comment_long", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("18"))
			t = new Token("comment", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("20"))
			t = new Token("relop_ge", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("21"))
			t = new Token("relop_gl", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("23"))
			t = new Token("relop_le", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("25"))
			t = new Token("relop_e", lexeme, currentLocation-lexeme.length(), lexeme.length());
		else if(s.equals("26")){
			if (lexeme.equals("/")){
				t = new Token("div", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("<")){
				t = new Token("relop_l", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals(">")){
				t = new Token("relop_g", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("=")){
				t = new Token("assignop", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else {
				t = new Token("error at 26", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
		}
		else if(s.equals("27")){
			if (lexeme.equals(";")){
				t = new Token("semi", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals(",")){
				t = new Token("comma", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals(".")){
				t = new Token("dot", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("+")){
				t = new Token("add", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("-")){
				t = new Token("sub", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("*")){
				t = new Token("mul", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("(")){
				t = new Token("openpar", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals(")")){
				t = new Token("closepar", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("[")){
				t = new Token("opensq", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("]")){
				t = new Token("closesq", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("{")){
				t = new Token("opencur", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else if(lexeme.equals("}")){
				t = new Token("closecur", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
			else {
				t = new Token("error at 27", lexeme, currentLocation-lexeme.length(), lexeme.length());
			}
		}
		else
			t = new Token("error out of loop", lexeme, currentLocation-lexeme.length(), lexeme.length());
		
		lexeme = "";
		return t;
	}

	//returns corresponding entry in 2D array
	static String tableLookup(int state, int column){
		String s = lookupTable[state][column];
		System.out.println("tableLookup: " + s);
		return s;
	}
	
	//prints the lookup table
	static void printLookupTable(){
		for(int i = 0; i<lookupTable.length; i++){
			for(int j = 0; j <lookupTable[i].length; j++){
				System.out.print(lookupTable[i][j] + ",");
			}
			System.out.println();
		}
	}
	
	//creates the lookup table
	static void createLookupTable(){

		String tempArray0[] = {"","[1-9]", "0", "[a-zA-Z]", ".", "/", "*", "<", ">", "=", "_", ";", ",", "+", "-", "{", "}", "(", ")", "[", "]", "\n || null", " ", "final", "backup"};
		String tempArray1[] = {"1", "2", "9", "10", "27", "13", "27", "19", "22", "24", "12", "27", "27", "27", "27", "27", "27", "27", "27", "27", "27", "1", "1", "no", "no"};
		String tempArray2[] = {"2", "2", "2", "5", "3", "5", "5", "5", "5", "5", "12", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "no", "no"};
		String tempArray3[] = {"3", "6", "4", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "no", "no"};
		String tempArray4[] = {"4", "6", "7", "5", "5", "5", "5", "5", "5", "5", "12", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "no", "no"};
		String tempArray5[] = {"5", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[num]", "yes"};
		String tempArray6[] = {"6", "6", "8", "5", "5", "5", "5", "5", "5", "5", "12", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "no", "no"};
		String tempArray7[] = {"7", "6", "8", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "no", "no"};
		String tempArray8[] = {"8", "6", "8", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "no", "no"};
		String tempArray9[] = {"9", "5", "5", "5", "3", "5", "5", "5", "5", "5", "12", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "no", "no"};
		String tempArray10[] = {"10", "10", "10", "10", "11", "11", "11", "11", "11", "11", "10", "11", "11", "11", "11", "11", "11", "11", "11", "11", "11", "11", "11", "no", "no"};
		String tempArray11[] = {"11", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[id]", "yes"};
		String tempArray12[] = {"12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[error]", "no"};
		String tempArray13[] = {"13", "26", "26", "26", "26", "17", "14", "26", "26", "26", "12", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "no", "no"};
		String tempArray14[] = {"14", "14", "14", "14", "14", "14", "15", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "no", "no"};
		String tempArray15[] = {"15", "14", "14", "14", "14", "16", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "14", "no", "no"};
		String tempArray16[] = {"16", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[/*..*/]", "no"};
		String tempArray17[] = {"17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "17", "18", "17", "no", "no"};
		String tempArray18[] = {"18", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[//..]", "no"};
		String tempArray19[] = {"19", "26", "26", "26", "26", "26", "26", "26", "21", "20", "12", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "no", "no"};
		String tempArray20[] = {"20", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[relop_ge]", "no"};
		String tempArray21[] = {"21", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[relop_gl]", "no"};
		String tempArray22[] = {"22", "26", "26", "26", "26", "26", "26", "26", "26", "23", "12", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "no", "no"};
		String tempArray23[] = {"23", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[relop_le]", "no"};
		String tempArray24[] = {"24", "26", "26", "26", "26", "26", "26", "26", "26", "25", "12", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "26", "no", "no"};
		String tempArray25[] = {"25", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[relop_e]", "no"};
		String tempArray26[] = {"26", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[various]", "yes"};
		String tempArray27[] = {"27", "1", "1", "1", "1", "1", "1", "1", "1", "1", "12", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "[various]", "no"};
		
		for (int i = 0; i< tempArray0.length; i++){
			lookupTable[0][i] = tempArray0[i];
			lookupTable[1][i] = tempArray1[i];
			lookupTable[2][i] = tempArray2[i];
			lookupTable[3][i] = tempArray3[i];
			lookupTable[4][i] = tempArray4[i];
			lookupTable[5][i] = tempArray5[i];
			lookupTable[6][i] = tempArray6[i];
			lookupTable[7][i] = tempArray7[i];
			lookupTable[8][i] = tempArray8[i];
			lookupTable[9][i] = tempArray9[i];
			lookupTable[10][i] = tempArray10[i];
			lookupTable[11][i] = tempArray11[i];
			lookupTable[12][i] = tempArray12[i];
			lookupTable[13][i] = tempArray13[i];
			lookupTable[14][i] = tempArray14[i];
			lookupTable[15][i] = tempArray15[i];
			lookupTable[16][i] = tempArray16[i];
			lookupTable[17][i] = tempArray17[i];
			lookupTable[18][i] = tempArray18[i];
			lookupTable[19][i] = tempArray19[i];
			lookupTable[20][i] = tempArray20[i];
			lookupTable[21][i] = tempArray21[i];
			lookupTable[22][i] = tempArray22[i];
			lookupTable[23][i] = tempArray23[i];
			lookupTable[24][i] = tempArray24[i];
			lookupTable[25][i] = tempArray25[i];
			lookupTable[26][i] = tempArray26[i];
			lookupTable[27][i] = tempArray27[i];
		}
	}
	
	//prints file content
	static void printFileContent(){
		for (int i = 0; i<fileContent.length; i++)
			System.out.print(fileContent[i]);
		System.out.println();
	}
	
	//returns char array of file read
	static char[] ReadFileToCharArray(String filePath) throws IOException {
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
	
	static void WriteToFile(String filePath, String c){
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
