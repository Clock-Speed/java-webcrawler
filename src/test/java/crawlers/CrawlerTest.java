package crawlers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CrawlerTest {

  public static void crawlerTest(Crawler crawler) {
    Set<String> results = crawler.crawlFrom("https://crawler-test.com/links/max_external_links");
    Set<String> expected = Set.of(
        "https://crawler-test.com/links/max_external_links",
        "https://crawler-test.com/",
        "http://bbc.co.uk",
        "http://binarycat.co.uk",
        "http://bukowydwor.pl",
        "http://deepcrawl.co.uk",
        "http://glenaholmmotopo.pl",
        "http://google.com",
        "http://lolcats.com",
        "http://momoweb.co.uk",
        "http://robotto.org",
        "http://robotto.org/broken",
        "http://semetrical.com",
        "http://somerandomdomain.com",
        "http://wyszehrad.pl"
    );

    assertTrue(results.size() == 10 && expected.containsAll(results));
  }
}
