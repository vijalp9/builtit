package com.crawlerv9.output;

import com.google.common.collect.Sets;

import java.util.Set;

public class PageSummary {

    private String pageUrl;
    private boolean deadLink;

    private Set<String> imageLinks;
    private Set<String> sameDomainLinks;
    private Set<String> externalLinks;

    public PageSummary(String pageUrl) {
        this.pageUrl = pageUrl;
        this.deadLink = false;
        this.imageLinks = Sets.newHashSet();
        this.sameDomainLinks = Sets.newHashSet();
        this.externalLinks = Sets.newHashSet();
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public boolean isDeadLink() {
        return deadLink;
    }

    public void setDeadLink(boolean deadLink) {
        this.deadLink = deadLink;
    }

    public Set<String> getImageLinks() {
        return imageLinks;
    }

    public Set<String> getSameDomainLinks() {
        return sameDomainLinks;
    }

    public Set<String> getExternalLinks() {
        return externalLinks;
    }

    @Override
    public String toString() {
        return "PageSummary{" +
                "pageUrl='" + pageUrl + '\'' +
                ", deadLink=" + deadLink +
                ", linkedImages=" + imageLinks +
                ", sameDomainLinks=" + sameDomainLinks +
                ", externalLinks=" + externalLinks +
                '}';
    }
}
