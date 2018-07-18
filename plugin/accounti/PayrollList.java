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

import core.datasource.*;

public class PayrollList extends AbstractDataInput implements DockingComponent {

	private static Vector<String> selectedElements = new Vector<String>();
	private WebCheckBoxList boxList;
	private ProcessAccounts action;
	private CheckBoxListModel model;
	private String company;

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
				List list = model.getElements();
				// payroll to process
				for (int i = 0; i < list.size(); i++) {
					CheckBoxCellData cd = (CheckBoxCellData) model.elementAt(i);
					String pid = cd.getUserObject().toString().split("[:]")[0];
					selectedElements.remove(company + pid);
					if (model.isCheckBoxSelected(i)) {
						selectedElements.add(company + pid);
					} 
				}
				// enable actions if any payroll is selected for any company
				action.setEnabled(!selectedElements.isEmpty());
			}
		});
	}

	public String getSelected() {
		String ssel = "";
		for (Object obj : selectedElements) {
			String c_p = obj.toString();
			ssel += c_p.substring(0, 2) + "  " + c_p.substring(2, 4) + "  ";
		}
		// return ssel.substring(0, ssel.length()-2);
		return ssel.substring(0, ssel.length());
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
	public void propertyChange(PropertyChangeEvent evt) {
		Object src = evt.getSource();
		Object nval = evt.getNewValue();
		if (src instanceof CompanyList) {
			CheckBoxCellData cd = (CheckBoxCellData) nval;
			this.company = cd.getUserObject().toString().split(":")[0];
			buildModel();
		}
	}

	@Override
	public void validateFields() {

	}

	private void buildModel() {
		Vector<Record> rlist = ConnectionManager.getAccessTo("SLE$USER_PAYROLLS").search(
				"COMPANY_ID ='" + company + "'", null);
		model = new CheckBoxListModel();
		for (Record rcd : rlist) {
			String pid = (String) rcd.getFieldValue("PAYROLL_ID");
			boolean sel = selectedElements.contains(company + pid);
			model.addCheckBoxElement(pid + ": " + rcd.getFieldValue("PAYROLL_NAME"), sel);
		}
		boxList.setModel(model);
		setMessage(null);
	}
}
