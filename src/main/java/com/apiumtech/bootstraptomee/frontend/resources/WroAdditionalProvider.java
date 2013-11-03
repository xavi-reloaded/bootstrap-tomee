package com.apiumtech.bootstraptomee.frontend.resources;

import com.google.common.collect.Maps;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;
import ro.isdc.wro.model.resource.processor.support.ProcessorProvider;

import java.util.Map;

/**
 * Build-time solution
 * 
 */
public class WroAdditionalProvider implements ProcessorProvider {

	@Override
	public Map<String, ResourcePreProcessor> providePreProcessors() {
		Map<String, ResourcePreProcessor> map = Maps.newHashMap();
		map.put("sassOnlyProcessor", new SassOnlyProcessor());
		map.put("sassImport", new SassImportProcessor());
		map.put("timestamp", new TimestampProcessor());
		return map;
	}

	@Override
	public Map<String, ResourcePostProcessor> providePostProcessors() {
		return Maps.newHashMap();
	}

}
