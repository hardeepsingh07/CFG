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

public class CFG extends Applet {

	public static Scanner sc = null;
	public static String mainSt, ifSt, ifSt2, ifData, elseSt, elseData, whileSt, whileData, line, previous;
	public static boolean ifTrigger = false, something = false, elseTrigger = false, whileTrigger = false;
	public static ArrayList<String> ifStatements = new ArrayList<String>();
	public static DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(
			DefaultEdge.class);

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("testing.txt"));

		while (sc.hasNextLine()) {
			if ((line = sc.nextLine()).isEmpty()) {
			} else {
				if (line.contains("if") || line.contains("while") || line.contains("else")) {
					// Before parsing the if, else or while keep track of main
					// left off statement
					mainSt = previous;
					if (line.contains("if")) {
						parseIf();
					} else if (line.contains("else")) {
						parseElse();
					} else if (line.contains("while")) {
						parseWhile();
					}
				} else {
					if (previous == null) {
						graph.addVertex(line);
						previous = line;
					} else {
						if (ifTrigger && elseTrigger) {
							graph.addVertex(line);
							graph.addEdge(ifData, line);
							graph.addEdge(elseData, line);
							previous = line;
							ifTrigger = false;
							elseTrigger = false;
						} else {
							graph.addVertex(line);
							graph.addEdge(previous, line);
							previous = line;
						}
					}
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

	public static void parseIf() {
		graph.addVertex(line);
		graph.addEdge(previous, line);

		// keep instance of if to attach to else if found later
//		ifSt = line;
		ifStatements.add(line);
		// keep track of previous to move forward
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
			} else {
				if (something) {
					graph.addVertex(line);
					graph.addEdge(ifStatements.get(ifStatements.size() - 1), line);
					graph.addEdge(previous, line);
					previous = line;
					ifStatements.remove(ifStatements.size() - 1);
					ifTrigger = false;
				} else {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					previous = line;
					//System.out.println(previous + " | " + line);
				}
			}
		}
		ifData = previous;
		previous = ifSt;
		ifTrigger = true;
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
		// keep instance of while statement to
		// loop back to it
		whileSt = line;

		// add while statement vertex
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
