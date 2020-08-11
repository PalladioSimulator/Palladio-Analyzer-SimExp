package org.palladiosimulator.simexp.pcm.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;

public class PcmExperienceSimulationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Ui", "Hello, Eclipse world");

		PcmExperienceSimulationExecutor.get().execute();
		PcmExperienceSimulationExecutor.get().evaluate();
	
		return null;
	}

}
