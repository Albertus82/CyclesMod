package it.albertus.cyclesmod.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;

import it.albertus.cyclesmod.gui.CyclesModGui;
import it.albertus.cyclesmod.resources.Messages;
import it.albertus.util.StringUtils;

public class OpenSelectionListener extends AskForSavingListener implements SelectionListener {

	private static final String[] EXTENSIONS = { "*.INF;*.inf;*.CFG;*.cfg" };

	public OpenSelectionListener(final CyclesModGui gui) {
		super(gui);
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (askForSaving(Messages.get("win.title"), Messages.get("msg.confirm.open.message"))) {
			final FileDialog openDialog = new FileDialog(gui.getShell(), SWT.OPEN);
			openDialog.setFilterExtensions(EXTENSIONS);
			final String fileName = openDialog.open();
			if (StringUtils.isNotBlank(fileName)) {
				gui.open(fileName);
			}
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {/* Ignore */}

}