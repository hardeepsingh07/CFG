import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import java.applet.*;
import java.awt.*;
import java.net.URL;
import javax.swing.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

public class CFG2 extends Applet {

	public static Scanner sc = null;
	public static String mainSt, ifSt, ifSt2, ifData, elseSt, elseData, whileSt, whileData, line, previous;
	public static boolean ifTrigger, something = false, elseTrigger = false, whileTrigger = false;
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
				pIf();
			} else if (line.contains("while")) {

			} else {
				// to let the graph know that we want to connect to previous and
				// if 'if' statement is false the move on and skip if
				System.out.println(ifTrigger);
				if (ifTrigger) {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
					previous = line;
					ifTrigger = false;
				} else {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					previous = line;
					mainSt = line;
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

	public static void pIf() {
		if (something) {
			graph.addVertex(line);
			graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
			graph.addEdge(previous, line);
			ifStatements.remove(ifStatements.get(ifStatements.size() - 1));
			ifStatements.add(line);
			previous = line;
			something = false;
		} else {
			ifStatements.add(line);
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}
		// parse if data
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}
		ifTrigger = true;
		something = true;
	}

	public static void parseIf() {
		graph.addVertex(line);
		graph.addEdge(previous, line);
		ifStatements.add(line);
		previous = line;

		// move through if data
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			if (line.contains("if")) {
				graph.addVertex(line);
				graph.addEdge(previous, line);

				ifStatements.add(line);
				previous = line;

				while (!((line = sc.nextLine()).trim().equals("}"))) {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					previous = line;
				}
				something = true;
			} else if (line.contains("else")) {
				parseElseInside();
			} else {
				if (something) {
					graph.addVertex(line);
					graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
					graph.addEdge(previous, line);
					previous = line;
					ifStatements.remove(ifStatements.size() - 1);
					something = false;
				} else {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					previous = line;
				}
			}
		}
		ifData = previous;
		previous = ifSt;
		ifTrigger = true;
	}

	public static void parseElseInside() {
		previous = ifStatements.get(ifStatements.size() - 1);
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}
		something = false;
		ifStatements.remove(ifStatements.size() - 1);
	}

	public static void parseElse() {
		previous = ifStatements.get(ifStatements.size() - 1);
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}
		elseData = previous;
		elseTrigger = true;
	}

	public static void parseWhile() {
		whileSt = line;
		graph.addVertex(line);

		if (ifTrigger && elseTrigger) {
			// attach while to the end of both if and else
			graph.addEdge(ifData, line);
			graph.addEdge(elseData, line);
			ifTrigger = false;
			elseTrigger = false;
			previous = line;
		} else {
			// if no if or else before then just attach the the main
			graph.addEdge(previous, line);
			previous = line;
		}

		// move through while data
		while (!((line = sc.nextLine()).trim().equals("}"))) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			previous = line;
		}

		// once done connect the last line of while to the
		// loop statement for looping
		graph.addEdge(previous, whileSt);
		previous = whileSt;
		whileTrigger = true;
	}

	public void init() {
		boolean selfReferencesAllowed = false;
		setLayout(new BorderLayout());
		setSize(500, 500);
		add(new GraphPanel<String, DefaultEdge>(graph, selfReferencesAllowed), BorderLayout.CENTER);
	}
}
