package ch.woerz.virtualthreads;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

public class VirtualThreads {

    @SneakyThrows
    @Test
    void test() {
        List<Thread> threadList = IntStream.range(0, 1_000_000)
                .mapToObj(i -> Thread.ofVirtual().unstarted(() -> {
                    saveSleep(20);
                    if(i == 0) {
                        System.out.println(Thread.currentThread());
                    }
                    saveSleep(20);
                    if(i == 0) {
                        System.out.println(Thread.currentThread());
                    }
                    saveSleep(20);
                    if(i == 0) {
                        System.out.println(Thread.currentThread());
                    }
                })).toList();
        threadList.forEach(Thread::start);
        for (Thread thread : threadList) {
            thread.join();
        }
    }

    @SneakyThrows
    void saveSleep(int ms) {
        Thread.sleep(ms);
    }

}
