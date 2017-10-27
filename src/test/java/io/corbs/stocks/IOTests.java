package io.corbs.stocks;

import org.junit.Test;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.util.concurrent.Future;

public class IOTests {

    public static final String TICKERS_CSV = "src/test/resources/tickers.csv";
    public static final int KB = 1024;
    public static final int MB = 1024 * KB;

    /**
     * The traditional IO model in Java is blocking IO.
     *
     * Main difference between blocking IO and non-blocking IO in Java
     * is blocking IO is Stream based. When a thread reads or writes
     * data that thread is blocked until the operation is complete.
     */
    @Test
    public void blockingIO() throws IOException {
        InputStream input = new FileInputStream(new File("src/test/resources/tickers.csv"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        // Processing progresses only when there is data to read
        // if this was a network stream with idle time then we'd still wait
        // for data or a timeout event.  In any case the thread conducting IO
        // is blocked until data is fully read or fully written.
        String line;
        int lineNumber = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("blocking-io-test");
        while((line = reader.readLine()) != null) {
            System.out.println("["  + ++lineNumber + "] " + line);
        }
        stopWatch.stop();
        System.out.println("--------------------------------------------------");
        System.out.println("blocking-io-test");
        System.out.println("blocked duration (ms): " + stopWatch.getTotalTimeMillis());
        System.out.println("number of lines read: " + lineNumber);
        System.out.println("--------------------------------------------------");

        reader.close();
    }

    @Test
    public void nonBlockingIO() throws IOException {

        AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(TICKERS_CSV));
        ByteBuffer buffer = ByteBuffer.allocate(MB);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("non-blocking-io-test");
        Future<Integer> result = channel.read(buffer, 0);
        long blockedDuration = stopWatch.getTotalTimeMillis();
        while(!result.isDone()) {
            // hold up this thread while async IO operation completes
        }
        stopWatch.stop();
        System.out.println("--------------------------------------------------");
        System.out.println("non-blocking-io-test");
        System.out.println("blocked duration (ms): " + blockedDuration);
        System.out.println("total read time (ms): " + stopWatch.getTotalTimeMillis());
        System.out.println("--------------------------------------------------");

        String content = new String(buffer.array());
        System.out.println(content);
        channel.close();
    }

}
