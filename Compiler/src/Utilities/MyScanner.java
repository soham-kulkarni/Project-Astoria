package Utilities;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MyScanner {

	public static enum CLASS {
		VAR, ARR, /* PRO, */ FUNC, NONE
	};

	public CLASS mCurrentClass;

	private Scanner sc;
	public int currentToken; // The current token on the input, STILL NOT PROCESSED
	public int val; // Value of the last no encountered
	public int id; // ID of the last Identifier encountered, check ID_TABLE
	private List<String> keywords;
	public String token = "";
	private char residualChar = '\0';
	private static int linecount;
	private boolean ignoreTillEndLine = false, ignoreTillCommentEndToken = false;
	private ArrayList<Integer> valuesForArrays = null;

	protected MyScanner(String FileName) throws IOException {
		sc = new Scanner(new FileReader(FileName));
		sc.useDelimiter("");
		linecount = 1;
		keywords = Arrays.asList("then", "do", "od", "fi", "else", "let", "call", "if", "while", "return", "var",
				"array", "function", "procedure", "main");
		mCurrentClass = CLASS.NONE;
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
			token = "==";
			currentToken = ScannerUtils.eqlToken;
			break;
		case '!':
			ch = sc.next().charAt(0);
			if (ch != '=')
				Utils.error("Invalid token \"!" + ch + "\"");
			token = "!=";
			currentToken = ScannerUtils.eqlToken;
			break;
		case '<':
			ch = sc.next().charAt(0);
			if (ch == '=') {
				token = "<=";
				currentToken = ScannerUtils.leqToken;
			} else if (ch == '-') {
				token = "<-";
				currentToken = ScannerUtils.becomesToken;
			} else {
				currentToken = ScannerUtils.lssToken;
				residualChar = ch;
			}
			break;
		case '>':
			ch = sc.next().charAt(0);
			if (ch == '=') {
				token = ">=";
				currentToken = ScannerUtils.geqToken;
			} else {
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
		case '#':
			ignoreTillEndLine = true;
			next();
			break;
		case '/':
			ch = sc.next().charAt(0);
			if (ch == '/') {
				/* Comment has started */
				ignoreTillEndLine = true;
				next();
			} else if (ch == '*') {
				/* Multiline has Started */
				ignoreTillCommentEndToken = true;
				next();
			} else {
				residualChar = ch;
				currentToken = ScannerUtils.divToken;
			}
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
			Utils.error("WRONG INPUT, invalid token ch = \"" + ch + "\"");
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

				while (ignoreTillCommentEndToken || ignoreTillEndLine || ch == ' ' || ch == '\t' || ch == '\n'
						|| (int) ch == 13) {
					if (!sc.hasNext()) {
						/* EOF */
						currentToken = ScannerUtils.eofToken;
						return;
					}

					if (ch == '\n') {
						linecount++;
						ignoreTillEndLine = false;
					} else if (ch == '*') {
						ch = sc.next().charAt(0);
						if (ch == '/') {
							/* Multiline has ended */
							ignoreTillCommentEndToken = false;
							residualChar = '\0';
						} else
							residualChar = ch;
					}

					ch = (residualChar == '\0' ? sc.next().charAt(0) : residualChar);
				}

				token += ch;
				if (Character.isDigit(ch)) {
					/* We know that it is a number */
					number();
					currentToken = ScannerUtils.number;
					val = Integer.parseInt(token);
					break;
				} else if (Character.isLetter(ch)) {
					/*
					 * We know that the token is either an identifier or a
					 * keyword
					 */
					restOfIdentifier();
					token = token.toLowerCase();
					/*
					 * Token accepted, now we categorize it as either token or
					 * id
					 */
					if (keywords.contains(token)) {
						handleKeyword(token);
					} else {
						currentToken = ScannerUtils.ident;
						id = Utils.identifier2Address(token, valuesForArrays, mCurrentClass);
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

	public static int getLineCount() {
		return linecount;
	}

	public void setValues(ArrayList<Integer> values) {
		valuesForArrays = values;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!shutDown)
			shutDown();
		super.finalize();
	}

	boolean shutDown = false;

	protected void shutDown() {
		sc.close();
		keywords = null;
		shutDown = true;
	}
}