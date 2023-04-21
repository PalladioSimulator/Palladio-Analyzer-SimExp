package org.palladiosimulator.simexp.ui.workflow.config;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.LaunchConfigPlugin;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpConfigurationTab extends AbstractLaunchConfigurationTab {
	public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
	public static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";
	
	private Text textSimulationID;
	private Text textNumberOfRuns;
	private Text textNumerOfSimulationsPerRun;
	private TabFolder simulationEngineTabFolder;
	
	private Button buttonPerformance;
	private Button buttonReliability;
	private Button buttonPerformability;
	
	private Text textMonitorRepository;
	private List monitors;
	
	private Text textFailureScenarioModel;
	
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
        
        final Label simulationEngineLabel = new Label(container, SWT.NONE);
        simulationEngineLabel.setText("Simulation Engine");
        
        simulationEngineTabFolder = new TabFolder(container, SWT.BORDER);
        simulationEngineTabFolder.setLayout(new GridLayout());
        simulationEngineTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        simulationEngineTabFolder.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		setDirty(true);
                updateLaunchConfigurationDialog();
        	}
		});
        
        createPcmTab();
        
        TabItem prismTab = new TabItem(simulationEngineTabFolder, SWT.NULL);
        prismTab.setText("PRISM");
	}
	
	private void createPcmTab() {
		TabItem pcmTab = new TabItem(simulationEngineTabFolder, SWT.NULL);
		pcmTab.setText("PCM");
		
		final Composite pcmContainer = new Composite(simulationEngineTabFolder, SWT.NONE);
		pcmContainer.setLayout(new GridLayout());
		pcmTab.setControl(pcmContainer);
		
		final Group qualityObjectivesGroup = new Group(pcmContainer, SWT.NONE);
		qualityObjectivesGroup.setText("Quality objectives");
		qualityObjectivesGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		buttonPerformance = new Button(qualityObjectivesGroup, SWT.RADIO);
		buttonPerformance.setText("Performance");
		buttonPerformance.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
                updateLaunchConfigurationDialog();
                monitors.setItems("ResponseTime");
			}
		});

		buttonReliability = new Button(qualityObjectivesGroup, SWT.RADIO);
		buttonReliability.setText("Reliability");
		buttonReliability.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
                updateLaunchConfigurationDialog();
                monitors.setItems("ResponseTime");
			}
		});
		
		buttonPerformability = new Button(qualityObjectivesGroup, SWT.RADIO);
		buttonPerformability.setText("Performability");
		buttonPerformability.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
                updateLaunchConfigurationDialog();
                monitors.setItems("ResponseTime", "SystemExecutionResultType");
			}
		});
		
		textMonitorRepository = new Text(pcmContainer, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(pcmContainer, modifyListener, "Monitor Repository File",
                ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION, textMonitorRepository, "Select Monitor Repository File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
        
        final Group monitorsGroup = new Group(pcmContainer, SWT.NONE);
        monitorsGroup.setText("Monitors");
        monitorsGroup.setLayout(new GridLayout(1, false));
        monitorsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        monitors = new List(monitorsGroup, SWT.MULTI | SWT.BORDER);
        monitors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        textFailureScenarioModel = new Text(pcmContainer, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(pcmContainer, modifyListener, "Failure Scenario File",
                ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE_EXTENSION, textFailureScenarioModel, "Select Failure Scenario File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
            textSimulationID.setText(configuration.getAttribute(SimulationConstants.SIMULATION_ID, SimulationConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(),"Simulation ID", e.getMessage());
        }
		
		try {
			int numberOfRuns = configuration.getAttribute(SimulationConstants.NUMBER_OF_RUNS, SimulationConstants.DEFAULT_NUMBER_OF_RUNS);
            textNumberOfRuns.setText(String.valueOf(numberOfRuns));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(),"Number of runs", e.getMessage());
        }
		
		try {
			int numberOfSimulationsPerRun = configuration.getAttribute(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN,
					SimulationConstants.DEFAULT_NUMBER_OF_SIMULATIONS_PER_RUN);
            textNumerOfSimulationsPerRun.setText(String.valueOf(numberOfSimulationsPerRun));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(),"Number of simulations per run", e.getMessage());
        }
		
		try {
			int selectedEngine = configuration.getAttribute("Simulation Engine", 0);
			simulationEngineTabFolder.setSelection(selectedEngine);
		} catch (CoreException e) {
			LaunchConfigPlugin.errorLogger(getName(), "Simulation Engine", e.getMessage());
		}
		
		try {
			boolean performanceSelected = configuration.getAttribute("Performance", true);
			buttonPerformance.setSelection(performanceSelected);
			if (performanceSelected) {
				monitors.setItems("ResponseTime");
			}
		} catch (CoreException e) {
			LaunchConfigPlugin.errorLogger(getName(), "Simulation Engine", e.getMessage());
		}
		
		try {
			boolean reliabilitySelected = configuration.getAttribute("Reliability", false);
			buttonReliability.setSelection(reliabilitySelected);
			if (reliabilitySelected) {
				monitors.setItems("ResponseTime");
			}
		} catch (CoreException e) {
			LaunchConfigPlugin.errorLogger(getName(), "Simulation Engine", e.getMessage());
		}
		
		try {
			boolean performabilitySelected = configuration.getAttribute("Performability", false);
			buttonPerformability.setSelection(performabilitySelected);
			if (performabilitySelected) {
				monitors.setItems("ResponseTime", "SystemExecutionResultType");
			}
		} catch (CoreException e) {
			LaunchConfigPlugin.errorLogger(getName(), "Simulation Engine", e.getMessage());
		}
		
		try {
            textMonitorRepository.setText(configuration.getAttribute(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Monitor Repository File", e.getMessage());
        }
		
		try {
            textFailureScenarioModel.setText(configuration.getAttribute(ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Failure Scenario File", e.getMessage());
        }
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(SimulationConstants.SIMULATION_ID, textSimulationID.getText());
		
		try {
			int numberOfRuns = Integer.parseInt(textNumberOfRuns.getText());
			configuration.setAttribute(SimulationConstants.NUMBER_OF_RUNS, numberOfRuns);
		} catch (NumberFormatException ignore) {
			
		}
		
		try {
			int numberOfSimulationsPerRun = Integer.parseInt(textNumerOfSimulationsPerRun.getText());
			configuration.setAttribute(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN, numberOfSimulationsPerRun);
		} catch (NumberFormatException ignore) {
			
		}
		
		configuration.setAttribute("Simulation Engine", simulationEngineTabFolder.getSelectionIndex());
		configuration.setAttribute("Performance", buttonPerformance.getSelection());
		configuration.setAttribute("Reliability", buttonReliability.getSelection());
		configuration.setAttribute("Performability", buttonPerformability.getSelection());
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		
		if (textSimulationID.getText().isBlank()) {
			setErrorMessage("Simulation ID is missing");
			return false;
		}
		
		if (textNumberOfRuns.getText().isBlank()) {
			setErrorMessage("Number of runs is missing");
			return false;
		}
		
		try {
			int numberOfRuns = Integer.parseInt(textNumberOfRuns.getText());
			
			if (numberOfRuns < 0) {
				setErrorMessage("Number of runs must not be negative");
				return false;
			}
		} catch (Exception e) {
			setErrorMessage("Number of runs must be an integer");
			return false;
		}
		
		if (textNumerOfSimulationsPerRun.getText().isBlank()) {
			setErrorMessage("Number of simulations per run is missing");
			return false;
		}
		
		try {
			int numberOfSimulationsPerRun = Integer.parseInt(textNumerOfSimulationsPerRun.getText());
			
			if (numberOfSimulationsPerRun < 0) {
				setErrorMessage("Number of simulations per run must not be negative");
				return false;
			}
		} catch (NumberFormatException e) {
			setErrorMessage("Number of simulations per run must be an integer");
			return false;
		}
		
		if (simulationEngineTabFolder.getSelection()[0].getText().equals("PCM")) {
			if (!TabHelper.validateFilenameExtension(textMonitorRepository.getText(), ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION)) {
	            setErrorMessage("Monitor Repository is missing.");
	            return false;
	        }
			
			if (buttonPerformability.getSelection()) {
				if (!TabHelper.validateFilenameExtension(textFailureScenarioModel.getText(), ModelFileTypeConstants.FAILURE_SCENARIO_MODEL_FILE_EXTENSION)) {
		            setErrorMessage("Failure Scenario Model is missing.");
		            return false;
		        }
			}
		} else if (simulationEngineTabFolder.getSelection()[0].getText().equals("PRISM")) {
			// TODO
		}
		
		return true;
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
