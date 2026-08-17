package org.palladiosimulator.simexp.workflow.launcher;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.workflow.api.ILaunchFactory;

public class SimulationExecutorLookupTest {
    private SimulationExecutorLookup lookup;

    @Mock
    private ILaunchFactory factory1;
    @Mock
    private ILaunchFactory factory2;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        lookup = new SimulationExecutorLookup();
    }

    @Test
    public void testSelectCandidateMaxLevel() {
        List<Pair<ILaunchFactory, Integer>> candidates = new ArrayList<>();
        candidates.add(new ImmutablePair<>(factory1, 1));
        candidates.add(new ImmutablePair<>(factory2, 10));

        ILaunchFactory actualCandidate = lookup.selectCandidate(candidates, Integer.MAX_VALUE);

        assertEquals(factory2, actualCandidate);
    }

    @Test
    public void testSelectCandidateMaxLevel10() {
        List<Pair<ILaunchFactory, Integer>> candidates = new ArrayList<>();
        candidates.add(new ImmutablePair<>(factory1, 1));
        candidates.add(new ImmutablePair<>(factory2, 10));

        ILaunchFactory actualCandidate = lookup.selectCandidate(candidates, 10);

        assertEquals(factory1, actualCandidate);
    }
}
