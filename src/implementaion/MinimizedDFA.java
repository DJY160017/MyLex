package implementaion;

import utility.FAInfo.DFAInfo;
import utility.FAInfo.MinimizedDFAInfo;
import utility.FAInfo.NFAInfo;
import utility.State;
import utility.Transition;

import java.util.*;

public class MinimizedDFA {

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
    private List<Transition> transitions_DFA;

    /**
     * DFA中的最小自动机标志
     */
    private List<Character> tags;

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
    private List<Integer> startStates_min;

    /**
     * 最小化DFA的终止状态
     */
    private List<Integer> finalStates_min;


    public MinimizedDFA(DFAInfo dfaInfo) {
        dfaStates = dfaInfo.getDfaStates();
        dfaStatesWithNumbering = dfaInfo.getDfaStatesWithNumbering();
        finalStates = dfaInfo.getFinalStates();
        startStates = dfaInfo.getStartStates();
        transitions_DFA = dfaInfo.getTransitions();
        tags = dfaInfo.getTags();
        transitions = new ArrayList<>();
        states = new HashMap<>();
        startStates_min = new ArrayList<>();
        finalStates_min = new ArrayList<>();
    }

    /**
     * 完成整个最小化的过程
     *
     * @return MinimizedDFAInfo 最小化DAF信息载体
     */
    public MinimizedDFAInfo handle(){
        //完成最小化DFA的创建
        minimize();
        transitions = removeDuplicateTransition(transitions);
        setStartAndFinalState();

        return new MinimizedDFAInfo(transitions, states,startStates_min, finalStates_min);
    }

    /**
     * 设置最小化DFA的初始状态和结束状态
     */
    private void setStartAndFinalState() {
        for (Integer key : states.keySet()) {
            if (finalStates.contains(key)) {
                states.get(key).setFinal(true);
                finalStates_min.add(key);
            }

            if (startStates.contains(key)) {
                states.get(key).setStart(true);
                startStates_min.add(key);
            }
        }

        for(Transition transition : transitions){
            State start = states.get(transition.getHead().getStateID());
            State end = states.get(transition.getTail().getStateID());
            if(!start.getNextStates().contains(end)){
                start.getNextStates().add(end);
            }

            if(!end.getPreviousStates().contains(start)){
                end.getPreviousStates().add(start);
            }
        }
    }

    /**
     * 最小化DFA
     */
    private void minimize() {
        List<Integer> start_partition = new ArrayList<>();
        List<Integer> final_partition = new ArrayList<>();

        for (List<State> states : dfaStates) {
            int stateID = dfaStatesWithNumbering.get(states);
            if (finalStates.contains(stateID)) {
                final_partition.add(stateID);
            } else {
                start_partition.add(stateID);
            }
        }

        List<List<Integer>> initial_partiton = new ArrayList<>();
        initial_partiton.add(start_partition);
        initial_partiton.add(final_partition);

        List<List<Integer>> last_partition = newPartition(initial_partiton);

        //逐步遍历每一层获取最后的分区结果
        while (!last_partition.equals(initial_partiton)) {
            initial_partiton = last_partition;
            last_partition = newPartition(initial_partiton);
        }

        HashMap<Integer, List<Integer>> state_partition = new HashMap<>();
        HashMap<List<Integer>, Integer> partition_stateID = new HashMap<>();
        for (List<Integer> partition : last_partition) {
            State state = new State(partition.get(0)); // 选取第一个作为分区的代表
            states.put(partition.get(0), state);
            for (int id : partition) {
                state_partition.put(id, partition);
                partition_stateID.put(partition, partition.get(0));
            }
        }

        for (List<Integer> partition : last_partition) {
            for (int i = 0; i < partition.size(); i++) {
                int stateID = partition.get(i);
                for (Transition transition : transitions_DFA) {
                    if (i == 0) { //当前分区的代表ID
                        if (transition.getHead().getStateID() == stateID) {
                            List<Integer> partition_head = state_partition.get(stateID);
                            List<Integer> partition_tail = state_partition.get(transition.getTail().getStateID());
                            State head = new State(stateID);
                            if (isSame(partition_head, partition_tail)) {
                                State tail = new State(stateID);
                                Transition temp_transition = new Transition(transition.getTag(), head, tail);
                                if (!transitions.contains(temp_transition)) {
                                    transitions.add(temp_transition);
                                }
                            } else {
                                State tail = new State(partition_stateID.get(partition_tail));
                                Transition temp_transition = new Transition(transition.getTag(), head, tail);
                                if (!transitions.contains(temp_transition)) {
                                    transitions.add(temp_transition);
                                }
                            }
                        }
                    } else { //当前分区的被代表ID
                        if (transition.getTail().getStateID() == stateID) {
                            State tail = new State(partition.get(0));
                            List<Integer> partition_head = state_partition.get(transition.getHead().getStateID());
                            List<Integer> partition_tail = state_partition.get(stateID);
                            if (isSame(partition_head, partition_tail)) {
                                State head = new State(partition.get(0));
                                Transition temp_transition = new Transition(transition.getTag(), head, tail);
                                if (!transitions.contains(temp_transition)) {
                                    transitions.add(temp_transition);
                                }
                            } else {
                                State head = new State(partition_stateID.get(partition_head));
                                Transition temp_transition = new Transition(transition.getTag(), head, tail);
                                if (!transitions.contains(temp_transition)) {
                                    transitions.add(temp_transition);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 去除冗余的边
     *
     * @param transitions 边的集合
     * @return List<Transition> 新边的集合
     */
    private List<Transition> removeDuplicateTransition(List<Transition> transitions) {
        Set<String> set = new HashSet<>();
        List<Transition> newTransition = new ArrayList<>();
        for (Transition transition : transitions) {
            String key = transition.toString();
            if (!set.contains(key)) {
                set.add(key);
                newTransition.add(transition);
            }
        }
        return newTransition;
    }

    /**
     * 根据初始分区产生符合要求最后一个分区形式
     *
     * @param partitions 初始分区（开始状态和终止状态的分区）
     * @return List<List<Integer>> 新分区
     */
    private List<List<Integer>> newPartition(List<List<Integer>> partitions) {
        List<List<Integer>> newPartition = new ArrayList<>();

        for (List<Integer> partition : partitions) {

            for (int state : partition) {

                List<Integer> tempPartition = new ArrayList<>();
                for (int nextState : partition) {

                    if (state != nextState) { //这是nextState不是当前State
                        tempPartition.add(state);

                        int count = 0; //用于判断全部标志下都属于同一集合
                        for (Character character : tags) {
                            String currentTag = String.valueOf(character);
                            int start_final = -1;
                            int nextState_final = -1;

                            for (Transition transition : transitions_DFA) {
                                if (transition.getHead().getStateID() == state && transition.getTag().equals(currentTag)) {
                                    start_final = transition.getTail().getStateID();
                                }

                                if (transition.getHead().getStateID() == nextState && transition.getTag().equals(currentTag)) {
                                    nextState_final = transition.getTail().getStateID();
                                }
                            }

                            for (List<Integer> partitionSet : partitions) {
                                if (partitionSet.contains(start_final) && partitionSet.contains(nextState_final)) {
                                    count++; //在当前标志下属于同一集合
                                } else if (start_final == -1 && nextState_final == -1) {
                                    count++; //没有任何边发出，也属于同一集合
                                }
                            }
                        }

                        if (count >= tags.size()) {
                            tempPartition.add(nextState);
                        }
                    }
                }

                tempPartition = removeDuplicate(tempPartition);
                if (!newPartition.contains(tempPartition) && !tempPartition.isEmpty()) {
                    newPartition.add(tempPartition);
                }
            }

            int count = 0;
            for (int state : partition) {
                for (List<Integer> tempPartition : newPartition) {
                    if (tempPartition.contains(state)) {
                        count++;
                    }
                }
                if (count == 0) { // 代表该状态不属于产生的任何分区，自成一个分区
                    List<Integer> singlePartition = new ArrayList<>();
                    singlePartition.add(state);
                    newPartition.add(singlePartition);
                }
            }
        }

        return newPartition;
    }

    /**
     * 去除冗余的状态
     *
     * @param partition 分区
     * @return List<Integer> 新分区
     */
    private List<Integer> removeDuplicate(List<Integer> partition) {
        Set<Integer> set = new HashSet<>();
        set.addAll(partition);
        List<Integer> result = new ArrayList<>();
        result.addAll(set);
        return result;
    }

    /**
     * 判断两个集合内容是否相等
     *
     * @param collection_1 集合1
     * @param collection_2 集合2
     * @return boolean 是否相等
     */
    private boolean isSame(List<Integer> collection_1, List<Integer> collection_2) {
        if (collection_1 == null && collection_2 == null) {
            return true;
        }

        if (collection_1 == null || collection_2 == null) {
            return false;
        }

        if (collection_1.size() != collection_2.size()) {
            return false;
        }

        int count = 0;
        for (int i = 0; i < collection_1.size(); i++) {
            if (collection_1.get(i).equals(collection_2.get(i))) {
                count++;
            }
        }

        if (count == collection_1.size()) {
            return true;
        } else {
            return false;
        }
    }
}
