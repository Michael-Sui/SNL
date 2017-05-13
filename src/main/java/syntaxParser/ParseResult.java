package syntaxParser;

import java.util.List;

/**
 * Created by Michael on 2017/5/9.
 * 分析结果
 */
public class ParseResult {
    private SyntaxTree tree;
    private List<String> errors;

    public boolean isSuccess() {
        return errors == null || errors.size() == 0;
    }

    public SyntaxTree getTree() {
        return tree;
    }

    public void setTree(SyntaxTree tree) {
        this.tree = tree;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
