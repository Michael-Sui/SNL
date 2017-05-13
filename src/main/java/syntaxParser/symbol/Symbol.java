package syntaxParser.symbol;

import syntaxParser.TreeNode;

/**
 * Created by Michael on 2017/5/9.
 * 符号，派生出终极符和非终极符
 */
public abstract class Symbol {
    public abstract TreeNode getNode();
}
