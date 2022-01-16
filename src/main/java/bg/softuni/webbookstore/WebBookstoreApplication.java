package bg.softuni.webbookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebBookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebBookstoreApplication.class, args);
    }

}
