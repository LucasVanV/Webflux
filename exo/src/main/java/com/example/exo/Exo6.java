package com.example.exo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class Exo6 {

	/**
	 * Endpoint that processes a flux of numbers by filtering, transforming and formatting.
	 * @return Flux<String> containing processed numbers in the format "Processed: X"
	 */
	@GetMapping("/processed-numbers")
	public Flux<String> processedNumbers() {
		return Flux.range(1, 10)
				.filter(n -> n % 2 == 0)
				.map(n -> n * 10)
				.map(n -> "Processed: " + n);
	}
}
