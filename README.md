# java-web crawler
java-webcrawler contains two small web crawlers written in Java. The first crawls sequentially and the other concurrently.
The concurrent crawler is significantly faster than the sequential one with speed-ups of over 100x.

## Usage
```shell
java -jar crawler.jar {'-c'/'-s'} [Start URL] [Number of URLs to find]
```
Using the '-c' option uses the concurrent crawler while the '-s'  option uses the sequential one.
