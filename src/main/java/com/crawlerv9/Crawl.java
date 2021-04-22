package com.crawlerv9;

import com.crawlerv9.crawlers.WebCrawler;
import com.crawlerv9.output.PageSummary;

import java.util.Collection;

public class Crawl {

    /*
        Idea for this crawler is simple.
            -> We're fed a base URL (e.g. http://wiprodigital.com/)
            -> We load the URL and extract every link available [a href=xxxx] on the page
                -> If it points to a static resource [*.jp[e]g|*.png|*.gif|*.bmp], just make note of it
                -> If it points to an external webpage that is NOT in the base domain, just make note of it
                -> If it points to a webpage within the base domain,
                    -> Validate that we haven't queued/visited the link before, then run through these same steps again...
            -> Print out the summary of pages crawled with data mentioned above...
     */
    public static void main(String[] args) throws Exception {
        String crawlUrl = "http://www.wiprodigital.com";
        if (args != null || args.length == 1){
            crawlUrl = args[0];
        }
        System.out.println("Starting crawl of " + crawlUrl);

        Collection<PageSummary> pageSummaries = new WebCrawler(crawlUrl).crawl();

        System.out.println("Printing summary.  Total of " + pageSummaries.size() + " pages.");

        for (PageSummary ps : pageSummaries) {
            System.out.println("--------------------------------------------------------------------------");
            if (ps.isDeadLink()) {
                System.out.println("** ERROR ACCESSING: " + ps.getPageUrl());
                continue;
            }

            System.out.println("URL Crawled: " + ps.getPageUrl());
            if (ps.getImageLinks().size() > 0) {
                System.out.println("Linked Images: ");
                for (String imageLink : ps.getImageLinks()) {
                    System.out.println(" -> " + imageLink);
                }
            }

            if (ps.getExternalLinks().size() > 0) {
                System.out.println("External Links [NOT CRAWLING]: ");
                for (String imageLink : ps.getExternalLinks()) {
                    System.out.println(" -> " + imageLink);
                }
            }

            if (ps.getSameDomainLinks().size() > 0) {
                System.out.println("Same Domain Links [CRAWLABLE]: ");
                for (String imageLink : ps.getSameDomainLinks()) {
                    System.out.println(" -> " + imageLink);
                }
            }
            System.out.println("--------------------------------------------------------------------------");
        }
    }

}
