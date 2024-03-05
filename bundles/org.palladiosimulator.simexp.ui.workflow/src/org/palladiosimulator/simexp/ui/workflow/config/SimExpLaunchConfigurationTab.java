package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
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
            for (Binding binding : ctx.getBindings()) {
                binding.validateTargetToModel();
            }
            setDirty(true);
            updateLaunchConfigurationDialog();
        }
    };
}
