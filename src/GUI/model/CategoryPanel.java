package GUI.model;

import BUS.CategoryBUS;
import DTO.CategoryDTO;
import DTO.ValidationResult;
import GUI.dialog.group.CategoryDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class CategoryPanel extends JPanel implements FeatureControllerInterface {
    private CategoryBUS categoryBUS;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public CategoryPanel(CategoryBUS categoryBUS) {
        this.categoryBUS = categoryBUS;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        String[] columns = { "ID Thể loại", "Tên thể loại", "Thứ tự", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        for (CategoryDTO cat : categoryBUS.getAll()) {
            tableModel.addRow(new Object[] { cat.getId(), cat.getName(), cat.getDisplayOrder(), cat.getStatus() });
        }
    }

    // ===================== ACTIONS =====================

    @Override
    public void onAdd() {
        openDialog(null);
    }

    @Override
    public void onEdit() {
        CategoryDTO selected = getSelected();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần sửa!");
            return;
        }
        openDialog(selected);
    }

    @Override
    public void onDelete() {
        CategoryDTO selected = getSelected();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa thể loại \"" + selected.getName() + "\"?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        ValidationResult vr = categoryBUS.delete(selected.getId());
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
        categoryBUS.loadDataFromDB();
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
        if (config.SessionManager.getCurrentAccount() == null) {
            return new boolean[] { false, false, false, false, false, false };
        }

        boolean canAdd = config.SessionManager.hasPermission(452, "Thêm");
        boolean canEdit = config.SessionManager.hasPermission(452, "Sửa");
        boolean canDelete = config.SessionManager.hasPermission(452, "Xóa");
        boolean canDetail = config.SessionManager.hasPermission(452, "Xem");

        return new boolean[] { canAdd, canEdit, canDelete, canDetail, false, false };
    }

    // ===================== UTILS =====================

    private CategoryDTO getSelected() {
        int row = table.getSelectedRow();
        if (row < 0)
            return null;
        int id = (int) table.getValueAt(row, 0);
        return categoryBUS.getById(id);
    }

    private void openDialog(CategoryDTO cat) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (!(parent instanceof Frame))
            return;
        CategoryDialog dialog = new CategoryDialog((Frame) parent, cat);
        dialog.setVisible(true);
        if (dialog.isSuccess())
            loadDataToTable();
    }
}