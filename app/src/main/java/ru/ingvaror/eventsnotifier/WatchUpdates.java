package ru.ingvaror.eventsnotifier;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WatchUpdates extends Thread {

    public static String NOTIFY_IMAGES_UPDATE = "images";
    private boolean stopped = false;
    private String url;
    private List<String> imageUrls = new ArrayList<>();
    private PropertyChangeListener listener;

    public WatchUpdates(String url) {
        this.url = url;
    }

    public void subscribeOnUpdateContent(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public String getURL() {
        return url;
    }

    public void stopWatch() {
        this.stopped = true;
    }

    public void run() {
        while (!stopped) {
            try {
                StringBuilder webpageContent = new StringBuilder();
                URL url_ = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) url_.openConnection();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        webpageContent.append(line).append("\n");
                    }
                } finally {
                    urlConnection.disconnect();
                }
                // Fetch and process the webpage content
                final List<String> newImageUrls = extractImageUrls(webpageContent.toString());
                if (imageUrls != null
                        && imageUrls.retainAll(newImageUrls)
                        && imageUrls.size() != newImageUrls.size()) {
                    listener.propertyChange(new PropertyChangeEvent(imageUrls, NOTIFY_IMAGES_UPDATE, imageUrls, newImageUrls));
                }
                imageUrls = newImageUrls;
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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