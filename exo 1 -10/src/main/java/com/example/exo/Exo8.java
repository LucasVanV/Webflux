package com.example.exo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class Exo8 {

	/**
	 * Endpoint that returns a list of article titles.
	 * @return Flux<String> containing article titles
	 */
	@GetMapping("/articles")
	public Flux<String> getArticles() {
		return Flux.just(
				"Introduction to Spring WebFlux",
				"Reactive Programming with Project Reactor",
				"Building APIs with Spring Boot"
		);
	}
}
