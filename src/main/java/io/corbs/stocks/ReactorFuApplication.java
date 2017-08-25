package io.corbs.stocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactorFuApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ReactorFuApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactorFuApplication.class, args);
	}

}
