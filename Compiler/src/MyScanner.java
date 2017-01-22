import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MyScanner {
	private Scanner sc;
	int sym; // The current token on the input, STILL NOT PROCESSED
	int val; // Value of the last no encountered
	int id; // ID of the last Identifier encountered, check ID_TABLE
	private ArrayList<String> idTable;
	private ArrayList<String> keywords;
	private String token = "";
	private char residualChar = '\0';

	MyScanner(String FileName) throws IOException {
		sc = new Scanner(new FileReader(FileName));
		sc.useDelimiter("");
		idTable = new ArrayList<String>();
		keywords = new ArrayList<String>();
		keywords.add("then");
		keywords.add("do");
		keywords.add("od");
		keywords.add("fi");
		keywords.add("else");
		keywords.add("let");
		keywords.add("call");
		keywords.add("if");
		keywords.add("while");
		keywords.add("return");
		keywords.add("var");
		keywords.add("array");
		keywords.add("function");
		keywords.add("procedure");
		keywords.add("main");
	}

	private void number() throws Exception {
		if (sc.hasNext()) {
			char ch = sc.next().charAt(0);
			if (Character.isDigit(ch)) {
				token += ch;
				number();
			} else if (Character.isLetter(ch)) {
				error("INVALID TOKEN FOR NUMBER, letter encountered");
			} else {
				residualChar = ch;
			}
		}
	}

	private void identifier() {
		if (sc.hasNext()) {
			rest();
		}
	}

	private void rest() {
		if (sc.hasNext()) {
			char ch = sc.next().charAt(0);
			if (Character.isLetterOrDigit(ch)) {
				token += ch;
				rest();
			} else {
				residualChar = ch;
			}
		}
	}

	private void handleIndividualTokens(char ch) throws Exception {
		switch (ch) {
		case '=':
			ch = sc.next().charAt(0);
			if (ch != '=')
				error("Invalid token \"=" + ch + "\"");
			sym = ScannerUtils.eqlToken;
			break;
		case '!':
			ch = sc.next().charAt(0);
			if (ch != '=')
				error("Invalid token \"!" + ch + "\"");
			sym = ScannerUtils.eqlToken;
			break;
		case '<':
			ch = sc.next().charAt(0);
			if (ch == '=')
				sym = ScannerUtils.leqToken;
			else if (ch == '-')
				sym = ScannerUtils.becomesToken;
			else {
				sym = ScannerUtils.lssToken;
				residualChar = ch;
			}
			break;
		case '>':
			ch = sc.next().charAt(0);
			if (ch == '=')
				sym = ScannerUtils.geqToken;
			else {
				sym = ScannerUtils.gtrToken;
				residualChar = ch;
			}
			break;
		case '+':
			sym = ScannerUtils.plusToken;
			break;
		case '*':
			sym = ScannerUtils.timesToken;
			break;
		case '/':
			sym = ScannerUtils.divToken;
			break;
		case '-':
			sym = ScannerUtils.minusToken;
			break;
		case '.':
			sym = ScannerUtils.periodToken;
			break;
		case ',':
			sym = ScannerUtils.commaToken;
			break;
		case '[':
			sym = ScannerUtils.openbracketToken;
			break;
		case ']':
			sym = ScannerUtils.closebracketToken;
			break;
		case ')':
			sym = ScannerUtils.closeparanToken;
			break;
		case '(':
			sym = ScannerUtils.openparanToken;
			break;
		case ';':
			sym = ScannerUtils.semiToken;
			break;
		case '}':
			sym = ScannerUtils.endToken;
			break;
		case '{':
			sym = ScannerUtils.beginToken;
			break;
		default:
			error("WRONG INPUT, invalid token ch = \""+ch+"\"");
		}
	}

	private void handleKeyword(String token) throws Exception {
		switch (token) {
		case "else":
			sym = ScannerUtils.elseToken;
			break;
		case "then":
			sym = ScannerUtils.thenToken;
			break;
		case "do":
			sym = ScannerUtils.doToken;
			break;
		case "od":
			sym = ScannerUtils.odToken;
			break;
		case "fi":
			sym = ScannerUtils.fiToken;
			break;
		case "let":
			sym = ScannerUtils.letToken;
			break;
		case "call":
			sym = ScannerUtils.callToken;
			break;
		case "if":
			sym = ScannerUtils.ifToken;
			break;
		case "while":
			sym = ScannerUtils.whileToken;
			break;
		case "return":
			sym = ScannerUtils.returnToken;
			break;
		case "var":
			sym = ScannerUtils.varToken;
			break;
		case "array":
			sym = ScannerUtils.arrToken;
			break;
		case "function":
			sym = ScannerUtils.funcToken;
			break;
		case "procedure":
			sym = ScannerUtils.procToken;
			break;
		case "main":
			sym = ScannerUtils.mainToken;
			break;
		default:
			error("WRONG KEYWORD " + token + "\n, COMPILER CONSTRUCTION ERROR ");
		}
	}

	/**
	 * This function will generate the next token and update sym/val/id
	 */
	public void next() throws Exception {
		token = "";
		if (residualChar != '\0' || sc.hasNext()) {
			while (residualChar != '\0' || sc.hasNext()) {
				char ch;
				if (residualChar != '\0') {
					ch = residualChar;
					residualChar = '\0';
				} else
					ch = sc.next().charAt(0);

				while (ch == ' ' || ch == '\t' || ch == '\n' || (int)ch == 13)
					ch = sc.next().charAt(0);

				token += ch;
				if (Character.isDigit(ch)) {
					number();
					sym = ScannerUtils.number;
					val = Integer.parseInt(token);
					break;
				} else if (Character.isLetter(ch)) {
					identifier();
					token = token.toLowerCase();
					if (keywords.contains(token)) {
						handleKeyword(token);
					} else {
						sym = ScannerUtils.ident;
						id = string2Id(token);
					}
					break;
				} else {
					handleIndividualTokens(ch);
					break;
				}
			}
		} else {
			/* EOF */
			sym = ScannerUtils.eofToken;
		}
	}

	public static void error(String errorMsg) throws Exception {
		throw new Exception(errorMsg);
	}

	private String id2String(int id) throws Exception {
		if (id >= idTable.size() || id < 0)
			error("ID NOT FOUND");
		return idTable.get(id);
	}

	private int string2Id(String name) {
		if (!idTable.contains(name))
			idTable.add(name);

		return idTable.indexOf(name);
	}
	
	@Override
	protected void finalize() throws Throwable {
		shutDown();
		super.finalize();
	}

	protected void shutDown() {
		sc.close();
		keywords.clear();
		idTable.clear();
	}
}