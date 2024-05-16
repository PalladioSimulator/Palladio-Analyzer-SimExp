package org.palladiosimulator.simexp.model.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Analyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Planner;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.EmptyQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.MultiQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QvtoModelTransformation;

public class ModelledReconfigurationStrategyTest {

    private ModelledReconfigurationStrategy strategy;

    @Mock
    private Monitor monitor;
    @Mock
    private Analyzer analyzer;
    @Mock
    private Planner planner;
    @Mock
    private IQVToReconfigurationManager qvtoReconfigurationManager;
    @Mock
    private State source;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        strategy = new ModelledReconfigurationStrategy("testStrategy", monitor, analyzer, planner,
                qvtoReconfigurationManager);
    }

    @Test
    public void testDelegateToMonitorWorks() {
        strategy.monitor(source, null);

        verify(monitor).monitor(source);
    }

    @Test
    public void testDelegateToAnalyzerWorks() throws Exception {
        when(analyzer.analyze()).thenReturn(true);
        
        boolean actualResult = strategy.analyse(source, null);

        assertThat(actualResult).isTrue();
    }

    @Test
    public void testDelegateToAnalyzerWithNothingToAnalyzeWorks() throws Exception {
        when(analyzer.analyze()).thenReturn(false);
        
        boolean actualResult = strategy.analyse(source, null);
        
        assertThat(actualResult).isFalse();
    }

    @Test
    public void testDelegateToPlannerWorksWithNoReconfigurations() throws Exception {
        QVToReconfiguration expectedResult = EmptyQVToReconfiguration.empty();

        QVToReconfiguration actualResult = strategy.plan(source, null, null);

        assertThat(actualResult.getStringRepresentation()).isEqualTo(expectedResult.getStringRepresentation());
    }

    @Test
    public void testDelegateToPlannerWorksWithSingleReconfiguration() throws Exception {
        QvtoModelTransformation expectedTransformation = mock(QvtoModelTransformation.class, "qvtoTransformationMock");
        List<SingleQVToReconfiguration> expectedTransformations = new ArrayList<>();
        expectedTransformations.add(SingleQVToReconfiguration.of(expectedTransformation, qvtoReconfigurationManager));
        QVToReconfiguration expectedResult = MultiQVToReconfiguration.of(expectedTransformations);
        String expectedTransformationName = "testTransformation";
        Action expectedAction = SmodelFactory.eINSTANCE.createAction();
        expectedAction.setName(expectedTransformationName);
        ResolvedAction expectedResolvedAction = new ResolvedAction(expectedAction, null);
        List<ResolvedAction> expectedResolvedActions = new ArrayList<>();
        expectedResolvedActions.add(expectedResolvedAction);
        when(planner.plan()).thenReturn(expectedResolvedActions);
        when(qvtoReconfigurationManager.findQvtoModelTransformation(expectedTransformationName))
            .thenReturn(expectedTransformation);

        QVToReconfiguration actualResult = strategy.plan(source, null, null);

        assertThat(actualResult.getStringRepresentation()).isEqualTo(expectedResult.getStringRepresentation());
    }

    @Test
    public void testDelegateToPlannerWorksWithTwoReconfigurations() throws Exception {
        QvtoModelTransformation expectedTransformation1 = mock(QvtoModelTransformation.class,
                "qvtoTransformationMock1");
        QvtoModelTransformation expectedTransformation2 = mock(QvtoModelTransformation.class,
                "qvtoTransformationMock2");
        List<SingleQVToReconfiguration> expectedTransformations = new ArrayList<>();
        expectedTransformations.add(SingleQVToReconfiguration.of(mock(QvtoModelTransformation.class, "qvtoTransformationMock1"), qvtoReconfigurationManager));
        expectedTransformations.add(SingleQVToReconfiguration.of(mock(QvtoModelTransformation.class, "qvtoTransformationMock2"), qvtoReconfigurationManager));
        QVToReconfiguration expectedResult = MultiQVToReconfiguration.of(expectedTransformations);
        String expectedTransformationName1 = "testTransformation1";
        ResolvedAction expectedResolvedAction1 = createResolvedAction(expectedTransformationName1);
        String expectedTransformationName2 = "testTransformation2";
        ResolvedAction expectedResolvedAction2 = createResolvedAction(expectedTransformationName2);
        List<ResolvedAction> expectedResolvedActions = new ArrayList<>();
        expectedResolvedActions.add(expectedResolvedAction1);
        expectedResolvedActions.add(expectedResolvedAction2);
        when(planner.plan()).thenReturn(expectedResolvedActions);
        when(qvtoReconfigurationManager.findQvtoModelTransformation(expectedTransformationName1))
            .thenReturn(expectedTransformation1);
        when(qvtoReconfigurationManager.findQvtoModelTransformation(expectedTransformationName2))
            .thenReturn(expectedTransformation2);

        QVToReconfiguration actualResult = strategy.plan(source, null, null);

        assertThat(actualResult.getStringRepresentation()).isEqualTo(expectedResult.getStringRepresentation());
    }

    private ResolvedAction createResolvedAction(String actionName) {
        Action action = SmodelFactory.eINSTANCE.createAction();
        action.setName(actionName);
        ResolvedAction resolvedAction = new ResolvedAction(action, null);
        return resolvedAction;
    }

}
