package crawlers;

import utils.RobotsChecker;
import utils.Scraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A crawler which crawls webpages concurrently.
 */
public class ConcurrentCrawler implements Crawler {

  /**
   * Limit for the number of URL to find.
   */
  private final int limit;

  /**
   * The RobotChecker for this crawler.
   */
  private final RobotsChecker robotsChecker;

  /**
   * Constructs a new ConcurrentCrawler.
   *
   * @param limit The number of URLs to find.
   * @param name The name of this crawler, used as the agent name of the robotsChecker.
   */
  public ConcurrentCrawler(int limit, String name) {
    this.limit = limit;
    this.robotsChecker = new RobotsChecker(name);

  }

  /**
   * Crawls the web from the given URL returning a Set
   * containing the urls found.
   *
   * @param baseUrl The url to start crawling from.
   * @return The set of urls found.
   */
  public Set<String> crawlFrom(String baseUrl) {
    /*
     * This function acts as the Master in a Master/Worker pattern.
     */

    /* The set of all urls found. */
    final Set<String> urls = new HashSet<>();

    /*
     * Shared resource between master and workers. The master will take a
     * list of URLs from the queue and submit a worker to the executor
     * service for each url in the list.
     * The worker then places a list of the urls it finds at the given urls
     * webpage onto the queue.
     * The queue has to be blocking so that the master waits for the worker
     * to place something into the queue.
     */
    final BlockingQueue<List<String>> pending = new LinkedBlockingDeque<>();

    /*
     * ExecutorService to run worker threads. A WorkStealingPool is used to allow
     * for work stealing between idle threads.
     */
    final ExecutorService executorService = Executors.newWorkStealingPool();

    pending.add(Collections.singletonList(baseUrl));

    /* Represents number of workers executing. We terminate the loop if n becomes 0*/
    int n = 1;

    /* Indicates whether to break out of the loop. */
    boolean toBreak = false;
    try {
      do {

        /*
         * Blocking operation on the pending queue.
         * Waits for a nextUrl to become present.
         * */
        List<String> nextUrls = pending.take();

        for (String urlString : nextUrls) {
          if (!urls.contains(urlString)) {

            /* If url is malformed do not process further. */
            URL url;
            try {
              url = new URL(urlString);
            } catch (MalformedURLException e) {
              continue;
            }

            /*
             * Add url to the set. If the size of the set is equal
             * to the limit the required work is done and we can
             * break out.
             */
            urls.add(urlString);
            if (urls.size() == limit) {
              toBreak = true;
              break;
            }

            /* Create new worker task and submit to service. */
            n += 1;
            executorService.submit(() -> crawlUrl(url, pending));

          }
        }

        n--;
        if (toBreak) {
          break;
        }
      } while (n > 0);

      executorService.shutdownNow();
      return urls;

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Crawls the given webpage and adds all of its links to the pending
   * queue.
   *
   * @param url The url to extract urls from.
   * @param pending The blocking queue to put the urls onto.
   */
  private void crawlUrl(URL url, BlockingQueue<List<String>> pending) {
    try {
      /*
       * Check if the url obeys the domains robots.txt.
       * If it doesn't add an empty list to the pending queue as if there
       * was no urls on that page (needed to ensure the count of workers
       * in the master is managed correctly).
       */
      if (robotsChecker.obeysRobotTxt(url)) {
        /* Get urls from that webpage and place them onto the pending queue */
        List<String> fetchedUrls = Scraper.getLinks(url);
        pending.put(fetchedUrls);
      } else {
        pending.put(Collections.emptyList());
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    ConcurrentCrawler crawler = new ConcurrentCrawler(1000, "ConcurrentCrawler");
    Set<String> res = crawler.crawlFrom("https://news.ycombinator.com/");
    System.out.println(res);
    System.out.println(res.size());
  }
}
