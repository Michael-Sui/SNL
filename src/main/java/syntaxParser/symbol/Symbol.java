package syntaxParser.symbol;

import syntaxParser.TreeNode;

/**
 * Created by lin on 2016-06-01-001.
 * 符号，派生出终极符和非终极符
 */
public abstract class Symbol {
    public abstract TreeNode getNode();
}
