package com.apiumtech.bootstraptomee.backend;

import com.apiumtech.bootstraptomee.backend.services.ApplicationSettingsService;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.Date;

/**
 * Contains all scheduled tasks
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ScheduledTasks {

	@Inject
	ApplicationSettingsService applicationSettingsService;


	/**
	 * clean old read statuses, runs every day at midnight
	 */
	@Schedule(hour = "0", persistent = false)
	private void cleanupOldStatuses() {
		Date threshold = applicationSettingsService.getUnreadThreshold();
		if (threshold != null) {
			//cleaner.cleanStatusesOlderThan(threshold);
		}
	}
}
