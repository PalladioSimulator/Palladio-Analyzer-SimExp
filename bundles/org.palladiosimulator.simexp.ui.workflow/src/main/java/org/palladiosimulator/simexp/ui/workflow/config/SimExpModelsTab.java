package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.CompoundStringValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ExtensionValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.FileURIValidator;
import org.palladiosimulator.simexp.ui.workflow.config.debug.BaseLaunchConfigurationTab;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpModelsTab extends BaseLaunchConfigurationTab implements IModelValueProvider {
    /** The id of this plug-in. */
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    /** The path to the image file for the tab icon. */
    private static final String FILENAME_TAB_IMAGE_PATH = "icons/filenames_tab.gif";

    private Text textAllocation;
    private Text textUsage;
    private Text textExperiments;
    private Text textStaticModel;
    private Text textDynamicModel;
    private IObservableValue<String> experimentsTarget;

    public SimExpModelsTab(DataBindingContext ctx) {
        super(ctx);
    }

    @Override
    public IObservableValue<String> getExperimentsModel() {
        return experimentsTarget;
    }

    @Override
    public void doCreateControl(Composite parent, DataBindingContext ctx) {
        ModifyListener modifyListener = new SimExpModifyListener();

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
        experimentsTarget = WidgetProperties.text(SWT.Modify)
            .observe(textExperiments);

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
    protected void doInitializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        IObservableValue<String> allocationTarget = WidgetProperties.text(SWT.Modify)
            .observe(textAllocation);
        IObservableValue<String> allocationModel = ConfigurationProperties
            .string(ModelFileTypeConstants.ALLOCATION_FILE)
            .observe(configuration);
        UpdateValueStrategy<String, String> allocationUpdateStrategy = createUpdateStrategy("Allocation file",
                ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION[0]);
        Binding allocationBindValue = ctx.bindValue(allocationTarget, allocationModel, allocationUpdateStrategy, null);
        ControlDecorationSupport.create(allocationBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> usageTarget = WidgetProperties.text(SWT.Modify)
            .observe(textUsage);
        IObservableValue<String> usageModel = ConfigurationProperties.string(ModelFileTypeConstants.USAGE_FILE)
            .observe(configuration);
        UpdateValueStrategy<String, String> usageUpdateStrategy = createUpdateStrategy("Usage file",
                ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION[0]);
        Binding usageBindValue = ctx.bindValue(usageTarget, usageModel, usageUpdateStrategy, null);
        ControlDecorationSupport.create(usageBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> experimentsModel = ConfigurationProperties
            .string(ModelFileTypeConstants.EXPERIMENTS_FILE)
            .observe(configuration);
        UpdateValueStrategy<String, String> experimentsUpdateStrategy = createUpdateStrategy("Experiments file",
                ModelFileTypeConstants.EXPERIMENTS_FILE_EXTENSION[0]);
        Binding experimentsBindValue = ctx.bindValue(experimentsTarget, experimentsModel, experimentsUpdateStrategy,
                null);
        ControlDecorationSupport.create(experimentsBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> staticTarget = WidgetProperties.text(SWT.Modify)
            .observe(textStaticModel);
        IObservableValue<String> staticModel = ConfigurationProperties.string(ModelFileTypeConstants.STATIC_MODEL_FILE)
            .observe(configuration);
        UpdateValueStrategy<String, String> staticUpdateStrategy = createUpdateStrategy("Static environment file",
                ModelFileTypeConstants.STATIC_MODEL_FILE_EXTENSION[0]);
        Binding staticBindValue = ctx.bindValue(staticTarget, staticModel, staticUpdateStrategy, null);
        ControlDecorationSupport.create(staticBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> dynamicTarget = WidgetProperties.text(SWT.Modify)
            .observe(textDynamicModel);
        IObservableValue<String> dynamicModel = ConfigurationProperties
            .string(ModelFileTypeConstants.DYNAMIC_MODEL_FILE)
            .observe(configuration);
        UpdateValueStrategy<String, String> dynamicUpdateStrategy = createUpdateStrategy("Dynamic environment file",
                ModelFileTypeConstants.DYNAMIC_MODEL_FILE_EXTENSION[0]);
        Binding dynamicBindValue = ctx.bindValue(dynamicTarget, dynamicModel, dynamicUpdateStrategy, null);
        ControlDecorationSupport.create(dynamicBindValue, SWT.TOP | SWT.RIGHT);
    }

    private UpdateValueStrategy<String, String> createUpdateStrategy(String field, String extension) {
        UpdateValueStrategy<String, String> updateValueStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        updateValueStrategy.setBeforeSetValidator(new CompoundStringValidator(
                Arrays.asList(new FileURIValidator(field), new ExtensionValidator(field, extension))));
        return updateValueStrategy;
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
