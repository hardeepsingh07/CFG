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
	public static boolean ifTrigger = false, secondIfCheck = false, elseTrigger = false;
	public static ArrayList<String> ifStatements = new ArrayList<String>();
	public static DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(
			DefaultEdge.class);

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("testing2.txt"));

		// Always start with previous as "Start"
		previous = "Start";
		graph.addVertex(previous);
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.contains("if")) {
				parseIf();
			} else if (line.contains("else")) {
				parseElse();
			} else if (line.contains("while")) {
				parseWhile();
			} else if (line.contains("for")) {
				parseFor();
			} else if (line.contains("//")) {
				parseComment();
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
		secondIfCheck = false;
	}

	public static void ifTriggerComp() {
		graph.addVertex(line);
		graph.addEdge(previous, line);
		graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
		previous = line;
		ifTrigger = false;
	}

	public static void doubleIFs() {
		graph.addVertex(line);
		graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
		graph.addEdge(previous, line);
		ifStatements.remove(ifStatements.get(ifStatements.size() - 1));
		ifStatements.add(line);
		previous = line;
		secondIfCheck = false;
		elseTrigger = false;
	}

	public static void parseIf() {
		if (elseTrigger) {
			elseTriggerComp();
		}

		if (secondIfCheck) {
			doubleIFs();
		} else {
			ifStatements.add(line);
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}
		// parse if data
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			if (line.contains("if")) {
				ifStatements.add(line);
				graph.addVertex(line);
				graph.addEdge(previous, line);
				previous = line;

				while (!((line = sc.nextLine()).trim().equals("}"))) {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					previous = line;
				}
				ifTrigger = true;
				secondIfCheck = true;
			} else if(line.contains("else")) {
				endIfData = previous;	
				line = sc.nextLine();
				graph.addVertex(line);
				graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
				ifStatements.remove(ifStatements.get(ifStatements.size() - 1));
				previous = line;
				while (!((line = sc.nextLine()).trim().equals("}"))) {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					previous = line;
				}
				ifTrigger = false;
				elseTrigger = true;
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
		ifTrigger = true;
		secondIfCheck = true;
	}

	public static void parseElse() {
		endIfData = previous;

		// move one more to skip else
		line = sc.nextLine();
		graph.addVertex(line);
		graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
		previous = line;
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}
		ifTrigger = false;
		elseTrigger = true;
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
		secondIfCheck = false;
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
		secondIfCheck = false;
	}

	public static void parseComment() {
		performLocationChecks();
		previous = line;
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
