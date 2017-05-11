package test;

import lexer.Lexer;
import lexer.LexerResult;
import lexer.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Michael on 2017/5/10.
 */
public class Test {
    private void log4jTest() {
        Logger LOG = LogManager.getLogger(Test.class);
        LOG.trace("trace");
        LOG.debug("debug");
        LOG.warn("debug");
        LOG.error("error");
    }

    private void lexerTest() {
        InputStream in = Lexer.class.getClassLoader().getResourceAsStream("p.snl");
        Lexer lexer = new Lexer();
        try {
            LexerResult result = lexer.getResult(new InputStreamReader(in));
            if (result.getErrors().size() == 0) {
                List<Token> list = result.getTokenList();
                System.out.println();
                if (list.size() > 0) {
                    System.out.printf("[ 行:列 ]|【 语义信息 】| 词法信息 \n");
                    System.out.printf("---------+--------------+----------\n");
                }
                for (Token t : list) {
                    System.out.printf("[%3d:%-3d]|【%10s】|%10s\n", t.getLine(), t.getColumn(), t.getValue(), t.getType().getStr());
                }
                if (list.size() > 0) {
                    System.out.printf("---------+--------------+----------\n");
                    System.out.printf("[ 行:列 ]|【 语义信息 】| 词法信息 \n");
                }
            } else {
                System.err.println("词法分析错误");
                result.getErrors().forEach(System.err::println);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        //test.log4jTest();
        //test.lexerTest();
    }
}
