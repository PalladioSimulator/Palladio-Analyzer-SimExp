package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class LaunchSimExpTabGroup extends AbstractLaunchConfigurationTabGroup {
    
    
    public LaunchSimExpTabGroup() {}


    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        // Assemble the tab pages:
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { 
                new SimExpArchitectureModelsTab()
                , new CommonTab()
        };
        
        setTabs(tabs);
        
    }

}
