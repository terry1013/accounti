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
import javax.swing.event.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import core.*;

public class SendAccountsUI extends AbstractDataInput {

	private JRadioButton toFileRB;

	public SendAccountsUI() {
		super(null);

		// movement
		ButtonGroup bg = new ButtonGroup();
		JRadioButton jrb = TUIUtils.getJRadioButton("ttsac.todb", "sac.todb", true);
		bg.add(jrb);
		addInputComponent("sac.todb", jrb, false, true);

		toFileRB = TUIUtils.getJRadioButton("ttsac.tofile", "sac.tofile", false);
		toFileRB.addChangeListener((ChangeEvent ce) -> {
			setEnabledInputComponent("sac.filename", toFileRB.isSelected());
			preValidate(null);
		});
		bg.add(toFileRB);
		addInputComponent("sac.tofile", toFileRB, false, true);
		addInputComponent("sac.filename", TUIUtils.getWebFileChooserField("ttexport.filename", null), true, false);
		addInputComponent("sac.delat", TUIUtils.getJCheckBox("sac.delat", false), false, true);

		FormLayout lay = new FormLayout("left:pref, 3dlu, 70dlu", // columns
				"p, 3dlu, p, 3dlu, p, p, 3dlu, p"); // rows
		CellConstraints cc = new CellConstraints();
		PanelBuilder build = new PanelBuilder(lay);
		build.add(getInputComponent("sac.todb"), cc.xy(1, 1));
		build.add(getInputComponent("sac.tofile"), cc.xy(1, 3));
		build.add(getLabelFor("sac.filename"), cc.xy(1, 5));
		build.add(getInputComponent("sac.filename"), cc.xyw(1, 6, 3));
		build.add(getInputComponent("sac.delat"), cc.xy(1, 8));
		JPanel jp_mov = build.getPanel();
		jp_mov.setBorder(new TitledBorder("Movimientos"));

		// vaucher selection
		addInputComponent("sac.year", TUIUtils.getJFormattedTextField("ttsac.year", 0, 4), false, true);
		addInputComponent("sac.month", TUIUtils.getJFormattedTextField("ttsac.month", 0, 2), false, true);
		addInputComponent("sac.type", TUIUtils.getJTextField("ttsac.type", "", 2), false, true);
		addInputComponent("sac.number", TUIUtils.getJTextField("ttsac.number", "", 20), false, true);
		addInputComponent("sac.date", TUIUtils.getWebDateField("ttsac.date", TStringUtils.ZERODATE), false, true);
		addInputComponent("sac.user", TUIUtils.getJTextField("ttsac.user", "", 20), false, true);

		lay = new FormLayout("left:pref, 3dlu, left:pref, 7dlu, left:pref, 3dlu, left:pref", // columns
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"); // rows
		cc = new CellConstraints();
		build = new PanelBuilder(lay);
		build.add(getLabelFor("sac.year"), cc.xy(1, 1));
		build.add(getInputComponent("sac.year"), cc.xy(3, 1));
		build.add(getLabelFor("sac.month"), cc.xy(5, 1));
		build.add(getInputComponent("sac.month"), cc.xy(7, 1));
		build.add(getLabelFor("sac.type"), cc.xy(1, 3));
		build.add(getInputComponent("sac.type"), cc.xy(3, 3));
		build.add(getLabelFor("sac.number"), cc.xy(1, 5));
		build.add(getInputComponent("sac.number"), cc.xyw(3, 5, 5));
		build.add(getLabelFor("sac.date"), cc.xy(1, 7));
		build.add(getInputComponent("sac.date"), cc.xy(3, 7));
		build.add(getLabelFor("sac.user"), cc.xy(1, 9));
		build.add(getInputComponent("sac.user"), cc.xyw(3, 9, 5));
		JPanel jp_vau = build.getPanel();
		jp_vau.setBorder(new TitledBorder("Selección de comprobantes"));

		JPanel jp = new JPanel(new GridLayout(2, 1, 4, 4));
		jp.add(jp_mov);
		jp.add(jp_vau);
		addWithoutBorder(jp);

		setDefaultActionBar();
		preValidate(null);
	}

	@Override
	public void validateFields() {

	}
}
