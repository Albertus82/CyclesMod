package it.albertus.cycles.gui.torquegraph;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import it.albertus.cycles.model.Bike;

public class TorqueGraphCanvas extends Canvas {

	private final TorqueGraphCanvasContextMenu contextMenu;
	private final SimpleTorqueGraph torqueGraph;

	public TorqueGraphCanvas(final Composite parent, final Bike bike) {
		super(parent, SWT.NONE);

		final LightweightSystem lws = new LightweightSystem(this);
		torqueGraph = new SimpleTorqueGraph(bike);
		lws.setContents(torqueGraph.getXyGraph());

		setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		contextMenu = new TorqueGraphCanvasContextMenu(this, torqueGraph);
	}

	public void updateTexts() {
		torqueGraph.updateTexts();
		contextMenu.updateTexts();
	}

	public ITorqueGraph getTorqueGraph() {
		return torqueGraph;
	}

	public TorqueGraphCanvasContextMenu getContextMenu() {
		return contextMenu;
	}

}
