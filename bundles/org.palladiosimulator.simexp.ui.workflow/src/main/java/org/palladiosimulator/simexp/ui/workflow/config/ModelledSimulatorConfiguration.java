package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.CompoundStringValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ControllableValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.EnumEnabler;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ExtensionValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.FileURIValidator;

import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class ModelledSimulatorConfiguration implements IModelledOptimizerProvider {

    private SelectObservableValue<ModelledOptimizationType> modelledOptimizationTypeTarget;
    private Text textSModel;

    public ModelledSimulatorConfiguration() {
    }

    @Override
    public SelectObservableValue<ModelledOptimizationType> getModelledOptimizationType() {
        return modelledOptimizationTypeTarget;
    }

    public Composite createControl(Composite parent, DataBindingContext ctx, ModifyListener modifyListener) {
        Group modelledParent = new Group(parent, SWT.NONE);
        modelledParent.setText(SimulatorType.MODELLED.getName());
        modelledParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        modelledParent.setLayout(new GridLayout(1, false));

        Composite modelledContainer = new Composite(modelledParent, SWT.NONE);
        modelledContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        modelledContainer.setLayout(new GridLayout(4, false));
        textSModel = new Text(modelledContainer, SWT.SINGLE | SWT.BORDER);
        textSModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        TabHelper.createFileInputSection(modelledContainer, modifyListener, "SModel File",
                ModelFileTypeConstants.SMODEL_FILE_EXTENSION, textSModel, "Select SModel File", parent.getShell(),
                ModelFileTypeConstants.EMPTY_STRING);

        Composite optimizeParent = new Composite(modelledParent, SWT.NONE);
        optimizeParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        optimizeParent.setLayout(new GridLayout(2, false));

        Group optimizationTypeGroup = new Group(optimizeParent, SWT.NONE);
        optimizationTypeGroup.setText("Optimization Kind");
        optimizationTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        optimizationTypeGroup.setLayout(new GridLayout());

        modelledOptimizationTypeTarget = new SelectObservableValue<>();
        for (ModelledOptimizationType type : ModelledOptimizationType.values()) {
            Button button = new Button(optimizationTypeGroup, SWT.RADIO);
            button.setText(type.getName());
            ISWTObservableValue<Boolean> observeable = WidgetProperties.buttonSelection()
                .observe(button);
            modelledOptimizationTypeTarget.addOption(type, observeable);
        }

        return modelledParent;
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SimulationConstants.MODELLED_OPTIMIZATION_TYPE,
                SimulationConstants.DEFAULT_MODELLED_OPTIMIZATION_TYPE.name());
    }

    public void initializeFrom(ILaunchConfiguration configuration, DataBindingContext ctx,
            SelectObservableValue<SimulatorType> simulatorTypeTarget) {
        IObservableValue<String> smodelTarget = WidgetProperties.text(SWT.Modify)
            .observe(textSModel);
        IObservableValue<String> smodelModel = ConfigurationProperties.string(ModelFileTypeConstants.SMODEL_FILE)
            .observe(configuration);
        ControllableValidator.Enabled isSmodelEnabled = new EnumEnabler<>(SimulatorType.MODELLED, simulatorTypeTarget);
        UpdateValueStrategy<String, String> smodelUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        IValidator<String> smodelUriValidator = new ControllableValidator<>(
                new CompoundStringValidator(Arrays.asList(new FileURIValidator("Usage file"),
                        new ExtensionValidator("Usage file", ModelFileTypeConstants.SMODEL_FILE_EXTENSION[0]))),
                isSmodelEnabled);
        smodelUpdateStrategy.setBeforeSetValidator(smodelUriValidator);
        Binding smodelBindValue = ctx.bindValue(smodelTarget, smodelModel, smodelUpdateStrategy, null);
        ControlDecorationSupport.create(smodelBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<ModelledOptimizationType> modelledOptimizationTypeModel = ConfigurationProperties
            .enummeration(SimulationConstants.MODELLED_OPTIMIZATION_TYPE, ModelledOptimizationType.class)
            .observe(configuration);
        UpdateValueStrategy<ModelledOptimizationType, ModelledOptimizationType> modelledOptimizationTypeUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindValue(modelledOptimizationTypeTarget, modelledOptimizationTypeModel,
                modelledOptimizationTypeUpdateStrategy, null);
    }
}
