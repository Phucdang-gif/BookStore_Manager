package BUS;

import DAO.PublisherDAO;
import DTO.PublisherDTO;
import DTO.ValidationResult;
import java.util.ArrayList;

public class PublisherBUS {
    private PublisherDAO publisherDAO;
    private ArrayList<PublisherDTO> publisherList;

    public PublisherBUS() {
        this.publisherDAO = new PublisherDAO();
        loadDataFromDB();
    }

    public void loadDataFromDB() {
        this.publisherList = publisherDAO.selectAll();
        if (this.publisherList == null)
            this.publisherList = new ArrayList<>();
    }

    public ArrayList<PublisherDTO> getAll() {
        return publisherList;
    }

    public PublisherDTO getById(int id) {
        return publisherList.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public String getNameById(int id) {
        PublisherDTO pub = getById(id);
        return pub != null ? pub.getName() : "";
    }

    // ===================== THÊM MỚI =====================

    public ValidationResult add(PublisherDTO pub) {
        ValidationResult vr = Validator.validatePublisher(pub, this.publisherList);
        if (!vr.isValid())
            return vr;

        int id = publisherDAO.insert(pub);
        if (id > 0) {
            loadDataFromDB();
            return vr; // isValid() == true
        } else
            vr.addError("system", "Lỗi hệ thống khi thêm nhà xuất bản!");
        return vr;
    }

    // ===================== CẬP NHẬT =====================

    public ValidationResult update(PublisherDTO pub) {
        ValidationResult vr = Validator.validatePublisher(pub, this.publisherList);
        if (!vr.isValid())
            return vr;

        if (publisherDAO.update(pub) > 0) {
            loadDataFromDB();
            return vr; // isValid() == true
        } else
            vr.addError("system", "Lỗi hệ thống khi cập nhật nhà xuất bản!");
        return vr;
    }

    // ===================== XÓA =====================

    public ValidationResult delete(int id) {
        ValidationResult vr = new ValidationResult();
        try {
            if (publisherDAO.delete(id) > 0) {
                loadDataFromDB();
                return vr; // isValid() == true
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                vr.addError("system", "Không thể xóa! NXB này đang được sử dụng bởi một hoặc nhiều sách.");
                return vr;
            }
            e.printStackTrace();
        }

        vr.addError("system", "Lỗi hệ thống khi xóa nhà xuất bản!");
        return vr;
    }

    public ArrayList<PublisherDTO> search(String keyword) {
        ArrayList<PublisherDTO> result = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (PublisherDTO pub : publisherList) {
            if (pub.getName().toLowerCase().contains(key) || pub.getPhone().contains(key))
                result.add(pub);
        }
        return result;
    }
}