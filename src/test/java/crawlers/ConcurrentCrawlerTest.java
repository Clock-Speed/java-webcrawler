package crawlers;

import org.junit.jupiter.api.Test;

class ConcurrentCrawlerTest {

  @Test
  public void testConcurrentCrawler() {
    Crawler crawler = new ConcurrentCrawler(10, "ConcurrentCrawler");
    CrawlerTest.crawlerTest(crawler);
  }
}