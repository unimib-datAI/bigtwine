package it.unimib.disco.bigtwine.streamprocessor;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonPropertyOrder({
        "status__id",
        "status__text",
        "status__coordinates",
        "status__user__id",
        "status__user__name",
        "status__user__location",
        "status__user__coordinates",
        "entity__position",
        "entity__link",
        "entity__nil",
        "entity__nil__cluster",
        "entity__confidence",
        "entity__category",
        "entity__resource__name",
        "entity__resource__thumb",
        "entity__resource__coordinates",
        "entity__resource__extra",
        "process_date",
})
public class TwitterNeelExtendedResultRow {
    @JsonProperty("status__id")
    private String statusId;

    @JsonProperty("status__text")
    private String statusText;

    @JsonProperty("status__coordinates")
    private String statusCoordinates;

    @JsonProperty("status__user__id")
    private String statusUserId;

    @JsonProperty("status__user__name")
    private String statusUserName;

    @JsonProperty("status__user__location")
    private String statusUserLocation;

    @JsonProperty("status__user__coordinates")
    private String statusUserCoordinates;

    @JsonProperty("entity__position")
    private String entityPosition;

    @JsonProperty("entity__link")
    private String entityLink;

    @JsonProperty("entity__nil")
    private String entityNil;

    @JsonProperty("entity__nil__cluster")
    private String entityNilCluster;

    @JsonProperty("entity__confidence")
    private String entityConfidence;

    @JsonProperty("entity__category")
    private String entityCategory;

    @JsonProperty("entity__resource__name")
    private String entityResourceName;

    @JsonProperty("entity__resource__thumb")
    private String entityResourceThumb;

    @JsonProperty("entity__resource__coordinates")
    private String entityResourceCoordinates;

    @JsonProperty("entity__resource__extra")
    private String entityResourceExtra;

    @JsonProperty("process_date")
    private String processDate;

    public TwitterNeelExtendedResultRow() {
    }

    public String getStatusId() {
        return statusId;
    }

    public TwitterNeelExtendedResultRow setStatusId(String statusId) {
        this.statusId = statusId;
        return this;
    }

    public String getStatusText() {
        return statusText;
    }

    public TwitterNeelExtendedResultRow setStatusText(String statusText) {
        this.statusText = statusText;
        return this;
    }

    public String getStatusCoordinates() {
        return statusCoordinates;
    }

    public TwitterNeelExtendedResultRow setStatusCoordinates(String statusCoordinates) {
        this.statusCoordinates = statusCoordinates;
        return this;
    }

    public String getStatusUserId() {
        return statusUserId;
    }

    public TwitterNeelExtendedResultRow setStatusUserId(String statusUserId) {
        this.statusUserId = statusUserId;
        return this;
    }

    public String getStatusUserName() {
        return statusUserName;
    }

    public TwitterNeelExtendedResultRow setStatusUserName(String statusUserName) {
        this.statusUserName = statusUserName;
        return this;
    }

    public String getStatusUserLocation() {
        return statusUserLocation;
    }

    public TwitterNeelExtendedResultRow setStatusUserLocation(String statusUserLocation) {
        this.statusUserLocation = statusUserLocation;
        return this;
    }

    public String getStatusUserCoordinates() {
        return statusUserCoordinates;
    }

    public TwitterNeelExtendedResultRow setStatusUserCoordinates(String statusUserCoordinates) {
        this.statusUserCoordinates = statusUserCoordinates;
        return this;
    }

    public String getEntityPosition() {
        return entityPosition;
    }

    public TwitterNeelExtendedResultRow setEntityPosition(String entityPosition) {
        this.entityPosition = entityPosition;
        return this;
    }

    public String getEntityLink() {
        return entityLink;
    }

    public TwitterNeelExtendedResultRow setEntityLink(String entityLink) {
        this.entityLink = entityLink;
        return this;
    }

    public String getEntityNil() {
        return entityNil;
    }

    public TwitterNeelExtendedResultRow setEntityNil(String entityNil) {
        this.entityNil = entityNil;
        return this;
    }

    public String getEntityNilCluster() {
        return entityNilCluster;
    }

    public TwitterNeelExtendedResultRow setEntityNilCluster(String entityNilCluster) {
        this.entityNilCluster = entityNilCluster;
        return this;
    }

    public String getEntityConfidence() {
        return entityConfidence;
    }

    public TwitterNeelExtendedResultRow setEntityConfidence(String entityConfidence) {
        this.entityConfidence = entityConfidence;
        return this;
    }

    public String getEntityCategory() {
        return entityCategory;
    }

    public TwitterNeelExtendedResultRow setEntityCategory(String entityCategory) {
        this.entityCategory = entityCategory;
        return this;
    }

    public String getEntityResourceName() {
        return entityResourceName;
    }

    public TwitterNeelExtendedResultRow setEntityResourceName(String entityResourceName) {
        this.entityResourceName = entityResourceName;
        return this;
    }

    public String getEntityResourceThumb() {
        return entityResourceThumb;
    }

    public TwitterNeelExtendedResultRow setEntityResourceThumb(String entityResourceThumb) {
        this.entityResourceThumb = entityResourceThumb;
        return this;
    }

    public String getEntityResourceCoordinates() {
        return entityResourceCoordinates;
    }

    public TwitterNeelExtendedResultRow setEntityResourceCoordinates(String entityResourceCoordinates) {
        this.entityResourceCoordinates = entityResourceCoordinates;
        return this;
    }

    public String getEntityResourceExtra() {
        return entityResourceExtra;
    }

    public TwitterNeelExtendedResultRow setEntityResourceExtra(String entityResourceExtra) {
        this.entityResourceExtra = entityResourceExtra;
        return this;
    }

    public String getProcessDate() {
        return processDate;
    }

    public TwitterNeelExtendedResultRow setProcessDate(String processDate) {
        this.processDate = processDate;
        return this;
    }
}
