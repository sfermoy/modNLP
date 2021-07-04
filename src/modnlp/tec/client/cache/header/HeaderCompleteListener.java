/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modnlp.tec.client.cache.header;

/**
 *
 * @author shane
 */
public interface HeaderCompleteListener {
    void notifyOfThreadComplete(final HeaderDownloadThread thread);
}
