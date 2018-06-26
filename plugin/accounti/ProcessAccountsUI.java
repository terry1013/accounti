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

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import core.*;
import core.datasource.*;

public class ProcessAccountsUI extends AbstractDataInput {

	public ProcessAccountsUI(Record rcd) {
		super(null);

		//Selection
		addInputComponent("acp.year", TUIUtils.getJFormattedTextField("ttacp.year", 0,4), false, true);
		addInputComponent("acp.month", TUIUtils.getJFormattedTextField("ttacp.month", 0,2), false, true);
		addInputComponent("acp.prfrom", TUIUtils.getJTextField("ttacp.prfrom", "",2), false, true);
		addInputComponent("acp.prto", TUIUtils.getJTextField("ttacp.prto", "",2), false, true);
		addInputComponent("acp.subpfrom", TUIUtils.getJTextField("ttacp.subpfrom", "",2), false, true);
		addInputComponent("acp.subpto", TUIUtils.getJTextField("ttacp.subpto", "",2), false, true);

		FormLayout lay = new FormLayout("left:pref, 3dlu, left:pref, 7dlu, left:pref, 3dlu, left:pref", // columns
				"p, 3dlu, p, 3dlu, p"); // rows
		CellConstraints cc = new CellConstraints();
		PanelBuilder build = new PanelBuilder(lay);
		build.add(getLabelFor("acp.year"), cc.xy(1, 1));
		build.add(getInputComponent("acp.year"), cc.xy(3, 1));
		build.add(getLabelFor("acp.month"), cc.xy(5, 1));
		build.add(getInputComponent("acp.month"), cc.xy(7, 1));
		build.add(getLabelFor("acp.prfrom"), cc.xy(1, 3));
		build.add(getInputComponent("acp.prfrom"), cc.xy(3, 3));
		build.add(getLabelFor("acp.prto"), cc.xy(5, 3));
		build.add(getInputComponent("acp.prto"), cc.xy(7, 3));
		build.add(getLabelFor("acp.subpfrom"), cc.xy(1, 5));
		build.add(getInputComponent("acp.subpfrom"), cc.xy(3, 5));
		build.add(getLabelFor("acp.subpto"), cc.xy(5, 5));
		build.add(getInputComponent("acp.subpto"), cc.xy(7, 5));
		JPanel jp_selection = build.getPanel();
		jp_selection.setBorder(new TitledBorder("Selección"));

		// generation
		ButtonGroup bg = new ButtonGroup();
		JRadioButton jrb = TUIUtils.getJRadioButton("ttacp.gentest", "acp.gentest", true);
		bg.add(jrb);
		addInputComponent("acp.gentest", jrb, false, true);
		jrb = TUIUtils.getJRadioButton("ttacp.gendefi", "acp.gendefi", false);
		bg.add(jrb);
		addInputComponent("acp.gendefi", jrb, false, true);
		jrb = TUIUtils.getJRadioButton("ttacp.genacco", "acp.genacco", false);
		bg.add(jrb);
		addInputComponent("acp.genacco", jrb, false, true);

		lay = new FormLayout("left:pref", // columns
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"); // rows
		cc = new CellConstraints();
		build = new PanelBuilder(lay);
		build.add(getInputComponent("acp.gentest"), cc.xy(1, 1));
		build.add(getInputComponent("acp.gendefi"), cc.xy(1, 3));
		build.add(getInputComponent("acp.genacco"), cc.xy(1, 5));
		JPanel jp_generation = build.getPanel();
		jp_generation.setBorder(new TitledBorder("Generación"));

		// Print
		bg = new ButtonGroup();
		jrb = TUIUtils.getJRadioButton("ttacp.noprint", "acp.noprint", true);
		bg.add(jrb);
		addInputComponent("acp.noprint", jrb, false, true);
		jrb = TUIUtils.getJRadioButton("ttacp.printnopay", "acp.printnopay", false);
		bg.add(jrb);
		addInputComponent("acp.printnopay", jrb, false, true);
		jrb = TUIUtils.getJRadioButton("ttacp.printpay", "acp.printpay", false);
		bg.add(jrb);
		addInputComponent("acp.printpay", jrb, false, true);
		jrb = TUIUtils.getJRadioButton("ttacp.printpay", "acp.printboth", false);
		bg.add(jrb);
		addInputComponent("acp.printboth", jrb, false, true);
		addInputComponent("acp.discon", TUIUtils.getJCheckBox("acp.discon", false), false, true);
		
		lay = new FormLayout("left:pref", // columns
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"); // rows
		cc = new CellConstraints();
		build = new PanelBuilder(lay);
		build.add(getInputComponent("acp.noprint"), cc.xy(1, 1));
		build.add(getInputComponent("acp.printnopay"), cc.xy(1, 3));
		build.add(getInputComponent("acp.printpay"), cc.xy(1, 5));
		build.add(getInputComponent("acp.printboth"), cc.xy(1, 7));
		build.addSeparator("", cc.xy(1, 9));
		build.add(getInputComponent("acp.discon"), cc.xy(1, 11));
		JPanel jp_print = build.getPanel();
		jp_print.setBorder(new TitledBorder("Impresión"));

		JPanel jp = new JPanel(new GridLayout(3, 1, 4, 4));
		jp.add(jp_selection);
		jp.add(jp_generation);
		jp.add(jp_print);
		addWithoutBorder(jp);

		setDefaultActionBar();
		preValidate(null);
	}

	@Override
	public void validateFields() {

	}
}
