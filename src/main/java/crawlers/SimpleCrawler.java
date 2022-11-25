package crawlers;

import utils.RobotsChecker;
import utils.Scraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * A simple crawler which crawls webpages sequentially.
 */
public class SimpleCrawler implements Crawler {
  /**
   * Limit for the number of URL to find.
   */
  private final int limit;

  /**
   * The RobotChecker for this crawler.
   */
  private final RobotsChecker robotsChecker;

  /**
   * Constructs a SimpleCrawler.
   *
   * @param limit The number of URLs to find.
   */
  public SimpleCrawler(int limit, String name) {
    this.limit = limit;
    this.robotsChecker = new RobotsChecker(name);
  }

  /**
   * Crawls the world wide web starting from the given url.
   *
   * @param baseUrl URL string to start crawling from.
   * @return Returns a Set of size <= limit containing the URLs it finds.
   */
  public Set<String> crawlFrom(String baseUrl) {

    /* Set of found URLs */
    Set<String> urls = new HashSet<>();

    /* Queue of URLs to process */
    Queue<String> processQueue = new LinkedList<>();
    processQueue.add(baseUrl);

    /* Crawl urls in the processQueue until the limit is reached */
    while (!processQueue.isEmpty() && urls.size() < limit) {
      String urlToProcessString = processQueue.poll();

      /* If url already crawled, continue. */
      if (urls.contains(urlToProcessString)) {
        continue;
      }

      /* If url is malformed do not process further. */
      URL urlToProcess;
      try {
        urlToProcess = new URL(urlToProcessString);
      } catch (MalformedURLException e) {
        continue;
      }

      urls.add(urlToProcessString);

      /* If permitted to crawl url, add all links on its webpage to the process queue. */
      if (robotsChecker.obeysRobotTxt(urlToProcess)) {
        processQueue.addAll(Scraper.getLinks(urlToProcess));
      }
    }
    return urls;
  }
}
