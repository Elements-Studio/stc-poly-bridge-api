package org.starcoin.polybridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@EnableScheduling
@SpringBootApplication
public class PolyBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolyBridgeApplication.class, args);
    }

}
