package edu.touro.cs.mcon364;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WebScraping {

    static Queue<String> queueLink = new ConcurrentLinkedQueue<>();
    static Set<String> visitedLinks = Collections.synchronizedSet(new HashSet<>());
    static Set<String> emails = Collections.synchronizedSet(new HashSet<>());


    public static void main(String[] args) throws Exception {
        Logger.getLogger(WebScraping.class.getName()).log(Level.INFO, "*** Web Scraping With Multi threading***");
        queueLink.add("https://www.touro.edu/");
        ExecutorService es = Executors.newFixedThreadPool(10);
        while ( emails.size() < 10 ){
            es.submit(new Threading());
            Thread.sleep(1);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdown();

        try {
            if (!es.awaitTermination(1_000, TimeUnit.MILLISECONDS))
                es.shutdownNow();
        } catch (InterruptedException e) {
            es.shutdownNow();
        }
        Logger.getLogger(WebScraping.class.getName()).log(Level.INFO, "Heading TO DB");
        DataBaseConnection.ConnectingDB(emails);
    }

}


class Threading implements Runnable {

    @Override
    public void run() {
        Document doc = null;
        try {
            doc = Jsoup.connect(WebScraping.queueLink.poll()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.getLogger(WebScraping.class.getName()).log(Level.INFO, "EMAILS");
        Elements newsHeadlines = doc.select("a[href]");
        for (Element headline : newsHeadlines) {
            String href = headline.attr("href");
            Logger.getLogger(WebScraping.class.getName()).log(Level.INFO, "Regex");
            if (href.matches("^mailto:[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                System.out.println(href);
                WebScraping.emails.add(href);
            } else {
                if (WebScraping.visitedLinks.contains(href)) {
                    continue;
                } else {
                    WebScraping.queueLink.add(href);
                    WebScraping.visitedLinks.add(href);
                }
            }
        }
    }
}

/**
 * PSEUDOCODE:
 *
 *
 * Create an empty queue and 2 synchronized hashsets
 *
 * Add the first URL "https://www.touro.edu/" to the queue
 *
 * Make a loop that continues until the number of emails found reaches a certain amount
 * In the loop, get the next URL from the queue and search the page for emails using the regex formatting
 * For each email we get, check if it is in a valid format and add it to the email set
 * Keep track of the links that have already been searched by adding them to a set
 * and check if we already went to that link
 *
 * Once we get the emails and add it to our set, connect to the DB class and insert them into
 *  my DB
 *
 * In the DB class, bulk save all the Emails to the DB.
 *
 * (Logging statements implemented throughout the code)
 *
 */
