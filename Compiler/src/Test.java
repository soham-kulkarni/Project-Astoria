import java.io.FileReader;
import java.util.Scanner;

import Utilities.Utils;

public class Test {
	public static void main(String args[]) throws Exception {
		Scanner sc = new Scanner(new FileReader(args[0]));
		sc.useDelimiter("");
		while (sc.hasNext())
			Utils.SOPln(sc.next());
		sc.close();
	}
}
