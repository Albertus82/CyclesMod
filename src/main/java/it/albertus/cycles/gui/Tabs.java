package it.albertus.cycles.gui;

import it.albertus.cycles.model.Bike;
import it.albertus.cycles.model.BikesCfg;
import it.albertus.cycles.model.Gearbox;
import it.albertus.cycles.model.Setting;
import it.albertus.cycles.model.Settings;
import it.albertus.cycles.model.Torque;
import it.albertus.cycles.resources.Resources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class Tabs {

	private final TabFolder tabFolder;

	public Tabs(CyclesModGui gui) {
		tabFolder = new TabFolder(gui.getShell(), SWT.NULL);
		for (final Bike bike : gui.getBikesInf().getBikes()) {
			final TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
			tabItem.setText(bike.getType().getDisplacement() + " cc");

			final Composite tabComposite = new Composite(tabFolder, SWT.NULL);
			tabItem.setControl(tabComposite);
			GridLayout compositeGridLayout = new GridLayout(2, false);
			tabComposite.setLayout(compositeGridLayout);

			// Settings
			final Group settingsGroup = new Group(tabComposite, SWT.NULL);
			settingsGroup.setText(Resources.get("lbl.settings"));
			// Posizionamento dell'elemento all'interno del contenitore
			final GridData settingsGroupGridLayoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
			settingsGroup.setLayoutData(settingsGroupGridLayoutData);
			// Definizione di come saranno disposti gli elementi contenuti
			final GridLayout settingsGroupGridLayout = new GridLayout();
			settingsGroupGridLayout.numColumns = 6;
			settingsGroup.setLayout(settingsGroupGridLayout);

			final GridData settingGridData = new GridData();
			settingGridData.grabExcessHorizontalSpace = true;
			for (final Setting setting : bike.getSettings().getValues().keySet()) {
				final String key = BikesCfg.buildPropertyKey(bike.getType(), Settings.class, setting.toString());
				final String defaultValue = gui.getDefaultProperties().getProperty(key);
				final Label label = new Label(settingsGroup, SWT.NULL);
				label.setText(Resources.get("lbl." + setting.toString()));
				label.setToolTipText(key);
				final Text text = new Text(settingsGroup, SWT.BORDER);
				text.setText(Integer.toString(Settings.MAX_VALUE));
				text.setTextLimit(5);
				text.setToolTipText(Resources.get("msg.tooltip.default", defaultValue));
				text.setLayoutData(settingGridData);
				text.addFocusListener(new PropertyFocusListener(defaultValue));
				text.addListener(SWT.Verify, new PropertyVerifyListener());
				gui.getFormProperties().put(key, new FormProperty(label, text));
			}

			// Torque graph
			final TorqueGraph graph = new TorqueGraph(tabComposite, bike);
			gui.getTorqueGraphs().put(bike.getType(), graph);

			// Gearbox
			final Group gearboxGroup = new Group(tabComposite, SWT.NULL);
			gearboxGroup.setText(Resources.get("lbl.gearbox"));
			final GridLayout gearboxGroupGridLayout = new GridLayout();
			gearboxGroupGridLayout.numColumns = 10;
			gearboxGroup.setLayout(gearboxGroupGridLayout);
			final GridData gearboxGroupGridLayoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
			gearboxGroup.setLayoutData(gearboxGroupGridLayoutData);

			final GridData gearGridData = new GridData();
			gearGridData.grabExcessHorizontalSpace = true;
			for (int index = 0; index < bike.getGearbox().getRatios().length; index++) {
				final String key = BikesCfg.buildPropertyKey(bike.getType(), Gearbox.class, index);
				final String defaultValue = gui.getDefaultProperties().getProperty(key);
				final Label label = new Label(gearboxGroup, SWT.NULL);
				label.setText(Resources.get("lbl.gear", index != 0 ? index : "N"));
				label.setToolTipText(key);
				final Text text = new Text(gearboxGroup, SWT.BORDER);
				text.setText(Integer.toString(Gearbox.MAX_VALUE));
				text.setTextLimit(5);
				text.setToolTipText(Resources.get("msg.tooltip.default", defaultValue));
				text.setLayoutData(gearGridData);
				text.addFocusListener(new PropertyFocusListener(defaultValue));
				text.addListener(SWT.Verify, new PropertyVerifyListener());
				gui.getFormProperties().put(key, new FormProperty(label, text));
			}

			// Torque
			final Group torqueGroup = new Group(tabComposite, SWT.NULL);
			torqueGroup.setText(Resources.get("lbl.torque"));
			final GridLayout torqueGroupGridLayout = new GridLayout();
			torqueGroupGridLayout.numColumns = 18;
			torqueGroup.setLayout(torqueGroupGridLayout);
			final GridData torqueGroupGridLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
			torqueGroupGridLayoutData.horizontalSpan = 2;
			torqueGroup.setLayoutData(torqueGroupGridLayoutData);

			final GridData torqueGridData = new GridData();
			torqueGridData.grabExcessHorizontalSpace = true;
			for (int index = 0; index < bike.getTorque().getCurve().length; index++) {
				final String key = BikesCfg.buildPropertyKey(bike.getType(), Torque.class, index);
				final String defaultValue = gui.getDefaultProperties().getProperty(key);
				final Label label = new Label(torqueGroup, SWT.NULL);
				label.setText(Resources.get("lbl.rpm", Torque.getRpm(index)));
				label.setToolTipText(key);
				final Text text = new Text(torqueGroup, SWT.BORDER);
				text.setText(Short.toString(Torque.MAX_VALUE));
				text.setTextLimit(3);
				text.setToolTipText(Resources.get("msg.tooltip.default", defaultValue));
				text.setLayoutData(torqueGridData);
				text.addFocusListener(new TorquePropertyFocusListener(defaultValue, key, graph));
				text.addListener(SWT.Verify, new PropertyVerifyListener());
				gui.getFormProperties().put(key, new FormProperty(label, text));
			}
		}
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

}
