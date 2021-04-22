### Java-based web crawler - buildit

## How to build and run your solution (including any required installations)

### Build
```bash
git clone https://github.com/vijalp9/builtit.git
cd buildit
mvn clean install
```

### Run
```bash
mvn exec:java -Dexec.mainClass="com.crawlerv9.Crawl"
```
A default webpage of http://www.wiprodigital.com will be used if no argument is supplied.  

To specify a webpage, use:
```bash
mvn exec:java -Dexec.mainClass="com.crawlerv9.Crawl" -Dexec.args="http://algov9.com"
```

### Test
There's also an embedded Jetty server as part of the testing package which spins up a web server with three light dummy pages for the Crawler to iterate through.
To see the corresponding results/print statements, execute via:
```bash
mvn test
```

## Reasoning and describe any trade offs
Application uses [jsoup](https://jsoup.org/) for downloading and parsing the webpages to extract out the links.

Simple recursion to populate a queue of found links on each page, then iterate through each of the links looking for more new links until the queue is eventually emptied.

Once the queue is empty, print out the data that we found for each visited link for review.

Sample single page output:
```bash
URL Crawled: http://192.168.1.178:58048/
Linked Images: 
 -> http://192.168.1.178:58048/something.jpg
External Links [NOT CRAWLING]: 
 -> http://GOOGLE.com
 -> http://TWITTER.com
Same Domain Links [CRAWLABLE]: 
 -> http://192.168.1.178:58048/Page3.html
 -> http://192.168.1.178:58048/Page4.html
 -> http://192.168.1.178:58048/Page2.html
```



## Explanation of what could be done with more time

- Implement a wait-between-requests feature to not flood the web servers while crawling
- Set proper user-agent tags on requests
- Use max-depth when recursing over larger sites
- Consider making the crawler multi-threaded to process individual sites faster
- More thorough test cases / validation
- Logging
