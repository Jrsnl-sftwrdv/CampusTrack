package com.jabai.campustrack.Models;

import com.jabai.campustrack.Models.Enums.IncidentCategory;
import com.jabai.campustrack.Models.Enums.IncidentPriority;
import com.jabai.campustrack.Models.Enums.IncidentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incidents")
public class Incident {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "created_at", insertable = false, updatable = false)
  @Generated // Return the database-generated timestamp after an insert.
  private LocalDateTime createdAt;

  // Timestamps
  @Column(name = "evaluated_at") // legacy column, no longer used by the new workflow
  private LocalDateTime evaluatedAt;

  @Column(name = "inspected_at")
  private LocalDateTime inspectedAt;

  @Column(name = "reviewed_at")
  private LocalDateTime reviewedAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Column(name = "assigned_at")
  private LocalDateTime assignedAt;

  @Column(name = "resolved_at")
  private LocalDateTime resolvedAt;

  @Column(name = "closed_at") // legacy column, no longer used by the new workflow
  private LocalDateTime closedAt;

  @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();

  // Asset (optional)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asset_id")
  private Asset asset;

  // Room
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  // Reported by (the student)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reported_by", nullable = false)
  private User reportedBy;

  // Inspected by (the roaming worker who confirmed or rejected it)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inspected_by")
  private User inspectedBy;

  // Reviewed by (the admin who approved or declined it)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewed_by")
  private User reviewedBy;

  // Assigned to (the repair worker)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to")
  private User assignedTo;

  @Column(name = "incident_number", nullable = false, unique = true, length = 50)
  private String incidentNumber;

  // Filled in by the student
  @Column(name = "photo_path", length = 255)
  private String photoPath;

  // Filled in by the inspecting worker, so they are empty on a fresh report
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "category")
  private IncidentCategory category;

  @Enumerated(EnumType.STRING)
  @Column(name = "priority")
  private IncidentPriority priority;

  @Column(name = "safety_hazard", nullable = false)
  private boolean safetyHazard = false;

  @Column(name = "operational_impact", nullable = false)
  private boolean operationalImpact = false;

  @Column(name = "asset_functional")
  private Boolean assetFunctional; // null until the worker answers

  @Column(name = "affected_area", nullable = false)
  private Integer affectedArea = 1;

  @Column(name = "priority_score", nullable = false) // legacy, always 0 now
  private Integer priorityScore = 0;

  @Column(name = "rejection_reason", columnDefinition = "TEXT") // internal only, never shown to students
  private String rejectionReason;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private IncidentStatus status;

  protected Incident() { }

  // Student report: only a location, a photo and who reported it.
  public Incident(Room room, User reportedBy, String incidentNumber, String photoPath) {
    this.room = room;
    this.reportedBy = reportedBy;
    this.incidentNumber = incidentNumber;
    this.photoPath = photoPath;
    this.status = IncidentStatus.REPORTED;
  }

  // Old full constructor. Kept so IncidentService still compiles. Remove it once the old create flow is gone.
  public Incident(
          Room room,
          User reportedBy,
          String incidentNumber,
          String description,
          boolean safetyHazard,
          boolean operationalImpact,
          int affectedArea,
          int priorityScore,
          IncidentCategory category,
          IncidentPriority priority,
          IncidentStatus status
  ) {
    this.room = room;
    this.reportedBy = reportedBy;
    this.incidentNumber = incidentNumber;
    this.description = description;
    this.safetyHazard = safetyHazard;
    this.operationalImpact = operationalImpact;
    this.affectedArea = affectedArea;
    this.priorityScore = priorityScore;
    this.category = category;
    this.priority = priority;
    this.status = status;
  }

  // Getters
  public Long getId() { return id; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
  public LocalDateTime getInspectedAt() { return inspectedAt; }
  public LocalDateTime getReviewedAt() { return reviewedAt; }
  public LocalDateTime getApprovedAt() { return approvedAt; }
  public LocalDateTime getAssignedAt() { return assignedAt; }
  public LocalDateTime getResolvedAt() { return resolvedAt; }
  public LocalDateTime getClosedAt() { return closedAt; }
  public Asset getAsset() { return asset; }
  public Room getRoom() { return room; }
  public User getReportedBy() { return reportedBy; }
  public User getInspectedBy() { return inspectedBy; }
  public User getReviewedBy() { return reviewedBy; }
  public User getAssignedTo() { return assignedTo; }
  public String getIncidentNumber() { return incidentNumber; }
  public String getPhotoPath() { return photoPath; }
  public String getDescription() { return description; }
  public boolean getSafetyHazard() { return safetyHazard; }
  public boolean getOperationalImpact() { return operationalImpact; }
  public Boolean getAssetFunctional() { return assetFunctional; }
  public Integer getAffectedArea() { return affectedArea; }
  public Integer getPriorityScore() { return priorityScore; }
  public String getRejectionReason() { return rejectionReason; }
  public IncidentCategory getCategory() { return category; }
  public IncidentPriority getPriority() { return priority; }
  public IncidentStatus getStatus() { return status; }
  public List<MaintenanceRecord> getMaintenanceRecords() { return maintenanceRecords; }

  // Setters
  public void setEvaluatedAt(LocalDateTime value) { evaluatedAt = value; }
  public void setInspectedAt(LocalDateTime value) { inspectedAt = value; }
  public void setReviewedAt(LocalDateTime value) { reviewedAt = value; }
  public void setApprovedAt(LocalDateTime value) { approvedAt = value; }
  public void setAssignedAt(LocalDateTime value) { assignedAt = value; }
  public void setResolvedAt(LocalDateTime value) { resolvedAt = value; }
  public void setClosedAt(LocalDateTime value) { closedAt = value; }
  public void setAsset(Asset value) { asset = value; }
  public void setRoom(Room value) { room = value; }
  public void setReportedBy(User value) { reportedBy = value; }
  public void setInspectedBy(User value) { inspectedBy = value; }
  public void setReviewedBy(User value) { reviewedBy = value; }
  public void setAssignedTo(User value) { assignedTo = value; }
  public void setIncidentNumber(String value) { incidentNumber = value; }
  public void setPhotoPath(String value) { photoPath = value; }
  public void setDescription(String value) { description = value; }
  public void setSafetyHazard(boolean value) { safetyHazard = value; }
  public void setOperationalImpact(boolean value) { operationalImpact = value; }
  public void setAssetFunctional(Boolean value) { assetFunctional = value; }
  public void setAffectedArea(Integer value) { affectedArea = value; }
  public void setPriorityScore(Integer value) { priorityScore = value; }
  public void setRejectionReason(String value) { rejectionReason = value; }
  public void setCategory(IncidentCategory value) { category = value; }
  public void setPriority(IncidentPriority value) { priority = value; }
  public void setStatus(IncidentStatus value) { status = value; }
  public void removeMaintenanceRecord(MaintenanceRecord value) {
    maintenanceRecords.remove(value);
    value.setIncident(null);
  }
  public void addMaintenanceRecord(MaintenanceRecord value) {
    maintenanceRecords.add(value);
    value.setIncident(this);
  }
}