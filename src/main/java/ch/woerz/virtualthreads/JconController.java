package ch.woerz.virtualthreads;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;

import static java.util.concurrent.StructuredTaskScope.*;

@RestController
@RequestMapping("/api")
public class JconController {

    @Autowired
    private FakeHttpService fakeHttpService;

    @GetMapping("/speakers")
    List<Speaker> getSpeakers() {
        return fakeHttpService.retrieveSpeakersBlocking();
    }

    @SneakyThrows
    @GetMapping("/talks")
    Collection<Talk> getTalks() {
        // 1s
        var speakers = fakeHttpService.retrieveSpeakersBlocking();

        try (var scope = new TalkScope()) {
            speakers.forEach(speaker -> scope.fork(() -> fakeHttpService.retrieveTalk(speaker)));
            return scope.getTalks();
        }
    }

    @GetMapping("/talks-async")
    CompletableFuture<List<Talk>> getTalksAsync() {
        var speakersAsync = fakeHttpService.retrieveSpeakersAsync();
        return speakersAsync.thenApply(speakers -> {
            var talkFutures = speakers.stream()
                    .map(speaker -> fakeHttpService.retrieveTalkAsync(speaker))
                    .toList();
            return talkFutures.stream()
                    .map(CompletableFuture::join)
                    .toList();
        });
    }



    @GetMapping("/speakers-async")
    CompletableFuture<List<Speaker>> getSpeakersAsync() {
        return fakeHttpService.retrieveSpeakersAsync();
    }

}

class TalkScope extends StructuredTaskScope<Talk> {
    private final Collection<Talk> talks = new ConcurrentLinkedDeque<>();
    @Override
    protected void handleComplete(Subtask<? extends Talk> subtask) {
        talks.add(subtask.get());
    }

    @SneakyThrows
    public Collection<Talk> getTalks() {
        super.join();
        super.ensureOwnerAndJoined();
        return talks;
    }
}

record Speaker(String name) {}
record Talk(String title) {}
