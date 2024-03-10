package ru.ingvaror.eventsnotifier;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void watcherCheck() throws InterruptedException {
        WatchUpdates watcher = new WatchUpdates("https://tickets.spartak.ru/tickets/");
        watcher.start();
        Thread.sleep(3000);
        watcher.stopWatch();
        watcher.join();
    }
}