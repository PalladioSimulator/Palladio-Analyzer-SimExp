package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class LaunchSimExpTabGroup extends AbstractLaunchConfigurationTabGroup {
    private final DataBindingContext ctx;

    private boolean firstTime = true;

    public LaunchSimExpTabGroup() {
        this.ctx = new DataBindingContext();
    }

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        // Assemble the tab pages:
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { //
                new SimExpModelsTab(ctx), //
                new SimExpConfigurationTab(ctx), //
                new CommonTab() };

        setTabs(tabs);
    }

    @Override
    public void dispose() {
        ctx.dispose();
        super.dispose();
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        super.initializeFrom(configuration);

        if (configuration instanceof ILaunchConfigurationWorkingCopy) {
            if (firstTime) {
                firstTime = false;
                ctx.updateTargets();
                return;
            }
        }

        setReset(true);
        try {
            ctx.updateTargets();
        } finally {
            setReset(false);
        }
    }

    private void setReset(boolean reset) {
        ILaunchConfigurationTab[] tabs = getTabs();
        for (ILaunchConfigurationTab tab : tabs) {
            if (tab instanceof IResetLaunchConfigurationTab) {
                IResetLaunchConfigurationTab resetLaunchConfigurationTab = (IResetLaunchConfigurationTab) tab;
                resetLaunchConfigurationTab.setReset(reset);
            }
        }
    }
}
