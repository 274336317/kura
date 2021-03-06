/*******************************************************************************
 * Copyright (c) 2019 Eurotech and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kura.core.linux.executor.privileged;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.Charsets;
import org.eclipse.kura.core.internal.linux.executor.ExecutorUtil;
import org.eclipse.kura.core.linux.executor.LinuxExitStatus;
import org.eclipse.kura.core.linux.executor.LinuxSignal;
import org.eclipse.kura.executor.Command;
import org.eclipse.kura.executor.CommandStatus;
import org.eclipse.kura.executor.Pid;
import org.eclipse.kura.executor.PrivilegedExecutorService;
import org.eclipse.kura.executor.Signal;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivilegedExecutorServiceImpl implements PrivilegedExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(PrivilegedExecutorServiceImpl.class);
    private static final LinuxSignal DEFAULT_SIGNAL = LinuxSignal.SIGTERM;

    @SuppressWarnings("unused")
    private ComponentContext ctx;

    protected void activate(ComponentContext componentContext) {
        logger.info("activate...");
        this.ctx = componentContext;
    }

    protected void deactivate(ComponentContext componentContext) {
        logger.info("deactivate...");
        this.ctx = null;
    }

    @Override
    public CommandStatus execute(Command command) {
        if (command.getCommandLine() == null || command.getCommandLine().length == 0) {
            return buildErrorStatus(command);
        }
        if (command.getSignal() == null) {
            command.setSignal(DEFAULT_SIGNAL);
        }
        return ExecutorUtil.executePrivileged(command);
    }

    @Override
    public void execute(Command command, Consumer<CommandStatus> callback) {
        if (command.getCommandLine() == null || command.getCommandLine().length == 0) {
            callback.accept(buildErrorStatus(command));
        }
        if (command.getSignal() == null) {
            command.setSignal(DEFAULT_SIGNAL);
        }
        ExecutorUtil.executePrivileged(command, callback);
    }

    @Override
    public boolean stop(Pid pid, Signal signal) {
        boolean isStopped = false;
        if (signal == null) {
            isStopped = ExecutorUtil.stopPrivileged(pid, DEFAULT_SIGNAL);
        } else {
            isStopped = ExecutorUtil.stopPrivileged(pid, signal);
        }
        return isStopped;
    }

    @Override
    public boolean kill(String[] commandLine, Signal signal) {
        boolean isKilled = false;
        if (signal == null) {
            isKilled = ExecutorUtil.killPrivileged(commandLine, DEFAULT_SIGNAL);
        } else {
            isKilled = ExecutorUtil.killPrivileged(commandLine, signal);
        }
        return isKilled;
    }

    @Override
    public boolean isRunning(Pid pid) {
        return ExecutorUtil.isRunning(pid);
    }

    @Override
    public boolean isRunning(String[] commandLine) {
        return ExecutorUtil.isRunning(commandLine);
    }

    @Override
    public Map<String, Pid> getPids(String[] commandLine) {
        return ExecutorUtil.getPids(commandLine);
    }

    private CommandStatus buildErrorStatus(Command command) {
        CommandStatus status = new CommandStatus(command, new LinuxExitStatus(1));
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try {
            err.write("The commandLine cannot be empty or not defined".getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            logger.error("Cannot write to error stream", e);
        }
        status.setErrorStream(err);
        return status;
    }
}