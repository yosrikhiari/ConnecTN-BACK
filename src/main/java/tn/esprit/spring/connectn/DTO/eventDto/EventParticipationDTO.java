package tn.esprit.spring.connectn.DTO.eventDto;

public class EventParticipationDTO {
    private Long userId;
    private String username;
    private Long eventId;
    private String eventTitle;
    private int pointsEarned;
    private String createdAt;

    public EventParticipationDTO(Long userId, String username, Long eventId,
                                 String eventTitle, int pointsEarned) {
        this.userId = userId;
        this.username = username;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.pointsEarned = pointsEarned;
    }

    // Getters et setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }


}