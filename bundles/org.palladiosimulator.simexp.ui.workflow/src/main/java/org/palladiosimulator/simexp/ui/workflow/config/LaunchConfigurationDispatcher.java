package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;

public class LaunchConfigurationDispatcher implements ILaunchConfigurationWorkingCopy {
    private ILaunchConfigurationWorkingCopy delegate;

    public LaunchConfigurationDispatcher(ILaunchConfigurationWorkingCopy delegate) {
        this.delegate = delegate;
    }

    public void setDelegate(ILaunchConfigurationWorkingCopy delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean contentsEqual(ILaunchConfiguration configuration) {
        return delegate.contentsEqual(configuration);
    }

    @Override
    public ILaunchConfigurationWorkingCopy copy(String name) throws CoreException {
        return delegate.copy(name);
    }

    @Override
    public void delete() throws CoreException {
        delegate.delete();
    }

    @Override
    public void delete(int flag) throws CoreException {
        delegate.delete(flag);
    }

    @Override
    public boolean exists() {
        return delegate.exists();
    }

    @Override
    public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException {
        return delegate.getAttribute(attributeName, defaultValue);
    }

    @Override
    public int getAttribute(String attributeName, int defaultValue) throws CoreException {
        return delegate.getAttribute(attributeName, defaultValue);
    }

    @Override
    public List<String> getAttribute(String attributeName, List<String> defaultValue) throws CoreException {
        return delegate.getAttribute(attributeName, defaultValue);
    }

    @Override
    public Set<String> getAttribute(String attributeName, Set<String> defaultValue) throws CoreException {
        return delegate.getAttribute(attributeName, defaultValue);
    }

    @Override
    public Map<String, String> getAttribute(String attributeName, Map<String, String> defaultValue)
            throws CoreException {
        return delegate.getAttribute(attributeName, defaultValue);
    }

    @Override
    public String getAttribute(String attributeName, String defaultValue) throws CoreException {
        return delegate.getAttribute(attributeName, defaultValue);
    }

    @Override
    public Map<String, Object> getAttributes() throws CoreException {
        return delegate.getAttributes();
    }

    @Override
    public String getCategory() throws CoreException {
        return delegate.getCategory();
    }

    @Override
    public IFile getFile() {
        return delegate.getFile();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IPath getLocation() {
        return delegate.getLocation();
    }

    @Override
    public IResource[] getMappedResources() throws CoreException {
        return delegate.getMappedResources();
    }

    @Override
    public String getMemento() throws CoreException {
        return delegate.getMemento();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Set<String> getModes() throws CoreException {
        return delegate.getModes();
    }

    @Override
    public ILaunchDelegate getPreferredDelegate(Set<String> modes) throws CoreException {
        return delegate.getPreferredDelegate(modes);
    }

    @Override
    public ILaunchConfigurationType getType() throws CoreException {
        return delegate.getType();
    }

    @Override
    public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
        return delegate.getWorkingCopy();
    }

    @Override
    public boolean hasAttribute(String attributeName) throws CoreException {
        return delegate.hasAttribute(attributeName);
    }

    @Override
    public boolean isLocal() {
        return delegate.isLocal();
    }

    @Override
    public boolean isMigrationCandidate() throws CoreException {
        return delegate.isMigrationCandidate();
    }

    @Override
    public boolean isWorkingCopy() {
        return delegate.isWorkingCopy();
    }

    @Override
    public ILaunch launch(String mode, IProgressMonitor monitor) throws CoreException {
        return delegate.launch(mode, monitor);
    }

    @Override
    public ILaunch launch(String mode, IProgressMonitor monitor, boolean build) throws CoreException {
        return delegate.launch(mode, monitor, build);
    }

    @Override
    public ILaunch launch(String mode, IProgressMonitor monitor, boolean build, boolean register) throws CoreException {
        return delegate.launch(mode, monitor, build, register);
    }

    @Override
    public void migrate() throws CoreException {
        delegate.migrate();
    }

    @Override
    public boolean supportsMode(String mode) throws CoreException {
        return delegate.supportsMode(mode);
    }

    @Override
    public boolean isReadOnly() {
        return delegate.isReadOnly();
    }

    @Override
    public ILaunchConfiguration getPrototype() throws CoreException {
        return delegate.getPrototype();
    }

    @Override
    public boolean isAttributeModified(String attribute) throws CoreException {
        return delegate.isAttributeModified(attribute);
    }

    @Override
    public boolean isPrototype() {
        return delegate.isPrototype();
    }

    @Override
    public Collection<ILaunchConfiguration> getPrototypeChildren() throws CoreException {
        return delegate.getPrototypeChildren();
    }

    @Override
    public int getKind() throws CoreException {
        return delegate.getKind();
    }

    @Override
    public Set<String> getPrototypeVisibleAttributes() throws CoreException {
        return delegate.getPrototypeVisibleAttributes();
    }

    @Override
    public void setPrototypeAttributeVisibility(String attribute, boolean visible) throws CoreException {
        delegate.setPrototypeAttributeVisibility(attribute, visible);
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        return delegate.getAdapter(adapter);
    }

    @Override
    public boolean isDirty() {
        return delegate.isDirty();
    }

    @Override
    public ILaunchConfiguration doSave() throws CoreException {
        return delegate.doSave();
    }

    @Override
    public ILaunchConfiguration doSave(int flag) throws CoreException {
        return delegate.doSave(flag);
    }

    @Override
    public void setAttribute(String attributeName, int value) {
        delegate.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, String value) {
        delegate.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, List<String> value) {
        delegate.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, Map<String, String> value) {
        delegate.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, Set<String> value) {
        delegate.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, boolean value) {
        delegate.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, Object value) {
        delegate.setAttribute(attributeName, value);
    }

    @Override
    public ILaunchConfiguration getOriginal() {
        return delegate.getOriginal();
    }

    @Override
    public void rename(String name) {
        delegate.rename(name);
    }

    @Override
    public void setContainer(IContainer container) {
        delegate.setContainer(container);
    }

    @Override
    public void setAttributes(Map<String, ? extends Object> attributes) {
        delegate.setAttributes(attributes);
    }

    @Override
    public void setMappedResources(IResource[] resources) {
        delegate.setMappedResources(resources);
    }

    @Override
    public void setModes(Set<String> modes) {
        delegate.setModes(modes);
    }

    @Override
    public void setPreferredLaunchDelegate(Set<String> modes, String delegateId) {
        delegate.setPreferredLaunchDelegate(modes, delegateId);

    }

    @Override
    public void addModes(Set<String> modes) {
        delegate.addModes(modes);
    }

    @Override
    public void removeModes(Set<String> modes) {
        delegate.removeModes(modes);
    }

    @Override
    public Object removeAttribute(String attributeName) {
        return delegate.removeAttribute(attributeName);
    }

    @Override
    public ILaunchConfigurationWorkingCopy getParent() {
        return delegate.getParent();
    }

    @Override
    public void copyAttributes(ILaunchConfiguration prototype) throws CoreException {
        delegate.copyAttributes(prototype);
    }

    @Override
    public void setPrototype(ILaunchConfiguration prototype, boolean copy) throws CoreException {
        delegate.setPrototype(prototype, copy);
    }
}
