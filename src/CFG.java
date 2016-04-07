import java.io.File;
import java.util.Scanner;

public class CFG {

	public static Scanner sc = null;
	public static String mainData = "";
	public static String ifSt = "";
	public static String ifData = "";
	public static String elseSt = "";
	public static String elseData = "";
	public static String whileSt = "";
	public static String whileData = "";
	public static String line;

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("javaFile.txt"));
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.contains("if")) {
				parseIf(line);
			} else if (line.contains("else")) {
				parseElse(line);
			} else if (line.contains("while")) {
				parseWhile(line);
			} else {
				parseMain(line);
			}
		}
		System.out.println(mainData);
		System.out.println(ifSt);
		System.out.println(ifData);
		System.out.println(elseSt);
		System.out.println(elseData);
		System.out.println(whileSt);
		System.out.println(whileData);
	}

	public static void parseMain(String s) {
		mainData += s;
	}

	public static void parseIf(String s) {
		ifSt += s.trim();
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			if (line.contains("while")) {
				parseWhile(line);
			} else if (line.contains("if")) {
				parseIf(line);
			} else if (line.contains("else")) {
				parseElse(line);
			} else {
				ifData += line.trim();
			}
		}

	}

	public static void parseElse(String s) {
		elseSt += s.trim();
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			if (line.contains("while")) {
				parseWhile(line);
			} else if(line.contains("if")) {
				parseIf(line);
			} else {
				elseData += line.trim();
			}
		}
	}

	public static void parseWhile(String s) {
		whileSt += s.trim();
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			if (line.contains("if")) {
				parseIf(line);
			} else if(line.contains("while")) {
				parseWhile(line);
			} else if(line.contains("else")) {
				parseElse(line);			
			} else {
				whileData += line.trim();
			}
		}
	}
}
