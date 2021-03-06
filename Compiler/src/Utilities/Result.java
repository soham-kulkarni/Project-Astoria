package Utilities;

import java.util.ArrayList;

import Utilities.Utils.CODE;
import Utilities.Utils.RESULT_KIND;

public class Result {

	public RESULT_KIND kind;
	public boolean isArray;
	public boolean isFunc;
	public ArrayList<Result> expresssions;
	public int valueIfConstant;
	public String addressIfVariable;
	public Instruction instruction;
	public CODE cond;
	public String booleanIfCondition;
	public Result() {
		kind = RESULT_KIND.NONE;
		isArray = false;
		isFunc = false;
		expresssions = null;
		valueIfConstant = -1;
		addressIfVariable = null;
		instruction = null;
		cond = CODE.NONE;
		booleanIfCondition = null;
	}
}
