package GUI.model;

import BUS.CustomerBUS;
import DTO.CustomerDTO;
import GUI.dialog.CustomerDialog;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;

public class CustomerPanel extends JPanel implements FeatureControllerInterface {

    private CustomerBUS customerBUS = new CustomerBUS();
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerPanel() {
        initUI();
        loadDataToTable(customerBUS.getAll());
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = { "Mã KH", "Họ Tên", "Số Điện Thoại", "Điểm Tích Lũy", "Ngày Đăng Ký" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDataToTable(ArrayList<CustomerDTO> list) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tableModel.setRowCount(0);
        if (list != null) {
            for (CustomerDTO cus : list) {
                String dateStr = "";
                if (cus.getRegistrationDate() != null) {
                    dateStr = dateFormat.format(cus.getRegistrationDate());
                }
                tableModel.addRow(new Object[] {
                        cus.getCustomerId(), cus.getFullName(), cus.getPhone(),
                        cus.getLoyaltyPoints(), dateStr
                });
            }
        }
    }

    @Override
    public void onAdd() {
        CustomerDialog dialog = new CustomerDialog(null, true, "add", null, customerBUS);
        dialog.setVisible(true);
        onRefresh();
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        CustomerDTO selectedCus = customerBUS.getAll().stream().filter(c -> c.getCustomerId() == id).findFirst()
                .orElse(null);

        if (selectedCus != null) {
            CustomerDialog dialog = new CustomerDialog(null, true, "update", selectedCus, customerBUS);
            dialog.setVisible(true);
            onRefresh();
        }
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa khách hàng này?\n(Không thể xóa nếu khách đã từng mua hàng)", "Cảnh báo",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) table.getValueAt(row, 0);
            if (customerBUS.deleteCustomer(id)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                onRefresh();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa! Khách hàng này đã có lịch sử hóa đơn trong hệ thống.", "Lỗi Xóa",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onDetail() {
    }

    @Override
    public void onSearch(String text) {
        loadDataToTable(customerBUS.search(text));
    }

    @Override
    public void onRefresh() {
        loadDataToTable(customerBUS.getAll());
    }

    @Override
    public void onExportExcel() {
    }

    @Override
    public void onImportExcel() {
    }

    @Override
    public boolean[] getButtonConfig() {
        if (config.SessionManager.getCurrentAccount() == null) {
            return new boolean[] { false, false, false, false, false, false };
        }

        boolean canAdd = config.SessionManager.hasPermission(453, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(453, "Sửa");
        boolean canDelete = config.SessionManager.hasPermission(453, "Xóa");

        return new boolean[] { canAdd, canEdit, canDelete, false, false, false };
    }
}