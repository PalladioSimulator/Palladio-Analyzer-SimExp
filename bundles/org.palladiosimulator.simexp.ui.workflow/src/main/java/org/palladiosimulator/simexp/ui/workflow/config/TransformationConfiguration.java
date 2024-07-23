package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateSetStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
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
    private final ITrafoNameProvider trafoNameProvider;

    private CheckboxTableViewer viewer;

    public TransformationConfiguration(ITrafoNameProvider trafoNameProvider) {
        this.trafoNameProvider = trafoNameProvider;
    }

    public void createControl(Composite parent, DataBindingContext ctx, ModifyListener modifyListener) {
        Group container = new Group(parent, SWT.NONE);
        container.setText("Transformations");
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout());

        Label simulationIDLabel = new Label(container, SWT.NONE);
        simulationIDLabel.setText("Active transformations:");
        /*
         * ListViewer listViewer = new ListViewer(container, SWT.MULTI | SWT.BORDER);
         * listViewer.getControl() .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         * ObservableListContentProvider<String> observableInput = new
         * ObservableListContentProvider<>(); listViewer.setContentProvider(observableInput);
         */
        viewer = CheckboxTableViewer.newCheckList(container,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        viewer.getControl()
            .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        // viewer.setContentProvider(ArrayContentProvider.getInstance());
        ObservableListContentProvider<String> observableInput = new ObservableListContentProvider<>();
        viewer.setContentProvider(observableInput);

        List<String> availableTransformations = trafoNameProvider.getAvailableTransformations();
        ObservableList<String> ol = new WritableList<>();
        ol.addAll(availableTransformations);
        viewer.setInput(ol);
        /*
         * ISWTObservableList<String> monitorsTarget = WidgetProperties.items()
         * .observe(monitors.getList()); IObservableFactory<QualityObjective,
         * IObservableList<String>> detailFactory = new IObservableFactory<>() {
         * 
         * @Override public IObservableList<String> createObservable(QualityObjective master) {
         * IObservableList<String> listModel = new
         * WritableList<>(simulationKindMonitorItems.get(master), String.class); return listModel; }
         * }; IObservableList<String> monitorsModel =
         * MasterDetailObservables.detailList(qualityObjectiveTarget, detailFactory, String.class);
         * ctx.bindList(monitorsTarget, monitorsModel, new
         * UpdateListStrategy<>(UpdateValueStrategy.POLICY_NEVER), null);
         */
    }

    public void initializeFrom(ILaunchConfigurationWorkingCopy configuration, DataBindingContext ctx) {
        /*
         * IObservableValue<String> monitorRepositoryTarget = WidgetProperties.text(SWT.Modify)
         * .observe(textMonitorRepository); IObservableValue<String> monitorRepositoryModel =
         * ConfigurationProperties .string(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE)
         * .observe(configuration); UpdateValueStrategy<String, String>
         * monitorRepositoryUpdateStrategy = new ConditionalUpdateValueStrategy<>(
         * UpdateValueStrategy.POLICY_CONVERT, pcmUpdateController); IValidator<String>
         * monitorRepositoryValidator = new ControllableValidator<>( new CompoundStringValidator(
         * Arrays .asList(new FileURIValidator("Monitor Repository File"), new
         * ExtensionValidator("Monitor Repository File",
         * ModelFileTypeConstants.MONITOR_REPOSITORY_FILE_EXTENSION[0]))), isPcmEnabled);
         * monitorRepositoryUpdateStrategy.setBeforeSetValidator(monitorRepositoryValidator);
         * Binding monitorRepositoryBindValue = ctx.bindValue(monitorRepositoryTarget,
         * monitorRepositoryModel, monitorRepositoryUpdateStrategy, new
         * ConditionalUpdateValueStrategy<>(pcmUpdateController));
         * ControlDecorationSupport.create(monitorRepositoryBindValue, SWT.TOP | SWT.RIGHT);
         * 
         * ISWTObservableList<String> monitorTarget = WidgetProperties.items()
         * .observe(monitors.getList()); IObservableList<String> monitorModel =
         * ConfigurationProperties.list(ModelFileTypeConstants.MONITORS) .observe(configuration);
         * UpdateListStrategy<String, String> monitorsTargetToModel = new
         * ConditionalUpdateListStrategy<>( UpdateValueStrategy.POLICY_CONVERT,
         * pcmUpdateController); UpdateListStrategy<String, String> monitorsModelToTarget = new
         * ConditionalUpdateListStrategy<>( UpdateValueStrategy.POLICY_NEVER, pcmUpdateController);
         * ctx.bindList(monitorTarget, monitorModel, monitorsTargetToModel, monitorsModelToTarget);
         */

        IObservableSet<String> target = ViewerProperties.<CheckboxTableViewer, String> checkedElements(String.class)
            .observe(viewer);
        IObservableSet<String> model = ConfigurationProperties.set(ModelFileTypeConstants.TRANSFORMATIONS_ACTIVE)
            .observe(configuration);
        UpdateSetStrategy<String, String> updateStrategy = new UpdateSetStrategy<>(UpdateValueStrategy.POLICY_CONVERT);
        ctx.bindSet(target, model, updateStrategy, null);
    }

}
