
/* 
*	Created by Hardeep Singh
*	Context Flow Graph
*	Dr. Salloumn
*	Graph Represented Visually
*/

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import java.applet.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

public class FlowGraph {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	public static Scanner sc = null;
	public static String ifSt, endIfData, whileSt, forSt, line, previous;
	public static boolean ifTrigger = false, secondIf = false, elseTrigger = false;
	public static ArrayList<String> ifStatements = new ArrayList<String>();
	public static ArrayList<String> forStatements = new ArrayList<String>();
	public static ArrayList<String> whileStatements = new ArrayList<String>();
	public static ArrayList<String> endIfDataStatements = new ArrayList<String>();
	public static ArrayList<String> doStatements = new ArrayList<String>();
	public static DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(
			DefaultEdge.class);

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("extraCreditTesting.txt"));

		// Always start with previous as "Start"
		previous = "Start";
		graph.addVertex(previous);

		// parse the data given by file
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (!line.isEmpty()) {
				parseData();
			}
		}

		// attach a "End" node to the data
		line = "End";
		graph.addVertex(line);
		graph.addEdge(previous, line);

		// Call the classes to make the GUI calls
		Graph<String, DefaultEdge> g = graph;
		boolean selfReferencesAllowed = false;
		JFrame frame = new JFrame();
		frame.getContentPane().add(new GraphPanel<String, DefaultEdge>(g, selfReferencesAllowed));
		frame.setPreferredSize(new Dimension(1000, 1000));
		frame.setTitle("Hardeep Control Flow Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		try {
			Thread.sleep(5000000);
		} catch (InterruptedException ex) {
		}
	}

	public static void elseTriggerComp() {
		graph.addVertex(line);
		graph.addEdge(previous, line);
		graph.addEdge(endIfDataStatements.get(endIfDataStatements.size() - 1), line);
		endIfDataStatements.remove(endIfDataStatements.size() - 1);
		previous = line;
		elseTrigger = false;
		ifTrigger = false;
	}

	public static void ifTriggerComp() {
		graph.addVertex(line);
		graph.addEdge(previous, line);
		graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
		previous = line;
		ifTrigger = false;
		endIfDataStatements.remove(endIfDataStatements.size() - 1);
	}

	public static void parseData() {
		if (line.contains("if")) {
			checkIf();
		} else if (line.contains("else")) {
			checkElse();
		} else if (line.contains("while")) {
			checkWhile();
		} else if (line.contains("for")) {
			checkFor();
		} else if (line.contains("do")) {
			checkDoWhile();
		} else {
			checkData();
		}
	}

	// parse if data
	public static void checkIf() {
		if (secondIf) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
			ifStatements.remove(ifStatements.size() - 1);
			ifStatements.add(line);
			previous = line;
			secondIf = false;
		} else {
			checkData();
			ifStatements.add(line);
		}

		// parse data between braces
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			parseData();
		}

		// perform next line check
		nextCheck();
	}

	public static void nextCheck() {
		// check ahead statements
		endIfDataStatements.add(previous);
		line = sc.nextLine();
		if (line.contains("else")) {
			parseData();
		} else {
			if (line.contains("if")) {
				secondIf = true;
				parseData();
			} else {
				ifTrigger = true;
				ifTriggerComp();
				ifStatements.remove(ifStatements.size() - 1);
			}
		}
	}

	// parse else data
	public static void checkElse() {
		line = sc.nextLine();
		graph.addVertex(line);
		graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
		ifStatements.remove(ifStatements.size() - 1);
		previous = line;

		// parse data between braces
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			parseData();
		}

		ifTrigger = false;
		elseTrigger = true;
	}

	// parse while data
	public static void checkWhile() {
		checkData();
		whileStatements.add(line);

		// parse data between braces
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			parseData();
		}

		graph.addEdge(previous, whileStatements.get(whileStatements.size() - 1));
		previous = whileStatements.get(whileStatements.size() - 1);
		whileStatements.remove(whileStatements.size() - 1);
	}

	// parse do-while data
	public static void checkDoWhile() {
		checkData();
		doStatements.add(line);
		graph.addVertex(line);
		graph.addEdge(previous, line);

		// parse data between braces
		while (!((line = sc.nextLine()).trim().contains("}"))) {
			parseData();
		}

		line = line.replace("}", "");
		graph.addVertex(line);
		graph.addEdge(previous, line);
		graph.addEdge(line, doStatements.get(doStatements.size() - 1));
		previous = line;
	}

	// parse for data
	public static void checkFor() {
		graph.addVertex(line);
		graph.addEdge(previous, line);
		forStatements.add(line);
		previous = line;

		// parse data between braces
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			parseData();
		}

		graph.addEdge(previous, forStatements.get(forStatements.size() - 1));
		previous = forStatements.get(forStatements.size() - 1);
		forStatements.remove(forStatements.size() - 1);
	}

	// parse rest data
	public static void checkData() {
		if (ifTrigger) {
			ifTriggerComp();
		} else if (elseTrigger) {
			elseTriggerComp();
		} else {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}
	}
}