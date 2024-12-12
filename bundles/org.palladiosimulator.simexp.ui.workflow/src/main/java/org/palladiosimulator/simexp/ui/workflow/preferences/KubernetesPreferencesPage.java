package org.palladiosimulator.simexp.ui.workflow.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.palladiosimulator.simexp.workflow.preferences.KubernetesSettings;

public class KubernetesPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public KubernetesPreferencesPage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(
                new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.palladiosimulator.ui.simexp.workflow"));
        setDescription("Kubernetes cluster settings");

    }

    @Override
    protected void createFieldEditors() {
        addField(new StringFieldEditor(KubernetesSettings.CLUSTER_URL, KubernetesSettings.CLUSTER_URL,
                getFieldEditorParent()));
        addField(new StringFieldEditor(KubernetesSettings.API_TOKEN, KubernetesSettings.API_TOKEN,
                getFieldEditorParent()));
    }

}
