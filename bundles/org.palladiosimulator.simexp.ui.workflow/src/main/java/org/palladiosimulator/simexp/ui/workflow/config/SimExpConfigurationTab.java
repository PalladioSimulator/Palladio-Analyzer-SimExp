package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.MinIntegerValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.NotEmptyValidator;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;

public class SimExpConfigurationTab extends SimExpLaunchConfigurationTab {
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    public static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";

    private final SimulatorConfiguration simulatorConfiguration;

    private Text textSimulationID;
    private Text textNumberOfRuns;
    private Text textNumerOfSimulationsPerRun;

    public SimExpConfigurationTab() {
        this.simulatorConfiguration = new SimulatorConfiguration(ctx);
    }

    @Override
    public void createControl(Composite parent) {
        ModifyListener modifyListener = new SimExpModifyListener();

        Composite container = new Composite(parent, SWT.NONE);
        setControl(container);
        container.setLayout(new GridLayout());

        final Label simulationIDLabel = new Label(container, SWT.NONE);
        simulationIDLabel.setText("Simulation-ID:");
        textSimulationID = new Text(container, SWT.BORDER);
        textSimulationID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textSimulationID.addModifyListener(modifyListener);

        final Label numberOfRunsLabel = new Label(container, SWT.NONE);
        numberOfRunsLabel.setText("Number of runs:");
        textNumberOfRuns = new Text(container, SWT.BORDER);
        textNumberOfRuns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textNumberOfRuns.addModifyListener(modifyListener);

        final Label numberOfSimulationsPerRunLabel = new Label(container, SWT.NONE);
        numberOfSimulationsPerRunLabel.setText("Number of simulations per run:");
        textNumerOfSimulationsPerRun = new Text(container, SWT.BORDER);
        textNumerOfSimulationsPerRun.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textNumerOfSimulationsPerRun.addModifyListener(modifyListener);

        Composite simulationParent = new Composite(container, SWT.BORDER);
        simulationParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        simulationParent.setLayout(new GridLayout(2, false));

        simulatorConfiguration.createControl(simulationParent, modifyListener);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SimulationConstants.NUMBER_OF_RUNS, SimulationConstants.DEFAULT_NUMBER_OF_RUNS);
        configuration.setAttribute(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN,
                SimulationConstants.DEFAULT_NUMBER_OF_SIMULATIONS_PER_RUN);
        simulatorConfiguration.setDefaults(configuration);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        IObservableValue<String> simulationIdTarget = WidgetProperties.text(SWT.Modify)
            .observe(textSimulationID);
        IObservableValue<String> simulationIdModel = ConfigurationProperties.string(SimulationConstants.SIMULATION_ID)
            .observe(configuration);
        UpdateValueStrategy<String, String> simulationIdUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        simulationIdUpdateStrategy.setBeforeSetValidator(new NotEmptyValidator("Simulation ID"));
        Binding simulationIdBindValue = ctx.bindValue(simulationIdTarget, simulationIdModel, simulationIdUpdateStrategy,
                null);
        ControlDecorationSupport.create(simulationIdBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> numberOfRunsTarget = WidgetProperties.text(SWT.Modify)
            .observe(textNumberOfRuns);
        IObservableValue<Integer> numberOfRunsModel = ConfigurationProperties
            .integer(SimulationConstants.NUMBER_OF_RUNS)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> numberOfRunsUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        numberOfRunsUpdateStrategy.setBeforeSetValidator(new MinIntegerValidator("Number of runs", 1));
        Binding numberOfRunsBindValue = ctx.bindValue(numberOfRunsTarget, numberOfRunsModel, numberOfRunsUpdateStrategy,
                null);
        ControlDecorationSupport.create(numberOfRunsBindValue, SWT.TOP | SWT.RIGHT);

        IObservableValue<String> numberOfSimulationsPerRunTarget = WidgetProperties.text(SWT.Modify)
            .observe(textNumerOfSimulationsPerRun);
        IObservableValue<Integer> numberOfSimulationsPerRunModel = ConfigurationProperties
            .integer(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN)
            .observe(configuration);
        UpdateValueStrategy<String, Integer> numberOfSimulationsPerRunUpdateStrategy = new UpdateValueStrategy<>(
                UpdateValueStrategy.POLICY_CONVERT);
        numberOfSimulationsPerRunUpdateStrategy
            .setBeforeSetValidator(new MinIntegerValidator("Number of simulations per run", 1));
        Binding numberOfSimulationsPerRunBindValue = ctx.bindValue(numberOfSimulationsPerRunTarget,
                numberOfSimulationsPerRunModel, numberOfSimulationsPerRunUpdateStrategy, null);
        ControlDecorationSupport.create(numberOfSimulationsPerRunBindValue, SWT.TOP | SWT.RIGHT);

        simulatorConfiguration.initializeFrom(configuration);

        ctx.updateTargets();
    }

    @Override
    public Image getImage() {
        return ImageRegistryHelper.getTabImage(PLUGIN_ID, CONFIGURATION_TAB_IMAGE_PATH);
    }

    @Override
    public String getName() {
        return "Simulation Configuration";
    }

    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.SimExpSimulationConfigurationTab";
    }
}