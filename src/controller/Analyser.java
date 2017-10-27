package controller;

import implementaion.DFA;
import implementaion.MinimizedDFA;
import implementaion.NFA;
import utility.FAInfo.DFAInfo;
import utility.FAInfo.MinimizedDFAInfo;
import utility.FAInfo.NFAInfo;
import utility.State;
import utility.Transition;

import java.util.HashMap;
import java.util.List;

public class Analyser {

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

    /**
     * 当前分析器需要匹配的类型
     */
    private String type;

    /**
     * Analyser的初始化
     * @param regExp 正则表达式
     */
    public Analyser(String regExp) {
        NFA nfa = new NFA();
        NFAInfo nfaInfo = nfa.convert(regExp);
        DFA dfa = new DFA(nfaInfo);
        DFAInfo dfaInfo = dfa.handle();
        MinimizedDFA minimizedDFA = new MinimizedDFA(dfaInfo);
        MinimizedDFAInfo minimizedDFAInfo = minimizedDFA.handle();
        transitions = minimizedDFAInfo.getTransitions();
        states = minimizedDFAInfo.getStates();
        startStates = minimizedDFAInfo.getStartStates();
        finalStates = minimizedDFAInfo.getFinalStates();
    }

    /**
     * 用于判断给定单词是否匹配当前的正则表达式
     *
     * @param word 给定单词
     * @return 是否成功匹配
     */
    public boolean isMatch(String word) {
        char[] input = word.toCharArray();
        int currentState = startStates.get(0);
        boolean find_transition = true; //用于判断字符流尚未到达末尾，但FA已经到了最终状态，无法继续下去
        boolean find_tag = true; //用于判断单词当中存在错误字符的情况

        for (char character : input) {
            String currentChar = String.valueOf(character);
            for (Transition transition : transitions) {
                if (transition.getHead().getStateID() == currentState) {
                    find_transition = true;

                    if (transition.getTag().equals(currentChar)) { //正常情况，即理想状况
                        currentState = transition.getTail().getStateID();
                        find_tag = true;
                        break;
                    }

                    if (!transition.getTag().equals(currentChar)) { //发现当前未知标志，但可能后续标志符合
                        find_tag = false;
                    }
                } else {
                    find_transition = false;  //未找到符合当前状态继续下去的边，可能后续存在
                }
            }

            if(!find_tag){  //边集合遍历完成，无符合标志，匹配错误
                return false;
            }

            if (!find_transition) { //边集合遍历完成，当前状态无继续下去的边，匹配错误
                return false;
            }
        }

        if (!finalStates.contains(currentState)) { //整个字符流检索完毕，如果最后一个状态不是终止状态，匹配错误
            return false;
        }
        return true;
    }

    /**
     * 获取匹配后，word的类型
     *
     * @return String 结果
     */
    public String getType() {
        return type;
    }

    /**
     * 初始化type
     *
     * @param type 分析器所属的类型
     */
    public void setType(String type) {
        this.type = type;
    }
}
