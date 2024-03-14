package org.palladiosimulator.simexp.dsl.smodel.util;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;

public class ExpressionUtil {

    /**
     * Returns the next expression in the tree that contains either an operation, a field reference
     * or a literal.
     * 
     * @param expression
     * @return
     */
    public Expression getNextExpressionWithContent(Expression expression) {
        if (expression.getOp() != Operation.UNDEFINED || expression.getFieldRef() != null
                || expression.getLiteral() != null) {
            return expression;
        }
        return getNextExpressionWithContent(expression.getLeft());
    }
}
