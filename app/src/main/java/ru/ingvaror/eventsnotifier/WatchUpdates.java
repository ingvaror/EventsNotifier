package ru.ingvaror.eventsnotifier;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.reactive.function.client.WebClient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WatchUpdates extends Thread {

    private boolean stopped = false;
    private String URL;
    private List<String> imageUrls = new ArrayList<>();
    private PropertyChangeListener listener;

    public WatchUpdates(String URL) {
        this.URL = URL;
    }

    public void subscribeOnUpdateContent(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public String getURL() {
        return URL;
    }

    public void stopWatch() {
        this.stopped = true;
    }



    public void run() {
        while (!stopped) {
            try {
                final WebClient webClient = WebClient.create(URL);
                final String webpageContent = webClient.get()
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                // Fetch and process the webpage content
                final List<String> newImageUrls = extractImageUrls(webpageContent);
                if (imageUrls != null
                        && imageUrls.retainAll(newImageUrls)
                        && imageUrls.size() != newImageUrls.size()) {
                    listener.propertyChange(new PropertyChangeEvent(imageUrls, "images", imageUrls, newImageUrls));
                }
                imageUrls = newImageUrls;
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> extractImageUrls(String webpageContent) {
        final Document document = Jsoup.parse(webpageContent);

        return document.select("img").stream()
                .map(element ->
                {
                    String image = element.attr("src");
                    if (image != null && !image.isEmpty())
                        return element.attr("src");
                    else
                        return element.attr(":src");
                })
                .collect(Collectors.toList());
    }
}