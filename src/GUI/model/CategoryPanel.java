package GUI.model;

import BUS.CategoryBUS;
import DTO.CategoryDTO;
import GUI.dialog.group.CategoryDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.util.ArrayList;

public class CategoryPanel extends JPanel implements FeatureControllerInterface {

    private CategoryBUS categoryBUS;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public CategoryPanel() {
        this.categoryBUS = new CategoryBUS();
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Thêm cột Thứ tự (Display Order) vào bảng theo đúng DB
        String[] columns = { "ID Thể loại", "Tên thể loại", "Thứ tự", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        ArrayList<CategoryDTO> list = categoryBUS.getAll();
        for (CategoryDTO cat : list) {
            tableModel.addRow(new Object[] {
                    cat.getId(),
                    cat.getName(),
                    cat.getDisplayOrder(), // Giả định bạn đã thêm field này vào DTO
                    cat.getStatus()
            });
        }
    }

    @Override
    public void onAdd() {
        openDialog(null);
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần sửa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        CategoryDTO cat = categoryBUS.getById(id);
        openDialog(cat);
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần xóa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa thể loại này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (categoryBUS.delete(id)) {
                loadDataToTable();
            }
        }
    }

    private void openDialog(CategoryDTO cat) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof Frame) {
            CategoryDialog dialog = new CategoryDialog((Frame) parent, cat);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadDataToTable();
            }
        }
    }

    @Override
    public void onSearch(String text) {
        if (text.trim().isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
        }
    }

    @Override
    public void onRefresh() {
        categoryBUS.loadData();
        loadDataToTable();
    }

    @Override
    public void onExportExcel() {

    }

    @Override
    public void onImportExcel() {

    }

    @Override
    public boolean[] getButtonConfig() {
        return new boolean[] { true, true, true, false, false, false };
    }

    @Override
    public void onDetail() {

    }

}