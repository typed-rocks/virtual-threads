package ch.woerz.virtualthreads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@SpringBootApplication
public class VirtualThreadsApplication {

    public static void main(String[] args) {

        SpringApplication.run(VirtualThreadsApplication.class, args);
    }

}
