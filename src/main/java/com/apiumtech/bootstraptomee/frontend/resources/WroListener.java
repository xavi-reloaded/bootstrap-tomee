package com.apiumtech.bootstraptomee.frontend.resources;

import com.apiumtech.bootstraptomee.backend.services.ApplicationPropertiesService;
import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.http.WroServletContextListener;

public class WroListener extends WroServletContextListener {

	@Override
	protected WroConfiguration newConfiguration() {
		WroConfiguration conf = super.newConfiguration();
		ApplicationPropertiesService properties = ApplicationPropertiesService.get();
		boolean prod = properties.isProduction();

		conf.setResourceWatcherUpdatePeriod(prod ? 0 : 1);
		conf.setDisableCache(!prod);
		conf.setDebug(!prod);
		return conf;
	}

}
