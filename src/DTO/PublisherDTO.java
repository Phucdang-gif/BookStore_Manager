package DTO;

public class PublisherDTO {
    private int publisherId;
    private String publisherName, status;

    public PublisherDTO() {
    }

    public PublisherDTO(int publisherId, String publisherName, String status) {
        this.publisherId = publisherId;
        this.publisherName = publisherName;
        this.status = status;
    }

    public int getId() {
        return publisherId;
    }

    public void setId(int publisherId) {
        this.publisherId = publisherId;
    }

    public String getName() {
        return publisherName;
    }

    public void setName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return publisherName;
    }
}
