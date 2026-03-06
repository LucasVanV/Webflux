package com.example.exo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class Exo7 {

	/**
	 * Endpoint that demonstrates onErrorResume: provides fallback values when an error occurs.
	 * @return Flux<String> with values "A", "B", "C", then fallback "Default1", "Default2"
	 */
	@GetMapping("/error-resume")
	public Flux<String> errorResume() {
		return Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Simulated error")))
				.onErrorResume(e -> Flux.just("Default1", "Default2"));
	}

	/**
	 * Endpoint that demonstrates onErrorContinue: skips errors and continues processing.
	 * @return Flux<Integer> with numbers 1 to 5, ignoring the error at 2
	 */
	@GetMapping("/error-continue")
	public Flux<Integer> errorContinue() {
		return Flux.range(1, 5)
				.flatMap(n -> {
					if (n == 2) {
						return Flux.error(new RuntimeException("Error at number 2"));
					}
					return Flux.just(n);
				})
				.onErrorContinue((e, item) -> {
					// Log the error and continue processing
					System.err.println("Error occurred at item: " + item);
				});
	}
}
