package com.javiersanchez.irregularitiesApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class IrregularitiesAppApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(IrregularitiesAppApplication.class, args);

		orionSubscription orionSubscription = context.getBean(orionSubscription.class);
		orionSubscription.deleteAllSubscriptions();
		orionSubscription.createSubscription();
	}

}
