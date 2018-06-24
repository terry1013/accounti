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

public class AccountTransfer extends TAbstractAction {

	public AccountTransfer() {
		super(NO_SCOPE);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		View v = DockingContainer.createDynamicView(CompanyList.class.getName());
		DockingContainer.setWindow(v, getClass().getName());
	}
}
