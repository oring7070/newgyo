package kr.co.newgyo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewgyoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewgyoApplication.class, args);
    }

}
