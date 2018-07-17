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

import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import com.alee.extended.list.*;

import core.*;
import core.datasource.*;

public class CompanyList extends AbstractDataInput implements DockingComponent {

	private WebCheckBoxList boxList;
	private SendAccountsMovement action;
	private CheckBoxListModel model;
	public CompanyList() {
		super(null);
		setVisibleMessagePanel(false);
	}

	public String getSelected() {
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

	@Override
	public void init() {
		Vector<Record> rlist = ConnectionManager.getAccessTo("SLE$USER_COMPANY").search(
				"username = '" + Session.getUserName() + "'", null);
		model = new CheckBoxListModel();
		for (Record rcd : rlist) {
			model.addCheckBoxElement(rcd.getFieldValue("COMPANY_ID") + ": " + rcd.getFieldValue("COMPANY_NAME"));
		}
		this.boxList = new WebCheckBoxList(model);
		addWithoutBorder(new JScrollPane(boxList));
		action = new SendAccountsMovement(this);
		setToolBar(false, action);
		boxList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				model = boxList.getCheckBoxListModel();
				action.setEnabled(!model.getCheckedValues().isEmpty());
			}
		});

		boxList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				firePropertyChange("companySelected", null, model.get(boxList.getSelectedIndex()));
			}
		});
		addPropertyChangeListener("companySelected",
				(PropertyChangeListener) DockingContainer.getView(PayrollList.class.getName()).getComponent());
	}

	@Override
	public void validateFields() {

	}
}
