import java.util.ArrayList;


public class NonTerminalCollection {
	
	public NonTerminal ClassDecl;
	public NonTerminal ProgBody;
	public NonTerminal FuncBody;
	public NonTerminal Statement;
	public NonTerminal StatBlock;
	public NonTerminal Sign;
	public NonTerminal Factor;
	public NonTerminal Idnest2;
	public NonTerminal Indice2;
	public NonTerminal ArraySize2;
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
	public NonTerminal ClassDecl2;
	public NonTerminal FuncDef2;
	public NonTerminal VarDecl2;
	public NonTerminal Statement2;
	public NonTerminal ArithExpr2;
	public NonTerminal Term2;
	public NonTerminal Idnest;
	public NonTerminal Indice;
	public NonTerminal ArraySize;
	public NonTerminal Type;
	public NonTerminal Aparams;
	public NonTerminal FParamsTail2;
	public NonTerminal AParamsTail2;
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
		ClassDecl.addFollow("id");

		//2
		ProgBody = new NonTerminal("ProgBody");
		ProgBody.addFirst("id");
		ProgBody.addFollow("eof");


		//3
		FuncBody = new NonTerminal("FuncBody");
		FuncBody.addFirst("{");
		FuncBody.addFollow("eof");
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
		Factor.addFollow("eof");
		Factor.addFollow("mul");
		Factor.addFollow("div");
		Factor.addFollow("and");

		//8
		Idnest2 = new NonTerminal("Idnest2");
		Idnest2.addFirst("id");
		Idnest2.addFirst("epsilon");
		Idnest2.addFollow("id");

		//9
		Indice2 = new NonTerminal("Indice2");
		Indice2.addFirst("opensq");
		Indice2.addFirst("epsilon");
		Indice2.addFollow("dot");
		Indice2.addFollow("assignop");
		Indice2.addFollow("closepar");
		Indice2.addFollow("eof");
		Indice2.addFollow("mul");
		Indice2.addFollow("div");
		Indice2.addFollow("and");

		//10
		ArraySize2 = new NonTerminal("ArraySize2");
		ArraySize2.addFirst("opensq");
		ArraySize2.addFirst("epsilon");
		ArraySize2.addFollow("comma");
		ArraySize2.addFollow("semi");
		
		//11
		Fparams = new NonTerminal("Fparams");
		Fparams.addFirst("epsilon");
		Fparams.addFirst("int");
		Fparams.addFirst("float");
		Fparams.addFirst("id");
		Fparams.addFollow("closepar");

		//12
		FParamsTail = new NonTerminal("FParamsTail");
		FParamsTail.addFirst("comma");
		FParamsTail.addFollow("closepar");

		//13
		AParamsTail = new NonTerminal("AParamsTail");
		AParamsTail.addFirst("comma");
		AParamsTail.addFollow("closepar");

		//14
		AssignOp = new NonTerminal("AssignOp");
		AssignOp.addFirst("assignop");
		AssignOp.addFollow("num");
		AssignOp.addFollow("openpar");
		AssignOp.addFollow("not");
		AssignOp.addFollow("id");
		AssignOp.addFollow("add");
		AssignOp.addFollow("sub");

		//15
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

		//16
		Prog = new NonTerminal("Prog");
		Prog.addFirst("class");
		Prog.addFollow("eof");

		//17
		VarDecl = new NonTerminal("VarDecl");
		VarDecl.addFirst("int");
		VarDecl.addFirst("float");
		VarDecl.addFirst("id");
		VarDecl.addFollow("if");
		VarDecl.addFollow("for");
		VarDecl.addFollow("get");
		VarDecl.addFollow("put");
		VarDecl.addFollow("return");
		VarDecl.addFollow("id");
		VarDecl.addFollow("int");
		VarDecl.addFollow("float");
		
		//18
		FuncDef = new NonTerminal("FuncDef");
		FuncDef.addFirst("int");
		FuncDef.addFirst("float");
		FuncDef.addFirst("id");
		FuncDef.addFollow("closecur");
		FuncDef.addFollow("eof");

		//19
		Term = new NonTerminal("Term");
		Term.addFirst("num");
		Term.addFirst("openpar");
		Term.addFirst("not");
		Term.addFirst("id");
		Term.addFirst("add");
		Term.addFirst("sub");
		Term.addFollow("eof");
		Term.addFollow("add");
		Term.addFollow("sub");
		Term.addFollow("or");

		//20
		Expr = new NonTerminal("Expr");
		Expr.addFirst("num");
		Expr.addFirst("openpar");
		Expr.addFirst("not");
		Expr.addFirst("id");
		Expr.addFirst("add");
		Expr.addFirst("sub");
		Expr.addFollow("comma");
		Expr.addFollow("eof");
		Expr.addFollow("closepar");
		Expr.addFollow("semi");
		
		//21
		ClassDecl2 = new NonTerminal("ClassDecl2");
		ClassDecl2.addFirst("class");
		ClassDecl2.addFirst("epsilon");
		ClassDecl2.addFollow("id");
		
		//22
		FuncDef2 = new NonTerminal("FuncDef2");
		FuncDef2.addFirst("epsilon");
		FuncDef2.addFirst("int");
		FuncDef2.addFirst("float");
		FuncDef2.addFirst("id");
		FuncDef2.addFollow("closecur");
		FuncDef2.addFollow("eof");

		//23
		VarDecl2 = new NonTerminal("VarDecl2");
		VarDecl2.addFirst("epsilon");
		VarDecl2.addFirst("int");
		VarDecl2.addFirst("float");
		VarDecl2.addFirst("id");
		VarDecl2.addFollow("if");
		VarDecl2.addFollow("for");
		VarDecl2.addFollow("get");
		VarDecl2.addFollow("put");
		VarDecl2.addFollow("return");
		VarDecl2.addFollow("id");
		VarDecl2.addFollow("int");
		VarDecl2.addFollow("float");

		//24
		Statement2 = new NonTerminal("Statement2");
		Statement2.addFirst("if");
		Statement2.addFirst("for");
		Statement2.addFirst("get");
		Statement2.addFirst("put");
		Statement2.addFirst("return");
		Statement2.addFirst("epsilon");
		Statement2.addFirst("id");
		Statement2.addFollow("closecur");
		Statement2.addFollow("else");
		Statement2.addFollow("semi");

		//25
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
		
		//26
		Term2 = new NonTerminal("Term2");
		Term2.addFirst("epsilon");
		Term2.addFirst("mul");
		Term2.addFirst("div");
		Term2.addFirst("and");
		Term2.addFollow("eof");
		Term2.addFollow("add");
		Term2.addFollow("sub");
		Term2.addFollow("or");
		
		//27
		Idnest = new NonTerminal("Idnest");
		Idnest.addFirst("id");
		Idnest.addFollow("id");
		
		//28
		Indice = new NonTerminal("Indice");
		Indice.addFirst("opensq");
		Indice.addFollow("dot");
		Indice.addFollow("assignop");
		Indice.addFollow("closepar");
		Indice.addFollow("eof");
		Indice.addFollow("mul");
		Indice.addFollow("div");
		Indice.addFollow("and");

		//29
		ArraySize = new NonTerminal("ArraySize");
		ArraySize.addFirst("opensq");
		ArraySize.addFollow("comma");
		ArraySize.addFollow("semi");

		//30
		Type = new NonTerminal("Type");
		Type.addFirst("int");
		Type.addFirst("float");
		Type.addFirst("id");
		Type.addFollow("id");

		//31
		Aparams = new NonTerminal("Aparams");
		Aparams.addFirst("epsilon");
		Aparams.addFirst("num");
		Aparams.addFirst("openpar");
		Aparams.addFirst("not");
		Aparams.addFirst("id");
		Aparams.addFirst("add");
		Aparams.addFirst("sub");
		Aparams.addFollow("closepar");

		//32
		FParamsTail2 = new NonTerminal("FParamsTail2");
		FParamsTail2.addFirst("comma");
		FParamsTail2.addFirst("epsilon");
		FParamsTail2.addFollow("closepar");

		//33
		AParamsTail2 = new NonTerminal("AParamsTail2");
		AParamsTail2.addFirst("comma");
		AParamsTail2.addFirst("epsilon");
		AParamsTail2.addFollow("closepar");
		
		//34
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

		//35
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

		//36
		FuncHead = new NonTerminal("FuncHead");
		FuncHead.addFirst("int");
		FuncHead.addFirst("float");
		FuncHead.addFirst("id");
		FuncHead.addFollow("opencur");
		
		//37
		Variable = new NonTerminal("Variable");
		Variable.addFirst("id");
		Variable.addFollow("assignop");
		Variable.addFollow("closecur");
		Variable.addFollow("eof");
		Variable.addFollow("mul");
		Variable.addFollow("div");
		Variable.addFollow("and");

		//38
		AssignStat = new NonTerminal("AssignStat");
		AssignStat.addFirst("id");
		AssignStat.addFollow("semi");
		AssignStat.addFollow("closepar");

		//39
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

		//40
		RelExpr = new NonTerminal("RelExpr");
		RelExpr.addFirst("num");
		RelExpr.addFirst("openpar");
		RelExpr.addFirst("not");
		RelExpr.addFirst("id");
		RelExpr.addFirst("add");
		RelExpr.addFirst("sub");
		RelExpr.addFollow("semi");
		RelExpr.addFollow("comma");
		RelExpr.addFollow("eof");
		RelExpr.addFollow("closepar");
		
	}
	
}
