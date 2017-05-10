package lexer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Michael on 2017/5/9.
 * 词法分析器。
 */
public class Lexer {
    //枚举了状态机的各种状态。
    private enum State {
        Normal, InId, InNum, InComment, InChar, Error, InAssign, InRange, InDot
    }

    private static Logger LOG = LogManager.getLogger(Lexer.class);
    private int getMeFirst = -1;
    private int line = 1;
    private int column = 0;
    private List<String> errors;
    private Reader reader;

    //主函数调用这个函数获取词法分析的结果。
    public LexerResult getResult(Reader reader) throws IOException {
        errors = new ArrayList<>();
        LexerResult result = new LexerResult();
        List<Token> list = new ArrayList<>();
        if (reader == null) {
            errors.add("输入流不能为空。");
            result.setErrors(errors);
            result.setTokenList(list);
            return result;
        }
        this.reader = reader;
        Token token = getToken();
        while (token != null) {
            list.add(token);
            token = getToken();
        }
        result.setTokenList(list);
        result.setErrors(errors);
        for (String error : errors) {
            LOG.warn(error);
        }
        return result;
    }

    //获取token序列。
    private Token getToken() throws IOException {
        LOG.trace("调用getToken方法");
        State state = State.Normal;
        StringBuilder sb = new StringBuilder();
        int ch = getChar();
        while (ch != -1) {
            sb.append((char) ch);
            Token token = null;
            switch (state) {
                case Normal:
                    LOG.trace("进入Normal状态");
                    if (isAlpha(ch)) {
                        state = State.InId;
                    } else if (isDigit(ch)) {
                        state = State.InNum;
                    } else if (isBlank(ch)) {
                        sb.deleteCharAt(sb.length() - 1);
                        state = State.Normal;
                    } else if (ch == '+') {
                        token = new Token(line, column, TokenType.PLUS, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '-') {
                        token = new Token(line, column, TokenType.MINUS, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '*') {
                        token = new Token(line, column, TokenType.TIMES, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '/') {
                        token = new Token(line, column, TokenType.OVER, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '(') {
                        token = new Token(line, column, TokenType.LPAREN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ')') {
                        token = new Token(line, column, TokenType.RPAREN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '[') {
                        token = new Token(line, column, TokenType.LMIDPAREN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ']') {
                        token = new Token(line, column, TokenType.RMIDPAREN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ';') {
                        token = new Token(line, column, TokenType.SEMI, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ',') {
                        token = new Token(line, column, TokenType.COMMA, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '=') {
                        token = new Token(line, column, TokenType.EQ, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '<') {
                        token = new Token(line, column, TokenType.LT, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ':') {
                        state = State.InAssign;
                    } else if (ch == '{') {
                        //把【{】删除。
                        sb.deleteCharAt(sb.length() - 1);
                        state = State.InComment;
                    } else if (ch == '.') {
                        state = State.InDot;
                    } else if (ch == '\'') {
                        //把【'】删除。
                        sb.deleteCharAt(sb.length() - 1);
                        state = State.InChar;
                    } else {
                        LOG.trace("【ERROR】当前字符在预料之外:{}({})", (char) ch, ch);
                        state = State.Error;
                    }
                    break;
                case InId:
                    LOG.trace("进入InId状态");
                    if (isAlpha(ch) || isDigit(ch)) {
                        state = State.InId;
                    } else {
                        //当前字符已经不是`标识符`的组成部分，即已经把标识符识别出来了。
                        LOG.trace("当前字符已经不属于标识符组成了:{}({})", showChar(ch), ch);
                        //当前字符不属于标识符，因此回退，下次分析再读
                        unGetChar(sb.charAt(sb.length() - 1));
                        token = new Token(line, column, TokenType.ID, sb.substring(0, sb.length() - 1));
                        LOG.debug("已识别Token:" + token);
                        token.checkKeyWords();
                        return token;
                    }
                    break;
                case InNum:
                    LOG.trace("开始识别数字");
                    if (!isDigit(ch)) {
                        //数字识别完成。
                        unGetChar(sb.charAt(sb.length() - 1));
                        token = new Token(line, column, TokenType.INTC, sb.substring(0, sb.length() - 1));
                        LOG.debug("已识别Token:" + token);
                        return token;
                    }
                    break;
                //识别赋值符号:=。
                case InAssign:
                    if (ch == '=') {
                        token = new Token(line, column, TokenType.ASSIGN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else {
                        state = State.Error;
                    }
                    break;
                //处理注释。
                case InComment:
                    //删除读入的第一个注释中的字符。
                    sb.deleteCharAt(sb.length() - 1);
                    while (ch != -1 && ch != '}') {
                        ch = getChar();
                    }
                    //一直到注释结束，注释结束后继续识别。
                    state = State.Normal;
                    if (ch != '}') {
                        LOG.trace("期待注释结束符【}】却没有}");
                        state = State.Error;
                    }
                    break;
                case InDot:
                    //域的点号a.b。
                    if (isAlpha(ch)) {
                        unGetChar(ch);
                        //把多读入的字母删除。
                        sb.deleteCharAt(sb.length() - 1);
                        token = new Token(line, column, TokenType.DOT, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    }
                    //下标..。
                    if (ch == '.') {
                        state = State.InRange;
                        break;
                    }
                    while (isBlank(ch))ch=getChar();
                    //EOF。
                    if (ch == -1) {
                        token = new Token(line, column, TokenType.EOF, ".");
                        LOG.trace(sb.toString() + "  " + token);
                        return token;
                    }
                    LOG.trace("错误的点号");
                    //报错。
                    unGetChar(ch);
                    state = State.Error;
                    break;
                //下标界限..。
                case InRange:
                    if (isDigit(ch)) {
                        unGetChar(ch);
                        //把多读入的数字删掉。
                        sb.deleteCharAt(sb.length() - 1);
                        token = new Token(line, column, TokenType.UNDERRANGE, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    }
                    state = State.Error;
                    break;
                //识别引号括起来的字符。
                case InChar:
                    if (isAlpha(ch) || isDigit(ch)) {
                        ch = getChar();
                        if (ch == '\'') {
                            token = new Token(line, column, TokenType.CHARACTER, sb.toString());
                            LOG.debug("已识别Token:" + token);
                            return token;
                        }
                    }
                    state = State.Error;
                    break;
                //错误处理 返回空的Token 记录错误信息。
                case Error:
                    LOG.warn("[Error] Unrecognized token. near " + line + ":" + column);
                    errors.add("[Error] Unrecognized token. near " + line + ":" + column);
                    token = new Token();
                    return token;
                default:
                    state = State.Error;
            }
            ch = getChar();
        }
        //文件结束处理
        //文件已结束，且前一个符号是【.】说明程序结束
        if (state == State.InDot) {
            Token token = new Token(line, column, TokenType.EOF, ".");
            LOG.debug("已识别Token:" + token);
            return token;
        }
        if (state != State.Normal) {
            errors.add("[错误]在 " + line + "行 " + column + "列");
        }
        return null;
    }

    private int getChar() throws IOException {
        int ch;
        if (getMeFirst != -1 && getMeFirst != ' ' && getMeFirst != '\r' && getMeFirst != '\n') {
            ch = getMeFirst;
            getMeFirst = -1;
        } else {
            ch = reader.read();
        }
        if (ch == '\n') {
            column = 0;
            line ++;
        } else if (ch != -1) {
            column ++;
        }
        if (ch == '\r') {
            column --;
        }
        LOG.trace("当前字符是{}({})[{}:{}]", showChar(ch), ch, line, column);
        return ch;
    }

    //进入一个状态读取了了一个非法字符后将这个非法字符回退，重新进行回到normal状态进行状态跳转。
    private void unGetChar(int ch) {
        getMeFirst = ch;
        column--;
    }

    //判断是否是字母。
    private boolean isAlpha(int ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    //判断是否是数字。
    private boolean isDigit(int ch) {
        return (ch >= '0' && ch <= '9');
    }

    //判断是否是空格、回车、制表符。
    private boolean isBlank(int ch) {
        return ((char) ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
    }

    private String showChar(int ch) {
        if (ch == '\n')
            return "\\n";
        else if (ch == '\r')
            return "\\r";
        else return "" + (char) ch;
    }

    public static String getSourceFileAsString(InputStream in) {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
            sb.append("\n");
        }
        return sb.toString();
    }
}
