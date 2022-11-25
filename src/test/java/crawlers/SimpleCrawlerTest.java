package crawlers;

import org.junit.jupiter.api.Test;

class SimpleCrawlerTest {

  @Test
  public void testSimpleCrawler() {
    Crawler crawler = new SimpleCrawler(10, "SimpleCrawler");
    CrawlerTest.crawlerTest(crawler);
  }

}