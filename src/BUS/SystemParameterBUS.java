package BUS;

import DAO.SystemParameterDAO;
import DTO.SystemParameterDTO;
import DTO.ValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BUS cho bảng system_parameters.
 * Dùng Singleton + Cache để tránh truy vấn DB nhiều lần.
 *
 * Cách dùng ở bất kỳ lớp nào:
 * double tyLe = SystemParameterBUS.getInstance().getDouble("TY_LE_TICH_DIEM");
 */
public class SystemParameterBUS {

    // ── Singleton ────────────────────────────────────────────────────────────
    private static SystemParameterBUS instance;

    public static SystemParameterBUS getInstance() {
        if (instance == null) {
            instance = new SystemParameterBUS();
        }
        return instance;
    }

    // ── Cache (load 1 lần khi khởi động) ─────────────────────────────────────
    private final Map<String, SystemParameterDTO> cache = new HashMap<>();
    private final SystemParameterDAO dao = new SystemParameterDAO();

    private SystemParameterBUS() {
        reloadCache();
    }

    public void reloadCache() {
        cache.clear();
        List<SystemParameterDTO> list = dao.getAll();
        for (SystemParameterDTO p : list) {
            cache.put(p.getParameterCode(), p);
        }
    }

    public String getString(String code) {
        SystemParameterDTO p = cache.get(code);
        return (p != null) ? p.getParameterValue() : null;
    }

    public int getInt(String code, int defaultValue) {
        try {
            SystemParameterDTO p = cache.get(code);
            return (p != null) ? p.getValueAsInt() : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String code, double defaultValue) {
        try {
            SystemParameterDTO p = cache.get(code);
            return (p != null) ? p.getValueAsDouble() : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public List<SystemParameterDTO> getAll() {
        return dao.getAll();
    }

    public boolean update(SystemParameterDTO param) {
        ValidationResult result = Validator.validateSystemParameter(param, getAll(), true);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrors().values().iterator().next());
        }
        boolean ok = dao.update(param);
        if (ok) {
            cache.put(param.getParameterCode(), param);
        }
        return ok;
    }

    public boolean insert(SystemParameterDTO param) {
        ValidationResult result = Validator.validateSystemParameter(param, getAll(), false);
        if (!result.isValid()) {
            // Ném lỗi đầu tiên tìm thấy lên cho GUI xử lý
            throw new IllegalArgumentException(result.getErrors().values().iterator().next());
        }
        boolean ok = dao.insert(param);
        if (ok) {
            cache.put(param.getParameterCode(), param);
        }
        return ok;
    }

    // ── Các hằng tên tham số (tránh viết tay chuỗi) ──────────────────────────
    public static final String TY_LE_TICH_DIEM = "TY_LE_TICH_DIEM";
    public static final String TY_LE_QUI_DOI_DIEM = "TY_LE_QUI_DOI_DIEM";
    public static final String SO_LUONG_TOI_THIEU_CANH_BAO = "SO_LUONG_TOI_THIEU_CANH_BAO";
    public static final String THOI_GIAN_LUU_HOA_DON = "THOI_GIAN_LUU_HOA_DON";
}