package GUI.model;

import BUS.SystemParameterBUS;
import DTO.SystemParameterDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SystemParameterPanel extends JPanel implements FeatureControllerInterface {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaThamSo;
    private JTextField txtGiaTri;
    private JTextArea txtMoTa;

    public SystemParameterPanel() {
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        // Cài đặt Layout chính cho Panel
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = { "Mã Tham Số (Code)", "Giá Trị Đang Áp Dụng", "Mô Tả Chi Tiết" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // KHÓA: Không cho phép sửa trực tiếp trên ô của bảng
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Tham số Cấu hình Hệ thống"));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromSelectedRow();
            }
        });

        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory
                .createTitledBorder("Chi Tiết Cấu Hình (Chọn trên bảng rồi nhấn 'Sửa' trên thanh công cụ)"));

        // Lưới chứa các ô nhập liệu
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Hàng 1: Mã tham số (Chỉ đọc)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        inputPanel.add(new JLabel("Mã Tham Số:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        txtMaThamSo = new JTextField();
        txtMaThamSo.setEditable(false); // KHÓA: Tránh việc sửa nhầm mã
        txtMaThamSo.setBackground(new Color(240, 240, 240));
        inputPanel.add(txtMaThamSo, gbc);

        // Hàng 2: Mô tả (Chỉ đọc)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        inputPanel.add(new JLabel("Mô Tả:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.9;
        txtMoTa = new JTextArea(2, 20);
        txtMoTa.setEditable(false); // KHÓA: Admin không cần sửa mô tả
        txtMoTa.setBackground(new Color(240, 240, 240));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        inputPanel.add(new JScrollPane(txtMoTa), gbc);

        // Hàng 3: Giá trị (Cho phép nhập để chuẩn bị Sửa)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        inputPanel.add(new JLabel("Giá Trị Mới (*):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.9;
        txtGiaTri = new JTextField();
        txtGiaTri.setFont(new Font("Arial", Font.BOLD, 14));
        txtGiaTri.setForeground(Color.RED);
        inputPanel.add(txtGiaTri, gbc);

        formPanel.add(inputPanel, BorderLayout.CENTER);

        // Đã xóa nút btnCapNhat và buttonPanel ở đây vì logic chuyển xuống onEdit()

        // ==========================================
        // GẮN VÀO PANEL CHÍNH
        // ==========================================
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(formPanel, BorderLayout.SOUTH);
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ trên bảng
        List<SystemParameterDTO> list = SystemParameterBUS.getInstance().getAll();

        for (SystemParameterDTO p : list) {
            tableModel.addRow(new Object[] {
                    p.getParameterCode(),
                    p.getParameterValue(),
                    p.getDescription()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaThamSo.setText(tableModel.getValueAt(row, 0).toString());
            txtGiaTri.setText(tableModel.getValueAt(row, 1).toString());
            txtMoTa.setText(tableModel.getValueAt(row, 2).toString());
        }
    }

    // =====================================================================
    // IMPLEMENTS FEATURE CONTROLLER INTERFACE
    // =====================================================================

    @Override
    public boolean[] getButtonConfig() {
        if (config.SessionManager.getCurrentAccount() == null) {
            return new boolean[] { false, false, false, false, false, false };
        }
        boolean canAdd = config.SessionManager.hasPermission(459, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(459, "Sửa");
        return new boolean[] { canAdd, canEdit, false, false, false, false };
    }

    @Override
    public boolean hasSearch() {
        return true;
    }

    @Override
    public boolean hasRefresh() {
        return true;
    }

    @Override
    public void onSearch(String text) {
        tableModel.setRowCount(0);
        List<SystemParameterDTO> list = SystemParameterBUS.getInstance().getAll();
        String keyword = text.toLowerCase().trim();

        for (SystemParameterDTO p : list) {
            if (p.getParameterCode().toLowerCase().contains(keyword) ||
                    p.getDescription().toLowerCase().contains(keyword)) {

                tableModel.addRow(new Object[] {
                        p.getParameterCode(),
                        p.getParameterValue(),
                        p.getDescription()
                });
            }
        }
    }

    @Override
    public void onRefresh() {
        SystemParameterBUS.getInstance().reloadCache();
        loadDataToTable();

        txtMaThamSo.setText("");
        txtGiaTri.setText("");
        txtMoTa.setText("");
        table.clearSelection();
    }

    @Override
    public void onAdd() {
        // Tạo hộp thoại để nhập tham số mới
        JTextField txtNewCode = new JTextField();
        JTextField txtNewValue = new JTextField();
        JTextArea txtNewDesc = new JTextArea(3, 20);
        txtNewDesc.setLineWrap(true);
        txtNewDesc.setWrapStyleWord(true);

        Object[] message = {
                "Mã Tham Số (Code):", txtNewCode,
                "Giá Trị:", txtNewValue,
                "Mô Tả:", new JScrollPane(txtNewDesc)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Thêm Tham Số Cấu Hình Mới",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String code = txtNewCode.getText().trim();
            String value = txtNewValue.getText().trim();
            String desc = txtNewDesc.getText().trim();

            if (code.isEmpty() || value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã tham số và giá trị không được để trống!", "Lỗi nhập liệu",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra trùng lặp mã tham số
            if (SystemParameterBUS.getInstance().getString(code) != null) {
                JOptionPane.showMessageDialog(this, "Mã tham số này đã tồn tại trong hệ thống!", "Lỗi trùng lặp",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            SystemParameterDTO newParam = new SystemParameterDTO(code, value, desc);
            boolean success = SystemParameterBUS.getInstance().insert(newParam);

            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm tham số thành công!", "Hoàn tất",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable(); // Tải lại bảng
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu. Không thể thêm!", "Lỗi hệ thống",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onEdit() {
        // Chuyển toàn bộ logic của updateParameter() cũ vào đây
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng click chọn một tham số trên bảng để cập nhật!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = txtMaThamSo.getText().trim();
        String newValue = txtGiaTri.getText().trim();
        String desc = txtMoTa.getText().trim();

        if (newValue.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Giá trị không được để trống!",
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtGiaTri.requestFocus();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đổi giá trị của [" + code + "] thành [" + newValue + "]?\n" +
                        "Hệ thống sẽ áp dụng ngay lập tức cho các giao dịch tiếp theo.",
                "Xác nhận thay đổi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SystemParameterDTO param = new SystemParameterDTO(code, newValue, desc);
            boolean success = SystemParameterBUS.getInstance().update(param);

            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Hoàn tất",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();

                for (int i = 0; i < table.getRowCount(); i++) {
                    if (table.getValueAt(i, 0).toString().equals(code)) {
                        table.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lỗi kết nối cơ sở dữ liệu. Không thể cập nhật!",
                        "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onDelete() {
    }

    @Override
    public void onDetail() {
    }

    @Override
    public void onExportExcel() {
    }

    @Override
    public void onImportExcel() {
    }
}