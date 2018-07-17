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

import net.infonode.docking.*;

import com.alee.extended.list.*;

import core.datasource.*;

public class PayrollList extends AbstractDataInput implements DockingComponent {

	private WebCheckBoxList boxList;
	private ProcessAccounts action;
	private CheckBoxListModel model;

	public PayrollList() {
		super(null);
		setVisibleMessagePanel(false);
		this.boxList = new WebCheckBoxList();
		addWithoutBorder(new JScrollPane(boxList));
		action = new ProcessAccounts(this);
		setToolBar(false, action);
		boxList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				model = boxList.getCheckBoxListModel();
				action.setEnabled(!model.getCheckedValues().isEmpty());
			}
		});
	}

	public String getSelected() {
		List<Object> sel = model.getCheckedValues();
		String ssel = "";
		for (Object obj : sel) {
			ssel += obj.toString().split("[:]")[0];
		}
		return ssel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object src = evt.getSource();
		Object nval = evt.getNewValue();
		if (src instanceof CompanyList) {
			CheckBoxCellData cd = (CheckBoxCellData) nval;
			buildModel(cd.getUserObject().toString().split(":")[0]);
		}
	}

	private void buildModel(String cid) {
		Vector<Record> rlist = ConnectionManager.getAccessTo("SLE$USER_PAYROLLS").search("COMPANY_ID ='" + cid + "'",
				null);
		model = new CheckBoxListModel();
		for (Record rcd : rlist) {
			model.addCheckBoxElement(rcd.getFieldValue("PAYROLL_ID") + ": " + rcd.getFieldValue("PAYROLL_NAME"));
		}
		boxList.setModel(model);
		setMessage(null);
	}
	@Override
	public void init() {
		setMessage("ic.ui.msg01");

		/*
		 * boxList.addListSelectionListener(e -> { model = boxList.getCheckBoxListModel();
		 * action.setEnabled(!model.getCheckedValues().isEmpty());
		 * 
		 * });
		 */
	}

	@Override
	public void validateFields() {

	}
}
