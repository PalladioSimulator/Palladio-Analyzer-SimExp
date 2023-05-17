package org.palladiosimulator.simexp.pcm.process;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class TemplateTest {
    
    @Mock private List<String> mockedList;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void testUseMockObjects() {
        // using mock object
        mockedList.add("one");
        mockedList.clear();

        // verification
        verify(mockedList).add("one");
        verify(mockedList).clear();

        // stubbing
        when(mockedList.get(0)).thenReturn("first");
        
        String result = mockedList.get(0);
        
        assertEquals("first", result);
    }

}
