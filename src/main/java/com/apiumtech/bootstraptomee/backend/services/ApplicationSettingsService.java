package com.apiumtech.bootstraptomee.backend.services;

import com.apiumtech.bootstraptomee.backend.dao.ApplicationSettingsDAO;
import com.apiumtech.bootstraptomee.backend.model.ApplicationSettings;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.Enumeration;

@ApplicationScoped
public class ApplicationSettingsService {

	@Inject
	ApplicationSettingsDAO applicationSettingsDAO;

	private ApplicationSettings settings;

	@PostConstruct
	private void init() {
		settings = Iterables.getFirst(applicationSettingsDAO.findAll(), null);
	}

	public ApplicationSettings get() {
		return settings;
	}

	public void save(ApplicationSettings settings) {
		applicationSettingsDAO.saveOrUpdate(settings);
		this.settings = settings;
		applyLogLevel();
	}

	public Date getUnreadThreshold() {
		int keepStatusDays = get().getKeepStatusDays();
		return keepStatusDays > 0 ? DateUtils.addDays(new Date(), -1 * keepStatusDays) : null;
	}

	@SuppressWarnings("unchecked")
	public void applyLogLevel() {
		String logLevel = get().getLogLevel();
		Level level = Level.toLevel(logLevel);

		Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
		while (loggers.hasMoreElements()) {
			Logger logger = loggers.nextElement();
			if (logger.getName().startsWith("com.apiumtech.bootstraptomee")) {
				logger.setLevel(level);
			}
		}
	}
}
