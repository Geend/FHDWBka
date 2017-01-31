package viewModelSwing;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;

@SuppressWarnings("serial")
public class SpecialDefaultListModel<T> extends DefaultListModel<T>{

	public void fireEntryChanged(int index) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				SpecialDefaultListModel.this.fireContentsChanged(this, index, index);
			}
		});
	}

}
