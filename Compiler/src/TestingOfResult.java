import Utilities.MyScanner;
import Utilities.Result;
import Utilities.ScannerUtils;
import Utilities.Utils;
import Utilities.Utils.CODE;
import Utilities.Utils.RESULT_KIND;

public class TestingOfResult {
	static int COUNTER = 5;
	static MyScanner sc;

	private static Result F() throws Exception {
		Result X = new Result();
		if (sc.currentToken == ScannerUtils.beginToken) {
			sc.next();
			X = E();
			if (sc.currentToken == ScannerUtils.endToken) {
				sc.next();
			} else
				Utils.error("Expected } got " + sc.currentToken);
		} else if (sc.currentToken == ScannerUtils.number) {
			X.kind = RESULT_KIND.CONST;
			X.value = sc.val;
			sc.next();
		} else if (sc.currentToken == ScannerUtils.ident) {
			X.kind = RESULT_KIND.VAR;
			X.address = COUNTER++;
			sc.next();
		}
		return X;
	}

	private static Result T() throws Exception {
		Result X = F();
		while(sc.currentToken == ScannerUtils.timesToken) {
			sc.next(); // consumed
			Result Y = F();
			if(X.kind == RESULT_KIND.CONST && Y.kind == RESULT_KIND.CONST) {
				X.value *= Y.value;
			} else if (Y.kind == RESULT_KIND.CONST) {
				Utils.load(X);
				Utils.put(CODE.MULI, X.instructionIndex, X.instructionIndex, Y.value);
			} else {
				Utils.load(X);
				Utils.load(Y);
				Utils.put(CODE.MUL,X.instructionIndex,X.instructionIndex,Y.instructionIndex);
				Utils.deallocateRegister(Y.instructionIndex);
			}
		}
		return X;
	}

	private static Result E() throws Exception {
		Result X = T();
		while(sc.currentToken == ScannerUtils.plusToken) {
			sc.next(); // consumed
			Result Y = T();
			if(X.kind == RESULT_KIND.CONST && Y.kind == RESULT_KIND.CONST) {
				X.value += Y.value;
			} else if (Y.kind == RESULT_KIND.CONST) {
				Utils.load(X);
				Utils.put(CODE.ADDI, X.instructionIndex, X.instructionIndex, Y.value);
			} else {
				Utils.load(X);
				Utils.load(Y);
				Utils.put(CODE.ADD,X.instructionIndex,X.instructionIndex,Y.instructionIndex);
				Utils.deallocateRegister(Y.instructionIndex);
			}
		}
		return X;
	}

	private static Result S() throws Exception {
		Result R = new Result();
		if(sc.currentToken == ScannerUtils.ident) {
			sc.next(); // current token consumed
			if(sc.currentToken == ScannerUtils.becomesToken) {
				sc.next(); // consumed
				R = E();
			} else
				Utils.error("Expected Becomes, got "+sc.currentToken);
		} else if (sc.currentToken != ScannerUtils.eofToken)
			Utils.error("Expected Identifier, got "+sc.currentToken);
		return R;
	}

	public static void main(String args[]) throws Exception {
		sc = ScannerUtils.getScanner("D:\\Course Work\\ACC\\Project-Astoria\\Compiler\\src\\Test.txt");
		sc.next(); // get next token
		S();
		System.out.println("THIS ENDS HERE");
	}
}