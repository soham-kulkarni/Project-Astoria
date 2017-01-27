package Utilities;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MyScanner {
	private Scanner sc;
	public int currentToken; // The current token on the input, STILL NOT PROCESSED
	public int val; // Value of the last no encountered
	public int id; // ID of the last Identifier encountered, check ID_TABLE
	private ArrayList<String> idTable;
	private List<String> keywords;
	private String token = "";
	private char residualChar = '\0';

	protected MyScanner(String FileName) throws IOException {
		sc = new Scanner(new FileReader(FileName));
		sc.useDelimiter("");
		idTable = new ArrayList<String>();
		keywords = Arrays.asList("then", "do", "od", "fi", "else", "let", "call", "if", "while", "return", "var",
				"array", "function", "procedure", "main");
	}

	private void number() throws Exception {
		if (sc.hasNext()) {
			char ch = sc.next().charAt(0);
			if (Character.isDigit(ch)) {
				token += ch;
				number();
			} else if (Character.isLetter(ch)) {
				Utils.error("INVALID TOKEN FOR NUMBER, letter encountered");
			} else {
				residualChar = ch;
			}
		}
	}

	private void restOfIdentifier() {
		if (sc.hasNext()) {
			char ch = sc.next().charAt(0);
			if (Character.isLetterOrDigit(ch)) {
				token += ch;
				restOfIdentifier();
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
				Utils.error("Invalid token \"=" + ch + "\"");
			currentToken = ScannerUtils.eqlToken;
			break;
		case '!':
			ch = sc.next().charAt(0);
			if (ch != '=')
				Utils.error("Invalid token \"!" + ch + "\"");
			currentToken = ScannerUtils.eqlToken;
			break;
		case '<':
			ch = sc.next().charAt(0);
			if (ch == '=')
				currentToken = ScannerUtils.leqToken;
			else if (ch == '-')
				currentToken = ScannerUtils.becomesToken;
			else {
				currentToken = ScannerUtils.lssToken;
				residualChar = ch;
			}
			break;
		case '>':
			ch = sc.next().charAt(0);
			if (ch == '=')
				currentToken = ScannerUtils.geqToken;
			else {
				currentToken = ScannerUtils.gtrToken;
				residualChar = ch;
			}
			break;
		case '+':
			currentToken = ScannerUtils.plusToken;
			break;
		case '*':
			currentToken = ScannerUtils.timesToken;
			break;
		case '/':
			currentToken = ScannerUtils.divToken;
			break;
		case '-':
			currentToken = ScannerUtils.minusToken;
			break;
		case '.':
			currentToken = ScannerUtils.periodToken;
			break;
		case ',':
			currentToken = ScannerUtils.commaToken;
			break;
		case '[':
			currentToken = ScannerUtils.openbracketToken;
			break;
		case ']':
			currentToken = ScannerUtils.closebracketToken;
			break;
		case ')':
			currentToken = ScannerUtils.closeparanToken;
			break;
		case '(':
			currentToken = ScannerUtils.openparanToken;
			break;
		case ';':
			currentToken = ScannerUtils.semiToken;
			break;
		case '}':
			currentToken = ScannerUtils.endToken;
			break;
		case '{':
			currentToken = ScannerUtils.beginToken;
			break;
		default:
			Utils.error("WRONG INPUT, invalid token ch = \""+ch+"\"");
		}
	}

	private void handleKeyword(String token) throws Exception {
		switch (token) {
		case "else":
			currentToken = ScannerUtils.elseToken;
			break;
		case "then":
			currentToken = ScannerUtils.thenToken;
			break;
		case "do":
			currentToken = ScannerUtils.doToken;
			break;
		case "od":
			currentToken = ScannerUtils.odToken;
			break;
		case "fi":
			currentToken = ScannerUtils.fiToken;
			break;
		case "let":
			currentToken = ScannerUtils.letToken;
			break;
		case "call":
			currentToken = ScannerUtils.callToken;
			break;
		case "if":
			currentToken = ScannerUtils.ifToken;
			break;
		case "while":
			currentToken = ScannerUtils.whileToken;
			break;
		case "return":
			currentToken = ScannerUtils.returnToken;
			break;
		case "var":
			currentToken = ScannerUtils.varToken;
			break;
		case "array":
			currentToken = ScannerUtils.arrToken;
			break;
		case "function":
			currentToken = ScannerUtils.funcToken;
			break;
		case "procedure":
			currentToken = ScannerUtils.procToken;
			break;
		case "main":
			currentToken = ScannerUtils.mainToken;
			break;
		default:
			Utils.error("WRONG KEYWORD " + token + "\n, COMPILER CONSTRUCTION ERROR");
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
					/* We know that it is a number */
					number();
					currentToken = ScannerUtils.number;
					val = Integer.parseInt(token);
					break;
				} else if (Character.isLetter(ch)) {
					/* We know that the token is either an identifier or a
					 * keyword */
					restOfIdentifier();
					token = token.toLowerCase();
					/* Token accepted, now we categorize it as either token or
					 * id */
					if (keywords.contains(token)) {
						handleKeyword(token);
					} else {
						currentToken = ScannerUtils.ident;
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
			currentToken = ScannerUtils.eofToken;
		}
	}

	public String id2String(int id) throws Exception {
		if (id >= idTable.size() || id < 0)
			Utils.error("ID NOT FOUND");
		return idTable.get(id);
	}

	public int string2Id(String name) {
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