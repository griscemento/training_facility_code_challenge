package com.vinci.training_facility;

import org.springframework.boot.SpringApplication;

public class TestTrainingFacilityApplication {

	public static void main(String[] args) {
		SpringApplication.from(TrainingFacilityApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
