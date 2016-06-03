import com.touchgraph.graphlayout.*;

import java.awt.Color;
import java.util.*;
import org.jgrapht.*;

public class GraphConvertor<V, E> {
	public Node convertToTouchGraph(
			Graph<V, E> graph, TGPanel tgPanel, boolean selfReferencesAllowed)
			throws TGException {
		List<V> jgtNodes = new ArrayList<V>(graph.vertexSet());
		Node[] tgNodes = new Node[jgtNodes.size()];

		// add all the nodes...
		for (int i = 0; i < jgtNodes.size(); i++) {
			Node n;
			if (jgtNodes.get(i) instanceof Node) {
				n = (Node) jgtNodes.get(i);
			} else {
				n = new Node(jgtNodes.get(i).toString());
			}
			tgNodes[i] = n;
			if(tgNodes[i].getLabel().contains("//")) {
				tgNodes[i].setBackColor(Color.GRAY);
			} else if (tgNodes[i].getLabel().contains("continue")) {
				tgNodes[i].setBackColor(Color.GREEN);
			} else if (tgNodes[i].getLabel().contains("break")) {
				tgNodes[i].setBackColor(Color.RED);
			} else if (tgNodes[i].getLabel().contains("++") || tgNodes[i].getLabel().contains("--")) {
				tgNodes[i].setBackColor(Color.CYAN);
				tgNodes[i].setTextColor(Color.BLACK);
			} else if (tgNodes[i].getLabel().contains("()")) {
				tgNodes[i].setBackColor(Color.ORANGE);
				String temp = tgNodes[i].getLabel();
				tgNodes[i].setTextColor(Color.BLACK);
				tgNodes[i].setLabel(temp + ": FUNCTION CALL"); 
			}
			tgPanel.addNode(n);
		}

		// add the edges...
		for (int i = 0; i < tgNodes.length; i++) {
			for (int j = 0; j < tgNodes.length; j++) {
				if ((i != j) || selfReferencesAllowed) {
					if (graph.getEdge(jgtNodes.get(i), jgtNodes.get(j)) != null) {
						tgPanel.addEdge(new Edge(tgNodes[i], tgNodes[j]));
					}
				}
			}
		}
		return tgNodes[0];
	}
}
