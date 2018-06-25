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

import java.util.*;

import action.*;
import core.*;

/**
 * plugin entry for mail
 * 
 * @author terry
 * 
 */
public class AccountIP extends PluginAdapter {

	@Override
	public Object executePlugin(Object obj) {
		Vector<TAbstractAction> actl = new Vector<TAbstractAction>();
		actl.add(new AccountTransfer());
		actl.add(new AccountingProcess());
		
		return actl;
	}
}
