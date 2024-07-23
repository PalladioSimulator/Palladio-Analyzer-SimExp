package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateSetStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.ConfigurationProperties;

public class TransformationConfiguration {
    private final IModelValueProvider modelValueProvider;
    private final ITrafoNameProvider trafoNameProvider;

    private CheckboxTableViewer viewer;

    public TransformationConfiguration(IModelValueProvider modelValueProvider, ITrafoNameProvider trafoNameProvider) {
        this.modelValueProvider = modelValueProvider;
        this.trafoNameProvider = trafoNameProvider;
    }

    public void createControl(Composite parent, DataBindingContext ctx, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Transformations");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout());

        Label simulationIDLabel = new Label(container, SWT.NONE);
        simulationIDLabel.setText("Active transformations:");
        viewer = CheckboxTableViewer.newCheckList(container,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        viewer.getControl()
            .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ObservableListContentProvider<String> observableInput = new ObservableListContentProvider<>();
        viewer.setContentProvider(observableInput);
        WritableList<String> availableTransformationsList = new WritableList<>();
        IObservableList<String> transformations = ComputedList.create(() -> {
            IObservableValue<String> experimentsModel = modelValueProvider.getExperimentsModel();
            String experimentsModelValue = experimentsModel.getValue();
            URI experimentsFile = URI.createURI(experimentsModelValue);
            List<String> availableTransformations = trafoNameProvider.getAvailableTransformations(experimentsFile);
            availableTransformationsList.clear();
            availableTransformationsList.addAll(availableTransformations);
            return availableTransformationsList;
        });
        viewer.setInput(transformations);
    }

    public void initializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        IObservableSet<String> target = ViewerProperties.<CheckboxTableViewer, String> checkedElements(String.class)
            .observe(viewer);
        IObservableSet<String> model = ConfigurationProperties.set(ModelFileTypeConstants.TRANSFORMATIONS_ACTIVE)
            .observe(configuration);
        UpdateSetStrategy<String, String> updateStrategy = new UpdateSetStrategy<>(UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindSet(target, model, updateStrategy, null);
    }
}
