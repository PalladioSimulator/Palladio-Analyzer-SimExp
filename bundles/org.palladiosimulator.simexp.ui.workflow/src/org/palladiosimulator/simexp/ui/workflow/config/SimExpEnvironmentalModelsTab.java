package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;

public class SimExpEnvironmentalModelsTab extends AbstractLaunchConfigurationTab {
	public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
	private static final String FILENAME_TAB_IMAGE_PATH = "icons/filenames_tab.gif";
	
	private Composite container;
    private ModifyListener modifyListener;

	@Override
	public void createControl(Composite parent) {
		modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        };
        container = new Composite(parent, SWT.NONE);
        setControl(container);
        container.setLayout(new GridLayout());     
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
	}
	
	@Override
    public Image getImage() {
        return ImageRegistryHelper.getTabImage(PLUGIN_ID, FILENAME_TAB_IMAGE_PATH);
    }

	@Override
    public String getName() {
        return "Environmental Model(s)";
    }
    
    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.SimExpEnvironmentalModelsTab";
    }
}
