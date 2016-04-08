import java.applet.*;
import java.awt.*;
import java.net.URL;
import javax.swing.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

public class Something extends Applet {

    private static final long serialVersionUID = 6213379835360007840L;
    
    public static void main(String [] args)
    {
    	Graph<String, DefaultEdge> g = createSamplegraph();
    	boolean selfReferencesAllowed = false;
    	
    	JFrame frame = new JFrame();
    	frame.getContentPane().add(
    			new GraphPanel<String, DefaultEdge>(g, selfReferencesAllowed));
    	frame.setPreferredSize(new Dimension(500, 500));
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
    
    public static DirectedGraph<String, DefaultEdge> createSamplegraph()
    {
    	DirectedGraph<String, DefaultEdge> g =
                new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        String v1 = "String";
        String v2 = "n++";
        String v3 = "b++";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);

        // add edges to create a circuit
        g.addEdge(v1, v2);
        g.addEdge(v1, v3);

        return g;
    }

    public void init()
    {
        Graph<String, DefaultEdge> g = createSamplegraph();
        boolean selfReferencesAllowed = false;
        setLayout(new BorderLayout());
        setSize(500, 500);
        add(
            new GraphPanel<String, DefaultEdge>(g, selfReferencesAllowed),
            BorderLayout.CENTER);
    }

}
