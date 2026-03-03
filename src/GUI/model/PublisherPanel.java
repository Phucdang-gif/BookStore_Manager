package GUI.model;

import BUS.PublisherBUS;
import DTO.PublisherDTO;
import DTO.ValidationResult;
import GUI.dialog.group.PublisherDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class PublisherPanel extends JPanel implements FeatureControllerInterface {
    private PublisherBUS publisherBUS;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public PublisherPanel(PublisherBUS publisherBUS) {
        this.publisherBUS = publisherBUS;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        String[] columns = { "ID", "Tên NXB", "Số điện thoại", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        for (PublisherDTO pub : publisherBUS.getAll()) {
            tableModel.addRow(new Object[] { pub.getId(), pub.getName(), pub.getPhone(), pub.getStatus() });
        }
    }

    // ===================== ACTIONS =====================

    @Override
    public void onAdd() {
        openDialog(null);
    }

    @Override
    public void onEdit() {
        PublisherDTO selected = getSelected();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn NXB cần sửa!");
            return;
        }
        openDialog(selected);
    }

    @Override
    public void onDelete() {
        PublisherDTO selected = getSelected();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn NXB cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa NXB \"" + selected.getName() + "\"?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        ValidationResult vr = publisherBUS.delete(selected.getId());
        if (vr.isValid()) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loadDataToTable();
        } else {
            JOptionPane.showMessageDialog(this, vr.getSummary(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onDetail() {
        onEdit();
    }

    @Override
    public void onSearch(String text) {
        rowSorter.setRowFilter(text.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + text, 1));
    }

    @Override
    public void onRefresh() {
        publisherBUS.loadDataFromDB();
        loadDataToTable();
        JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu!");
    }

    @Override
    public void onExportExcel() {
    }

    @Override
    public void onImportExcel() {
    }

    @Override
    public boolean[] getButtonConfig() {
        DTO.AccountDTO currentAcc = config.SessionManager.getCurrentAccount();
        if (currentAcc == null)
            return new boolean[] { false, false, false, false, false, false };
        return new boolean[] { true, true, true, true, false, false };
    }

    // ===================== UTILS =====================

    private PublisherDTO getSelected() {
        int row = table.getSelectedRow();
        if (row < 0)
            return null;
        int id = (int) table.getValueAt(row, 0);
        return publisherBUS.getById(id);
    }

    private void openDialog(PublisherDTO pub) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (!(parent instanceof Frame))
            return;
        PublisherDialog dialog = new PublisherDialog((Frame) parent, pub);
        dialog.setVisible(true);
        if (dialog.isSuccess())
            loadDataToTable();
    }
}