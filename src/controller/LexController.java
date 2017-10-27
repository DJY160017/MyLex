package controller;

import utility.Buffer;
import utility.Token;
import utility.TokenInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LexController {

    /**
     * 输入流的指针
     */
    private int pointer;

    /**
     * 分析器工厂
     */
    private AnalyserFactory analyserFactory;

    public LexController() {
        this.pointer = 0;
        String root = System.getProperty("user.dir");
        System.out.println(root);
        String reg_path = root+"\\src\\resources\\regExp.txt";
        analyserFactory = new AnalyserFactory(reg_path);
    }

    public static void main(String[] args) {
        LexController lexController = new LexController();
        String root = System.getProperty("user.dir");
        String path = root + "\\src\\resources\\procedure.txt";
        lexController.handle(path);
    }

    /**
     * 用于处理整个词法分析的过程
     *
     * @param path_procedure 程序文件的位置
     */
    public void handle(String path_procedure){
        String root = System.getProperty("user.dir");
        String path_output = root + "\\src\\resources\\result.txt";
        List<Token> result = scan(path_procedure);
        this.writer(path_output, result);
    }

    /**
     *  用于扫描输入流得到分隔开的字符
     *
     * @param path 需要扫描的文件
     * @return List<Token> Token序列
     */
    private List<Token> scan(String path) {
        System.out.println("--------------"+"start scan analyser"+"--------------");
        TokenInfo tokenInfo = new TokenInfo();
        List<Token> tokens = new ArrayList<>();
        char[] input = this.reader(path);
        System.out.println(String.valueOf(input));

        Buffer buffer = new Buffer();
        while (input[pointer] != '$') {

            if (tokenInfo.isPunctuation(String.valueOf(input[pointer]))) { //扫描到当前的字符为标点符号
                handlePunctuation(tokens, input, buffer);
                continue; //已判断为标点符号，无需继续下去
            }

            if (tokenInfo.isOperator(String.valueOf(input[pointer]))) {
                handleOperator(tokens, input, buffer);
                continue; //已判断为操作符，无需继续下去
            }

            if (input[pointer] == 32 || input[pointer] == '\n') {
                if (!buffer.isEmpty()) {
                    tokens.add(analyserFactory.getToken(buffer.getValue()));
                    buffer.clear();
                }
                pointer = pointer + 1;
            } else {
                buffer.add(input[pointer]);
                pointer = pointer + 1; //正常字符流前进
            }
        }

        System.out.println("--------------"+"end scan analyser"+"--------------");
        return tokens;
    }

    /**
     *  当判断当前字符(input[pointer])为标点符号调用
     *
     * @param tokens 结果列表
     * @param input 输入流
     * @param buffer 需要添加字符的buffer
     */
    private void handlePunctuation(List<Token> tokens, char[] input, Buffer buffer) {
        TokenInfo tokenInfo = new TokenInfo();
        if (input[pointer] == '.') {
            if (tokenInfo.isNumber(input[pointer + 1])) {
                buffer.add(input[pointer]); //代表是double类型，正常进行过程
                pointer++;
            } else { //代表是操作符，需要先把word变成token
                if (!buffer.isEmpty()) {
                    tokens.add(analyserFactory.getToken(buffer.getValue()));
                    buffer.clear();
                }
                tokens.add(new Token("Punctuation", String.valueOf(input[pointer])));
                pointer++;
            }
        } else if (input[pointer] == '"') {
            buffer.add(input[pointer]);
            pointer++;

        } else if (input[pointer] == '[') {
            if (input[pointer + 1] == ']') {
                buffer.add(input[pointer]);
                pointer++;
                buffer.add(input[pointer]);
                pointer++;
            } else {
                if (!buffer.isEmpty()) {
                    tokens.add(analyserFactory.getToken(buffer.getValue()));
                    buffer.clear();
                }
                tokens.add(new Token("Punctuation", String.valueOf(input[pointer])));
                pointer++;
            }
        } else { //普通标点符号
            if (!buffer.isEmpty()) {
                tokens.add(analyserFactory.getToken(buffer.getValue()));
                buffer.clear();
            }
            tokens.add(new Token("Punctuation", String.valueOf(input[pointer])));
            System.out.println(String.valueOf(input[pointer]));
            pointer++;
        }
    }

    /**
     *  当判断当前字符(input[pointer])为操作符调用
     *
     * @param tokens 结果列表
     * @param input 输入流
     * @param buffer 需要添加字符的buffer
     */
    private void handleOperator(List<Token> tokens, char[] input, Buffer buffer) {
        TokenInfo tokenInfo = new TokenInfo();
        switch (input[pointer]) {
            case '+':
                if (input[pointer + 1] == '+') {
                    if (!buffer.isEmpty()) {
                        tokens.add(analyserFactory.getToken(buffer.getValue()));
                        buffer.clear();
                    }
                    tokens.add(new Token("Operator", "++")); //默认前方无空格
                    pointer = pointer + 2; //跳过第二个+号
                } else if (input[pointer + 1] == '=') {
                    tokens.add(new Token("Operator", "+="));
                    pointer = pointer + 2; //跳过=号
                } else {
                    tokens.add(new Token("Operator", "+"));
                    pointer = pointer + 1;
                }
                break;
            case '-':
                if (input[pointer + 1] == '-') {
                    if (buffer.isEmpty()) {
                        tokens.add(analyserFactory.getToken(buffer.getValue()));
                        buffer.clear();
                    }
                    tokens.add(new Token("Operator", "--")); //默认前方无空格
                    pointer = pointer + 2; //跳过第二个+号
                } else if (input[pointer + 1] == '=') {
                    tokens.add(new Token("Operator", "-="));
                    pointer = pointer + 2; //跳过=号
                } else if (tokenInfo.isNumber(input[pointer + 1])) {
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                } else {
                    tokens.add(new Token("Operator", "-"));
                    pointer = pointer + 1;
                }
                break;
            case '/':
                if (input[pointer + 1] == '=') {
                    tokens.add(new Token("Operator", "/="));
                    pointer = pointer + 2; //跳过=号
                } else if (input[pointer + 1] == '*') {
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                    buffer.add(input[pointer]); // 跳过第一个*
                    pointer = pointer + 1;

                    while ((input[pointer] != '*') || (input[pointer + 1] != '/')) {
                        buffer.add(input[pointer]);
                        pointer = pointer + 1;
                    }

                    // 跳过最后的*/
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                    if (!buffer.isEmpty()) {
                        tokens.add(analyserFactory.getToken(buffer.getValue()));
                        buffer.clear();
                    }
                    pointer = pointer + 1;
                } else if (input[pointer + 1] == '/') {
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                    buffer.add(input[pointer]); // 跳过//
                    pointer = pointer + 1;

                    while (input[pointer] != '\n') {
                        buffer.add(input[pointer]);
                        pointer = pointer + 1;
                    }

                    if (!buffer.isEmpty()) {
                        tokens.add(analyserFactory.getToken(buffer.getValue()));
                        buffer.clear();
                    }
                    pointer = pointer + 1;
                } else {
                    tokens.add(new Token("Operator", "/"));
                    pointer = pointer + 1;
                }
                break;
            case '&':
                if (input[pointer + 1] == '&') {
                    tokens.add(new Token("Operator", "&&"));
                    pointer = pointer + 2; //跳过&号
                } else {
                    tokens.add(new Token("Operator", "&"));
                    pointer = pointer + 1;
                }
                break;
            case '|':
                if (input[pointer + 1] == '|') {
                    tokens.add(new Token("Operator", "||"));
                    pointer = pointer + 2; //跳过|号
                } else {
                    tokens.add(new Token("Operator", "|"));
                    pointer = pointer + 1;
                }
                break;
            default:
                if (input[pointer + 1] == '=') {
                    tokens.add(new Token("Operator", String.valueOf(input[pointer]) + "="));
                    pointer = pointer + 2; //跳过=号
                } else {
                    tokens.add(new Token("Operator", String.valueOf(input[pointer])));
                    pointer = pointer + 1;
                }
                break;
        }
    }

    /**
     * 从文件读取字符流
     *
     * @param path 文件路径
     * @return char[] 输入流
     */
    private char[] reader(String path) {
        char[] input = new char[2000];
        int pointer = 0;

        try {
            File file = new File(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                char[] line_buffer = line.toCharArray();
                for (char character : line_buffer) {
                    input[pointer] = character;
                    pointer++;
                }
                input[pointer] = '\n';
                pointer++;
            }

            input[pointer] = '$'; //作为缓冲区的结束符
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Read File: " + path + " Failed");
        }
        return input;
    }

    private void writer(String path, List<Token> tokens){
        File file =new File(path);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for(Token token: tokens){
                bufferedWriter.write(token.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
