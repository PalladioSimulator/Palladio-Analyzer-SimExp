package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class TransformationConfiguration {

    public TransformationConfiguration() {
    }

    public void createControl(Composite parent, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Transformations");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout());

        Label simulationIDLabel = new Label(container, SWT.NONE);
        simulationIDLabel.setText("Active transformations:");
        ListViewer listViewer = new ListViewer(container, SWT.MULTI | SWT.BORDER);
        listViewer.getControl()
            .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    public void initializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        // TODO Auto-generated method stub

    }

}
