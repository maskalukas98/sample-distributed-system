package com.fooddelivery.commerce.unit.common.builder;

import com.fooddelivery.commerce.common.builder.StateMachine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
public class StateMachineTest {
    private StateMachine<String, String> stateMachine;

    @BeforeEach
    void setup() {
        stateMachine = new StateMachine<String, String>();
    }
    @Test
    public void testInitial_shouldInitialState() {
        // execute
        stateMachine.initial("created");

        // verify
        Assertions.assertEquals(stateMachine.getCurrentState().getFrom(), "created");
    }

    @ParameterizedTest
    @MethodSource("initialData")
    public void testInitial_shouldInitialStateAsDelivering(String state) {
        // execute
        stateMachine.initial(state);

        // verify
        Assertions.assertEquals(stateMachine.getCurrentState().getFrom(), state);
    }

    @ParameterizedTest
    @MethodSource("transitionsData")
    public void testSetTransition_shouldExecuteEvent(String from, String to, String event) {
        stateMachine.setTransition(from, to, event);
        stateMachine.setState(from);

        // execute
        stateMachine.executeEvent(event);

        // verify
        Assertions.assertEquals(stateMachine.getCurrentState().getFrom(), to);
    }

    private static Stream<Arguments> transitionsData() {
        return Stream.of(
                Arguments.of("created","delivering", "deliver"),
                Arguments.of("delivering","deliver", "complete")
        );
    }

    private static Stream<Arguments> initialData() {
        return Stream.of(
                Arguments.of("created"),
                Arguments.of("delivering")
        );
    }
}
