import crawlers.ConcurrentCrawler;
import crawlers.Crawler;
import crawlers.SimpleCrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class Main {

  public static void main(String[] args) {
    final String ERROR_MSG =
        "USAGE: "
            + "java -jar crawler.jar {'-c'/'-s'} [Start URL] [Number of URLs to find] \n"
            + "\tUse options '-c' and '-s' to run the concurrent and sequential crawlers"
            + " respectively.";

    if (args.length != 3) {
      System.out.println(ERROR_MSG);
      return;
    }

    String startUrl = args[1];
    try {
      new URL(startUrl);
    } catch (MalformedURLException e) {
      System.out.println(startUrl + " is not a valid URL.");
      return;
    }

    int limit;
    try {
      limit = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      System.out.println("Limit needs to be a valid Integer.");
      return;
    }

    Crawler crawler;

    if (args[0].equals("-c")) {
      crawler = new ConcurrentCrawler(limit, "ConcurrentCrawler");
    } else if (args[0].equals("-s")) {
      crawler = new SimpleCrawler(limit, "SequentialCrawler");
    } else {
      System.out.println(ERROR_MSG);
      return;
    }

    Set<String> results = crawler.crawlFrom(startUrl);
    results.forEach(System.out::println);
  }
}
