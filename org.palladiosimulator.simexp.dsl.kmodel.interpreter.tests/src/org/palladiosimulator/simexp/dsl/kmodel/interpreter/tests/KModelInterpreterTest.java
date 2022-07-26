package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.KmodelInterpreter;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks.TestProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks.TestVariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Constant;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.DataType;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Expression;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KmodelFactory;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Statement;


public class KModelInterpreterTest {
    
    private KmodelInterpreter interpreter;
    private ProbeValueProvider pvp;
    private VariableValueProvider vvp;
    
    
    @Before
    public void setUp() {
        pvp = new TestProbeValueProvider();
        vvp = new TestVariableValueProvider();
    }
    
    @Test
    public void testSimpleModel() throws Exception {
        
        /**
         * sample model:
         * if (true) {
         * 
         * }
         * */
        
        KmodelFactory kmodelFactory = KmodelFactory.eINSTANCE;
        Kmodel kmodel = kmodelFactory.createKmodel();
        Constant constant = kmodelFactory.createConstant();
        constant.setName("simpleCondition");
        constant.setDataType(DataType.BOOL);
        kmodel.getFields().add(constant);
        Statement rule = kmodelFactory.createStatement();
        Expression condExpr = kmodelFactory.createExpression();
        BoolLiteral boolLiteral = kmodelFactory.createBoolLiteral();
        condExpr.setLiteral(boolLiteral);
        rule.setCondition(condExpr);
        interpreter = new KmodelInterpreter(kmodel, pvp, vvp);
        
        boolean actual = interpreter.analyze();
        
        assertFalse(actual);
    }
    
}
