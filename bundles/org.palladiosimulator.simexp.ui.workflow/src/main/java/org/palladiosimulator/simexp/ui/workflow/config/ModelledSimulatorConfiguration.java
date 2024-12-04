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
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.CompoundStringValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ControllableValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.EnumEnabler;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ExtensionValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.FileURIValidator;

import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class ModelledSimulatorConfiguration {
    private Text textSModel;

    public Composite createControl(Composite parent, DataBindingContext ctx, ModifyListener modifyListener) {
        // Composite modelledContainer = new Composite(comp, SWT.NONE);
        // Composite modelledContainer = new Composite(parent, SWT.BORDER);
        Group modelledContainer = new Group(parent, SWT.NONE);
        modelledContainer.setText(SimulatorType.MODELLED.getName());
        modelledContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        modelledContainer.setLayout(new GridLayout(4, false));
        textSModel = new Text(modelledContainer, SWT.SINGLE | SWT.BORDER);
        textSModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        /*
         * TabHelper.createFileInputParts(modelledContainer, modifyListener,
         * ModelFileTypeConstants.SMODEL_FILE_EXTENSION, textSModel, "Select SModel File",
         * ModelFileTypeConstants.EMPTY_STRING);
         */

        TabHelper.createFileInputSection(modelledContainer, modifyListener, "SModel File",
                ModelFileTypeConstants.SMODEL_FILE_EXTENSION, textSModel, "Select SModel File", parent.getShell(),
                ModelFileTypeConstants.EMPTY_STRING);

        return modelledContainer;
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
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
    }
}
