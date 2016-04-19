import com.touchgraph.graphlayout.*;
import com.touchgraph.graphlayout.interaction.*;
import java.awt.*;
import java.util.*;
import org.jgrapht.*;

public class GraphPanel<V, E> extends GLPanel {
	private static final long serialVersionUID = -7441058429719746032L;
	private Color defaultBackColor = new Color(255, 255, 255);
	private Color defaultBorderBackColor = new Color(0x02, 0x35, 0x81);
	private Color defaultForeColor = new Color((float) 0.95, (float) 0.85, (float) 0.55);

	Graph<V, E> graph;
	boolean selfReferencesAllowed = true;

    public GraphPanel(Graph<V, E> graph, boolean selfReferencesAllowed)
    {
        this.graph = graph;
        this.selfReferencesAllowed = selfReferencesAllowed;
        preinitialize();
        initialize(); 
    }

	@SuppressWarnings("rawtypes")
	public void preinitialize() {
		this.setBackground(defaultBorderBackColor);
		this.setForeground(defaultForeColor);
		scrollBarHash = new Hashtable();
		tgLensSet = new TGLensSet();
		tgPanel = new TGPanel();
		tgPanel.setBackColor(defaultBackColor);
		hvScroll = new HVScroll(tgPanel, tgLensSet);
		//zoomScroll = new ZoomScroll(tgPanel);
		hyperScroll = new HyperScroll(tgPanel);
		rotateScroll = new RotateScroll(tgPanel);
		localityScroll = new LocalityScroll(tgPanel);
	}

	public void initialize() {
		buildPanel();
		buildLens();
		tgPanel.setLensSet(tgLensSet);
		addUIs();
		try {
			if (this.graph == null) {
				randomGraph();
			} else {
				GraphConvertor<V, E> converter = new GraphConvertor<V, E>();
				Node n = (Node) converter.convertToTouchGraph(this.graph, tgPanel, this.selfReferencesAllowed);
				getHVScroll().slowScrollToCenter(n);
				tgPanel.setLocale(n, Integer.MAX_VALUE);
			}
		} catch (TGException tge) {
			System.err.println(tge.getMessage());
			tge.printStackTrace(System.err);
		}
		setVisible(true);
	}
}
