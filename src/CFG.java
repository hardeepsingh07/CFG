import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
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
	public static String mainSt, ifSt, ifData, elseSt, elseData, whileSt, whileData, line, previous;
	public static boolean ifTrigger = false, elseTrigger = false, whileTrigger = false;
	public static DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(
			DefaultEdge.class);

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("testing.txt"));

		while (sc.hasNextLine()) {
			if ((line = sc.nextLine()).isEmpty()) {
			} else {
				if (line.contains("if") || line.contains("while") || line.contains("else")) {
					// Before parsing the if, else or while keep track of where
					// main
					// was left off
					mainSt = previous;
					if (line.contains("if")) {
						graph.addVertex(line);
						graph.addEdge(previous, line);

						// keep instance of if to attach to else if found later
						ifSt = line;

						// keep track of previous to move forward
						previous = line;

						// move through if data
						while (!((line = sc.nextLine()).trim().equals("}"))) {
							graph.addVertex(line);
							graph.addEdge(previous, line);
							previous = line;
						}
						ifData = previous;
						ifTrigger = true;
					} else if (line.contains("else")) {
						previous = ifSt;
						while (!((line = sc.nextLine()).trim().equals("}"))) {
							graph.addVertex(line);
							graph.addEdge(previous, line);
							previous = line;
						}
						elseData = previous;
						elseTrigger = true;
					} else if (line.contains("while")) {						
						// keep instance of while statement to
						// loop back to it
						whileSt = line;
												
						//add while statement vertex
						graph.addVertex(line);
						if (ifTrigger && elseTrigger) {
							//attach while to the end of both if and else
							graph.addEdge(ifData, line);
							graph.addEdge(elseData, line);
							ifTrigger = false;
							elseTrigger = false;
							previous = line;
						} else {
							//if no if or else before then just attach the the main 
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
						whileData = previous;
						whileTrigger = true;
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

	public static void parseMain(String s) {
		mainSt += s;
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
			} else if (line.contains("if")) {
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
			} else if (line.contains("while")) {
				parseWhile(line);
			} else if (line.contains("else")) {
				parseElse(line);
			} else {
				whileData += line.trim();
			}
		}
	}

	public void init() {
		boolean selfReferencesAllowed = false;
		setLayout(new BorderLayout());
		setSize(500, 500);
		add(new GraphPanel<String, DefaultEdge>(graph, selfReferencesAllowed), BorderLayout.CENTER);
	}
}
