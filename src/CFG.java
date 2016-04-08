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

public class CFG extends Applet{

	public static Scanner sc = null;
	public static String mainData = "";
	public static String ifSt = "";
	public static String ifData = "";
	public static String elseSt = "";
	public static String elseData = "";
	public static String whileSt = "";
	public static String whileData = "";
	public static String line;
	public static DirectedGraph<String, DefaultEdge> graph =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

	public static void main(String[] args) throws Exception {
		sc = new Scanner(new File("testing.txt"));
		
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			if(line.contains("if") || line.contains("while")) {
				graph.addVertex(mainData);
				if(line.contains("if")) {
					ifSt = line;
					graph.addVertex(ifSt);
					graph.addEdge(mainData, ifSt);
					while(!((line = sc.nextLine()).trim().equals("}"))) {
						ifData += "\n" + line;
					}
					graph.addVertex(ifData);
					graph.addEdge(ifSt, ifData);
					if((line = sc.nextLine()).trim().contains("else")) {
						elseSt = line;
						graph.addVertex(elseSt);
						graph.addEdge(mainData, elseSt);
						while(!((line = sc.nextLine()).trim().equals("}"))) {
							elseData += line;
						}
						graph.addVertex(elseData);
						graph.addEdge(elseSt, elseData);
					}
				} else if (line.contains("while")) {
					whileSt = line;
					graph.addVertex(whileSt);
					graph.addEdge(mainData, whileSt);
					while(!((line = sc.nextLine()).trim().equals("}"))) {
						whileData += line;
					}
					graph.addVertex(whileData);
					graph.addEdge(whileSt, whileData);
					graph.addEdge(whileData, whileSt);
				}
			} else {
				mainData += line;
			}
			//How to tack on the last main????
		}
		
		
		
//		while (sc.hasNextLine()) {
//			line = sc.nextLine();
//			if (line.contains("if")) {
//				parseIf(line);
//			} else if (line.contains("else")) {
//				parseElse(line);
//			} else if (line.contains("while")) {
//				parseWhile(line);
//			} else {
//				parseMain(line);
//			}
//		}
//		System.out.println(mainData);
//		System.out.println(ifSt);
//		System.out.println(ifData);
//		System.out.println(elseSt);
//		System.out.println(elseData);
//		System.out.println(whileSt);
//		System.out.println(whileData);
		
		Graph<String, DefaultEdge> g = graph;
    	boolean selfReferencesAllowed = false;
    	
    	JFrame frame = new JFrame();
    	frame.getContentPane().add(
    			new GraphPanel<String, DefaultEdge>(g, selfReferencesAllowed));
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
	
	public void init()
    {
        boolean selfReferencesAllowed = false;
        setLayout(new BorderLayout());
        setSize(500, 500);
        add(
            new GraphPanel<String, DefaultEdge>(graph, selfReferencesAllowed),
            BorderLayout.CENTER);
    }
}
