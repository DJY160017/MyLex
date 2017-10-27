package implementaion;

import syntax.Postfix;
import syntax.RegExpSimplifier;
import utility.FAInfo.NFAInfo;
import utility.State;
import utility.Transition;

import java.util.*;

/**
 * 用于将正则表达式转换为NFA
 */
public class NFA {

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
    private List<State> startStates;

    /**
     * MFA中终止状态的集合
     */
    private List<State> finalStates;

    /**
     * 用于MFA中设置唯一ID
     */
    private static int stateID;

    /**
     * 保存初始状态的栈
     */
    private Stack<State> stackStart;

    /**
     * 保存终止状态的栈
     */
    private Stack<State> stackFinal;

    public NFA() {
        stateID = 0;
        transitions = new ArrayList<>();
        startStates = new ArrayList<>();
        finalStates = new ArrayList<>();
        stackFinal = new Stack<>();
        stackStart = new Stack<>();
    }

    /**
     * 完成整个NFA的转换过程
     *
     * @param regExp 正则表达式
     * @return NFAInfo NFA的信息载体
     */
    public NFAInfo convert(String regExp) {
        String postFixRegExp = init(regExp);
        tags = getTags(postFixRegExp);
        regToNFA(postFixRegExp);
        setStartState();
        return new NFAInfo(tags, transitions, startStates, finalStates);
    }

    /**
     * 初始化后缀形式的正则表达式
     */
    private String init(String regExp) {
        RegExpSimplifier simplifier = new RegExpSimplifier(regExp);
        Postfix postfix = new Postfix();
        return postfix.convert(simplifier.handle());
    }

    /**
     * 获取正则表达式中的最小自动机的标志
     *
     * @return 正则表达式中的最小自动机的标志
     */
    private List<Character> getTags(String postFixRegExp) {
        List<Character> tags = new ArrayList<>();
        Postfix postfix = new Postfix();
        for (int i = 0; i < postFixRegExp.length(); i++) {
            if (!postfix.getPrecedenceCollection().containsKey(postFixRegExp.charAt(i))) {
                if (!tags.contains(postFixRegExp.charAt(i))) {
                    tags.add(postFixRegExp.charAt(i));
                }
            }
        }
        Collections.sort(tags);
        return tags;
    }

    /**
     * 获取唯一的状态ID
     *
     * @return int 唯一的状态ID
     */
    private static int getStateID() {
        return NFA.stateID;
    }

    /**
     * 唯一的状态ID自增
     */
    private static void stateIdPlusOne() {
        NFA.stateID = NFA.stateID + 1;
    }

    /**
     * 用于将正则表达式转换为NFA
     *
     * @param postFixRegExp 后缀形式的正则表达式
     */
    private void regToNFA(String postFixRegExp) {
        char[] character = postFixRegExp.toCharArray();

        for (int i = 0; i < character.length; i++) {
            switch (character[i]) {
                case '|':
                    State low_start = stackStart.pop();
                    State low_final = stackFinal.pop();
                    State upper_start = stackStart.pop();
                    State upper_final = stackFinal.pop();
                    unify(upper_start, upper_final, low_start, low_final);
                    break;
                case '*':
                    State start_kleene = stackStart.pop();
                    State end_kleene = stackFinal.pop();
                    kleene(start_kleene, end_kleene);
                    break;
                case '·':
                    State last_Final = stackFinal.pop(); //因为连接是取上一个终止状态，则需要提前弹出最后一个终止状态
                    State start_concatenate = stackStart.pop();
                    State end_concatenate = stackFinal.pop();
                    concatenate(end_concatenate, start_concatenate, last_Final); // 由于连接是与上一个终止状态，所以需要反过来
                    break;
                default:
                    int headID = NFA.getStateID();
                    NFA.stateIdPlusOne();
                    int tailID = NFA.getStateID();
                    NFA.stateIdPlusOne();

                    Transition transition = new Transition(String.valueOf(character[i]), headID, tailID);
                    transitions.add(transition);

                    State start = transition.getHead();
                    State end = transition.getTail();
                    stackStart.push(start);
                    stackFinal.push(end);
                    break;
            }

            if (i == character.length - 1) { //到了循环的末尾
                State finalState = stackFinal.pop();
                finalState.setFinal(true);
                finalStates.add(finalState); //设置NFA的最终状态
                if (tags.contains('ε')) {
                    tags.remove(tags.indexOf('ε'));
                }
            }
        }
    }

    /**
     * 设置整个NFA的开始状态
     */
    private void setStartState() {
        State start = stackStart.pop();
        start.setStart(true);
        startStates.add(start);
    }

    /**
     * 用于处理‘|’情况
     *
     * @param upperFinalState 在图形中上方那条边的末尾
     * @param upperStartState 在图形中上方那条边的首部
     * @param lowFinalState   在图形中下方那条边的末尾
     * @param lowStartState   在图形中下方那条边的首部
     */
    private void unify(State upperStartState, State upperFinalState, State lowStartState, State lowFinalState) {
        State in = new State(NFA.getStateID());
        NFA.stateIdPlusOne(); //为下一个State的ID做好准备

        State out = new State(NFA.getStateID());
        NFA.stateIdPlusOne(); //为下一个State的ID做好准备

        //create new transitions
        Transition transition_in_upper = new Transition("ε", in, upperStartState);
        Transition transition_in_low = new Transition("ε", in, lowStartState);
        Transition transition_out_upper = new Transition("ε", upperFinalState, out);
        Transition transition_out_low = new Transition("ε", lowFinalState, out);

        transitions.add(transition_in_upper);
        transitions.add(transition_in_low);
        transitions.add(transition_out_upper);
        transitions.add(transition_out_low);

        stackStart.push(in);
        stackFinal.push(out);
    }

    /**
     * 用于处理‘.’情况
     *
     * @param startState      第一个自动机的尾部
     * @param finalState      第二个自动机的头部
     * @param last_finalState 第二个自动机的尾部
     */
    private void concatenate(State startState, State finalState, State last_finalState) {
        Transition transition = new Transition("ε", startState, finalState);
        transitions.add(transition);
        stackFinal.push(last_finalState); //因为需要的是前一个自动机的尾部，需要提前弹出最后一个，再压回去
    }

    /**
     * 用于处理‘*’情况
     *
     * @param startState 当前自动机的头部
     * @param finalState 当前自动机的尾部
     */
    private void kleene(State startState, State finalState) {
        State in = new State(NFA.getStateID());
        NFA.stateIdPlusOne(); //为下一个State的ID做好准备

        State out = new State(NFA.getStateID());
        NFA.stateIdPlusOne(); //为下一个State的ID做好准备

        Transition in_start = new Transition("ε", in, startState);
        Transition final_out = new Transition("ε", finalState, out);
        Transition final_start = new Transition("ε", finalState, startState);
        Transition in_out = new Transition("ε", in, out);

        transitions.add(in_start);
        transitions.add(final_out);
        transitions.add(final_start);
        transitions.add(in_out);

        stackStart.push(in);
        stackFinal.push(out);
    }
}
