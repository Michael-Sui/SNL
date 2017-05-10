package main;

import lexer.Lexer;
import lexer.LexerResult;
import lexer.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
            String path = "C:\\Users\\Michael\\Desktop\\SNL语言例子\\一般例子\\C1.TXT";

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
            } else {
                errors.forEach(System.err::println);
                System.exit(1);
            }

            //释放占用的资源。
            unicodeReader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
