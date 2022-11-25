package utils;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to check whether a given url can be crawled
 * according to the robots.txt file of the host.
 */
public class RobotsChecker {
  /**
   * The name of the crawler, used to see which robots.txt directives apply.
   */
  private final String userAgent;

  /**
   * Cache mapping from host url strings to the robots.txt rules that apply to that domain.
   */
  private final Map<String, BaseRobotRules> hostToRules = new HashMap<>();


  /**
   * Constructs a RobotChecker.
   *
   * @param userAgent Name of the crawler, used to see which directives apply.
   */
  public RobotsChecker(String userAgent) {
    this.userAgent = userAgent;
  }

  /**
   * Queries whether a given URL can be crawled.
   *
   * @param url URL to query against robots.txt of host.
   * @return Returns whether the given URL is allowed to be crawled.
   */
  public synchronized boolean obeysRobotTxt(URL url) {
    String hostUrlString = url.getProtocol() + "://" + url.getHost();

    BaseRobotRules rules = hostToRules.get(hostUrlString);

    /* If the host url is already in the cache, retrieve its associated rule
     * and check whether the input follows them.
     */
    if (rules != null) {
      return rules.isAllowed(url.toString());
    }


    /*
     * Try to create new URL object from the host url concatenated with "/robots.txt".
     * Should always succeed as the host url should already be a valid url.
     */

    try {
      URL robotsUrl = new URL(hostUrlString + "/robots.txt");
      HttpURLConnection connection = (HttpURLConnection) robotsUrl.openConnection();

      int responseCode = connection.getResponseCode();

      /*
       * If successfully able to open connection to robots.txt file,
       * parse the file to get the rules for that domain.
       */
      if (responseCode >= 200 && responseCode < 300) {
        SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
        rules = parser.parseContent(
            robotsUrl.toString(),
            connection.getInputStream().readAllBytes(),
            "text/plain",
            userAgent
        );
        /* If not successful due to client error assume no crawl restrictions. */
      } else if (responseCode >= 400 && responseCode < 500) {
        rules = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);
        /* If not successful due to any other error assume not able to crawl entire domain. */
      } else {
        rules = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_NONE);
      }

      /* Add rule to the cache. */
      hostToRules.put(hostUrlString, rules);
      /* Return whether given url can be crawled under the rules.*/
      return rules.isAllowed(url.toString());

      /*
       * Try to create new URL object from the host url concatenated with "/robots.txt".
       * Should always succeed as the host url should already be a valid url.
       */
    } catch (MalformedURLException e) {
      throw new RuntimeException(
          "UNREACHABLE: appending \"/robots.txt\" to a valid url resulted in invalid url"
      );

      /* Special case when URL not using HTTP. Cannot crawl non http urls. */
    } catch (ClassCastException e) {
      return false;

      /* For all IO exceptions assume webpage cannot be crawled. */
    } catch (IOException e) {
      rules = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_NONE);
      hostToRules.put(hostUrlString, rules);
      return rules.isAllowed(url.toString());
    }
  }
}
