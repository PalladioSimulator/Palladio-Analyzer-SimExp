package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.debug.ui.ILaunchConfigurationTab2;

public interface IResetLaunchConfigurationTab extends ILaunchConfigurationTab2 {
    void setReset(boolean reset);
}
