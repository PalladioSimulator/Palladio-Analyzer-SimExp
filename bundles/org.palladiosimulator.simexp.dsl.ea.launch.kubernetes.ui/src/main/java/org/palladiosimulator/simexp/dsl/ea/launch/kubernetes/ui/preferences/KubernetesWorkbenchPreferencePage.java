package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences.KubernetesPreferenceConstants;

public class KubernetesWorkbenchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public KubernetesWorkbenchPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, KubernetesPreferenceConstants.ID));
        setDescription("Kubernetes cluster settings");
    }

    @Override
    protected void createFieldEditors() {
        addField(new StringFieldEditor(KubernetesPreferenceConstants.CLUSTER_URL,
                KubernetesPreferenceConstants.CLUSTER_URL, getFieldEditorParent()));
        addField(new StringFieldEditor(KubernetesPreferenceConstants.API_TOKEN, KubernetesPreferenceConstants.API_TOKEN,
                getFieldEditorParent()));

        addField(new StringFieldEditor(KubernetesPreferenceConstants.RABBIT_MQ_URL,
                KubernetesPreferenceConstants.RABBIT_MQ_URL, getFieldEditorParent()));
        addField(new StringFieldEditor(KubernetesPreferenceConstants.INTERNAL_RABBIT_MQ_URL,
                KubernetesPreferenceConstants.INTERNAL_RABBIT_MQ_URL, getFieldEditorParent()));
    }

}
