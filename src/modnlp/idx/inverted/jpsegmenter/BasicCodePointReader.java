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
 * Readerを�(c)ップして、ひとつずつUnicodeコードポイントを
 * 取得するためのデコレータク�(c)ス。サロゲートペアを正しく認識する。<br>
 * 
 * 不正なサロゲートペアは、{@link #getAlternativeCodePoint()}で得られる
 * 代替コードポイントに置換される。
 */
public class BasicCodePointReader implements CodePointReader
{
    /**
     * 不正なサロゲートペアを置換する文字の既定値。
     */
    public static final int DEFAULT_ALTERNATION_CODEPOINT = '〓';

    private PushbackReader reader = null;
    private long position = 0;
    private int alternationCodePoint = DEFAULT_ALTERNATION_CODEPOINT;
    private boolean eos = false;

    /**
     * コードポイントイテレータを構築する。
     * 
     * @param sequence
     *            ソースとなるcharのシーケンス
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
            // 次の文字を検査
            ci = reader.read();
            ++position;
            if (ci < 0) {
                // シーケンスがhigh surrogateで終わっている。
                // 代替文字を返すと共に、EOSフ�(c)グをONにする。
                eos = true;
                --position;
                return alternationCodePoint;
            }

            c2 = (char)ci;
            if (Character.isLowSurrogate(c2)) {
                // サロゲートペアをコードポイントに変換して返す。
                return Character.toCodePoint(c, c2);
            } else {
                // high surrogateに続くcharが、low surrogateでない。
                // c2をプッシュバックして代替文字を返す。
                reader.unread(c2);
                --position;
                return alternationCodePoint;
            }
        } else if (Character.isLowSurrogate(c)) {
            // 単独で存在するlow surrogateを発見。
            // 代替文字を返す。
            return alternationCodePoint;
        } else {
            // 基本文字。そのまま返す。
            return c;
        }
    }
    
    public void reset()
    {
        position = 0;
        eos = false;
    }
}
