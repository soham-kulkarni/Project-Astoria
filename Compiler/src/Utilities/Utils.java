package Utilities;
import java.util.ArrayList;

public class Utils {

	public static enum RESULT_KIND {
		CONST, VAR, INS, CONDITION
	};
	
	public static enum CODE {
		ADD, SUB, MUL, DIV, MOD, CMP, OR, AND, BIC, XOR, LSH, ASH, CHK, ADDI, SUBI, MULI, DIVI, MODI, CMPI, ORI, ANDI, BICI, XORI, LSHI, ASHI, CHKI, LDW, LDX, POP, STW, STX, PSH, BEQ, BNE, BLT, BGE, BLE, BGT, BSR, JSR, RET, RDD, WRD, WRH, WRL, MOV 
	};

	final public static boolean BINARY = false;
	public static int programCounter = 0;
	public static int stackPointer = 0;
	private static ArrayList<Integer> buffer = new ArrayList<Integer>();
	private static ArrayList<String> tempResult = new ArrayList<String>();
	public static ArrayList<Instruction> inslist = new ArrayList<Instruction>();

	public static void conditionalJump(Result X) throws Exception {
		put(negateCondition(X.cond), X.instructionIndex, 0, 0);
		X.fixuplocation = programCounter - 1;
	}

	public static int getCurrentInstructionIndex() {
		return inslist.size() - 1;
	}

	public static void fixup(int index) {
		Instruction i = inslist.get(index);
		i.aIns = getCurrentInstructionIndex()+1-index;
		System.out.println(i);
	}

	public static void fixupOld(int index) {
		int currentVal = buffer.remove(index);
		currentVal = currentVal & 0xffff0000 + (programCounter - index);
		buffer.add(index, currentVal);
	}

	public static CODE negateCondition(CODE cond) throws Exception {
		switch (cond) {
		case BEQ:
			return CODE.BNE;
		case BNE:
			return CODE.BEQ;
		case BLT:
			return CODE.BGE;
		case BGE:
			return CODE.BLT;
		case BLE:
			return CODE.BGT;
		case BGT:
			return CODE.BLE;
		default:
			error("Invalid Conditional Operator !");
		}
		return null;
	}

	private static String format(String input, int length) {
		for (int i = input.length() + 1; i <= length; i++)
			input = "0" + input;
		return input;
	}

	private static void putF3(int code, int c, String operation) throws Exception {
		String op = Integer.toBinaryString(code);
		op = format(op, 6);
		String cbits = Integer.toBinaryString(c);
		if (cbits.length() > 26)
			error("Length of Cbits is greater than 26 !! ");

		cbits = format(cbits, 26);
		if (BINARY) {
			System.out.println(op + "-" + cbits);
		} else {
			System.out.println(operation + " " + c);
		}
		buffer.add(programCounter, Integer.parseInt(op + cbits));
		tempResult.add(programCounter++, operation+" c = "+c);
	}

	private static void putF2(int code, int a, int b, int c, String operation) throws Exception {
		if (a >= 32)
			error("Length of abits is greater than 5 !! ");

		if (b >= 32)
			error("Length of bbits is greater than 5 !! ");

		if (c >= 32)
			error("Length of cbits is greater than 5 !! ");
		implementF1F2(code, a, b, c, operation);
	}

	private static void implementF1F2(int code, int a, int b, int c, String operation) {
		String op = Integer.toBinaryString(code);
		op = format(op, 6);
		String abits = Integer.toBinaryString(a);
		abits = format(abits, 5);
		String bbits = Integer.toBinaryString(b);
		bbits = format(bbits, 5);
		String cbits = Integer.toBinaryString(c);
		cbits = format(cbits, 16);
		if (BINARY)
			System.out.println(op + "-" + abits + "-" + bbits + "-" + cbits);
		else
			System.out.println(operation + " " + a + " " + b + " " + c);

		//buffer.add(programCounter, Integer.parseInt(op + abits + bbits + cbits));
		tempResult.add(programCounter++, operation+" a = "+a+" b = "+b+" c = "+c);
	}

	private static void putF1(int code, int a, int b, int c, String operation) throws Exception {
		if (a >= 32)
			error("Length of abits is greater than 5 !! ");

		if (b >= 32)
			error("Length of bbits is greater than 5 !! ");

		if (c >= 65536)
			error("Length of cbits is greater than 16 !! ");

		implementF1F2(code, a, b, c, operation);
	}

	public static void emit(String command) {
		System.out.println(command);
	}

	public static void put(CODE op, int a, int b, int c) throws Exception {
		if (a < 0 || b < 0 || c < 0)
			error("Invalid Operand, can't have negative operands ");
		switch (op) {
		case CHK:
			if (b != 0)
				error("Invalid Operand, can't give b  ");

			putF2(14, a, b, c, op.toString());
			break;
		case ASH:
			putF2(13, a, b, c, op.toString());
			break;
		case LSH:
			putF2(12, a, b, c, op.toString());
			break;
		case XOR:
			putF2(11, a, b, c, op.toString());
			break;
		case BIC:
			putF2(10, a, b, c, op.toString());
			break;
		case AND:
			putF2(9, a, b, c, op.toString());
			break;
		case OR:
			putF2(8, a, b, c, op.toString());
			break;
		case CMP:
			putF2(5, a, b, c, op.toString());
			break;
		case MOD:
			putF2(4, a, b, c, op.toString());
			break;
		case DIV:
			putF2(3, a, b, c, op.toString());
			break;
		case MUL:
			putF2(2, a, b, c, op.toString());
			break;
		case SUB:
			putF2(1, a, b, c, op.toString());
			break;
		case ADD:
			putF2(0, a, b, c, op.toString());
			break;
		case CHKI:
			if (b != 0)
				error("Invalid Operand, can't give b  ");

			putF1(30, a, b, c, op.toString());
			break;
		case ASHI:
			putF1(29, a, b, c, op.toString());
			break;
		case LSHI:
			putF1(28, a, b, c, op.toString());
			break;
		case XORI:
			putF1(27, a, b, c, op.toString());
			break;
		case BICI:
			putF1(26, a, b, c, op.toString());
			break;
		case ANDI:
			putF1(25, a, b, c, op.toString());
			break;
		case ORI:
			putF1(24, a, b, c, op.toString());
			break;
		case CMPI:
			putF1(21, a, b, c, op.toString());
			break;
		case MODI:
			putF1(20, a, b, c, op.toString());
			break;
		case DIVI:
			putF1(19, a, b, c, op.toString());
			break;
		case MULI:
			putF1(18, a, b, c, op.toString());
			break;
		case SUBI:
			putF1(17, a, b, c, op.toString());
			break;
		case ADDI:
			putF1(16, a, b, c, op.toString());
			break;
		case LDW:
			putF1(32, a, b, c, op.toString());
			break;
		case LDX:
			putF1(33, a, b, c, op.toString());
			break;
		case POP:
			putF1(34, a, b, c, op.toString());
			break;
		case STW:
			putF1(36, a, b, c, op.toString());
			break;
		case STX:
			putF2(37, a, b, c, op.toString());
			break;
		case PSH:
			putF1(38, a, b, c, op.toString());
			break;
		case BEQ:
			if (b != 0)
				error("Invalid Operand, can't give b  ");

			putF1(40, a, 0, c, op.toString());
			break;
		case BNE:
			if (b != 0)
				error("Invalid Operand, can't give b  ");

			putF1(41, a, 0, c, op.toString());
			break;
		case BLT:
			if (b != 0)
				error("Invalid Operand, can't give b  ");

			putF1(42, a, 0, c, op.toString());
			break;
		case BGE:
			if (b != 0)
				error("Invalid Operand, can't give b  ");

			putF1(43, a, 0, c, op.toString());
			break;
		case BLE:
			if (b != 0)
				error("Invalid Operand, can't give b  ");

			putF1(44, a, 0, c, op.toString());
			break;
		case BGT:
			if (b != 0)
				error("Invalid Operand, can't give b  ");

			putF1(45, a, 0, c, op.toString());
			break;
		case BSR:
			if (a != 0 || b != 0)
				error("Invalid Operand, can't give a and b  ");

			putF1(46, 0, 0, c, op.toString());
			break;
		case JSR:
			if (a != 0 || b != 0)
				error("Invalid Operand, can't give a and b  ");

			putF3(48, c, op.toString());
			break;
		case RET:
			if (a != 0 || b != 0)
				error("Invalid Operand, can't give a and b  ");

			putF2(49, 0, 0, c, op.toString());
			break;
		case RDD:
			if (c != 0 || b != 0)
				error("Invalid Operand, can't give c and b  ");

			putF2(50, a, 0, 0, op.toString());
			break;
		case WRD:
			if (c != 0 || a != 0)
				error("Invalid Operand, can't give a and c  ");

			putF2(51, 0, b, 0, op.toString());
			break;
		case WRH:
			if (c != 0 || a != 0)
				error("Invalid Operand, can't give a and c  ");

			putF2(51, 0, b, 0, op.toString());
			break;
		case WRL:
			if (b != 0 || a != 0 || c != 0)
				error("Invalid Operand, can't give operands");

			putF1(53, 0, 0, 0, op.toString());
			break;
		default:
			error("Invalid Operand code ");
		}
	}

	private static boolean registers[] = new boolean[10];

	private static int allocateRegister() {
		for (int i = 1; i < registers.length; i++) {
			if (!registers[i]) {
				registers[i] = true;
				return i;
			}
		}
		return -1;
	}

	public static void handleBecomes(Result X, Result Y) throws Exception {
		if(Y.kind == RESULT_KIND.CONST) {
			Instruction i = Instruction.getInstruction(CODE.ADDI, getCurrentInstructionIndex()+1, "#"+Y.value);
			X.instructionIndex = i.index;
		} else {
			load(Y);
			Instruction i = Instruction.getInstruction(CODE.MOV,Y.instructionIndex, -1);
			X.instructionIndex = i.index;
		}
	}

	public static void compute(int opCode, Result X, Result Y) throws Exception {
		if(opCode == ScannerUtils.becomesToken)
			error("DO NOT CALL COMPUTE FOR BECOMES");

		if (X.kind == RESULT_KIND.CONST && Y.kind == RESULT_KIND.CONST) {
			switch (opCode) {
			case ScannerUtils.plusToken:
				X.value += Y.value;
				break;
			case ScannerUtils.minusToken:
				X.value -= Y.value;
				break;
			case ScannerUtils.timesToken:
				X.value *= Y.value;
				break;
			case ScannerUtils.divToken:
				X.value /= Y.value;
				break;
			}
		} else {
			load(X);
/*			if (X.instructionIndex == 0) {
				X.instructionIndex = allocateRegister();
				//Instruction i = new Instruction()
				//put(CODE.ADD, X.regno, 0, 0);
			}
*/
			if(Y.kind == RESULT_KIND.CONST) {
				CODE command = null;
				switch(opCode) {
				case ScannerUtils.becomesToken:
					command = CODE.STX;
					break;
				case ScannerUtils.plusToken:
					command = CODE.ADDI;
					break;
				case ScannerUtils.minusToken:
					command = CODE.SUBI;
					break;
				case ScannerUtils.timesToken:
					command = CODE.MULI;
					break;
				case ScannerUtils.divToken:
					command = CODE.DIVI;
					break;
				case ScannerUtils.leqToken:
				case ScannerUtils.neqToken:
				case ScannerUtils.eqlToken:
				case ScannerUtils.geqToken:
				case ScannerUtils.gtrToken:
				case ScannerUtils.lssToken:
					command = CODE.CMPI;
					break;
				}
				Instruction i = Instruction.getInstruction(command, X.instructionIndex, "#"+Y.value);
				X.instructionIndex = i.index;
				if(command == CODE.CMPI)
					handleCompare(opCode);

				//put(command,X.regno,X.regno,Y.value);
			} else {
				load(Y);
				CODE command = null;
				switch(opCode) {
				case ScannerUtils.plusToken:
					command = CODE.ADD;
					break;
				case ScannerUtils.minusToken:
					command = CODE.SUB;
					break;
				case ScannerUtils.timesToken:
					command = CODE.MUL;
					break;
				case ScannerUtils.divToken:
					command = CODE.DIV;
					break;
				case ScannerUtils.leqToken:
				case ScannerUtils.neqToken:
				case ScannerUtils.eqlToken:
				case ScannerUtils.geqToken:
				case ScannerUtils.gtrToken:
				case ScannerUtils.lssToken:
					command = CODE.CMP;
					break;
				}
				Instruction i = Instruction.getInstruction(command, X.instructionIndex, Y.instructionIndex);
				X.instructionIndex = i.index;
				if(command == CODE.CMP)
					handleCompare(opCode);

				/*put(command,X.regno,X.regno,Y.regno);
				deallocateRegister(Y.regno);*/
			}
		}
	}

	public static void handleCompare(int code) {
		switch(code) {
		case ScannerUtils.leqToken:
			Instruction.getInstruction(CODE.BGT, -1, -1);
			break;
		case ScannerUtils.neqToken:
			Instruction.getInstruction(CODE.BEQ, -1, -1);
			break;
		case ScannerUtils.eqlToken:
			Instruction.getInstruction(CODE.BNE, -1, -1);
			break;
		case ScannerUtils.geqToken:
			Instruction.getInstruction(CODE.BLT, -1, -1);
			break;
		case ScannerUtils.gtrToken:
			Instruction.getInstruction(CODE.BLE, -1, -1);
			break;
		case ScannerUtils.lssToken:
			Instruction.getInstruction(CODE.BGE, -1, -1);
			break;
		}
	}

	public static void deallocateRegister(int regNo) {
		registers[regNo] = false;
	}

	public static void load(Result R) throws Exception{
		Instruction i = null;
		if (R.kind == RESULT_KIND.CONST) {
			//put(CODE.ADDI, R.instructionIndex, 0, R.value);
			i = Instruction.getInstruction(CODE.ADDI, "#0", "#"+R.value);
			R.instructionIndex = i.index;
		} else if (R.kind == RESULT_KIND.VAR) {
			i = Instruction.getInstruction(CODE.LDW, "#30", "#"+R.address);
			R.instructionIndex = i.index;
			//put(CODE.LDW,R.instructionIndex,30,R.address);
		}
		R.kind = RESULT_KIND.INS;
	}

	public static void error(String errorMsg) throws Exception {
		throw new Exception(errorMsg);
	}
}