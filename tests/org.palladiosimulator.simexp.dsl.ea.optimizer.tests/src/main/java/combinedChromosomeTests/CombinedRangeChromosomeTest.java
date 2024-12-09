package combinedChromosomeTests;

import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.EAOptimizerFactory;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.DecoderEncodingPair;
import org.palladiosimulator.simexp.dsl.smodel.SmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import com.google.inject.Injector;

import io.jenetics.internal.collection.ArrayISeq;
import io.jenetics.internal.collection.Empty.EmptyISeq;
import utility.RangeBoundsHelper;

//review-finding-2024-12-09: 
//- class under test is 'EAOptimizer' thus rename test class to EAOptimizerTest; 
//- package naming convention violated; CUT should be placed in a package of same name as in 'impl' -> org.palladiosimulator.simexp.dsl.ea.optimizer.impl
//- chromosome tests should be realized as several test methods within this test class  
public class CombinedRangeChromosomeTest {

    private static final double DOUBLE_EPSILON = 1e-15;

    private static final double EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS = 0.9;

    @Mock
    private IEAConfig eaConfig;

    @Mock
    private IEAEvolutionStatusReceiver statusReceiver;

    @Mock
    private IEAFitnessEvaluator fitnessEvaluator;

    @Mock
    private IOptimizableProvider optimizableProvider;

    @Mock
    private ISmodelConfig smodelConfig;

    private SmodelCreator smodelCreator;

    @Mock
    private IExpressionCalculator calculator;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    // review-finding-2024-12-09: parameter order violated: (1) class under test, (2) mocks and
    // others
    private IEAOptimizer optimizer;

    @Before
    public void setUp() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        when(smodelConfig.getEpsilon()).thenReturn(DOUBLE_EPSILON);
        when(optimizableProvider.getExpressionCalculator()).thenReturn(calculator);

        Injector injector = new SmodelStandaloneSetup().createInjectorAndDoEMFRegistration();

        EAOptimizerFactory eaOptimizer = new EAOptimizerFactory();
        optimizer = eaOptimizer.create(eaConfig);
    }

    @Test
    public void integerDoubleOptimizableRangeTest() {
        int upperBoundInteger = 20;
        RangeBounds intRangeBound = RangeBoundsHelper.initializeIntegerRangeBound(smodelCreator, calculator, 0,
                upperBoundInteger, 1);
        Optimizable intOptimizable = smodelCreator.createOptimizable("test", DataType.INT, intRangeBound);
        double upperBoundDouble = 20.0;
        RangeBounds doubleRangeBound = RangeBoundsHelper.initializeDoubleRangeBound(smodelCreator, calculator, 0.0,
                upperBoundDouble, 1.0);
        Optimizable doubleOptimizable = smodelCreator.createOptimizable("test", DataType.DOUBLE, doubleRangeBound);

        when(optimizableProvider.getOptimizables()).thenReturn(List.of(intOptimizable, doubleOptimizable));
        when(fitnessEvaluator.calcFitness(any(List.class))).thenAnswer(new Answer<Future<Double>>() {
            @Override
            public Future<Double> answer(InvocationOnMock invocation) throws Throwable {

                return getFitnessFunctionAsFuture(invocation);
            }
        });

        optimizer.optimize(optimizableProvider, fitnessEvaluator, statusReceiver);

        verify(statusReceiver).reportStatus(any(List.class),
                gt((upperBoundInteger + upperBoundDouble) * EXPECTED_QUALITY_THRESHOLD_LARGE_TESTS));
    }

    private Future<Double> getFitnessFunctionAsFuture(InvocationOnMock invocation) {
        return executor.submit(() -> {
            List<IEAFitnessEvaluator.OptimizableValue> optimizableValues = invocation.getArgument(0);

            double value = 0;

            for (IEAFitnessEvaluator.OptimizableValue singleOptimizableValue : optimizableValues) {
                DecoderEncodingPair chromoPair = (DecoderEncodingPair) singleOptimizableValue.getValue();
                DataType optimizableDataType = singleOptimizableValue.getOptimizable()
                    .getDataType();

                Object apply = chromoPair.first()
                    .apply(chromoPair.second());

                if (apply instanceof ArrayISeq arraySeq) {
                    if (arraySeq.size() == 1) {
                        for (Object element : arraySeq.array) {
                            if (optimizableDataType == DataType.DOUBLE) {
                                value += (Double) element;
                            } else if (optimizableDataType == DataType.INT) {
                                value += (Integer) element;
                            }
                        }
                    }
                } else if (apply instanceof EmptyISeq emptySeq) {
                    // do nothing
                } else {
                    throw new RuntimeException("Unknown chromosome type specified: " + chromoPair.second()
                        .getClass());
                }
            }

            return value;
        });
    }

}
