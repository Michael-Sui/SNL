package lexer;

import java.util.List;

/**
 * Created by Michael on 2017/5/9.
 * 词法分析结果
 */
public class LexerResult {
    private List<Token> tokenList;
    private List<String> errors;

    public List<String> getErrors() {
        return errors;
    }

    void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
    }
}
