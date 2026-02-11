package DTO;

public class PublisherDTO {
    private int publisherId;
    private String publisherName, status, phone;

    public PublisherDTO() {
    }

    public PublisherDTO(int publisherId, String publisherName, String phone, String status) {
        this.publisherId = publisherId;
        this.publisherName = publisherName;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return publisherName + '\'' + phone + '\'' + status;
    }
}
