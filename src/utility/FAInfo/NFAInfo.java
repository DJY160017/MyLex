package utility.FAInfo;

import utility.State;
import utility.Transition;

import java.util.List;

public class NFAInfo {

    /**
     * 正则表达式的最小自动机的标志
     */
    private List<Character> tags;

    /**
     * NFA中边的集合
     */
    private List<Transition> transitions;

    /**
     * MFA中初始状态的集合
     */
    private List<State> startState;

    /**
     * MFA中终止状态的集合
     */
    private List<State> finalStates;

    public NFAInfo(List<Character> tags, List<Transition> transitions, List<State> startState, List<State> finalStates) {
        this.tags = tags;
        this.transitions = transitions;
        this.startState = startState;
        this.finalStates = finalStates;
    }

    public List<Character> getTags() {
        return tags;
    }

    public void setTags(List<Character> tags) {
        this.tags = tags;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public List<State> getStartState() {
        return startState;
    }

    public void setStartState(List<State> startState) {
        this.startState = startState;
    }

    public List<State> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(List<State> finalStates) {
        this.finalStates = finalStates;
    }
}
