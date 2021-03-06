/*import Utilities.MyScanner;
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
			X.valueIfConstant = sc.val;
			sc.next();
		} else if (sc.currentToken == ScannerUtils.ident) {
			X.kind = RESULT_KIND.VAR;
			X.addressIfVariable = COUNTER++;
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
				X.valueIfConstant *= Y.valueIfConstant;
			} else if (Y.kind == RESULT_KIND.CONST) {
				Utils.load(X);
				Utils.put(CODE.MULI, X.instruction, X.instruction, Y.valueIfConstant);
			} else {
				Utils.load(X);
				Utils.load(Y);
				Utils.put(CODE.MUL,X.instruction,X.instruction,Y.instruction);
				Utils.deallocateRegister(Y.instruction);
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
				X.valueIfConstant += Y.valueIfConstant;
			} else if (Y.kind == RESULT_KIND.CONST) {
				Utils.load(X);
				Utils.put(CODE.ADDI, X.instruction, X.instruction, Y.valueIfConstant);
			} else {
				Utils.load(X);
				Utils.load(Y);
				Utils.put(CODE.ADD,X.instruction,X.instruction,Y.instruction);
				Utils.deallocateRegister(Y.instruction);
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
		sc = ScannerUtils.getScanner("D:\\Course Work\\ACC\\Project-Astoria\\Compiler\\src\\Utilities\\Test Cases\\Test.txt");
		sc.next(); // get next token
		S();
		System.out.println("THIS ENDS HERE");
	}
}*/