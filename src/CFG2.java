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
	public static ArrayList<String> forStatements = new ArrayList<String>();
	public static ArrayList<String>	whileStatements = new ArrayList<String>();
	public static DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(
			DefaultEdge.class);

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("testing2.txt"));

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
				if(elseTrigger) {
					elseTriggerComp();
				} else if(ifTrigger) {
					ifTriggerComp();
				} else {
					graph.addVertex(line);
					graph.addEdge(previous, line);
					previous = line;
				}
				ifStatements.add(line);
			}
			while (!((line = sc.nextLine()).trim().equals("}"))) {
				parseData();
			}
			endIfData = previous;
			line = sc.nextLine();
			if (line.contains("else")) {
				System.out.println(line);
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
			checkP();
			whileStatements.add(line);
			while (!((line = sc.nextLine()).trim().equals("}"))) {
				parseData();
			}
			graph.addEdge(previous, whileStatements.get(whileStatements.size() - 1));
			previous = whileStatements.get(whileStatements.size() - 1);
			whileStatements.remove(whileStatements.size() - 1);
		} else if (line.contains("for")) {
			graph.addVertex(line);
			graph.addEdge(previous, line);
			forStatements.add(line);
			previous = line;
			while (!((line = sc.nextLine()).trim().equals("}"))) {
				parseData();
			}
			graph.addEdge(previous, forStatements.get(forStatements.size() - 1));
			previous = forStatements.get(forStatements.size() - 1);
			forStatements.remove(forStatements.size() - 1);
		} else {
			checkP();
		}
	}
	
	public static void checkP() {
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
	//end of main
}