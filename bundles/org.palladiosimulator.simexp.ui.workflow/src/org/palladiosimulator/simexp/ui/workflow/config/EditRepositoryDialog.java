package org.palladiosimulator.simexp.ui.workflow.config;

import java.util.Arrays;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.CompoundStringValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.ExtensionValidator;
import org.palladiosimulator.simexp.ui.workflow.config.databinding.validation.FileURIValidator;

import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class EditRepositoryDialog extends InputDialog {
    private final String type;
    private final String[] extension;
    private final String initialValue;
    private final DataBindingContext ctx;

    @SuppressWarnings("unused")
    private static class Data {

        private String repositoryUri;

        public String getRepositoryUri() {
            return repositoryUri;
        }

        public void setRepositoryUri(String repositoryUri) {
            this.repositoryUri = repositoryUri;
        }
    };

    private IObservableValue<String> model;

    public EditRepositoryDialog(Shell parentShell, String title, String type, String[] extension) {
        this(parentShell, title, type, extension, null);
    }

    public EditRepositoryDialog(Shell parentShell, String title, String type, String[] extension, String initialValue) {
        super(parentShell, title, title, "", null);
        this.type = type;
        this.extension = extension;
        this.ctx = new DataBindingContext();
        this.initialValue = initialValue;
    }

    public String getRepositoryModelUri() {
        return model.getValue();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        ModifyListener modifyListener = (e) -> {
        };
        Composite container = (Composite) super.createDialogArea(parent);
        TabHelper.createFileInputSection(container, modifyListener, type, extension, getText(), "Select " + type,
                getShell(), true, true, ModelFileTypeConstants.EMPTY_STRING);

        IObservableValue<String> target = WidgetProperties.text(SWT.Modify)
            .observe(getText());

        Data data = new Data();
        model = PojoProperties.value("repositoryUri", String.class)
            .observe(data);

        UpdateValueStrategy<String, String> updateValueStrategy = new UpdateValueStrategy<>();
        updateValueStrategy.setBeforeSetValidator(new CompoundStringValidator(
                Arrays.asList(new FileURIValidator(type), new ExtensionValidator(type, extension[0]))));
        Binding allocationBindValue = ctx.bindValue(target, model, updateValueStrategy, null);
        ControlDecorationSupport.create(allocationBindValue, SWT.TOP | SWT.RIGHT);

        return container;
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);

        AggregateValidationStatus aggregateValidationStatus = new AggregateValidationStatus(ctx.getBindings(),
                AggregateValidationStatus.MAX_SEVERITY);
        ISideEffect.create(() -> {
            IStatus status = aggregateValidationStatus.getValue();
            if (status.isOK()) {
                return null;
            }
            return String.format("Error: %s", status.getMessage());
        }, this::setErrorMessage);

        model.setValue(initialValue);

        return contents;
    }
}
