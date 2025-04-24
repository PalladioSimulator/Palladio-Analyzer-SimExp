package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class PodManagerTest {
    private PodManager podManager;

    @Mock
    private ClassLoader classloader;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        this.podManager = new PodManager(classloader);
    }

    @Test
    public void testGetPodName() {
        String actualPodName = podManager.getPodName("node02:default.simexp-c6f6d95f4-8b9f8");

        assertEquals("simexp-c6f6d95f4-8b9f8", actualPodName);
    }

}
