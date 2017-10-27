package utility.FAInfo;

import utility.State;
import utility.Transition;

import java.util.HashMap;
import java.util.List;

public class DFAInfo {

    /**
     * DFA的状态列表
     */
    private List<List<State>> dfaStates;

    /**
     * DFA中带标号的列表
     */
    private HashMap<List<State>, Integer> dfaStatesWithNumbering;

    /**
     * DFA中所有终止状态
     */
    private List<Integer> finalStates;

    /**
     * DFA中所有起始状态
     */
    private List<Integer> startStates;

    /**
     * DFA的边集合
     */
    private List<Transition> transitions;

    /**
     * DFA中的最小自动机标志
     */
    private List<Character> tags;

    public DFAInfo(List<List<State>> dfaStates, HashMap<List<State>, Integer> dfaStatesWithNumbering, List<Integer> finalStates, List<Integer> startStates, List<Transition> transitions, List<Character> tags) {
        this.dfaStates = dfaStates;
        this.dfaStatesWithNumbering = dfaStatesWithNumbering;
        this.finalStates = finalStates;
        this.startStates = startStates;
        this.transitions = transitions;
        if (tags.contains('ε')) {
            tags.remove(tags.indexOf('ε'));
        }
        this.tags = tags;
    }

    public List<List<State>> getDfaStates() {
        return dfaStates;
    }

    public void setDfaStates(List<List<State>> dfaStates) {
        this.dfaStates = dfaStates;
    }

    public HashMap<List<State>, Integer> getDfaStatesWithNumbering() {
        return dfaStatesWithNumbering;
    }

    public void setDfaStatesWithNumbering(HashMap<List<State>, Integer> dfaStatesWithNumbering) {
        this.dfaStatesWithNumbering = dfaStatesWithNumbering;
    }

    public List<Integer> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(List<Integer> finalStates) {
        this.finalStates = finalStates;
    }

    public List<Integer> getStartStates() {
        return startStates;
    }

    public void setStartStates(List<Integer> startStates) {
        this.startStates = startStates;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public List<Character> getTags() {
        return tags;
    }

    public void setTags(List<Character> tags) {
        this.tags = tags;
    }
}
