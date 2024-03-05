package org.palladiosimulator.simexp.ui.workflow.config.databinding;

import org.eclipse.core.databinding.UpdateListStrategy;

public class ConditionalUpdateListStrategy<S, D> extends UpdateListStrategy<S, D> {
    private final UpdateStrategyController updateStrategyController;

    public ConditionalUpdateListStrategy(int updatePolicy, UpdateStrategyController updateStrategyController) {
        super(updatePolicy);
        this.updateStrategyController = updateStrategyController;
    }

    @Override
    public int getUpdatePolicy() {
        if (updateStrategyController.isEnabled()) {
            return super.getUpdatePolicy();
        }
        return POLICY_NEVER;
    }
}
