package com.sik.markiv.api;
/**
 * @author sik
 *
 */
import org.joda.time.LocalDateTime;

public class UpdateRecord {
    private String lastUpdatedBy;
    private LocalDateTime lastUpdated;
    public UpdateRecord() {
        super();
        this.lastUpdated = new LocalDateTime(0);
    }
    public UpdateRecord(final String lastUpdatedBy, final LocalDateTime lastUpdated) {
        super();
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdated = lastUpdated;
    }
    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }
    public void setLastUpdatedBy(final String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    public LocalDateTime getLastUpdated() {
        return this.lastUpdated;
    }
    public void setLastUpdated(final LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
