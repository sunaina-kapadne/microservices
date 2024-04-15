package com.microservices.demo.project.notificationservice;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Objects;

@SpringBootApplication
@Slf4j
@EnableDiscoveryClient
public class NotificationServiceApplication {

	private final ObservationRegistry observationRegistry;
	private final Tracer tracer;

    public NotificationServiceApplication(ObservationRegistry observationRegistry, Tracer tracer) {
        this.observationRegistry = observationRegistry;
        this.tracer = tracer;
    }

    public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@KafkaListener(topics = "notificationTopic")
	public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
		Observation.createNotStarted("on-message", this.observationRegistry).observe(() -> {
			log.info("Received order placed event: {}", orderPlacedEvent.getOrderNumber());
			log.info("Got message <{}>", orderPlacedEvent);
            assert this.tracer != null;
            log.info("TraceId- {}, Received Notification for Order - {}", Objects.requireNonNull(this.tracer.currentSpan()).context().traceId(),
					orderPlacedEvent.getOrderNumber());
		});
		// send out an email notification
	}
}