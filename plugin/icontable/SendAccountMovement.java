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
import action.*;

public class SendAccountMovement extends EditRecord2 {

	public SendAccountMovement(UIListPanel uilp) {
		super(uilp);
		setIcon("SendAccountMovement");
		messagePrefix = "SendAccountMovement.";
	}
	@Override
	public void actionPerformed2() {

		dialog.dispose();

	}
}
