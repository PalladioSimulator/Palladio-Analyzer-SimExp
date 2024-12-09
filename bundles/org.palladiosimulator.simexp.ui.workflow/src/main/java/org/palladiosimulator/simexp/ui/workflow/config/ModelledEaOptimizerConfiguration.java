package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ModelledEaOptimizerConfiguration {

    public Composite createControl(Composite parent, DataBindingContext ctx, ModifyListener modifyListener) {
        Composite eaParent = new Composite(parent, SWT.NONE);
        eaParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        eaParent.setLayout(new GridLayout(1, false));

        return eaParent;
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    }

    public void initializeFrom(ILaunchConfiguration configuration, DataBindingContext ctx) {
    }
}
