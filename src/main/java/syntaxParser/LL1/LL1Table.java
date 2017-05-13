package syntaxParser.LL1;

import lexer.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import syntaxParser.symbol.NON_TERMINAL_SYMBOLS;
import syntaxParser.symbol.NonTerminalSymbol;
import syntaxParser.symbol.Symbol;

import java.util.List;

/**
 * Created by Michael on 2017/5/9.
 * LL1表
 */
class LL1Table {
    private static Logger LOG = LogManager.getLogger(LL1Table.class);

    static List<Symbol> find(NonTerminalSymbol nonTerminalSymbol, Token predict) {
        return lookUp(nonTerminalSymbol, predict);
    }

    private static List<Symbol> lookUp(NonTerminalSymbol nonTerminalSymbol, Token predict) {
        String value = nonTerminalSymbol.getValue();
        LOG.trace("查表 非终极符=" + value + ", 展望符=" + predict.getValue());
        NON_TERMINAL_SYMBOLS symbols = NON_TERMINAL_SYMBOLS.valueOf(value);
        return symbols.find(predict);
    }
}
