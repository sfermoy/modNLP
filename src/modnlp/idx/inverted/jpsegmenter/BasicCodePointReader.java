/*
 **
 **  Feb. 1, 2009
 **
 **  The author disclaims copyright to this source code.
 **  In place of a legal notice, here is a blessing:
 **
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 **
 **                                         Stolen from SQLite :-)
 **  Any feedback is welcome.
 **  Kohei TAKETA <k-tak@void.in>
 **
 */
package modnlp.idx.inverted.jpsegmenter;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * Readerãã(c)ãããã¦ãã²ã¨ã¤ãã¤Unicodeã³ã¼ããã¤ã³ãã
 * åå¾ããããã®ãã³ã¬ã¼ã¿ã¯ã(c)ã¹ããµã­ã²ã¼ããã¢ãæ­£ããèªè­ããã<br>
 * 
 * ä¸æ­£ãªãµã­ã²ã¼ããã¢ã¯ã{@link #getAlternativeCodePoint()}ã§å¾ããã
 * ä»£æ¿ã³ã¼ããã¤ã³ãã«ç½®æãããã
 */
public class BasicCodePointReader implements CodePointReader
{
    /**
     * ä¸æ­£ãªãµã­ã²ã¼ããã¢ãç½®æããæå­ã®æ¢å®å¤ã
     */
    public static final int DEFAULT_ALTERNATION_CODEPOINT = 'ã';

    private PushbackReader reader = null;
    private long position = 0;
    private int alternationCodePoint = DEFAULT_ALTERNATION_CODEPOINT;
    private boolean eos = false;

    /**
     * ã³ã¼ããã¤ã³ãã¤ãã¬ã¼ã¿ãæ§ç¯ããã
     * 
     * @param sequence
     *            ã½ã¼ã¹ã¨ãªãcharã®ã·ã¼ã±ã³ã¹
     */
    public BasicCodePointReader(Reader reader)
    {
        this.reader = new PushbackReader(reader, 1);
    }

    public void setAlternationCodePoint(int cp)
    {
        this.alternationCodePoint = cp;
    }

    public int getAlternationCodePoint()
    {
        return alternationCodePoint;
    }

    public long getPosition()
    {
        return position;
    }

    public int read() throws IOException
    {
        int ci;
        char c, c2;

        if (eos) {
            return -1;
        }

        ci = reader.read();
        ++position;

        if (ci < 0) {
            // end of character stream
            eos = true;
            return -1;
        } else {
            c = (char)ci;
        }

        if (Character.isHighSurrogate(c)) {
            // æ¬¡ã®æå­ãæ¤æ»
            ci = reader.read();
            ++position;
            if (ci < 0) {
                // ã·ã¼ã±ã³ã¹ãhigh surrogateã§çµãã£ã¦ããã
                // ä»£æ¿æå­ãè¿ãã¨å±ã«ãEOSãã(c)ã°ãONã«ããã
                eos = true;
                --position;
                return alternationCodePoint;
            }

            c2 = (char)ci;
            if (Character.isLowSurrogate(c2)) {
                // ãµã­ã²ã¼ããã¢ãã³ã¼ããã¤ã³ãã«å¤æãã¦è¿ãã
                return Character.toCodePoint(c, c2);
            } else {
                // high surrogateã«ç¶ãcharããlow surrogateã§ãªãã
                // c2ãããã·ã¥ããã¯ãã¦ä»£æ¿æå­ãè¿ãã
                reader.unread(c2);
                --position;
                return alternationCodePoint;
            }
        } else if (Character.isLowSurrogate(c)) {
            // åç¬ã§å­å¨ããlow surrogateãçºè¦ã
            // ä»£æ¿æå­ãè¿ãã
            return alternationCodePoint;
        } else {
            // åºæ¬æå­ããã®ã¾ã¾è¿ãã
            return c;
        }
    }
    
    public void reset()
    {
        position = 0;
        eos = false;
    }
}
