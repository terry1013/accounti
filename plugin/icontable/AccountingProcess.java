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
package plugin.icontable;

import gui.docking.*;

import java.awt.event.*;

import net.infonode.docking.*;
import action.*;

public class AccountingProcess extends TAbstractAction {

	public AccountingProcess() {
		super(NO_SCOPE);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		SplitWindow sw = new SplitWindow(false, 0.6f, DockingContainer.createDynamicView(CompanyList.class.getName()),
				DockingContainer.createDynamicView(PayrollList.class.getName()));
		DockingContainer.setWindow(sw, getClass().getName());
	}
}
