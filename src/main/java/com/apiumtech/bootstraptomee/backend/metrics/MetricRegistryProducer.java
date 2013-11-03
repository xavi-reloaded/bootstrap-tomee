package com.apiumtech.bootstraptomee.backend.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
@Slf4j
public class MetricRegistryProducer {

	private MetricRegistry registry;

	@PostConstruct
	private void init() {
		log.info("initializing metrics registry");
		registry = new MetricRegistry();
		JmxReporter.forRegistry(registry).build().start();
		log.info("metrics registry initialized");
	}

	@Produces
	public MetricRegistry produceMetricsRegistry() {
		return registry;
	}
}
