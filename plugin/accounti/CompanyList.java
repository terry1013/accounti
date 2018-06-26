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
import gui.docking.*;

import java.beans.*;

import javax.swing.*;

import core.*;
import core.datasource.*;

public class CompanyList extends UIListPanel implements DockingComponent {

	private ServiceRequest request;

	public CompanyList() {
		super(null);
		this.request = new ServiceRequest(ServiceRequest.DB_QUERY, "SLE_COMPANY", null);
		setToolBar(new SendAccountsMovement(this));
		putClientProperty(TConstants.SHOW_COLUMNS, "id;name");
		putClientProperty(TConstants.ICON_PARAMETERS, "-1; ");
	}

	@Override
	public UIComponentPanel getUIFor(AbstractAction aa) {
		UIComponentPanel pane = null;
		if (aa instanceof SendAccountsMovement) {
			pane = new SendAccountsUI(getRecord());
		}
		return pane;
	}

	@Override
	public void init() {
		setServiceRequest(request);
		// setView(LIST_VIEW_MOSAIC);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}
}
