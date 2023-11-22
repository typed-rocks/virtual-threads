package ch.woerz.virtualthreads;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
public class FakeHttpService {

    private final List<Speaker> speakers = IntStream.range(0, 10)
            .mapToObj(i -> new Speaker("Speaker " + i))
            .toList();

    @SneakyThrows
    public List<Speaker> retrieveSpeakersBlocking() {
        Thread.sleep(1000);
        return speakers;
    }

    public CompletableFuture<List<Speaker>> retrieveSpeakersAsync() {
        return CompletableFuture.supplyAsync(() -> speakers,
                CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS));
    }

    @SneakyThrows
    public Talk retrieveTalk(Speaker speaker) {
        Thread.sleep(500);
        return new Talk("Talk for speaker " + speaker.name());
    }

    public CompletableFuture<Talk> retrieveTalkAsync(Speaker speaker) {
        return CompletableFuture.supplyAsync(() ->
                new Talk("Async talk from " + speaker.name()),
                CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS));

    }
}
