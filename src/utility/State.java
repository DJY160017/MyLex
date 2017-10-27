package utility;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 用于记录每个顶点的状态信息
 *
 * */
public class State {

    /**
     * 当前状态的ID
     */
    private int stateID;

    /**
     * 当前状态的所有前继状态（NFA下会有多个）
     */
    private List<State> previousStates;

    /**
     * 当前状态的所有后继状态（NFA下会有多个）
     */
    private List<State> nextStates;

    /**
     * 标志该状态是否为FA的起始状态
     */
    private boolean isStart;

    /**
     * 标志该状态是否为FA的终止状态
     */
    private boolean isFinal;

    public State(int stateID) {
        this.stateID = stateID;
        previousStates = new ArrayList<>();
        nextStates = new ArrayList<>();
    }

    public int getStateID() {
        return stateID;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public List<State> getPreviousStates() {
        return previousStates;
    }

    public void setPreviousStates(List<State> previousStates) {
        this.previousStates = previousStates;
    }

    public List<State> getNextStates() {
        return nextStates;
    }

    public void setNextStates(List<State> nextStates) {
        this.nextStates = nextStates;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public void addPreviousState(State previousState) {
        this.previousStates.add(previousState);
    }

    public void addNextState(State nextState) {
        this.nextStates.add(nextState);
    }

    @Override
    public String toString() {
        return String.valueOf(stateID);
    }
}
