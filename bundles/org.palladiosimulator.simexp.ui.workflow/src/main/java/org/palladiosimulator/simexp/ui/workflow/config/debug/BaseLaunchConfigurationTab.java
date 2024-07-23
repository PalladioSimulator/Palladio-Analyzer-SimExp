package org.palladiosimulator.simexp.ui.workflow.config.debug;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.palladiosimulator.simexp.ui.workflow.config.IResetLaunchConfigurationTab;

public abstract class BaseLaunchConfigurationTab extends AbstractLaunchConfigurationTab
        implements IResetLaunchConfigurationTab {
    private final DataBindingContext ctx;

    private LaunchConfigurationDispatcher dispatcher;
    private boolean isReset = false;

    public BaseLaunchConfigurationTab(DataBindingContext ctx) {
        this.ctx = ctx;
    }

    protected class SimExpModifyListener implements ModifyListener {
        public SimExpModifyListener() {
        }

        @Override
        public void modifyText(ModifyEvent e) {
            if (isReset) {
                return;
            }
            validateTargetToModel();
            setDirty(true);
            updateLaunchConfigurationDialog();
        }
    };

    private void validateTargetToModel() {
        for (Binding binding : ctx.getBindings()) {
            binding.validateTargetToModel();
        }
    }

    @Override
    public final void createControl(Composite parent) {
        doCreateControl(parent, ctx);
    }

    protected abstract void doCreateControl(Composite parent, DataBindingContext ctx);

    @Override
    public final void initializeFrom(ILaunchConfiguration configuration) {
        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            if (dispatcher == null) {
                ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = (ILaunchConfigurationWorkingCopy) configuration;
                dispatcher = new LaunchConfigurationDispatcher(launchConfigurationWorkingCopy);
                doInitializeFrom(dispatcher, ctx);
                return;
            }
        }

        // Reset and copy handling.
        try {
            ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
            dispatcher.setDelegate(workingCopy);
        } catch (CoreException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void setReset(boolean reset) {
        this.isReset = reset;
    }

    protected abstract void doInitializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx);

    @Override
    public final void performApply(ILaunchConfigurationWorkingCopy configuration) {
        dispatcher.setDelegate(configuration);
        ctx.updateModels();
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        for (ValidationStatusProvider statusProvider : ctx.getValidationStatusProviders()) {
            IStatus validationStatus = statusProvider.getValidationStatus()
                .getValue();
            if (!validationStatus.isOK()) {
                setErrorMessage(validationStatus.getMessage());
                return false;
            }
        }
        setErrorMessage(null);
        return true;
    }
}
