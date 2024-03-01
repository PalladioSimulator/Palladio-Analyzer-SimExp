package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
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
import org.palladiosimulator.simexp.ui.workflow.config.databinding.CompoundStringValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationObservableValue;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ExtensionValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.FileURIValidator;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpModelsTab extends AbstractLaunchConfigurationTab {

    /** The id of this plug-in. */
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    /** The path to the image file for the tab icon. */
    private static final String FILENAME_TAB_IMAGE_PATH = "icons/filenames_tab.gif";

    private final DataBindingContext ctx;

    private Text textAllocation;
    private Text textUsage;
    private Text textExperiments;
    private Text textStaticModel;
    private Text textDynamicModel;
    private Text textKModel;

    public SimExpModelsTab() {
        this.ctx = new DataBindingContext();
    }

    @Override
    public void createControl(Composite parent) {
        ModifyListener modifyListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                for (Binding binding : ctx.getBindings()) {
                    binding.validateTargetToModel();
                }
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        };

        Composite container = new Composite(parent, SWT.NONE);
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

        textKModel = new Text(architecturalModelsGroup, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(architecturalModelsGroup, modifyListener, "KModel File",
                ModelFileTypeConstants.KMODEL_FILE_EXTENSION, textKModel, "Select KModel File", getShell(),
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
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        IObservableValue<String> allocationTarget = WidgetProperties.text(SWT.Modify)
            .observe(textAllocation);
        IObservableValue<String> allocationModel = new ConfigurationObservableValue(configuration,
                ModelFileTypeConstants.ALLOCATION_FILE);
        UpdateValueStrategy<String, String> allocationUpdateStrategy = createUpdateStrategy("Allocation file",
                ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION[0]);
        Binding allocationBindValue = ctx.bindValue(allocationTarget, allocationModel, allocationUpdateStrategy, null);
        ControlDecorationSupport.create(allocationBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> usageTarget = WidgetProperties.text(SWT.Modify)
            .observe(textUsage);
        IObservableValue<String> usageModel = new ConfigurationObservableValue(configuration,
                ModelFileTypeConstants.USAGE_FILE);
        UpdateValueStrategy<String, String> usageUpdateStrategy = createUpdateStrategy("Usage file",
                ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION[0]);
        Binding usageBindValue = ctx.bindValue(usageTarget, usageModel, usageUpdateStrategy, null);
        ControlDecorationSupport.create(usageBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> experimentsTarget = WidgetProperties.text(SWT.Modify)
            .observe(textExperiments);
        IObservableValue<String> experimentsModel = new ConfigurationObservableValue(configuration,
                ModelFileTypeConstants.EXPERIMENTS_FILE);
        UpdateValueStrategy<String, String> experimentsUpdateStrategy = createUpdateStrategy("Experiments file",
                ModelFileTypeConstants.EXPERIMENTS_FILE_EXTENSION[0]);
        Binding experimentsBindValue = ctx.bindValue(experimentsTarget, experimentsModel, experimentsUpdateStrategy,
                null);
        ControlDecorationSupport.create(experimentsBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> staticTarget = WidgetProperties.text(SWT.Modify)
            .observe(textStaticModel);
        IObservableValue<String> staticModel = new ConfigurationObservableValue(configuration,
                ModelFileTypeConstants.STATIC_MODEL_FILE);
        UpdateValueStrategy<String, String> staticUpdateStrategy = createUpdateStrategy("Static environment file",
                ModelFileTypeConstants.STATIC_MODEL_FILE_EXTENSION[0]);
        Binding staticBindValue = ctx.bindValue(staticTarget, staticModel, staticUpdateStrategy, null);
        ControlDecorationSupport.create(staticBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> dynamicTarget = WidgetProperties.text(SWT.Modify)
            .observe(textDynamicModel);
        IObservableValue<String> dynamicModel = new ConfigurationObservableValue(configuration,
                ModelFileTypeConstants.DYNAMIC_MODEL_FILE);
        UpdateValueStrategy<String, String> dynamicUpdateStrategy = createUpdateStrategy("Dynamic environment file",
                ModelFileTypeConstants.DYNAMIC_MODEL_FILE_EXTENSION[0]);
        Binding dynamicBindValue = ctx.bindValue(dynamicTarget, dynamicModel, dynamicUpdateStrategy, null);
        ControlDecorationSupport.create(dynamicBindValue, SWT.TOP | SWT.RIGHT);

        ctx.updateTargets();
    }

    private UpdateValueStrategy<String, String> createUpdateStrategy(String field, String extension) {
        UpdateValueStrategy<String, String> updateValueStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        updateValueStrategy.setBeforeSetValidator(new CompoundStringValidator(
                Arrays.asList(new FileURIValidator(field), new ExtensionValidator(field, extension))));
        return updateValueStrategy;
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
