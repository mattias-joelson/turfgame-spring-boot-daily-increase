package org.joelson.turf.dailyinc;

import org.joelson.turf.dailyinc.service.FeedImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class TurfDailyIncreaseApplication {

    Logger logger = LoggerFactory.getLogger(TurfDailyIncreaseApplication.class);

    @Autowired
    FeedImporterService feedImporterService;

    public static void main(String[] args) {
        SpringApplication.run(TurfDailyIncreaseApplication.class, args);
    }

    @Bean
    public CommandLineRunner argumentHandler(ApplicationContext ctx) {
        return args -> {
            for (String filename : args) {
                logger.info(String.format("Importing data from '%s'", filename));
                feedImporterService.importFeed(filename);
            }
            if (args.length > 0) {
                logger.info("Done importing data.");
            }
        };
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            // arguments passed through -Dspring-boot.run.arguments="test1 test2"
            logArray("Program arguments:", "No program arguments.", args);
            //printContextBeans(ctx);
        };
    }

    private void printContextBeans(ApplicationContext ctx) {
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        logArray("Provided beans:", "No beans provided.", beanNames);
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
