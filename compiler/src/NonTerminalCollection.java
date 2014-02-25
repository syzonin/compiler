import java.util.ArrayList;


public class NonTerminalCollection {
	
	public NonTerminal ClassDeclList;
	public NonTerminal ClassDecl;
	public NonTerminal ClassMemberDeclList;
	public NonTerminal ProgBody;
	public NonTerminal FuncDefList;
	public NonTerminal FuncBody;
	public NonTerminal FuncBodyMemberList;
	public NonTerminal FuncBodyMember;
	public NonTerminal FuncBodyMember2;
	public NonTerminal FuncBodyMember3;
	public NonTerminal StatementList;
	public NonTerminal Statement2;
	public NonTerminal StatBlock;
	public NonTerminal Expr2;
	public NonTerminal ArithExpr2;
	public NonTerminal Sign;
	public NonTerminal Term2;
	public NonTerminal Factor;
	public NonTerminal Factor2;
	public NonTerminal Variable;
	public NonTerminal IdnestList;
	public NonTerminal IndiceList;
	public NonTerminal Indice;
	public NonTerminal ArraySizeList;
	public NonTerminal ArraySize;
	public NonTerminal Type;
	public NonTerminal FParams;
	public NonTerminal AParams;
	public NonTerminal FParamsTailList;
	public NonTerminal FParamsTail;
	public NonTerminal AParamsTailList;
	public NonTerminal AParamsTail;
	public NonTerminal RelOp;
	public NonTerminal AddOp;
	public NonTerminal MultOp;
	public NonTerminal ClassMemberDecl;
	public NonTerminal FuncHead;
	public NonTerminal Statement;
	public NonTerminal Prog;
	public NonTerminal FuncDef;
	public NonTerminal Term;
	public NonTerminal ArithExpr;
	public NonTerminal Expr;
	public NonTerminal ClassMemberDecl2;


	public NonTerminalCollection() {

		String[] first1 = {"class"};
		String[] follow1 = {"program"};
		ClassDeclList = new NonTerminal("ClassDeclList", first1, follow1, first1, null, null, null, null, null);
		
		String[] first2 = {"class"};
		String[] follow2 = {"class", "program"};
		ClassDecl = new NonTerminal("ClassDecl", first2, follow2, first2, null, null, null, null, null);
		
		String[] first3 = {"int", "float", "id"};
		String[] follow3 = {"int", "float", "id", "closecur"};
		ClassMemberDecl = new NonTerminal("ClassMemberDecl", first3, follow3, first3, null, null, null, null, null);
		
		String[] first4 = {"openpar", "opensq", "semi"};
		String[] rhs4_1 = {"opensq", "semi"};
		String[] rhs4_2 = {"openpar"};
		String[] follow4 = {"int", "float", "id", "closecur"};
		ClassMemberDecl2 = new NonTerminal("ClassMemberDecl2", first4, follow4, rhs4_1, rhs4_2, null, null, null, null);
		
		String[] first5 = {"int", "float", "id"};
		String[] follow5 = {"closecur"};
		ClassMemberDeclList = new NonTerminal("ClassMemberDeclList", first5, follow5, first5, null, null, null, null, null);
		
		String[] first6 = {"int", "float", "id"};
		String[] follow6 = {"eof"};
		FuncDefList = new NonTerminal("FuncDefList", first6, follow6, first6, null, null, null, null, null);
		
		String[] first7 = {"program"};
		String[] follow7 = {"eof"};
		ProgBody = new NonTerminal("ProgBody", first7, follow7, first7, null, null, null, null, null);
		
		String[] first8 = {"int", "float", "id"};
		String[] follow8 = {"opencur"};
		FuncHead = new NonTerminal("FuncHead", first8, follow8, first8, null, null, null, null, null);
		
		String[] first9 = {"int", "float", "id"};
		String[] follow9 = {"int", "float", "id", "eof"};
		FuncDef = new NonTerminal("FuncDef", first9, follow9, first9, null, null, null, null, null);
		
		String[] first10 = {"opencur"};
		String[] follow10 = {"semi"};
		FuncBody = new NonTerminal("FuncBody", first10, follow10, first10, null, null, null, null, null);
		
		String[] first11 = {"int", "float", "id", "if", "for", "get", "put", "return"};
		String[] rhs11_1 = {"if", "for", "get", "put", "return"};
		String[] rhs11_2 = {"id"};
		String[] rhs11_3 = {"float"};
		String[] rhs11_4 = {"int"};
		String[] follow11 = {"int", "float", "id", "if", "for", "get", "put", "return", "closecur"};
		FuncBodyMember = new NonTerminal("FuncBodyMember", first11, follow11, rhs11_1, rhs11_2, rhs11_3, rhs11_4, null, null);
		
		String[] first12 = {"id", "opensq", "dot", "assignop"};
		String[] rhs12_1 = {"opensq", "dot", "assignop"};
		String[] rhs12_2 = {"id"};
		String[] follow12 = {"int", "float", "id", "if", "for", "get", "put", "return", "closecur"};
		FuncBodyMember2 = new NonTerminal("FuncBodyMember2", first12, follow12, rhs12_1, rhs12_2, null, null, null, null);
		
		String[] first13 = {"dot", "assignop"};
		String[] rhs13_1 = {"assignop"};
		String[] rhs13_2 = {"dot"};
		String[] follow13 = {"int", "float", "id", "if", "for", "get", "put", "return", "closecur"};
		FuncBodyMember3 = new NonTerminal("FuncBodyMember3", first13, follow13, rhs13_1, rhs13_2, null, null, null, null);
		
		String[] first14 = {"int", "float", "id", "if", "for", "get", "put", "return"};
		String[] follow14 = {"closecur"};
		FuncBodyMemberList = new NonTerminal("FuncBodyMemberList", first14, follow14, first14, null, null, null, null, null);
		
		String[] first15 = {"int", "float", "id"};
		String[] rhs15_1 = {"float"};
		String[] rhs15_2 = {"int"};
		String[] rhs15_3 = {"id"};
		String[] follow15 = {"id"};
		Type = new NonTerminal("Type", first15, follow15, rhs15_1, rhs15_2, rhs15_3, null, null, null);
		
		String[] first16 = {"opensq"};
		String[] follow16 = {"comma", "semi", "closepar"};
		ArraySizeList = new NonTerminal("ArraySizeList", first16, follow16, first16, null, null, null, null, null);
		
		String[] first17 = {"id", "if", "for", "get", "put", "return"};
		String[] rhs17_1 = {"if", "for", "get", "put", "return"};
		String[] rhs17_2 = {"id"};
		String[] follow17 = {"id", "if", "for", "get", "put", "return", "else", "semi", "closecur"};
		Statement = new NonTerminal("Statement", first17, follow17, rhs17_1, rhs17_2, null, null, null, null);
		
		String[] first18 = {"if", "for", "get", "put", "return"};
		String[] rhs18_1 = {"if"};
		String[] rhs18_2 = {"for"};
		String[] rhs18_3 = {"get"};
		String[] rhs18_4 = {"put"};
		String[] rhs18_5 = {"return"};
		String[] follow18 = {"id", "if", "for", "get", "put", "return", "else", "semi", "int", "float", "closecur"};
		Statement2 = new NonTerminal("Statement2", first18, follow18, rhs18_1, rhs18_2, rhs18_3, rhs18_4, rhs18_5, null);
		
		String[] first19 = {"opencur", "id", "if", "for", "get", "put", "return"};
		String[] rhs19_1 = {"id", "if", "for", "get", "put", "return"};
		String[] rhs19_2 = {"opencur"};
		String[] follow19 = {"else", "semi"};
		StatBlock = new NonTerminal("StatBlock", first19, follow19, rhs19_1, rhs19_2, null, null, null, null);
		
		String[] first20 = {"id", "if", "for", "get", "put", "return"};
		String[] follow20 = {"closecur"};
		StatementList = new NonTerminal("StatementList", first20, follow20, first20, null, null, null, null, null);
		
		String[] first21 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		String[] follow21 = {"semi", "comma", "closepar"};
		Expr = new NonTerminal("Expr", first21, follow21, first21, null, null, null, null, null);
		
		String[] first22 = {"relop_e", "relop_lege", "relop_l", "relop_g", "relop_le", "relop_ge"};
		String[] follow22 = {"semi", "comma", "closepar"};
		Expr2 = new NonTerminal("Expr2", first22, follow22, first22, null, null, null, null, null);
		
		String[] first23 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		String[] follow23 = {"closesq", "closepar", "relop_e", "relop_lege", 
				"relop_l", "relop_g", "relop_le", "relop_ge", "comma", "semi"};
		ArithExpr = new NonTerminal("ArithExpr", first23, follow23, first23, null, null, null, null, null);
		
		String[] first24 = {"add", "sub", "or"};
		String[] follow24 = {"closesq", "closepar", "relop_e", "relop_lege", 
				"relop_l", "relop_g", "relop_le", "relop_ge", "comma", "semi"};
		ArithExpr2 = new NonTerminal("ArithExpr2", first24, follow24, first24, null, null, null, null, null);
		
		String[] first25 = {"add", "sub"};
		String[] rhs25_1 = {"add"};
		String[] rhs25_2 = {"sub"};
		String[] follow25 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		Sign = new NonTerminal("Sign", first25, follow25, rhs25_1, rhs25_2, null, null, null, null);
		
		String[] first26 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		String[] follow26 = {"or", "add", "sub", "semi", "closepar", "comma", "relop_l", "relop_e", "relop_g", 
				"relop_ge", "relop_le", "relop_lege", "closecur"};
		Term = new NonTerminal("Term", first26, follow26, first26, null, null, null, null, null);
		
		String[] first27 = {"mul", "div", "and"};
		String[] follow27 = {"or", "add", "sub", "semi", "closepar", "comma", "relop_l", "relop_e", "relop_g", 
				"relop_ge", "relop_le", "relop_lege", "closecur", "closesq"};
		Term2 = new NonTerminal("Term2", first27, follow27, first27, null, null, null, null, null);
		
		String[] first28 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		String[] rhs28_1 = {"openpar"};
		String[] rhs28_2 = {"id"};
		String[] rhs28_3 = {"num", "INT"};
		String[] rhs28_4 = {"not"};
		String[] rhs28_5 = {"add", "sub"};
		String[] follow28 = {"mul", "div", "and", "or", "add", "sub", "semi", "closepar", "comma", "relop_l", 
				"relop_e", "relop_g", "relop_ge", "relop_le", "relop_lege", "closesq"};
		Factor = new NonTerminal("Factor", first28, follow28, rhs28_1, rhs28_2, rhs28_3, rhs28_4, rhs28_5, null);
		
		String[] first29 = {"openpar"};
		String[] follow29 = {"mul", "div", "and", "or", "add", "sub", "semi", "closepar", "comma", "relop_l", 
				"relop_e", "relop_g", "relop_ge", "relop_le", "relop_lege", "closesq"};
		Factor2 = new NonTerminal("Factor2", first29, follow29, first29, null, null, null, null, null);
		
		String[] first30 = {"dot", "opensq"};
		String[] rhs30_1 = {"dot"};
		String[] rhs30_2 = {"opensq"};
		String[] follow30 = {"assignop", "closepar", "semi", "comma", "openpar", "relop_l", "relop_e", "relop_g", 
				"relop_ge", "relop_le", "relop_lege", "closesq", "add", "sub", "or", "mul", "div", "and"};
		IdnestList = new NonTerminal("IdnestList", first30, follow30, rhs30_1, rhs30_2, null, null, null, null);
		
		String[] first31 = {"id"};
		String[] follow31 = {"closepar", "assignop"};
		Variable = new NonTerminal("Variable", first31, follow31, first31, null, null, null, null, null);
		
		String[] first32 = {"opensq"};
		String[] follow32 = {"assignop", "closepar", "semi", "comma", "openpar", "relop_l", "relop_e", "relop_g", 
				"relop_ge", "relop_le", "relop_lege", "closesq", "add", "sub", "or", "mul", "div", "and", "dot"};
		IndiceList = new NonTerminal("IndiceList", first32, follow32, first32, null, null, null, null, null);
		
		String[] first33 = {"opensq"};
		String[] follow33 = {"opensq", "closepar", "assignop", "semi", "comma", "openpar", "relop_l", "relop_e", "relop_g", 
				"relop_ge", "relop_le", "relop_lege", "closesq", "add", "sub", "or", "mul", "div", "and", "dot"};
		Indice = new NonTerminal("Indice", first33, follow33, first33, null, null, null, null, null);
		
		String[] first34 = {"opensq"};
		String[] follow34 = {"opensq", "semi", "closepar", "comma"};
		ArraySize = new NonTerminal("ArraySize", first34, follow34, first34, null, null, null, null, null);
		
		String[] first35 = {"int", "float", "id"};
		String[] follow35 = {"closepar"};
		FParams = new NonTerminal("FParams", first35, follow35, first35, null, null, null, null, null);
		
		String[] first36 = {"comma"};
		String[] follow36 = {"closepar"};
		FParamsTailList = new NonTerminal("FParamsTailList", first36, follow36, first36, null, null, null, null, null);
		
		String[] first37 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		String[] follow37 = {"closepar"};
		AParams = new NonTerminal("AParams", first37, follow37, first37, null, null, null, null, null);
		
		String[] first38 =  {"comma"};
		String[] follow38 = {"closepar"};
		AParamsTailList = new NonTerminal("AParamsTailList", first38, follow38, first38, null, null, null, null, null);
		
		String[] first39 =  {"comma"};
		String[] follow39 = {"comma", "closepar"};
		FParamsTail = new NonTerminal("FParamsTail", first39, follow39, first39, null, null, null, null, null);
		
		String[] first40 =  {"comma"};
		String[] follow40 = {"comma", "closepar"};
		AParamsTail = new NonTerminal("AParamsTail", first40, follow40, first40, null, null, null, null, null);
		
		String[] first41 =  {"relop_e", "relop_lege", "relop_l", "relop_g", "relop_le", "relop_ge"};
		String[] rhs41_1 = {"relop_e"};
		String[] rhs41_2 = {"relop_lege"};
		String[] rhs41_3 = {"relop_l"};
		String[] rhs41_4 = {"relop_g"};
		String[] rhs41_5 = {"relop_le"};
		String[] rhs41_6 = {"relop_ge"};
		String[] follow41 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		RelOp = new NonTerminal("RelOp", first41, follow41, rhs41_1, rhs41_2, rhs41_3, rhs41_4, rhs41_5, rhs41_6);
		
		String[] first42 = {"add", "sub", "or"};
		String[] rhs42_1 = {"or"};
		String[] rhs42_2 = {"sub"};
		String[] rhs42_3 = {"add"};
		String[] follow42 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		AddOp = new NonTerminal("AddOp", first42, follow42, rhs42_1, rhs42_2, rhs42_3, null, null, null);
		
		String[] first43 = {"mul", "div", "and"};
		String[] rhs43_1 = {"and"};
		String[] rhs43_2 = {"div"};
		String[] rhs43_3 = {"mul"};
		String[] follow43 = {"id", "num", "INT", "openpar", "not", "add", "sub"};
		MultOp = new NonTerminal("MultOp", first43, follow43, rhs43_1, rhs43_2, rhs43_3, null, null, null);
		
		String[] first44 = {"class", "program"};
		String[] follow44 = {"eof"};
		Prog = new NonTerminal("Prog", first44, follow44, first44, null, null, null, null, null);
		

	}
}
