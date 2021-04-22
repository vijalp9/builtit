package com.crawlerv9.crawlers;

import com.crawlerv9.output.PageSummary;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class WebCrawler {

    private String baseUrl;
    private String domainKey;
    private Map<String, PageSummary> visitedPages;
    private Queue<String> pagesToVisit;

    private static final List<String> _BASE_URL_WEB_PREFIXES = Lists.newArrayList("https://", "http://", "www.");
    private static final Set<String> _IMAGE_SUFFIXES = Sets.newHashSet(".jpg", ".jpeg", ".gif", ".png", ".bmp");

    public WebCrawler(String url) {
        this.baseUrl = url;
        this.domainKey = extractBaseDomain(url);
        this.visitedPages = Maps.newHashMap();
        this.pagesToVisit = new LinkedList<String>();
    }

    public Collection<PageSummary> crawl() throws Exception {
        // Crawl the parent page and let it populate the queue of future pages to crawl
        this.crawlPage(this.baseUrl);

        // Keep crawling through the marked pages and add to the queue as we discover new pages
        while (!this.pagesToVisit.isEmpty()) {
            try {
                this.crawlPage(this.pagesToVisit.remove());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Return all the PageSummary objects, so we can generate a report from the calling class
        return this.visitedPages.values();
    }

    private void crawlPage(String url) {
        if (this.visitedPages.containsKey(url)) {
            // If we've crawled it already, move on...
            return;
        }

        System.out.println("Crawling URL: " + url);
        PageSummary pageSummary = new PageSummary(url);
        this.visitedPages.put(url, pageSummary);

        // Load the page:
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (Exception e) {
            System.err.println(" XXXX " + url + ": Error accessing.  " + e.getMessage());
            pageSummary.setDeadLink(true);
            return;
        }

        // Fetch all the links:
        Elements allLinks = document.select("a[href]");

        for (Element link : allLinks) {
            // Convert the link to be absolute and confirm it's an actual link
            String fullLink = link.attr("abs:href");
            if (this.isValidLink(fullLink)) {

                // If it's an image, just note the link...
                if (this.isImageLink(fullLink)) {
                    pageSummary.getImageLinks().add(fullLink);
                    continue;
                }

                // If it's an external link, just note the link...
                if (!this.isCrawlableDomain(fullLink)) {
                    pageSummary.getExternalLinks().add(fullLink);
                    continue;
                }

                // If it's the same domain, let's mark it for crawling [strip hashlink page markers from link]:
                String crawlableLink = this.removeHashLinkSuffix(fullLink);
                pageSummary.getSameDomainLinks().add(crawlableLink);
                this.pagesToVisit.add(crawlableLink);
            }
        }
    }

    // Filter out <a href="mailto:xxx@yyy.com"> / erroneous links
    private boolean isValidLink(String link) {
        return link != null && (link.startsWith("http://") || link.startsWith("https://"));
    }

    private boolean isImageLink(String link) {
        link = link.toLowerCase();
        for (String imageSuffix : _IMAGE_SUFFIXES) {
            if (link.endsWith(imageSuffix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCrawlableDomain(String link) {
        return extractBaseDomain(link).startsWith(this.domainKey);
    }

    // Returns the link without common web prefixes (http:// | https:// | www.)
    private String extractBaseDomain(String link) {
        for (String prefix : _BASE_URL_WEB_PREFIXES) {
            if (link.startsWith(prefix)) {
                link = link.substring(prefix.length());
            }
        }
        return link;
    }

    private String removeHashLinkSuffix(String link) {
        return link.contains("#") ? link.substring(0, link.indexOf("#")) : link;
    }

}
