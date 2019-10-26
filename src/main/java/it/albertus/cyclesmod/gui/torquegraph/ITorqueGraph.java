package it.albertus.cyclesmod.gui.torquegraph;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.nebula.visualization.xygraph.dataprovider.IDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;

public interface ITorqueGraph {

	IXYGraph getXyGraph();

	Axis getAbscissae();

	Axis getOrdinates();

	IDataProvider getDataProvider();

	Trace getTrace();

	double getValue(int index);

	void setValue(int index, double value);

	void refresh();

	short getTorqueValue(Point location);

	int getTorqueIndex(Point location);

}
