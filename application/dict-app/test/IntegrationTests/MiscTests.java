package IntegrationTests;

import logics.WordLogic;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import utilities.LogPrint;

import java.util.Set;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class MiscTests {

    LogPrint log;
    WordLogic wordLogic;

    @Before
    public void setup() {

        log = new LogPrint(MiscTests.class);
        wordLogic = WordLogic.factory();
    }

    @Test @Ignore
    public void test() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333");
            assertTrue(browser.pageSource().contains("Your new application is ready."));
        });
    }

    @Test @Ignore
    public void searchWordsByPrefixPerformanceTune() throws Exception{

        int i = 0;

        while (i < 10) {

            long current_time = System.nanoTime();

            String prefix = "ত";

            Set<String> results = null;// play.api.cache.Cache.get(prefix, );

            if(results != null) {
                log.info("Found in memory");
            } else {
                log.info("Not found in memory");
                results = wordLogic.searchWordsBySpelling(prefix, 10);
                //if(i == 4)
                //play.api.cache.Cache.set(prefix, results, 20000, );
            }

            long total_time = System.nanoTime() - current_time;

            log.info("Words for prefix: \"" + prefix + "\":" + results.toString());
            log.info("[Total Time:" + (total_time / 1000000.0) + "ms]");
            i++;
        }
    }

}