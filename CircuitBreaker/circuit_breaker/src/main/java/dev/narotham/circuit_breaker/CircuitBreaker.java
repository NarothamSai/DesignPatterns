package dev.narotham.circuit_breaker;

import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;

public class CircuitBreaker {
    private int successThreshold;
    private int failureThreshold;
    private long timeout;
    private BreakerState breakerState = BreakerState.GREEN;

    private LocalDateTime nextAttemptAt;

    private int successAttempts = 0;
    private int failureAttempts = 0;

    CircuitBreaker(int successThreshold, int failureThreshold, long timeout){
        this.successThreshold = successThreshold;
        this.failureThreshold = failureThreshold;
        this.timeout = timeout;
    }

    public void log(String result){
        System.out.println("Result:"+result);
        System.out.println("Breaker State:"+this.breakerState);
        System.out.printf("successAttempts:%d\n",successAttempts);
        System.out.printf("failureAttempts:%d\n",failureAttempts);
        System.out.println("nextAttemptAt:" + nextAttemptAt);
    }

    public void resetAttempts(){
        this.successAttempts = 0;
        this.failureAttempts = 0;
    }

    public String exec() throws Exception{
        if(this.breakerState == BreakerState.RED){
            System.out.println(this.nextAttemptAt);
            if(LocalDateTime.now().isAfter(nextAttemptAt) == true){
                this.resetAttempts();
                this.breakerState = BreakerState.GREEN;
                this.nextAttemptAt = null;
            }else{
                throw new Exception("Server suspended. Please try again after some time.");
            }
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:3000/example";
            String result = restTemplate.getForObject(url, String.class);
            this.success();
            return result;
        }catch (RestClientResponseException e){
            if(e.getRawStatusCode() >= 500){
                this.failure();
            }else{
                this.success();
            }
            return e.toString();
        }
    }

    public void success(){
        this.failureAttempts = 0;
        this.breakerState = BreakerState.GREEN;
        this.successAttempts += 1;
        this.log("success");
    }

    public void failure(){
        this.successAttempts = 0;
        this.failureAttempts += 1;

        if(this.failureAttempts >= this.failureThreshold){
            this.breakerState = BreakerState.RED;
            this.nextAttemptAt = LocalDateTime.now().plusSeconds(timeout);
        }
        this.log("failure");
    }
}
