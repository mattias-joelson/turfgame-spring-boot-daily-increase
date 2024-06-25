package org.joelson.turf.dailyinc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class TurfDailyIncreaseApplication {

	Logger logger = LoggerFactory.getLogger(TurfDailyIncreaseApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TurfDailyIncreaseApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			// arguments passed through -Dspring-boot.run.arguments="test1 test2"
			logArray("Program arguments:", "No program arguments.", args);
			//String[] beanNames = ctx.getBeanDefinitionNames();
			//Arrays.sort(beanNames);
			//logArray("Provided beans:", "No beans provided.", beanNames);
		};
	}

	private void logArray(String hasElements, String noElements, String[] strings) {
		if (strings.length > 0) {
			logger.info(hasElements);
			int maxLength = String.valueOf(strings.length).length();
			String format = String.format("  [%%%dd] %%s", maxLength);
			for (int i = 0; i < strings.length; i += 1) {
				logger.info(String.format(format, i, strings[i]));
			}
		} else {
			logger.info(noElements);
		}
	}
}
