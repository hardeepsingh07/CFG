import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import java.applet.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

public class CFG2 extends Applet {

	public static Scanner sc = null;
	public static String ifSt, endIfData, whileSt, forSt, line, previous;
	public static boolean ifTrigger = false, secondIf = false, elseTrigger = false;
	public static ArrayList<String> ifStatements = new ArrayList<String>();
	public static DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(
			DefaultEdge.class);

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("testing3.txt"));

		// Always start with previous as "Start"
		previous = "Start";
		graph.addVertex(previous);
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			parseData();
		}
		Graph<String, DefaultEdge> g = graph;
		boolean selfReferencesAllowed = false;

		JFrame frame = new JFrame();
		frame.getContentPane().add(new GraphPanel<String, DefaultEdge>(g, selfReferencesAllowed));
		frame.setPreferredSize(new Dimension(1000, 1000));
		frame.setTitle("Context Flow Graph");
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
		graph.addEdge(endIfData, line);
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
	}

	public static void parseData() {
		if (line.contains("if")) {
			if (secondIf) {
				graph.addVertex(line);
				graph.addEdge(previous, line);
				graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
				ifStatements.remove(ifStatements.size() - 1);
				ifStatements.add(line);
				previous = line;
				secondIf = false;
			} else {
				ifStatements.add(line);
				graph.addVertex(line);
				graph.addEdge(previous, line);
				previous = line;
			}
			while (!((line = sc.nextLine()).trim().equals("}"))) {
				parseData();
			}

			endIfData = previous;
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
		} else if (line.contains("else")) {
			line = sc.nextLine();
			graph.addVertex(line);
			graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
			ifStatements.remove(ifStatements.size() - 1);
			previous = line;
			while (!((line = sc.nextLine()).trim().equals("}"))) {
				parseData();
			}
			ifTrigger = false;
			elseTrigger = true;
		} else if (line.contains("while")) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			whileSt = line;
			previous = line;
			while (!((line = sc.nextLine()).trim().equals("}"))) {
				parseData();
			}
			graph.addEdge(previous, whileSt);
			previous = whileSt;
		} else if (line.contains("for")) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			forSt = line;
			previous = line;
			while (!((line = sc.nextLine()).trim().equals("}"))) {
				parseData();
			}
			graph.addEdge(previous, forSt);
			previous = forSt;
		} else {
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

	public static void parseWhile() {
		performLocationChecks();
		whileSt = line;
		previous = line;

		// parse while data
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}

		graph.addEdge(previous, whileSt);
		previous = whileSt;
		ifTrigger = false;
		elseTrigger = false;
	}

	public static void parseFor() {
		performLocationChecks();
		forSt = line;
		previous = line;

		// parse for data
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}

		graph.addEdge(previous, forSt);
		previous = forSt;
		ifTrigger = false;
		elseTrigger = false;
	}

	public static void performLocationChecks() {
		if (elseTrigger) {
			elseTriggerComp();
		} else if (ifTrigger) {
			ifTriggerComp();
		} else {
			graph.addVertex(line);
			graph.addEdge(previous, line);
		}
	}
}

// public static void parseElse() {
// endIfData = previous;
//
// // move one more to skip else
// line = sc.nextLine();
// graph.addVertex(line);
// graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
// previous = line;
// while (!((line = sc.nextLine()).trim().equals("}"))) {
// graph.addVertex(line);
// graph.addEdge(previous, line);
// previous = line;
// }
// ifTrigger = false;
// elseTrigger = true;
// }
// public static void parseIf() {
// if (elseTrigger) {
// elseTriggerComp();
// }
//
// if (secondIfCheck) {
// doubleIFs();
// } else {
// ifStatements.add(line);
// graph.addVertex(line);
// graph.addEdge(previous, line);
// previous = line;
// }
// // parse if data
// while (!((line = sc.nextLine()).trim().equals("}"))) {
// parseData();
// }
// ifTrigger = true;
// secondIfCheck = true;
// }
// public static void doubleIFs() {
// graph.addVertex(line);
// graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
// graph.addEdge(previous, line);
// ifStatements.remove(ifStatements.get(ifStatements.size() - 1));
// ifStatements.add(line);
// previous = line;
// secondIfCheck = false;
// elseTrigger = false;
// }