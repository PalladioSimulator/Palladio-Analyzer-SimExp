package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.function.Consumer;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.text.StringToNumberConverter;
import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.debug.core.ILaunchConfiguration;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelledOptimizationType;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.MinIntegerValidator;
import org.palladiosimulator.simexp.ui.workflow.config.debug.BaseLaunchConfigurationTab;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;

public class EvolutionaryAlgorithmConfigurationTab extends BaseLaunchConfigurationTab {
    private static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    private static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";

    private final IModelledOptimizerProvider modelledOptimizerProvider;

    private Text textPopulationSize;
    private Text textMaxGenerations;

    public EvolutionaryAlgorithmConfigurationTab(DataBindingContext ctx,
            IModelledOptimizerProvider modelledOptimizerProvider) {
        super(ctx);
        this.modelledOptimizerProvider = modelledOptimizerProvider;
    }

    @Override
    public void doCreateControl(Composite parent, DataBindingContext ctx) {
        ModifyListener modifyListener = new SimExpModifyListener();

        Composite container = new Composite(parent, SWT.NONE);
        setControl(container);
        container.setLayout(new GridLayout());

        SelectObservableValue<ModelledOptimizationType> modelledOptimizationTypeTarget = modelledOptimizerProvider
            .getModelledOptimizationType();

        ISideEffect.create(() -> {
            return modelledOptimizationTypeTarget.getValue();
        }, new Consumer<ModelledOptimizationType>() {

            @Override
            public void accept(ModelledOptimizationType selectedType) {
                if (selectedType == null) {
                    return;
                }

                recursiveSetEnabled(container, selectedType == ModelledOptimizationType.EVOLUTIONARY_ALGORITHM);
                modifyListener.modifyText(null);
            }
        });

        Composite simContainer = new Composite(container, SWT.NONE);
        simContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout simContainerLayout = new GridLayout();
        simContainerLayout.marginWidth = 0;
        simContainer.setLayout(simContainerLayout);

        createBasics(simContainer, modifyListener);
        createLimits(simContainer, modifyListener);
    }

    private void createBasics(Composite parent, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Basics");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));

        final Label simulationIDLabel = new Label(container, SWT.NONE);
        simulationIDLabel.setText("Population size:");
        textPopulationSize = new Text(container, SWT.BORDER);
        textPopulationSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPopulationSize.addModifyListener(modifyListener);
    }

    private void createLimits(Composite parent, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Limits");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));

        final Label simulationIDLabel = new Label(container, SWT.NONE);
        simulationIDLabel.setText("Max Generations:");
        textMaxGenerations = new Text(container, SWT.BORDER);
        textMaxGenerations.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMaxGenerations.addModifyListener(modifyListener);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SimulationConstants.POPULATION_SIZE, SimulationConstants.DEFAULT_POPULATION_SIZE);
    }

    @Override
    protected void doInitializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        initializeBasicsFrom(configuration, ctx);
        initializeLimitsFrom(configuration, ctx);
    }

    private void initializeBasicsFrom(ILaunchConfiguration configuration, DataBindingContext ctx) {
        IObservableValue<String> numberOfRunsTarget = WidgetProperties.text(SWT.Modify)
            .observe(textPopulationSize);
        IObservableValue<Integer> numberOfRunsModel = ConfigurationProperties
            .integer(SimulationConstants.POPULATION_SIZE)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> numberOfRunsUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        numberOfRunsUpdateStrategy.setBeforeSetValidator(new MinIntegerValidator("Population size", 1));
        Binding numberOfRunsBindValue = ctx.bindValue(numberOfRunsTarget, numberOfRunsModel, numberOfRunsUpdateStrategy,
                null);
        ControlDecorationSupport.create(numberOfRunsBindValue, SWT.TOP | SWT.RIGHT);
    }

    private void initializeLimitsFrom(ILaunchConfiguration configuration, DataBindingContext ctx) {
        IObservableValue<String> maxGenTarget = WidgetProperties.text(SWT.Modify)
            .observe(textMaxGenerations);
        IObservableValue<Integer> maxGenModel = ConfigurationProperties
            .integer(SimulationConstants.MAX_GENERATIONS, false)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> customMaxGenUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        customMaxGenUpdateStrategy.setConverter(StringToNumberConverter.toInteger(false));
        Binding maxGenBindValue = ctx.bindValue(maxGenTarget, maxGenModel, customMaxGenUpdateStrategy, null);
        ControlDecorationSupport.create(maxGenBindValue, SWT.TOP | SWT.RIGHT);
    }

    @Override
    public Image getImage() {
        return ImageRegistryHelper.getTabImage(PLUGIN_ID, CONFIGURATION_TAB_IMAGE_PATH);
    }

    @Override
    public String getName() {
        return "Evolutionary Algorithm Configuration";
    }

    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.EvolutionaryAlgorithmConfigurationTab";
    }
}