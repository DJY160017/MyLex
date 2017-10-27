package syntax;

public class RegExpSimplifier {

    /**
     * 后缀形式的正则表达式
     */
    private String regExp;

    public RegExpSimplifier(String regExp) {
        this.regExp = regExp;
    }

    /**
     * simplifier regExp
     *
     * @return String 化简后的RegExp
     */
    public String handle() {
        this.handlePlusMark();
        this.handleQuestionMark();
        return regExp;
    }

    /**
     * handles '?' operator
     */
    private void handleQuestionMark() {

        for (int i = 0; i < regExp.length(); i++) {
            if (String.valueOf(regExp.charAt(i)).equals("?")) {

                if (!String.valueOf(regExp.charAt(i - 1)).equals(")")) {
                    String symbol = String.valueOf(regExp.charAt(i - 1));
                    String subRegExp = "(" + symbol + "|ε)";
                    regExp = regExp.substring(0, i - 1) + subRegExp + regExp.substring(i + 1);
                } else {
                    int counter = 0;
                    for (int j = i - 1; j >= 0; j--) {
                        if ((j != i - 1) && (String.valueOf(regExp.charAt(j)).equals(")"))) {
                            counter++;
                        }

                        if (String.valueOf(regExp.charAt(j)).equals("(")) {
                            if (counter != 0) {
                                counter--;
                            } else {
                                String subRegExpWithBrackets = regExp.substring(j, i);
                                String subExp = "(" + subRegExpWithBrackets + "|ε)";
                                regExp = regExp.substring(0, j) + subExp + regExp.substring(i + 1);
                                break;
                            }
                        }
                    }
                }

            }
        }

    }

    /**
     * handles '+' operator
     */
    private void handlePlusMark() {
        for (int i = 0; i < regExp.length(); i++) {
            if (String.valueOf(regExp.charAt(i)).equals("+")) {

                if (!String.valueOf(regExp.charAt(i - 1)).equals(")")) {
                    String symbol = String.valueOf(regExp.charAt(i - 1));
                    String subExp = symbol + symbol + "*";
                    regExp = regExp.substring(0, i - 1) + subExp + regExp.substring(i + 1);
                } else {
                    int counter = 0; // 用于提出括号内的其余括号
                    for (int j = i - 1; j >= 0; j--) {
                        if ((j != i - 1) && (String.valueOf(regExp.charAt(j)).equals(")"))) {
                            counter++;
                        }

                        if (String.valueOf(regExp.charAt(j)).equals("(")) {
                            if (counter != 0) {
                                counter--;
                            } else {
                                String subRegExpWithBrackets = regExp.substring(j, i);
                                String subExp = subRegExpWithBrackets + subRegExpWithBrackets + "*";
                                regExp = regExp.substring(0, j) + subExp + regExp.substring(i + 1);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
