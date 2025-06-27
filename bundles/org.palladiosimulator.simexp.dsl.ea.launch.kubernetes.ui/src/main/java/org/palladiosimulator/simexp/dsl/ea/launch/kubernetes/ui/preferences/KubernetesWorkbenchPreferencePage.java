package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.ui.preferences;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
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
                75, 15, StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));

        addField(new StringFieldEditor(KubernetesPreferenceConstants.RABBIT_MQ_URL,
                KubernetesPreferenceConstants.RABBIT_MQ_URL, getFieldEditorParent()));
        addField(new StringFieldEditor(KubernetesPreferenceConstants.INTERNAL_RABBIT_MQ_URL,
                KubernetesPreferenceConstants.INTERNAL_RABBIT_MQ_URL, getFieldEditorParent()));
        addField(new StringFieldEditor(KubernetesPreferenceConstants.RABBIT_QUEUE_OUT,
                KubernetesPreferenceConstants.RABBIT_QUEUE_OUT, getFieldEditorParent()));
        addField(new StringFieldEditor(KubernetesPreferenceConstants.RABBIT_QUEUE_IN,
                KubernetesPreferenceConstants.RABBIT_QUEUE_IN, getFieldEditorParent()));
        IntegerFieldEditor consumerTimeoutEditor = new IntegerFieldEditor(
                KubernetesPreferenceConstants.RABBIT_CONSUMER_TIMEOUT,
                KubernetesPreferenceConstants.RABBIT_CONSUMER_TIMEOUT + " (h)", getFieldEditorParent());
        int week_hours = (int) TimeUnit.DAYS.toHours(7);
        consumerTimeoutEditor.setValidRange(1, week_hours);
        addField(consumerTimeoutEditor);

        IntegerFieldEditor maxDeliveryEditor = new IntegerFieldEditor(KubernetesPreferenceConstants.RABBIT_MAX_DELIVERY,
                KubernetesPreferenceConstants.RABBIT_MAX_DELIVERY, getFieldEditorParent());
        maxDeliveryEditor.setValidRange(1, 10);
        addField(maxDeliveryEditor);
    }

}
