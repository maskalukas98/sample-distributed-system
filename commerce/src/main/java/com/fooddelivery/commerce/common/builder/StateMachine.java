package com.fooddelivery.commerce.common.builder;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StateMachine<S,E> {
    public static class State<S,E> {
        @Getter
        S from;
        HashMap<E, State<S,E>> events = new HashMap<>();

        State(S from) {
            this.from = from;
        }

        public boolean isFinalState() {
            return events.isEmpty();
        }

        public boolean hasEvent(E e) {
            return events.containsKey(e);
        }

        public List<E> getSupportedEvents() {
            return new ArrayList<>(events.keySet());
        }
    }

    private State<S,E> initial;
    private State<S,E> currentState;

    private final HashMap<S, State<S,E>> states = new HashMap<>();

    public StateMachine<S,E> initial(S s) {
        State<S,E> initialState = new State<S,E>(s);
        initial = currentState = initialState;
        states.put(s, initialState);
        return this;
    }

    public StateMachine<S,E> setTransition(S from, S to, E event) {
        final State<S,E> newStateTo = new State<S,E>(to);

        if(!states.containsKey(to)) {
            states.put(to, newStateTo);
        }

        if(!states.containsKey(from)) {
            states.put(from, new State<S,E>(from));
        }

        states.get(from).events.put(event, states.get(to));
        return this;
    }

    public S executeEvent(E e) {
        if(!currentState.events.containsKey(e)) {
            return null;
        }

        currentState = currentState.events.get(e);
        return currentState.from;
    }

    public State<S,E> getCurrentState() {
        if(currentState == null) {
            throw new RuntimeException("Machine is empty.");
        }

        return currentState;
    }

    public void setState(S s) {
        if(!states.containsKey(s)) {
            throw new RuntimeException("State " + s + " not exists.");
        }
        currentState = states.get(s);
    }

    public boolean hasCurrentStateEvent(E e) {
        if(currentState == null) {
            return false;
        }

        return currentState.events.containsKey(e);
    }

    public State<S, E> getInitial() {
        if(currentState == null) {
            throw new RuntimeException("Machine is empty.");
        }

        return initial;
    }
}