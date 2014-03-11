
public class TerminalCollection {

	public Terminal t_id;
	public Terminal t_num;
	public Terminal t_relop_g;
	public Terminal t_relop_l;
	public Terminal t_relop_ge;
	public Terminal t_relop_le;
	public Terminal t_relop_e;
	public Terminal t_assignop;
	public Terminal t_add;
	public Terminal t_sub;
	public Terminal t_mul;
	public Terminal t_div;
	public Terminal t_semi;
	public Terminal t_comma;
	public Terminal t_dot;
	public Terminal t_openpar;
	public Terminal t_closepar;
	public Terminal t_lege;
	public Terminal t_and;
	public Terminal t_not;
	public Terminal t_or;
	public Terminal t_if;
	public Terminal t_then;
	public Terminal t_else;
	public Terminal t_for;
	public Terminal t_class;
	public Terminal t_int;
	public Terminal t_float;
	public Terminal t_get;
	public Terminal t_put;
	public Terminal t_return;
	public Terminal t_opencur;
	public Terminal t_closecur;
	public Terminal t_opensq;
	public Terminal t_closesq;
	public Terminal t_eof;
	public Terminal t_program;
	public Terminal t_INT;
	
	public TerminalCollection (){
		t_id = new Terminal("id", "");
		t_num = new Terminal("num", "");
		t_relop_g = new Terminal("relop_g", ">");
		t_relop_l = new Terminal("relop_l", "<");
		t_relop_ge = new Terminal("relop_ge", ">=");
		t_relop_le = new Terminal("relop_le", "<=");
		t_relop_e = new Terminal("relop_e", "==");
		t_assignop = new Terminal("assignop", "=");
		t_add = new Terminal("add", "+");
		t_sub = new Terminal("sub", "-");
		t_mul = new Terminal("mul", "*");
		t_div = new Terminal("div", "/");
		t_semi = new Terminal("semi", ";");
		t_comma = new Terminal("comma", ",");
		t_dot = new Terminal("dot", ".");
		t_openpar = new Terminal("openpar", "(");
		t_closepar = new Terminal("closepar", ")");
		t_lege = new Terminal("lege", "<>");
		t_and = new Terminal("and", "and");
		t_not = new Terminal("not", "not");
		t_or = new Terminal("or", "or");
		t_if = new Terminal("if", "if");
		t_then = new Terminal("then", "then");
		t_else = new Terminal("else", "else");
		t_for = new Terminal("for", "for");
		t_class = new Terminal("class", "class");
		t_int = new Terminal("int", "int");
		t_float = new Terminal("float", "float");
		t_get = new Terminal("get", "get");
		t_put = new Terminal("put", "put");
		t_return = new Terminal("return", "return");
		t_opencur = new Terminal("opencur", "{");
		t_closecur = new Terminal("closecur", "}");
		t_opensq = new Terminal("opensq", "[");
		t_closesq = new Terminal("closesq", "]");
		t_eof = new Terminal("eof", "$");
		t_program = new Terminal("program", "program");
		t_INT = new Terminal("INT", "");
	}

}
