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

/**
 * 一つずつUnicodeコードポイントを取得するためのク�(c)ス。
 * サロゲートペアを正しく認識する。
 */
public interface CodePointReader
{
    /**
     * 不正なサロゲートペアを置換するための代替文字をセットする。
     * このメソッドを呼び出さない場合の既定値は、
     * 「{@value #DEFAULT_ALTERNATIVE_CODEPOINT}」である。
     * 
     * @param cp
     *            代替文字のコードポイント
     */
    public abstract void setAlternationCodePoint(int cp);

    /**
     * 不正なサロゲートペアを置換するための代替文字を取得する。
     * 
     * @return 代替文字のコードポイント
     */
    public abstract int getAlternationCodePoint();

    /**
     * キャ�(c)クタストリーム中の現在の位置を返す。
     * コードポイント単位でなくchar単位で数えるので、
     * サロゲートペアが出現すると、位置は2大きくなる。
     * 
     * @return キャ�(c)クタストリーム中の位置。
     */
    public abstract long getPosition();

    /**
     * 次のコードポイントを取得する。
     * 
     * @return Unicodeコードポイント。
     */
    public abstract int read() throws IOException;

    /**
     * 状態をリセットする。
     */
    public abstract void reset();
}
