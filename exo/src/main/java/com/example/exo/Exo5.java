package com.example.exo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class Exo5 {

	/**
	 * Endpoint that returns a single message as a Mono.
	 * @return Mono<String> containing "Welcome to Project Reactor!"
	 */
	@GetMapping("/welcome")
	public Mono<String> welcome() {
		return Mono.just("Welcome to Project Reactor!");
	}

	/**
	 * Endpoint that returns a sequence of numbers from 1 to 5 as a Flux.
	 * @return Flux<Integer> containing numbers 1 to 5
	 */
	@GetMapping("/numbers")
	public Flux<Integer> numbers() {
		return Flux.range(1, 5);
	}
}
