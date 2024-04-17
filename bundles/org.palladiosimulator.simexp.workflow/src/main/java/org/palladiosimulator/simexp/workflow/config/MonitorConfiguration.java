package org.palladiosimulator.simexp.workflow.config;

import java.util.List;

public class MonitorConfiguration {
	private final String monitorRepositoryFile;
	private final List<String> monitors;

	public MonitorConfiguration(String monitorRepositoryFile, List<String> monitors) {
		this.monitorRepositoryFile = monitorRepositoryFile;
		this.monitors = monitors;
	}
	
	public String getMonitorRepositoryFile() {
		return monitorRepositoryFile;
	}
	
	public List<String> getMonitors() {
		return List.copyOf(monitors);
	}
}
