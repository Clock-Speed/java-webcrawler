package crawlers;

import java.util.Set;

public interface Crawler {

  Set<String> crawlFrom(String baseUrl);
}
