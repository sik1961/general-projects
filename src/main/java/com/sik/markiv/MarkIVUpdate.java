package com.sik.markiv;
/**
 * @author sik
 */
import org.apache.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sik.markiv.events.EventManager;
import com.sik.markiv.exception.MarkIVException;
import com.sik.markiv.google.calendar.MarkIVCalendarFeed;

@Component
public class MarkIVUpdate {

	DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
	//private static final long INTERVAL_MILLIS = 1000L * 60; //every minute - for testing
	//private static final long INIT_DELAY_MILLIS = 1000L * 60; //every minute - for testing 
	//private static final String CRON_EXP = "0 0 6,18 * * *"; // 6am/6pm
	private static final String CRON_EXP = "0 0/30 7-23 * * *"; // hourly 7am-11pm
	private static final Logger LOG = Logger.getLogger(MarkIVUpdate.class);

	MarkIVHelper m4h;
	private EventManager em;
	private MarkIVCalendarFeed feed;
	private LocalDateTime calendarLastUpdate;

	public MarkIVUpdate() {
		LOG.info("Mark IV Mgt - Initialised");
		this.feed = new MarkIVCalendarFeed();
		this.em = new EventManager(this.feed.getFeed());
		this.m4h = new MarkIVHelper(em);
		this.calendarLastUpdate = new LocalDateTime(0);
	}

	//@Scheduled(fixedRate=INTERVAL_MILLIS, initialDelay=INIT_DELAY_MILLIS)
	@Scheduled(cron = CRON_EXP)
	public void m4Update() throws MarkIVException {
		update();
	}

	protected void update() {
		LOG.info("Mark IV Mgt - Checking calendar....");
	
		em.updateFromFeed(new MarkIVCalendarFeed().getFeed());
		LOG.info("Cal updated: " + 
				em.getLatestUpdate().getLastUpdated().toString(dtf) + 
				" (" + (em.getLatestUpdate().getLastUpdatedBy()!=null?em.getLatestUpdate().getLastUpdatedBy():"n/a") + 
				") - Web updated: " + 
				this.calendarLastUpdate.toString(dtf));
		if (em.getLatestUpdate().getLastUpdated()
				.isAfter(this.calendarLastUpdate)) {

			LOG.info("Mark IV Mgt - Building Gigs Page");
			m4h.buildGigsPage();

			LOG.info("Mark IV Mgt - Building Availability Page");
			m4h.buildAvailPage();

			LOG.info("Mark IV Mgt - Building Gallery Page");
			m4h.buildGalleryPage();

			LOG.info("Mark IV Mgt - Uploading");
			try {
				m4h.uploadFiles();
				LOG.info("Mark IV Mgt - Completed");
			} catch (MarkIVException e) {
				throw new MarkIVException("Caught Exception: ",e);
			}
			this.calendarLastUpdate = new LocalDateTime(System.currentTimeMillis());
			LOG.info("Mark IV Mgt - Last update time set to: " + this.calendarLastUpdate.toString());
		} else {
			LOG.info("Mark IV Mgt - Skipped - no calendar updates");
		}
	}
}
