package dev.narotham.circuit_breaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class CircuitBreakerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CircuitBreakerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CircuitBreaker circuitBreaker = new CircuitBreaker(5,5,10);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Every 5 seconds");
                try {
                    circuitBreaker.exec();
                }catch(Exception e){
                    System.out.println("error" + e.toString());
                }
            }
        }, 0, 5000);
    }
}
