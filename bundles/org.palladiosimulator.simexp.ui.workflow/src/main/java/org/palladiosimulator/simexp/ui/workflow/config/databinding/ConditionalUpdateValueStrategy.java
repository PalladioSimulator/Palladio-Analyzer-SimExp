package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import java.util.Objects;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;

public class ConditionalUpdateValueStrategy<S, D> extends UpdateValueStrategy<S, D> {
    private final UpdateStrategyController updateStrategyController;

    public ConditionalUpdateValueStrategy(UpdateStrategyController updateStrategyController) {
        this(POLICY_UPDATE, updateStrategyController);
    }

    public ConditionalUpdateValueStrategy(int updatePolicy, UpdateStrategyController updateStrategyController) {
        super(updatePolicy);
        this.updateStrategyController = updateStrategyController;
    }

    public static <S, D> UpdateValueStrategy<S, D> create(IConverter<S, D> converter,
            UpdateStrategyController updateStrategyController) {
        Objects.requireNonNull(converter);
        return new ConditionalUpdateValueStrategy<S, D>(updateStrategyController).setConverter(converter);
    }

    @Override
    public int getUpdatePolicy() {
        if (updateStrategyController.isEnabled()) {
            return super.getUpdatePolicy();
        }
        return POLICY_NEVER;
    }
}
