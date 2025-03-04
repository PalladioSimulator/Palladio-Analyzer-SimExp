package org.palladiosimulator.simexp.dsl.ea.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.palladiosimulator.simexp.dsl.ea.api.preferences.EADispatcherType;
import org.palladiosimulator.simexp.dsl.ea.api.preferences.EAPreferenceConstants;

public class EAWorkbenchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public EAWorkbenchPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, EAPreferenceConstants.ID));
        setDescription("Evolutionary algorithm preferences");
    }

    @Override
    public void createFieldEditors() {
        List<List<String>> enumValues = new ArrayList<>();
        for (EADispatcherType type : EADispatcherType.values()) {
            List<String> tuple = Arrays.asList(type.toString(), type.name());
            enumValues.add(tuple);
        }
        String[][] labelAndValues = enumValues.stream()
            .map(l -> l.stream()
                .toArray(String[]::new))
            .toArray(String[][]::new);
        addField(new RadioGroupFieldEditor(EAPreferenceConstants.DISPATCHER, "Dispatcher selection", 1, labelAndValues,
                getFieldEditorParent()));
    }
}
