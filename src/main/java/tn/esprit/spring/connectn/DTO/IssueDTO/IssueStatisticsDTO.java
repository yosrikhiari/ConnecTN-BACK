package tn.esprit.spring.connectn.DTO.IssueDTO;

public class IssueStatisticsDTO {
    private String city;
    private long openCount;
    private long inProgressCount;
    private long resolvedCount;

    // Getters and setters


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getOpenCount() {
        return openCount;
    }

    public void setOpenCount(long openCount) {
        this.openCount = openCount;
    }

    public long getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(long inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public long getResolvedCount() {
        return resolvedCount;
    }

    public void setResolvedCount(long resolvedCount) {
        this.resolvedCount = resolvedCount;
    }
}
