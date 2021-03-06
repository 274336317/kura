/*******************************************************************************
 * Copyright (c) 2019 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.kura.web.server;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.kura.web.Console;
import org.eclipse.kura.web.api.ClientExtensionBundle;
import org.eclipse.kura.web.shared.GwtKuraException;
import org.eclipse.kura.web.shared.model.GwtClientExtensionBundle;
import org.eclipse.kura.web.shared.service.GwtExtensionService;

public class GwtExtensionServiceImpl extends OsgiRemoteServiceServlet implements GwtExtensionService {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public List<GwtClientExtensionBundle> getConsoleExtensions() throws GwtKuraException {
        return Console.instance().getConsoleExtensions().stream().map(GwtExtensionServiceImpl::toGwt)
                .collect(Collectors.toList());
    }

    @Override
    public List<GwtClientExtensionBundle> getLoginExtensions() throws GwtKuraException {
        return Console.instance().getLoginExtensions().stream().map(GwtExtensionServiceImpl::toGwt)
                .collect(Collectors.toList());
    }

    private static GwtClientExtensionBundle toGwt(final ClientExtensionBundle clientExtension) {
        return new GwtClientExtensionBundle(clientExtension.getProperties(), clientExtension.getEntryPointUrl());
    }

}
