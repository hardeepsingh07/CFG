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
	public static String mainData, ifSt, ifData, elseSt, elseData, whileSt, whileData, line, previous;
	public static DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(
			DefaultEdge.class);

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("testing.txt"));

		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.contains("if") || line.contains("while") || line.contains("else")) {
				if(line.contains("if")) {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					
					//keep instance of if to attach to else if found later
					ifSt = line;
					
					//keep track of previous to move forward
					previous = line;
					
					//move through if data
					while(!((line = sc.nextLine()).trim().equals("}"))) {
						graph.addVertex(line);
						graph.addEdge(previous, line);
						previous = line;
					}
				} else if (line.contains("else")) {
					previous = ifSt;
					while(!((line = sc.nextLine()).trim().equals("}"))) {
						graph.addVertex(line);
						graph.addEdge(previous, line);
						previous = line;
					}
				}
			} else {
				if (previous == null) {
					graph.addVertex(line);
					previous = line;
				} else {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					previous = line;
				}
			}
		}

		// while (sc.hasNextLine()) {
		// line = sc.nextLine();
		// if (line.contains("if")) {
		// parseIf(line);
		// } else if (line.contains("else")) {
		// parseElse(line);
		// } else if (line.contains("while")) {
		// parseWhile(line);
		// } else {
		// parseMain(line);
		// }
		// }
		// System.out.println(mainData);
		// System.out.println(ifSt);
		// System.out.println(ifData);
		// System.out.println(elseSt);
		// System.out.println(elseData);
		// System.out.println(whileSt);
		// System.out.println(whileData);

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
