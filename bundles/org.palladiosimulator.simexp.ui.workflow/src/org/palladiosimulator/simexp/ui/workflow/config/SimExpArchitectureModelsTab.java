package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.ModelTypeConstants;
import org.palladiosimulator.simexp.pcm.examples.executor.MonitorRepositoryLoader;
import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.LaunchConfigPlugin;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimExpArchitectureModelsTab extends AbstractLaunchConfigurationTab {
    
    /** The id of this plug-in. */
    public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
    /** The path to the image file for the tab icon. */
    private static final String FILENAME_TAB_IMAGE_PATH = "icons/filenames_tab.gif";
    

    private Text textAllocation;
    private Text textUsage;
    private Text textMonitorRepository;
    private Text textExperiments;
    private List availableMonitors;
	private List selectedMonitors;
    
    
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
        
        
        textAllocation = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(container, modifyListener, "Allocation File",
                ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION, textAllocation, "Select Allocation File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
        
        textUsage = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(container, modifyListener, "Usage File",
                ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION, textUsage, "Select Usage File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
        
        textMonitorRepository = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(container, modifyListener, "MonitorRepository File",
                ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION, textMonitorRepository, "Select MonitorRepository File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
        
        textMonitorRepository.addModifyListener(new ModifyListener() {	
			@Override
			public void modifyText(ModifyEvent e) {
				availableMonitors.setItems(getMonitors(textMonitorRepository.getText()));
	            selectedMonitors.removeAll();
			}
		});
        
        textExperiments = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(container, modifyListener, "Experiments File",
                ModelFileTypeConstants.EXPERIMENTS_FILE_EXTENSION, textExperiments, "Select Experiments File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
        
        createMonitorLists();
    }
    
    private void createMonitorLists() {
        Composite monitorComposite = new Composite(container, SWT.NONE);
        monitorComposite.setLayout(new GridLayout(3, false));
        monitorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group leftGroup = new Group(monitorComposite, SWT.NONE);
        leftGroup.setText("Available monitors");
        leftGroup.setLayout(new GridLayout(1, false));
        leftGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        availableMonitors = new List(leftGroup, SWT.MULTI | SWT.BORDER);
        availableMonitors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite buttons = new Composite(monitorComposite, SWT.NONE);
        buttons.setLayout(new GridLayout(1, false));
        
        Button moveAllRight = new Button(buttons, SWT.PUSH);
        moveAllRight.setText(">>");
        moveAllRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        moveAllRight.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.Selection) {
					String[] items = availableMonitors.getItems();
					for (String s : items) {
						availableMonitors.remove(s);
						selectedMonitors.add(s);
					}
					if (items.length != 0) {
						setDirty(true);
		                updateLaunchConfigurationDialog();
					}
				}
			}
		});

        Button moveRight = new Button(buttons, SWT.PUSH);
        moveRight.setText(">");
        moveRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        moveRight.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.Selection) {
					String[] selection = availableMonitors.getSelection();
					for (String s : selection) {
						availableMonitors.remove(s);
						selectedMonitors.add(s);
					}
					if (selection.length != 0) {
						setDirty(true);
		                updateLaunchConfigurationDialog();
					}
				}
			}
		});
        
        Button moveLeft = new Button(buttons, SWT.PUSH);
        moveLeft.setText("<");
        moveLeft.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        moveLeft.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.Selection) {
					String[] selection = selectedMonitors.getSelection();
					for (String s : selection) {
						availableMonitors.add(s);
						selectedMonitors.remove(s);
					}
					if (selection.length != 0) {
						setDirty(true);
		                updateLaunchConfigurationDialog();
					}
				}
			}
		});
        
        Button moveAllLeft = new Button(buttons, SWT.PUSH);
        moveAllLeft.setText("<<");
        moveAllLeft.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        moveAllLeft.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.Selection) {
					String[] items = selectedMonitors.getItems();
					for (String s : items) {
						availableMonitors.add(s);
						selectedMonitors.remove(s);
					}
					if (items.length != 0) {
						setDirty(true);
		                updateLaunchConfigurationDialog();
					}
				}
			}
		});

        Group rightGroup = new Group(monitorComposite, SWT.NONE);
        rightGroup.setText("Selected monitors");
        rightGroup.setLayout(new GridLayout(1, false));
        rightGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        selectedMonitors = new List(rightGroup, SWT.MULTI | SWT.BORDER);
        selectedMonitors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // TODO Auto-generated method stub
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            textAllocation.setText(configuration.getAttribute(ModelFileTypeConstants.ALLOCATION_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Allocation File", e.getMessage());
        }
        
        try {
            textUsage.setText(configuration.getAttribute(ModelFileTypeConstants.USAGE_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Usage File", e.getMessage());
        }
        
        try {
            textMonitorRepository.setText(configuration.getAttribute(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "MonitorRepository File", e.getMessage());
        }
        
        try {
            textExperiments.setText(configuration.getAttribute(ModelFileTypeConstants.EXPERIMENTS_FILE, ModelFileTypeConstants.EMPTY_STRING));
        } catch (CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Experiments File", e.getMessage());
        }
        
        try {
        	java.util.List<String> selectedMonitors = configuration.getAttribute(ModelFileTypeConstants.MONITORS, Collections.emptyList());
        	this.selectedMonitors.setItems(selectedMonitors.toArray(String[]::new));
        	
        	for (String s : selectedMonitors) {
        		availableMonitors.remove(s);
        	}
        	
        } catch (CoreException e) {
			LaunchConfigPlugin.errorLogger(getName(), "Monitors", e.getMessage());
		}
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(ModelFileTypeConstants.ALLOCATION_FILE, textAllocation.getText());
        configuration.setAttribute(ModelFileTypeConstants.USAGE_FILE, textUsage.getText());
        configuration.setAttribute(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE, textMonitorRepository.getText());
        configuration.setAttribute(ModelFileTypeConstants.EXPERIMENTS_FILE, textExperiments.getText());
        configuration.setAttribute(ModelFileTypeConstants.MONITORS, Arrays.asList(selectedMonitors.getItems()));
    }
    
    
    @Override
    public boolean isValid(ILaunchConfiguration launchConfig){
        setErrorMessage(null);

        if (!TabHelper.validateFilenameExtension(textAllocation.getText(), ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION)) {
            setErrorMessage("Allocation is missing.");
            return false;
        }
        
        if (!TabHelper.validateFilenameExtension(textUsage.getText(), ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION)) {
            setErrorMessage("Usage is missing.");
            return false;
        }
        
        if (!TabHelper.validateFilenameExtension(textMonitorRepository.getText(), ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION)) {
            setErrorMessage("Monitor Repository is missing.");
            return false;
        }
        
        if (!TabHelper.validateFilenameExtension(textExperiments.getText(), ModelFileTypeConstants.EXPERIMENTS_FILE_EXTENSION)) {
            setErrorMessage("Experiments is missing.");
            return false;
        }
        
        if (selectedMonitors.getItems().length == 0) {
        	setErrorMessage("At least one monitor must be selected");
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
        return "Architecture Model(s)";
    }
    
    @Override
    public String getId() {
        return "org.palladiosimulator.simexp.ui.workflow.config.SimExpArchitectureModelsTab";
    }
    
    private String[] getMonitors(String monitorRepositoryFile) {
    	if (monitorRepositoryFile == null || !monitorRepositoryFile.endsWith(ModelTypeConstants.MONITOR_REPOSITORY_EXTENSION)) {
    		return new String[0];
    	}
    	
    	try {
    		ResourceSet rs = new ResourceSetImpl();
        	URI monitorRepositoryUri = URI.createURI(monitorRepositoryFile);
        	MonitorRepositoryLoader loader = new MonitorRepositoryLoader();
        	MonitorRepository repository = loader.load(rs, monitorRepositoryUri);
        	
        	return repository.getMonitors()
        			.stream()
        			.map(NamedElement::getEntityName)
        			.toArray(String[]::new);
    	} catch (Exception e) {
			return new String[0];
		}
    }
}
