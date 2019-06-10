/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modnlp.tec.client.cache.frequency;

/**
 *
 * @author shane
 */
public interface FqThreadCompleteListener {
    void notifyOfThreadComplete(final FqListDownloader thread);
}
