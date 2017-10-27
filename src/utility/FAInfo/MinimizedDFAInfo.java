package utility.FAInfo;

import utility.State;
import utility.Transition;

import java.util.HashMap;
import java.util.List;

public class MinimizedDFAInfo {

    /**
     * 最小化DFA的边集合
     */
    private List<Transition> transitions;

    /**
     * 最小化DFA的状态集合
     */
    private HashMap<Integer, State> states;

    /**
     * 最小化DFA的开始状态
     */
    private List<Integer> startStates;

    /**
     * 最小化DFA的终止状态
     */
    private List<Integer> finalStates;

    public MinimizedDFAInfo(List<Transition> transitions, HashMap<Integer, State> states, List<Integer> startStates, List<Integer> finalStates) {
        this.transitions = transitions;
        this.states = states;
        this.startStates = startStates;
        this.finalStates = finalStates;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public HashMap<Integer, State> getStates() {
        return states;
    }

    public void setStates(HashMap<Integer, State> states) {
        this.states = states;
    }

    public List<Integer> getStartStates() {
        return startStates;
    }

    public void setStartStates(List<Integer> startStates) {
        this.startStates = startStates;
    }

    public List<Integer> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(List<Integer> finalStates) {
        this.finalStates = finalStates;
    }
}
