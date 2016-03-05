package it.albertus.cycles.gui.listener;

import it.albertus.cycles.gui.CyclesModGui;
import it.albertus.cycles.gui.FormProperty;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PasteSelectionListener extends SelectionAdapter {

	private final CyclesModGui gui;

	public PasteSelectionListener(final CyclesModGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		paste();
	}

	public void paste() {
		for (final FormProperty fp : gui.getFormProperties().values()) {
			if (fp != null && fp.getText() != null && fp.getText().isFocusControl()) {
				fp.getText().paste();
				break;
			}
		}
	}
}
