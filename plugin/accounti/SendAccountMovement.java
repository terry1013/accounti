/*******************************************************************************
 * Copyright (C) 2017 terry.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     terry - initial API and implementation
 ******************************************************************************/
package plugin.accounti;

import gui.*;

import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import action.*;
import core.*;
import core.reporting.*;
import core.tasks.*;

public class SendAccountMovement extends TAbstractAction implements PropertyChangeListener {

	private AbstractDataInput dataInput;
	private JDialog dialog;
	private RedirectAction redirectAction;

	public SendAccountMovement(UIListPanel uilp) {
		super(RECORD_SCOPE);
		editableList = uilp;
		messagePrefix = "SendAccountMovement.";
	}
	@Override
	public void actionPerformed2() {
		Hashtable ht = dataInput.getFields();
		SendMovementTask et = new SendMovementTask(ht);
		TTaskManager.submitRunnable(et, null);
		//et.run();
		dialog.dispose();

		dialog.dispose();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		dataInput = (AbstractDataInput) editableList.getUIFor(this);
		if (dataInput != null) {
			dataInput.addPropertyChangeListener(TConstants.ACTION_PERFORMED, this);
			dialog = getDialog(dataInput, messagePrefix + "title");
			dialog.setVisible(true);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		redirectAction = (RedirectAction) evt.getNewValue();
		// does nothing if cancel action
		if (redirectAction instanceof DefaultCancelAction) {
			dialog.dispose();
			return;
		}
		// perform edit
		if (redirectAction instanceof DefaultAceptAction) {
			actionPerformed2();
		}
	}
}
