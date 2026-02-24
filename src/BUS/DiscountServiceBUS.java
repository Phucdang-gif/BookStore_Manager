package BUS;

import DAO.DiscountServiceDAO;
import DTO.DiscountServiceDTO;
import java.util.ArrayList;

public class DiscountServiceBUS {

    private DiscountServiceDAO dao = new DiscountServiceDAO();
    private ArrayList<DiscountServiceDTO> listDiscount;

    public DiscountServiceBUS() {
        this.listDiscount = dao.getAll();
    }

    public ArrayList<DiscountServiceDTO> getAll() {
        return this.listDiscount;
    }

    public boolean addDiscount(DiscountServiceDTO dto) {
        boolean success = dao.insert(dto);
        if (success) this.listDiscount = dao.getAll();
        return success;
    }

    public boolean updateDiscount(DiscountServiceDTO dto) {
        boolean success = dao.update(dto);
        if (success) this.listDiscount = dao.getAll();
        return success;
    }

    public boolean deleteDiscount(int id) {
        boolean success = dao.delete(id);
        if (success) this.listDiscount = dao.getAll();
        return success;
    }

    // HÀM QUAN TRỌNG: Lấy ra các mã Khuyến mãi CÒN HẠN SỬ DỤNG và ĐANG ACTIVE để hiển thị bên phần Bán Hàng
    public ArrayList<DiscountServiceDTO> getValidPromotions() {
        ArrayList<DiscountServiceDTO> validList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        
        for (DiscountServiceDTO d : listDiscount) {
            if(d.getEndDate() != null&& d.getStartDate()!=null){
                    if ("active".equals(d.getStatus()) && 
                    d.getStartDate().getTime() <= currentTime && 
                    d.getEndDate().getTime() >= currentTime) {
                    validList.add(d);
                }
            } // Bỏ qua nếu không có ngày kết thúc (đảm bảo không bị lỗi NullPointerException)
            
        }
        return validList;
    }

    public ArrayList<DiscountServiceDTO> search(String text) {
        ArrayList<DiscountServiceDTO> result = new ArrayList<>();
        text = text.toLowerCase().trim();
        if (text.isEmpty()) return this.listDiscount;

        for (DiscountServiceDTO d : listDiscount) {
            if (d.getServiceName().toLowerCase().contains(text) || 
                String.valueOf(d.getServiceId()).contains(text)) {
                result.add(d);
            }
        }
        return result;
    }
}
