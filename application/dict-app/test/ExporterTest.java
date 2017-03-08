import exporter.SamsadExporter;
import logics.WordLogic;
import objects.DictionaryWord;
import org.junit.Ignore;
import org.junit.Test;
import play.test.WithServer;
import utilities.LogPrint;
import java.util.Collection;

/**
 *
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
public class ExporterTest extends WithServer {

    LogPrint log = new LogPrint(ExporterTest.class);
    WordLogic wordLogic = WordLogic.factory();

    @Test
    public void createDictionaryFromSamsad() throws Exception {

        Collection<DictionaryWord> words = new SamsadExporter().getDictiionary();

        int total = 0;
        for(DictionaryWord word: words) {

            /*
            if(total == 0) break;

            if( "YES".equalsIgnoreCase(word.retrieveExtraMetaValueForKey("SIMPLE_SPELLING"))
             && "YES".equalsIgnoreCase(word.retrieveExtraMetaValueForKey("SIMPLE_MEANING"))
             && "YES".equalsIgnoreCase(word.retrieveExtraMetaValueForKey("UNDERSTANDABLE_TYPE")) ) {

                log.info("Next word: \n" + word.toString());

                total++;
            }
            */
            //wordLogic.saveDictionaryWord(word);
            total++;
        }

        log.info("Total words: " + total);
    }

}
