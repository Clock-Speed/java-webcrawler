package utils;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ScraperTest {

  @Test
  void testNonExistentUrlReturnsEmpty() throws MalformedURLException {
    assertTrue(Scraper.getLinks((new URL("https://www.doc.ic.ac.uk/apd221/doesnt/exist"))).isEmpty());
  }

  @Test
  void testValidUrl() throws MalformedURLException {
    List<String> results = Scraper.getLinks(new URL("https://crawler-test.com/links/page_with_external_links"));

    List<String> expected = List.of(
        "https://crawler-test.com/",
        "http://robotto.org",
        "http://semetrical.com",
        "http://deepcrawl.co.uk",
        "http://robotto.org",
        "http://robotto.org");

    assertTrue(
        results.size() == expected.size()
            && results.containsAll(expected)
            && expected.containsAll(results));
  }

}