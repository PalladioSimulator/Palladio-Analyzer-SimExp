package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.LaunchConfigPlugin;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpEnvironmentalModelsTab extends AbstractLaunchConfigurationTab {
	public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
	private static final String FILENAME_TAB_IMAGE_PATH = "icons/filenames_tab.gif";
	
	private Text textStaticModel;
	private Text textDynamicModel;
	
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
        
		textStaticModel = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(container, modifyListener, "Static Model File", 
        		ModelFileTypeConstants.STATIC_MODEL_FILE_EXTENSION, textStaticModel, 
        		"Select Static Model File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
        
        textDynamicModel = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(container, modifyListener, "Dynamic Model File", 
        		ModelFileTypeConstants.DYNAMIC_MODEL_FILE_EXTENSION, textDynamicModel, 
        		"Select Dynamic Model File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
            textStaticModel.setText(configuration.getAttribute(ModelFileTypeConstants.STATIC_MODEL_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(),"Static Model File", e.getMessage());
        }
		
		try {
            textDynamicModel.setText(configuration.getAttribute(ModelFileTypeConstants.DYNAMIC_MODEL_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(),"Dynamic Model File", e.getMessage());
        }
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ModelFileTypeConstants.STATIC_MODEL_FILE, textStaticModel.getText());
		configuration.setAttribute(ModelFileTypeConstants.DYNAMIC_MODEL_FILE, textDynamicModel.getText());
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		
		if (!TabHelper.validateFilenameExtension(textStaticModel.getText(), ModelFileTypeConstants.STATIC_MODEL_FILE_EXTENSION)) {
            setErrorMessage("Static Model is missing.");
            return false;
        }
		
		if (!TabHelper.validateFilenameExtension(textDynamicModel.getText(), ModelFileTypeConstants.DYNAMIC_MODEL_FILE_EXTENSION)) {
            setErrorMessage("Dynamic Model is missing.");
            return false;
        }
		
		return true;
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
