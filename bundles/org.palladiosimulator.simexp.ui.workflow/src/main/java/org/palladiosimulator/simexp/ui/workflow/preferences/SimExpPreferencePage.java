package org.palladiosimulator.simexp.ui.workflow.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class SimExpPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public SimExpPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, SimExpPreferenceConstants.ID));
        // setDescription("A demonstration of a preference page implementation");
    }

    @Override
    public void createFieldEditors() {
    }
}