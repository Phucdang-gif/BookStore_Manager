package BUS;

import DAO.PublisherDAO;
import DTO.PublisherDTO;
import java.util.ArrayList;

public class PublisherBUS {
    private PublisherDAO publisherDAO;
    private ArrayList<PublisherDTO> publisherList;

    public PublisherBUS() {
        this.publisherDAO = new PublisherDAO();
        loadData();
    }

    public void loadData() {
        this.publisherList = publisherDAO.selectAll();
    }

    public ArrayList<PublisherDTO> getAll() {
        return publisherList;
    }

    public PublisherDTO getById(int id) {
        for (PublisherDTO pub : publisherList) {
            if (pub.getId() == id)
                return pub;
        }
        return null;
    }

    public boolean add(PublisherDTO pub) {
        // Logic kiểm tra dữ liệu cơ bản
        if (pub.getName() == null || pub.getName().trim().isEmpty())
            return false;

        int id = publisherDAO.insert(pub);
        if (id > 0) {
            loadData(); // Cập nhật lại danh sách cache
            return true;
        }
        return false;
    }

    public boolean update(PublisherDTO pub) {
        if (publisherDAO.update(pub) > 0) {
            loadData();
            return true;
        }
        return false;
    }

    public boolean delete(int id) {
        if (publisherDAO.delete(id) > 0) {
            loadData();
            return true;
        }
        return false;
    }

    public ArrayList<PublisherDTO> search(String keyword) {
        ArrayList<PublisherDTO> result = new ArrayList<>();
        keyword = keyword.toLowerCase();
        for (PublisherDTO pub : publisherList) {
            if (pub.getName().toLowerCase().contains(keyword) ||
                    pub.getPhone().contains(keyword)) {
                result.add(pub);
            }
        }
        return result;
    }
}