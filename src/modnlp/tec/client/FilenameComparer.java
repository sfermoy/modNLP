
package modnlp.tec.client;
import java.util.StringTokenizer;
/**
 *  Compare based on filenames
 *
 * 
 * @author  Shane Sheehan
*/
public class FilenameComparer extends Comparator{
    public FilenameComparer (){
    super();
  }
  public FilenameComparer (int ctx, int half){
    super(ctx,half);
  }

  public FilenameComparer (int ctx, int half, boolean pton){
    super(ctx,half,pton);
  }

  // NB.: This could be made more effifient (space and timewise) if
  // instead of using getLeftSortArray() we worked directly with
  // ConcordanceObject's TokenIndex, scanning substrings of
  // ConcordanceObject.getLeftContext() as needed.
  public int compare(Object o1, Object o2) {
    
    ConcordanceObject coa = (ConcordanceObject) o1;
    ConcordanceObject cob = (ConcordanceObject) o2;

    String saa = coa.filename;
    String sab = cob.filename;

    return saa.compareTo(sab);

  }
    
}


 