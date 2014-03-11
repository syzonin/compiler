import java.util.ArrayList;


public class NonTerminalCollection {
	
	public NonTerminal ClassDecl;
	public NonTerminal ProgBody;
	public NonTerminal FuncBody;
	public NonTerminal Statement;
	public NonTerminal StatBlock;
	public NonTerminal Sign;
	public NonTerminal Factor;
	public NonTerminal Fparams;
	public NonTerminal FParamsTail;
	public NonTerminal AParamsTail;
	public NonTerminal AssignOp;
	public NonTerminal AddOp;
	public NonTerminal Prog;
	public NonTerminal VarDecl;
	public NonTerminal FuncDef;
	public NonTerminal Term;
	public NonTerminal Expr;
	public NonTerminal ArithExpr2;
	public NonTerminal Term2;
	public NonTerminal Idnest;
	public NonTerminal Indice;
	public NonTerminal ArraySize;
	public NonTerminal Type;
	public NonTerminal Aparams;
	public NonTerminal RelOp;
	public NonTerminal MultOp;
	public NonTerminal FuncHead;
	public NonTerminal Variable;
	public NonTerminal AssignStat;
	public NonTerminal ArithExpr;
	public NonTerminal RelExpr;
	
	public ArrayList<NonTerminal> NonTerminalArray = new ArrayList<NonTerminal>();

	public NonTerminalCollection() {
		
		ArrayList<String> first = new ArrayList<String>();
		ArrayList<String> follow = new ArrayList<String>();
		
		//1
		ClassDecl = new NonTerminal("ClassDecl");
		ClassDecl.addFirst("class");
		ClassDecl.addFollow("program");

		//2
		ProgBody = new NonTerminal("ProgBody");
		ProgBody.addFirst("program");
		ProgBody.addFollow("eof");


		//3
		FuncBody = new NonTerminal("FuncBody");
		FuncBody.addFirst("{");
		FuncBody.addFollow("int");
		FuncBody.addFollow("float");
		FuncBody.addFollow("id");
		FuncBody.addFollow("semi");

		//4
		Statement = new NonTerminal("Statement");
		Statement.addFirst("if");
		Statement.addFirst("for");
		Statement.addFirst("get");
		Statement.addFirst("put");
		Statement.addFirst("return");
		Statement.addFirst("id");
		Statement.addFollow("closecur");
		Statement.addFollow("else");
		Statement.addFollow("semi");

		
		//5
		StatBlock = new NonTerminal("StatBlock");
		StatBlock.addFirst("opencur");
		StatBlock.addFirst("epsilon");
		StatBlock.addFirst("if");
		StatBlock.addFirst("for");
		StatBlock.addFirst("get");
		StatBlock.addFirst("put");
		StatBlock.addFirst("return");
		StatBlock.addFirst("id");
		StatBlock.addFollow("else");
		StatBlock.addFollow("semi");
		
		//6
		Sign = new NonTerminal("Sign");
		Sign.addFirst("add");
		Sign.addFirst("sub");
		Sign.addFollow("num");
		Sign.addFollow("openpar");
		Sign.addFollow("not");
		Sign.addFollow("id");
		Sign.addFollow("add");
		Sign.addFollow("sub");
		
		//7
		Factor = new NonTerminal("Factor");
		Factor.addFirst("num");
		Factor.addFirst("openpar");
		Factor.addFirst("not");
		Factor.addFirst("id");
		Factor.addFirst("add");
		Factor.addFirst("sub");
		Factor.addFollow("mul");
		Factor.addFollow("div");
		Factor.addFollow("and");

		//8
		Fparams = new NonTerminal("Fparams");
		Fparams.addFirst("epsilon");
		Fparams.addFirst("int");
		Fparams.addFirst("float");
		Fparams.addFirst("id");
		Fparams.addFollow("closepar");

		//9
		FParamsTail = new NonTerminal("FParamsTail");
		FParamsTail.addFirst("comma");
		FParamsTail.addFollow("closepar");

		//10
		AParamsTail = new NonTerminal("AParamsTail");
		AParamsTail.addFirst("comma");
		AParamsTail.addFollow("closepar");

		//11
		AssignOp = new NonTerminal("AssignOp");
		AssignOp.addFirst("assignop");
		AssignOp.addFollow("num");
		AssignOp.addFollow("openpar");
		AssignOp.addFollow("not");
		AssignOp.addFollow("id");
		AssignOp.addFollow("add");
		AssignOp.addFollow("sub");

		//12
		AddOp = new NonTerminal("AddOp");
		AddOp.addFirst("add");
		AddOp.addFirst("sub");
		AddOp.addFirst("or");
		AddOp.addFollow("num");
		AddOp.addFollow("openpar");
		AddOp.addFollow("not");
		AddOp.addFollow("id");
		AddOp.addFollow("add");
		AddOp.addFollow("sub");

		//13
		Prog = new NonTerminal("Prog");
		Prog.addFirst("class");
		Prog.addFollow("eof");

		//14
		VarDecl = new NonTerminal("VarDecl");
		VarDecl.addFirst("int");
		VarDecl.addFirst("float");
		VarDecl.addFirst("id");
		VarDecl.addFollow("semi");
		VarDecl.addFollow("if");
		VarDecl.addFollow("for");
		VarDecl.addFollow("get");
		VarDecl.addFollow("put");
		VarDecl.addFollow("return");
		VarDecl.addFollow("id");
		VarDecl.addFollow("int");
		VarDecl.addFollow("float");
		
		//15
		FuncDef = new NonTerminal("FuncDef");
		FuncDef.addFirst("int");
		FuncDef.addFirst("float");
		FuncDef.addFirst("id");
		FuncDef.addFollow("closecur");
		FuncDef.addFollow("eof");

		//16
		Term = new NonTerminal("Term");
		Term.addFirst("num");
		Term.addFirst("openpar");
		Term.addFirst("not");
		Term.addFirst("id");
		Term.addFirst("add");
		Term.addFirst("sub");
		Term.addFollow("add");
		Term.addFollow("sub");
		Term.addFollow("or");

		//17
		Expr = new NonTerminal("Expr");
		Expr.addFirst("num");
		Expr.addFirst("openpar");
		Expr.addFirst("not");
		Expr.addFirst("id");
		Expr.addFirst("add");
		Expr.addFirst("sub");
		Expr.addFollow("comma");
		Expr.addFollow("closepar");
		Expr.addFollow("semi");

		//18
		ArithExpr2 = new NonTerminal("ArithExpr2");
		ArithExpr2.addFirst("epsilon");
		ArithExpr2.addFirst("add");
		ArithExpr2.addFirst("sub");
		ArithExpr2.addFirst("or");
		ArithExpr2.addFollow("closesq");
		ArithExpr2.addFollow("closepar");
		ArithExpr2.addFollow("relop_e");
		ArithExpr2.addFollow("lege");
		ArithExpr2.addFollow("relop_l");
		ArithExpr2.addFollow("relop_g");
		ArithExpr2.addFollow("relop_le");
		ArithExpr2.addFollow("relop_ge");
		ArithExpr2.addFollow("semi");
		ArithExpr2.addFollow("comma");
		ArithExpr2.addFollow("eof");
		
		//19
		Term2 = new NonTerminal("Term2");
		Term2.addFirst("epsilon");
		Term2.addFirst("mul");
		Term2.addFirst("div");
		Term2.addFirst("and");
		Term2.addFollow("add");
		Term2.addFollow("sub");
		Term2.addFollow("or");
		
		//20
		Idnest = new NonTerminal("Idnest");
		Idnest.addFirst("id");
		Idnest.addFollow("id");
		
		//21
		Indice = new NonTerminal("Indice");
		Indice.addFirst("opensq");
		Indice.addFollow("dot");
		Indice.addFollow("assignop");
		Indice.addFollow("closepar");
		Indice.addFollow("mul");
		Indice.addFollow("div");
		Indice.addFollow("and");

		//22
		ArraySize = new NonTerminal("ArraySize");
		ArraySize.addFirst("opensq");
		ArraySize.addFollow("comma");
		ArraySize.addFollow("semi");

		//23
		Type = new NonTerminal("Type");
		Type.addFirst("int");
		Type.addFirst("float");
		Type.addFirst("id");
		Type.addFollow("id");

		//24
		Aparams = new NonTerminal("Aparams");
		Aparams.addFirst("epsilon");
		Aparams.addFirst("num");
		Aparams.addFirst("openpar");
		Aparams.addFirst("not");
		Aparams.addFirst("id");
		Aparams.addFirst("add");
		Aparams.addFirst("sub");
		Aparams.addFollow("closepar");
		
		//25
		RelOp = new NonTerminal("RelOp");
		RelOp.addFirst("relop_e");
		RelOp.addFirst("lege");
		RelOp.addFirst("relop_l");
		RelOp.addFirst("relop_g");
		RelOp.addFirst("relop_le");
		RelOp.addFirst("relop_ge");
		RelOp.addFollow("num");
		RelOp.addFollow("openpar");
		RelOp.addFollow("not");
		RelOp.addFollow("id");
		RelOp.addFollow("add");
		RelOp.addFollow("sub");

		//26
		MultOp = new NonTerminal("MultOp");
		MultOp.addFirst("mul");
		MultOp.addFirst("div");
		MultOp.addFirst("and");
		MultOp.addFollow("num");
		MultOp.addFollow("openpar");
		MultOp.addFollow("not");
		MultOp.addFollow("id");
		MultOp.addFollow("add");
		MultOp.addFollow("sub");

		//27
		FuncHead = new NonTerminal("FuncHead");
		FuncHead.addFirst("int");
		FuncHead.addFirst("float");
		FuncHead.addFirst("id");
		FuncHead.addFollow("opencur");
		
		//28
		Variable = new NonTerminal("Variable");
		Variable.addFirst("id");
		Variable.addFollow("assignop");
		Variable.addFollow("closecur");
		Variable.addFollow("mul");
		Variable.addFollow("div");
		Variable.addFollow("and");

		//29
		AssignStat = new NonTerminal("AssignStat");
		AssignStat.addFirst("id");
		AssignStat.addFollow("semi");
		AssignStat.addFollow("closepar");

		//30
		ArithExpr = new NonTerminal("ArithExpr");
		ArithExpr.addFirst("num");
		ArithExpr.addFirst("openpar");
		ArithExpr.addFirst("not");
		ArithExpr.addFirst("id");
		ArithExpr.addFirst("add");
		ArithExpr.addFirst("sub");
		ArithExpr.addFollow("closesq");
		ArithExpr.addFollow("closepar");
		ArithExpr.addFollow("relop_e");
		ArithExpr.addFollow("lege");
		ArithExpr.addFollow("relop_l");
		ArithExpr.addFollow("relop_g");
		ArithExpr.addFollow("relop_le");
		ArithExpr.addFollow("relop_ge");
		ArithExpr.addFollow("semi");
		ArithExpr.addFollow("comma");
		ArithExpr.addFollow("eof");

		//31
		RelExpr = new NonTerminal("RelExpr");
		RelExpr.addFirst("num");
		RelExpr.addFirst("openpar");
		RelExpr.addFirst("not");
		RelExpr.addFirst("id");
		RelExpr.addFirst("add");
		RelExpr.addFirst("sub");
		RelExpr.addFollow("semi");
		RelExpr.addFollow("comma");
		RelExpr.addFollow("closepar");
		
	}
	
}
