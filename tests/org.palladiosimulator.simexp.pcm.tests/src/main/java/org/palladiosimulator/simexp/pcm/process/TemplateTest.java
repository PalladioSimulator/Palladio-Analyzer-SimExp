package org.palladiosimulator.simexp.pcm.process;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TemplateTest {
    
    @Mock private List<String> mockedList;

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void testUseMockObjects() {
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
