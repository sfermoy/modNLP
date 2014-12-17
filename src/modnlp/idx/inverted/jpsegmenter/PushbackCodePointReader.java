/*
 **
 **  Feb. 17, 2009
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

public class PushbackCodePointReader implements CodePointReader
{
    /**
     * ベースとなるCodePointReader
     */
    private CodePointReader reader = null;
    /**
     * ストリームに戻された各コードポイントを保持するスタック
     */
    private int codepoints[] = null;
    /**
     * ストリームに戻された各コードポイントの幅を保持するスタック
     */
    private int lengths[] = null;
    /**
     * readerのpositionからのoffset
     */
    private int offset = 0;
    /**
     * スタックトップの位置
     */
    private int stackpos = -1;

    public PushbackCodePointReader(CodePointReader reader, int size)
    {
        this.reader = reader;
        this.codepoints = new int[size];
        this.lengths = new int[size];
    }

    public void setAlternationCodePoint(int cp)
    {
        reader.setAlternationCodePoint(cp);
    }

    public int getAlternationCodePoint()
    {
        return reader.getAlternationCodePoint();
    }

    public long getPosition()
    {
        return reader.getPosition() - offset;
    }
    
    public int getStackSize()
    {
        return codepoints.length;
    }

    public int read() throws IOException
    {
        if (stackpos >= 0) {
            offset -= lengths[stackpos];
            return codepoints[stackpos--];
        } else {
            return reader.read();
        }
    }
    
    public void reset()
    {
        reader.reset();
        stackpos = -1;
    }

    /**
     * コードポイントを一つストリームに戻す。
     * 
     * @param cp
     *            プッシュバックするコードポイント
     * @param length
     *            cpの幅をchar数単位で指定
     */
    public void unread(int cp, int length) throws IOException
    {
        if (stackpos + 1 >= codepoints.length) {
            throw new IOException("Stack overflow.");
        }

        ++stackpos;
        codepoints[stackpos] = cp;
        lengths[stackpos] = length;
        offset += length;
    }
}
