package controller;


import utility.Token;
import utility.TokenInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnalyserFactory {

    /**
     * 该Lex的所有分析器
     */
    private List<Analyser> analysers;

    public AnalyserFactory(String path) {
        analysers =new ArrayList<>();
        this.createAnalyserFactory(path);
    }

    /**
     * 获取当前word的Token
     *
     * @param word 需要分析的word
     * @return Token 结果
     */
    public Token getToken(String word){
        TokenInfo tokenInfo = new TokenInfo();

        if(tokenInfo.isNumber(word.charAt(0))&&tokenInfo.isLetter(word.charAt(word.length()-1))){
            return new Token("Unknown Type", word, "未知类型，匹配错误");
        }

        if((word.startsWith("/*")&&word.endsWith("*/"))||word.startsWith("//")){
            return new Token("Notes", word);
        }

        System.out.println("--------------"+"start create token"+"--------------");
        for(Analyser analyser: analysers){
            if(analyser.isMatch(word)){
                return new Token(analyser.getType(), word);
            }
        }
        System.out.println("--------------"+"end create analyser"+"--------------");
        return new Token("ID", word);
    }

    /**
     * 产生所有分析器
     *
     * @param path_regExp 指定正则表达式
     */
    private void createAnalyserFactory(String path_regExp){
        System.out.println("--------------"+"start create analyser"+"--------------");
        File file = new File(path_regExp);
        try {
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;

            while((line = bufferedReader.readLine())!=null){
                String[] one_regExp = line.split(";");
                Analyser analyser =new Analyser(one_regExp[0]);
                analyser.setType(one_regExp[1]);
                analysers.add(analyser);
                System.out.println("create type: "+analyser.getType()+"create value: "+one_regExp[0]);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--------------"+"end create analyser"+"--------------");
    }
}
