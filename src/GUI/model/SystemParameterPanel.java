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
    private JButton btnCapNhat;

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
        formPanel.setBorder(BorderFactory.createTitledBorder("Chi Tiết Cấu Hình (Chỉ cho phép sửa Giá Trị)"));

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

        // Hàng 3: Giá trị (Cho phép nhập)
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

        // Nút Cập nhật
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCapNhat = new JButton("Lưu Cập Nhật Cấu Hình");
        btnCapNhat.setFont(new Font("Arial", Font.BOLD, 14));
        btnCapNhat.setBackground(new Color(50, 150, 250));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(btnCapNhat);

        // Bắt sự kiện nút Cập nhật
        btnCapNhat.addActionListener(e -> updateParameter());

        formPanel.add(inputPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.EAST);

        // ==========================================
        // GẮN VÀO PANEL CHÍNH
        // ==========================================
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(formPanel, BorderLayout.SOUTH);
    }

    /**
     * Tải dữ liệu từ DB (thông qua BUS) lên JTable
     */
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

    /**
     * Lấy dữ liệu từ dòng được chọn đổ xuống Form chỉnh sửa
     */
    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaThamSo.setText(tableModel.getValueAt(row, 0).toString());
            txtGiaTri.setText(tableModel.getValueAt(row, 1).toString());
            txtMoTa.setText(tableModel.getValueAt(row, 2).toString());
        }
    }

    /**
     * Xử lý logic khi bấm nút Cập nhật
     */
    private void updateParameter() {
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

    // =====================================================================
    // IMPLEMENTS FEATURE CONTROLLER INTERFACE
    // =====================================================================

    /**
     * Ẩn toàn bộ 6 nút trên thanh Toolbar (Add, Edit, Delete, Detail, Import,
     * Export)
     * Vì màn hình cấu hình này sử dụng nút "Lưu Cập Nhật" trực tiếp trên Panel.
     */
    @Override
    public boolean[] getButtonConfig() {
        return new boolean[] { false, false, false, false, false, false };
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
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<SystemParameterDTO> list = SystemParameterBUS.getInstance().getAll();
        String keyword = text.toLowerCase().trim();

        // Lọc theo Mã hoặc Mô tả
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
        // Yêu cầu BUS tải lại cấu hình từ CSDL (Phòng trường hợp Admin khác vừa đổi)
        SystemParameterBUS.getInstance().reloadCache();

        loadDataToTable();

        // Làm sạch form bên dưới
        txtMaThamSo.setText("");
        txtGiaTri.setText("");
        txtMoTa.setText("");
        table.clearSelection();
    }

    // Các tính năng dưới đây không được phép sử dụng ở màn hình này, để trống hàm
    @Override
    public void onAdd() {
    }

    @Override
    public void onEdit() {
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