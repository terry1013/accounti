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
/* 
 * Copyright (c) 2003 Arnaldo Fuentes. Todos los derechos reservados.
 */

package action;

import gui.*;

/** Accion estandar para botones <code>Ok</code>
 * 
 */
public class OkAction extends RedirectAction implements DefaultAceptAction {
	
	/** nueva accion
	 * 
	 * @param uip - panel
	 */
	public OkAction(UIComponentPanel uip) {
		super(uip);
	}	
}

