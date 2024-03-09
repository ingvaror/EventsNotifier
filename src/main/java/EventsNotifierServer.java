import java.util.List;

public class EventsNotifierServer {

    public static void main(String[] args) {
       WatchUpdates watcher = new WatchUpdates("https://tickets.spartak.ru/tickets/");
       watcher.subscribeOnUpdateContent(evt -> {
           List<String> images = (List<String>) evt.getNewValue();
           // Display image URLs
           System.out.println("Image URLs:");
           images.forEach(System.out::println);
       });
        watcher.run();
    }
}
