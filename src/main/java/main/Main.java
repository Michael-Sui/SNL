package main;

import lexer.Lexer;
import lexer.LexerResult;
import lexer.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import syntaxParser.LL1.LL1Parser;
import syntaxParser.ParseResult;
import syntaxParser.SyntaxParser;
import syntaxParser.SyntaxTree;
import utils.UnicodeReader;

import java.io.*;
import java.util.List;

/**
 * Created by Michael on 2017/5/9.
 * 主函数。
 */
public class Main {
    private static Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            //修改path实现对不同源文件的编译。
            String path = "C:\\Users\\Michael\\Desktop\\SNL语言\\一般例子\\C1.TXT";
            //String path = "C:\\Users\\Michael\\Desktop\\SNL语言\\错误检查例.TXT";

            //获取输入流。
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            UnicodeReader unicodeReader = new UnicodeReader(fileInputStream, "UTF-8");

            //进行词法分析。
            Lexer lexer = new Lexer();
            LexerResult lexerResult = lexer.getResult(unicodeReader);
            List<Token> list = null;
            List<String> errors = lexerResult.getErrors();
            if (errors.size() == 0) {
                PrintStream out = new PrintStream(path + ".token.list.txt");
                list = lexerResult.getTokenList();
                list.forEach(out::println);
                out.close();
            } else {
                errors.forEach(System.err::println);
                System.exit(1);
            }

            //进行语法分析。
            SyntaxParser parser = new LL1Parser();
            LOG.debug("参数: Parser=LL1, Encoding=UTF-8");
            ParseResult result = parser.parse(list);
            if (result == null) {
                System.err.println("获取分析结果错误");
                System.exit(1);
            }
            if (result.isSuccess()) {
                SyntaxTree.print(result.getTree().getRoot(), new PrintStream(path + ".tree.txt"),
                        "Syntax Tree for source code: " + path + "(by LL1)", 0);
            } else {
                System.err.println("LL1 Parser: parse Error. Error List:");
                result.getErrors().forEach(System.err::println);
            }

            //释放占用的资源。
            unicodeReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
