package syntaxParser.symbol;

import lexer.Token;
import lexer.TokenType;
import syntaxParser.TreeNode;

/**
 * Created by Michael on 2017/5/9.
 * 终极符
 */
public class TerminalSymbol extends Symbol {
    //不同于开始符，因为 空 不会产生子树，因此可以重用同一个
    public static final TerminalSymbol epsilon = new TerminalSymbol(new Token("空"));
    private final TreeNode node;
    private Token token;

    public boolean isEpsilon() {
        return this == epsilon;
    }

    public TerminalSymbol(Token token) {
        super();
        this.token = token;
        node = new TreeNode(token.getValue());
    }

    TerminalSymbol(TokenType type) {
        this(new Token(type));
    }

    public Token getToken() {
        return token;
    }

    @Override
    public TreeNode getNode() {
        return node;
    }
}
