package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.LaunchConfigPlugin;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpModelsTab extends AbstractLaunchConfigurationTab {

    /** The id of this plug-in. */
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    /** The path to the image file for the tab icon. */
    private static final String FILENAME_TAB_IMAGE_PATH = "icons/filenames_tab.gif";

    private Text textAllocation;
    private Text textUsage;
    private Text textExperiments;
    private Text textStaticModel;
    private Text textDynamicModel;
    private Text textSModel;

    private Composite container;
    private ModifyListener modifyListener;

    @Override
    public void createControl(Composite parent) {
        modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        };
        container = new Composite(parent, SWT.NONE);
        setControl(container);
        container.setLayout(new GridLayout());

        Group architecturalModelsGroup = new Group(container, SWT.NONE);
        architecturalModelsGroup.setText("Architectural Models");
        architecturalModelsGroup.setLayout(new GridLayout());
        architecturalModelsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        textAllocation = new Text(architecturalModelsGroup, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(architecturalModelsGroup, modifyListener, "Allocation File",
                ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION, textAllocation, "Select Allocation File", getShell(),
                ModelFileTypeConstants.EMPTY_STRING);

        textUsage = new Text(architecturalModelsGroup, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(architecturalModelsGroup, modifyListener, "Usage File",
                ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION, textUsage, "Select Usage File", getShell(),
                ModelFileTypeConstants.EMPTY_STRING);

        textExperiments = new Text(architecturalModelsGroup, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(architecturalModelsGroup, modifyListener, "Experiments File",
                ModelFileTypeConstants.EXPERIMENTS_FILE_EXTENSION, textExperiments, "Select Experiments File",
                getShell(), ModelFileTypeConstants.EMPTY_STRING);

        textSModel = new Text(architecturalModelsGroup, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(architecturalModelsGroup, modifyListener, "SModel File",
                ModelFileTypeConstants.SMODEL_FILE_EXTENSION, textSModel, "Select SModel File", getShell(),
                ModelFileTypeConstants.EMPTY_STRING);

        Group environmentalModelsGroup = new Group(container, SWT.NONE);
        environmentalModelsGroup.setText("Environmental Models");
        environmentalModelsGroup.setLayout(new GridLayout());
        environmentalModelsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        textStaticModel = new Text(environmentalModelsGroup, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(environmentalModelsGroup, modifyListener, "Static Model File",
                ModelFileTypeConstants.STATIC_MODEL_FILE_EXTENSION, textStaticModel, "Select Static Model File",
                getShell(), ModelFileTypeConstants.EMPTY_STRING);

        textDynamicModel = new Text(environmentalModelsGroup, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(environmentalModelsGroup, modifyListener, "Dynamic Model File",
                ModelFileTypeConstants.DYNAMIC_MODEL_FILE_EXTENSION, textDynamicModel, "Select Dynamic Model File",
                getShell(), ModelFileTypeConstants.EMPTY_STRING);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // TODO Auto-generated method stub
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            textAllocation.setText(configuration.getAttribute(ModelFileTypeConstants.ALLOCATION_FILE,
                    ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Allocation File", e.getMessage());
        }

        try {
            textUsage.setText(
                    configuration.getAttribute(ModelFileTypeConstants.USAGE_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Usage File", e.getMessage());
        }

        try {
            textExperiments.setText(configuration.getAttribute(ModelFileTypeConstants.EXPERIMENTS_FILE,
                    ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Experiments File", e.getMessage());
        }

        try {
            textSModel.setText(configuration.getAttribute(ModelFileTypeConstants.SMODEL_FILE,
                    ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "SModel File", e.getMessage());
        }

        try {
            textStaticModel.setText(configuration.getAttribute(ModelFileTypeConstants.STATIC_MODEL_FILE,
                    ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Static Model File", e.getMessage());
        }

        try {
            textDynamicModel.setText(configuration.getAttribute(ModelFileTypeConstants.DYNAMIC_MODEL_FILE,
                    ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Dynamic Model File", e.getMessage());
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(ModelFileTypeConstants.ALLOCATION_FILE, textAllocation.getText());
        configuration.setAttribute(ModelFileTypeConstants.USAGE_FILE, textUsage.getText());
        configuration.setAttribute(ModelFileTypeConstants.EXPERIMENTS_FILE, textExperiments.getText());
        configuration.setAttribute(ModelFileTypeConstants.SMODEL_FILE, textSModel.getText());
        configuration.setAttribute(ModelFileTypeConstants.STATIC_MODEL_FILE, textStaticModel.getText());
        configuration.setAttribute(ModelFileTypeConstants.DYNAMIC_MODEL_FILE, textDynamicModel.getText());
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        setErrorMessage(null);

        if (!TabHelper.validateFilenameExtension(textAllocation.getText(),
                ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION)) {
            setErrorMessage("Allocation is missing.");
            return false;
        }

        if (!TabHelper.validateFilenameExtension(textUsage.getText(),
                ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION)) {
            setErrorMessage("Usage is missing.");
            return false;
        }

        if (!TabHelper.validateFilenameExtension(textExperiments.getText(),
                ModelFileTypeConstants.EXPERIMENTS_FILE_EXTENSION)) {
            setErrorMessage("Experiments is missing.");
            return false;
        }

        if (!TabHelper.validateFilenameExtension(textSModel.getText(), ModelFileTypeConstants.SMODEL_FILE_EXTENSION)) {
            setErrorMessage("SModel is missing.");
            return false;
        }

        if (!TabHelper.validateFilenameExtension(textStaticModel.getText(),
                ModelFileTypeConstants.STATIC_MODEL_FILE_EXTENSION)) {
            setErrorMessage("Static Model is missing.");
            return false;
        }

        if (!TabHelper.validateFilenameExtension(textDynamicModel.getText(),
                ModelFileTypeConstants.DYNAMIC_MODEL_FILE_EXTENSION)) {
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
        return "Simulation Model(s)";
    }

    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.SimExpModelsTab";
    }
}
