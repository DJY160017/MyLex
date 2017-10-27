package syntax;

import java.util.*;

/**
 *
 * 用于将正则表达式转换为后缀形式
 *
 */
public class Postfix {

    /**
     * 中缀转后缀
     *
     * @param regex 默认不加点的正则表达式
     * @return String 正则表达式的后缀形式
     */
    public String convert(String regex){
        StringBuilder postfixReg = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        char []formattedReg = this.format(regex).toCharArray();
        for(char character: formattedReg){
            switch(character){
                case '(':
                    stack.push(character);
                    break;
                case ')':
                    while(!stack.peek().equals('(')){
                        postfixReg.append(stack.pop());
                    }
                    stack.pop();
                    break;
                default:
                    while(!stack.isEmpty()){
                        int peekedCharPrecedence = this.getPrecedence(stack.peek());
                        int currentCharPrecedence = this.getPrecedence(character);

                        if(peekedCharPrecedence >= currentCharPrecedence){ //栈顶优先级较高，则弹出继续循环
                            postfixReg.append(stack.pop());
                        } else {
                            break;
                        }
                    }
                    stack.push(character);
                    break;
            }
        }

        while(!stack.isEmpty()){
            postfixReg.append(stack.pop());
        }

        return postfixReg.toString();
    }

    /**
     * 为正则表达式添加点连接符，
     *
     * @param regex 默认不加点的正则表达式
     * @return String format过后的正则表达式
     */
    private String format(String regex){
        StringBuilder result = new StringBuilder();

        char character[] = regex.toCharArray();
        for(int i = 0;i< character.length;i++){
            String first = String.valueOf(character[i]);
            result.append(first);

            if((i+1)<character.length){
                String second = String.valueOf(character[i+1]);
                if(isConnect(first,second)){
                    result.append("·");
                }
            }

        }

        return result.toString();
    }

    /**
     * 获取一个操作符或字符的优先级
     *
     * @param operate 操作符或普通字符
     * @return int 优先级
     */
    private int getPrecedence(char operate){

        Map<Character, Integer> precedenceMap = getPrecedenceCollection();
        Integer precedence = precedenceMap.get(operate);

        if(precedence==null){
            return 6; //如果在map中获得准确优先级，则默认此字符为最大优先级
        }
        return precedence;
    }

    /**
     * 判断当前字符后面是否需要添加点运算符
     *
     * @param current 当前字符
     * @param next 下一个字符
     * @return int 优先级
     */
    private boolean isConnect(String current, String next){
        List<String> operators = Arrays.asList("*","+","|","?","^");
        List<String> binOperators = Arrays.asList("|","^"); //二目运算符
        return !current.equals("(") && !next.equals(")") && !operators.contains(next) && !binOperators.contains(current);
    }

    /**
     * 获取操作符和优先级
     *
     * @return Map<Character, Integer> 操作符和优先级集合
     */
    public Map<Character, Integer> getPrecedenceCollection(){
        Map<Character, Integer> precedenceMap = new HashMap<>();
        precedenceMap.put('(', 1);
        precedenceMap.put('|', 2);
        precedenceMap.put('·', 3);
        precedenceMap.put('?', 4);
        precedenceMap.put('*', 4);
        precedenceMap.put('+', 5);
        precedenceMap.put('^', 6);
        return precedenceMap;
    }
}
