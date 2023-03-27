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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.LaunchConfigPlugin;

public class SimExpSimulationParameterTab extends AbstractLaunchConfigurationTab {
	public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
	public static final String CONFIGURATION_TAB_IMAGE_PATH = "icons/configuration_tab.gif";
	
	private Text textSimulationID;
	private Text textNumberOfRuns;
	private Text textNumerOfSimulationsPerRun;
	
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
		
		return true;
	}

	@Override
    public Image getImage() {
        return ImageRegistryHelper.getTabImage(PLUGIN_ID, CONFIGURATION_TAB_IMAGE_PATH);
    }

	@Override
    public String getName() {
        return "Simulation Parameter";
    }
    
    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.SimExpSimulationParameterTab";
    }
}