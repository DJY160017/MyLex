package implementaion;

import utility.FAInfo.DFAInfo;
import utility.FAInfo.NFAInfo;
import utility.State;
import utility.Transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 用于将NFA转换为DFA
 */
public class DFA {

    /**
     * NFA的边集合
     */
    private List<Transition> transitions_NFA;

    /**
     * DFA的边集合
     */
    private List<Transition> transitions_DFA;

    /**
     * NFA中的最小自动机标志
     */
    private List<Character> tags;

    /**
     * NFA中所有起始状态
     */
    private List<State> startStates_NFA;

    /**
     * NFA中所有终止状态
     */
    private List<State> finalStates_NFA;

    /**
     * DFA中所有起始状态
     */
    private List<Integer> startStates_DFA;

    /**
     * DFA中所有终止状态
     */
    private List<Integer> finalStates_DFA;

    /**
     * 由NFA得到的初始边的列表
     */
    private HashMap<Integer, HashMap<String, List<State>>> transitionTable;

    /**
     * 经由ε闭包得到最终列表
     */
    private HashMap<List<State>, HashMap<String, List<State>>> table_DFA;

    /**
     * DFA的状态列表
     */
    private List<List<State>> states_DFA;

    /**
     * DFA中带标号的列表
     */
    private HashMap<List<State>, Integer> states_DFA_number;

    /**
     * 用于计算单集合闭包的次数
     */
    private int closureCount;

    public DFA(NFAInfo nfaInfo) {
        transitions_NFA = nfaInfo.getTransitions();
        startStates_NFA = nfaInfo.getStartState();
        finalStates_NFA = nfaInfo.getFinalStates();
        tags = nfaInfo.getTags();
        tags.add('ε');

        transitionTable = new HashMap<>();
        table_DFA = new HashMap<>();
        states_DFA = new ArrayList<>();
        states_DFA_number = new HashMap<>();
        transitions_DFA = new ArrayList<>();
        finalStates_DFA = new ArrayList<>();
        startStates_DFA = new ArrayList<>();
        closureCount = 0;
    }

    public DFAInfo handle() {
        createTranstionTable();
        createDFATable();
        setTransitions_DFA();
        setStartAndFinalStates();

        return new DFAInfo(states_DFA, states_DFA_number, finalStates_DFA, startStates_DFA, transitions_DFA, tags);
    }

    /**
     * 用于将DFA的顶点和边联系起来，形成最后的DFA图
     */
    private void setTransitions_DFA() {
        for (List<State> states : states_DFA) {
            int currentStateID = states_DFA_number.get(states);
            State startState = new State(currentStateID);

            HashMap<String, List<State>> currentLineStates = table_DFA.get(states);
            for (int i = 0; i < tags.size() - 1; i++) {
                String currentTag = String.valueOf(tags.get(i));
                List<State> current_tag_state = currentLineStates.get(currentTag);

                if (!current_tag_state.isEmpty()) {
                    State finalState = new State(states_DFA_number.get(current_tag_state));
                    Transition transition = new Transition(currentTag, startState, finalState);

                    if (!transitions_DFA.contains(transition)) {
                        transitions_DFA.add(transition);
                    }
                }
            }
        }
    }

    /**
     * 用于指定DFA图中的开始状态和终止状态
     */
    private void setStartAndFinalStates() {
        for (List<State> states : states_DFA) {
            for (State state : states) {
                if (state.isFinal()) {
                    if (!finalStates_DFA.contains(states_DFA_number.get(states))) {
                        finalStates_DFA.add(states_DFA_number.get(states));
                    }
                }

                if (state.isStart()) {
                    if (!startStates_DFA.contains(states_DFA_number.get(states))) {
                        startStates_DFA.add(states_DFA_number.get(states));
                    }
                }
            }
        }
    }

    /**
     * 用于创建最终的DFA列表
     */
    private void createDFATable() {
        HashMap<String, List<State>> first_line = transitionTable.get(startStates_NFA.get(0).getStateID());
        List<State> firstStates = first_line.get("ε");
        states_DFA.add(firstStates);

        for (int i = 0; i < states_DFA.size(); i++) {
            List<State> states = states_DFA.get(i);
            if (!table_DFA.containsKey(states)) {
                HashMap<String, List<State>> collection = new HashMap<>();

                for (int j = 0; j < tags.size() - 1; j++) { //因为ε根据程序是在最后一个位置，不需要遍历
                    String currentTag = String.valueOf(tags.get(j));
                    List<State> tag_epsilon_kleene = new ArrayList<>();

                    for (State state : states) {
                        HashMap<String, List<State>> info_state = transitionTable.get(state.getStateID());
                        List<State> currentTagStates = info_state.get(currentTag);
                        if (currentTagStates != null) {
                            for (State state_tag : currentTagStates) {
                                int id = state_tag.getStateID();
                                tag_epsilon_kleene.addAll(transitionTable.get(id).get("ε"));
                            }
                        }
                        collection.put(currentTag, tag_epsilon_kleene);
                    }
                    if (!states_DFA.contains(tag_epsilon_kleene)) {
                        if (!tag_epsilon_kleene.isEmpty()) {
                            states_DFA.add(tag_epsilon_kleene);
                        }
                    }
                }
                table_DFA.put(states, collection);
            }
        }

        //对每一个状态进行编号
        for (int i = 0; i < states_DFA.size(); i++) {
            states_DFA_number.put(states_DFA.get(i), i);
        }
    }

    /**
     * 用于创建初始边的列表
     */
    private void createTranstionTable() {
        for (Transition transition : transitions_NFA) {
            if (!transitionTable.containsKey(transition.getHead().getStateID())) {
                HashMap<String, List<State>> line_table = new HashMap<>();

                for (Character character : tags) {
                    String currentTag = String.valueOf(character);
                    List<State> closure = null;

                    if (transition.getTag().equals(currentTag)) {
                        closure = transition.getHead().getNextStates();

                        if (currentTag.equals("ε")) {
                            closure = eClosure(transition.getHead(), transition.getHead(), closure);
                            closure.add(transition.getHead());
                        }
                    } else {
                        if (currentTag.equals("ε")) {
                            closure = new ArrayList<>();
                            closure = eClosure(transition.getHead(), transition.getHead(), closure);
                            closure.add(transition.getHead());
                        }
                    }
                    line_table.put(currentTag, closure);
                    closureCount = 0;
                }
                transitionTable.put(transition.getHead().getStateID(), line_table);
            }
        }

        HashMap<String, List<State>> line_table = new HashMap<>();
        List<State> closure = null;
        for (Character character : tags) {
            String currentTag = String.valueOf(character);
            if (currentTag.equals("ε")) {
                closure = new ArrayList<>();
                closure.add(finalStates_NFA.get(0)); //根据NFA的实现方式，最后DFA的终止状态只会有一个
            }
            line_table.put(currentTag, closure);
        }
        transitionTable.put(finalStates_NFA.get(0).getStateID(), line_table);
    }

    /**
     * 用于寻找某边的ε闭包
     *
     * @param startState    该边的头顶点
     * @param previousState 某边的前一个顶点， 第一次调用的时候传入相同的该边的头顶点
     * @param closure       已经有的ε闭包或者为空
     * @return List<State> 完整的ε闭包
     */
    private List<State> eClosure(State startState, State previousState, List<State> closure) {
        for (Transition transition : transitions_NFA) {
            if (transition.getHead().equals(startState) && transition.getTag().equals("ε")) {
                if (!closure.contains(transition.getTail())) {
                    closure.add(transition.getTail());
                    closureCount = 0;
                }

                if (!closure.contains(startState)) { //则不会出现循环
                    if (transition.getTail().getStateID() != previousState.getStateID()) {
                        eClosure(transition.getTail(), startState, closure);
                    }
                }

                if (closure.contains(startState) && closureCount == 0) { //使用closureCount是为了限制死循环
                    if (transition.getTail().getStateID() != previousState.getStateID()) {
                        eClosure(transition.getTail(), startState, closure);
                    }
                    closureCount = 1;
                }
            }
        }

        return closure;
    }
}
