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

import gui.*;
import gui.docking.*;

import java.beans.*;

import javax.swing.*;

import action.*;
import core.*;
import core.datasource.*;

/**
 * Address book list
 * 
 * @author terry
 * 
 */
public class AddressBook extends UIListPanel implements DockingComponent {

	ServiceRequest request;

	/**
	 * new instance
	 */
	public AddressBook() {
		super(null);
		String myf = PluginManager.getMyFile("MailBomber", "m_address_book");
		this.request = new ServiceRequest(ServiceRequest.DB_QUERY, myf, null);
		setToolBar(new NewRecord(this), new EditRecord(this), new DeleteRecord(this));
		putClientProperty(TConstants.SHOW_COLUMNS, "m_abname;m_abemail;m_aborganization");
		putClientProperty(TConstants.ICON_PARAMETERS, "0;*;m_abphoto");
	}

	@Override
	public UIComponentPanel getUIFor(AbstractAction aa) {
		UIComponentPanel pane = null;
		if (aa instanceof NewRecord) {
			Record r = getRecordModel();
			r.setFieldValue(0, System.currentTimeMillis());
			pane = new AddressBookRecord(r, true);
		}
		if (aa instanceof EditRecord) {
			pane = new AddressBookRecord(getRecord(), false);
		}
		return pane;
	}

	@Override
	public void init() {
		setServiceRequest(request);
		setView(LIST_VIEW_VERTICAL);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}
}
