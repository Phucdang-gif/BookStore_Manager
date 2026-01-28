package BUS;

import DAO.PublisherDAO; // Bạn cần tạo thêm file PublisherDAO
import DTO.PublisherDTO;
import java.sql.SQLException;
import java.util.ArrayList;

public class PublisherBUS {
    private PublisherDAO publisherDAO;
    private ArrayList<PublisherDTO> publisherList;

    public PublisherBUS() {
        this.publisherDAO = new PublisherDAO();
        loadData();
    }

    public void loadData() {
        try {
            // Giả sử PublisherDAO đã có hàm selectAll()
            this.publisherList = publisherDAO.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
            this.publisherList = new ArrayList<>();
        }
    }

    public ArrayList<PublisherDTO> getAll() {
        try {
            return publisherDAO.selectAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public String getNameById(int id) {
        for (PublisherDTO pub : publisherList) {
            if (pub.getId() == id)
                return pub.getName();
        }
        return "";
    }
}