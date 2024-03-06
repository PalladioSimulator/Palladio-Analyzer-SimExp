package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public abstract class SimExpLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
    protected final DataBindingContext ctx;

    public SimExpLaunchConfigurationTab() {
        this.ctx = new DataBindingContext();
    }

    protected class SimExpModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            resetValidationStatuses();
            validateTargetToModel();
            setDirty(true);
            updateLaunchConfigurationDialog();
        }
    };

    private void resetValidationStatuses() {
        for (Binding binding : ctx.getBindings()) {
            IObservableValue<IStatus> validationStatus = binding.getValidationStatus();
            if (!validationStatus.getValue()
                .isOK()) {
                validationStatus.setValue(Status.OK_STATUS);
            }
        }
    }

    private void validateTargetToModel() {
        for (Binding binding : ctx.getBindings()) {
            binding.validateTargetToModel();
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
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
