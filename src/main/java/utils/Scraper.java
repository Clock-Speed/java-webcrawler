package utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Static class to provide method getLinks(URL baseUrl) to get a list of strings from given URL.
 */
public final class Scraper {

  private Scraper() {}

  /**
   * Return a list of links available on the webpage at given URL.
   *
   * @param baseUrl The url for the page to extract links from.
   * @return Returns the list of string extracted from the page at baseUrl.
   */
  public static List<String> getLinks(URL baseUrl) {
    Connection conn;
    Document doc;

    try {
      conn = Jsoup.connect(baseUrl.toString());
      doc = conn.get();
    } catch (IOException | IllegalArgumentException e) {
      return Collections.emptyList();
    }

    Elements linkElements = doc.select("a[href]");

    List<String> links = new ArrayList<>();

    for (Element elem : linkElements) {
      String url = elem.attr("href");
      if (url.isEmpty()) {
        continue;
      }
      if (url.charAt(0) == '/') {
        links.add(baseUrl.getProtocol() + "://" + baseUrl.getHost() + url);
      } else {
        links.add(elem.attr("href"));
      }
    }
    return links;
  }
}
