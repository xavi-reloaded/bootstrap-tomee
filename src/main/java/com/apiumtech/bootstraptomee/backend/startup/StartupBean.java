package com.apiumtech.bootstraptomee.backend.startup;

import com.apiumtech.bootstraptomee.backend.dao.ApplicationSettingsDAO;
import com.apiumtech.bootstraptomee.backend.model.ApplicationSettings;
import com.apiumtech.bootstraptomee.backend.model.UserRole.Role;
import com.apiumtech.bootstraptomee.backend.services.ApplicationSettingsService;
import com.apiumtech.bootstraptomee.backend.services.UserService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Starting point of the application
 * 
 */
@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Slf4j
public class StartupBean {

	public static final String USERNAME_ADMIN = "admin";
	public static final String USERNAME_DEMO = "demo";

	@Inject
	DatabaseUpdater databaseUpdater;

	@Inject
	ApplicationSettingsDAO applicationSettingsDAO;

	@Inject
	UserService userService;

	@Inject
	ApplicationSettingsService applicationSettingsService;

	private long startupTime;
	private Map<String, String> supportedLanguages = Maps.newHashMap();

	@PostConstruct
	private void init() {

		startupTime = System.currentTimeMillis();

		// update database schema
		databaseUpdater.update();

		if (applicationSettingsDAO.getCount() == 0) {
			// import initial data
			initialData();
		}
		applicationSettingsService.applyLogLevel();

		initSupportedLanguages();

		// start fetching feeds (not yet !)
//		taskGiver.start();
	}

	private void initSupportedLanguages() {
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = getClass().getResourceAsStream("/i18n/languages.properties");
			props.load(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		for (Object key : props.keySet()) {
			supportedLanguages.put(key.toString(), props.getProperty(key.toString()));
		}
	}

	/**
	 * create default users
	 */
	private void initialData() {
		log.info("Populating database with default values");

		ApplicationSettings settings = new ApplicationSettings();
		settings.setAnnouncement("You are Your Keyword");
        settings.setPublicUrl("http://localhost:8082/youkeyword");
		applicationSettingsService.save(settings);

		try {
			userService.register(USERNAME_ADMIN, "admin", "admin@youkeyword.com", Arrays.asList(Role.ADMIN, Role.USER), true);
			userService.register(USERNAME_DEMO, "demo", "demo@youkeyword.com", Arrays.asList(Role.USER), true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public long getStartupTime() {
		return startupTime;
	}

	public Map<String, String> getSupportedLanguages() {
		return supportedLanguages;
	}
}
