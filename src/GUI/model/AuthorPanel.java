package GUI.model;

import BUS.AuthorBUS;
import DTO.AuthorDTO;
import DTO.ValidationResult;
import GUI.dialog.group.AuthorDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class AuthorPanel extends JPanel implements FeatureControllerInterface {
    private AuthorBUS authorBUS;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public AuthorPanel(AuthorBUS authorBUS) {
        this.authorBUS = authorBUS;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        String[] columns = { "ID Tác giả", "Tên tác giả" };
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
        for (AuthorDTO a : authorBUS.getAll())
            tableModel.addRow(new Object[] { a.getAuthorId(), a.getAuthorName() });
    }

    // ===================== ACTIONS =====================

    @Override
    public void onAdd() {
        openDialog(null);
    }

    @Override
    public void onEdit() {
        AuthorDTO selected = getSelectedAuthor();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả cần sửa!");
            return;
        }
        openDialog(selected);
    }

    @Override
    public void onDelete() {
        AuthorDTO selected = getSelectedAuthor();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa \"" + selected.getAuthorName() + "\"?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        ValidationResult vr = authorBUS.deleteAuthor(selected.getAuthorId());
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
        authorBUS.loadDataFromDB();
        loadDataToTable();
        JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu!");
    }

    @Override
    public void onExportExcel() {
        JOptionPane.showMessageDialog(this, "Chức năng xuất Excel Tác giả chưa được hỗ trợ!");
    }

    @Override
    public void onImportExcel() {
        JOptionPane.showMessageDialog(this, "Chức năng nhập Excel Tác giả chưa được hỗ trợ!");
    }

    @Override
    public boolean[] getButtonConfig() {
        return new boolean[] { true, true, true, false, false, false };
    }

    // ===================== UTILS =====================

    private AuthorDTO getSelectedAuthor() {
        int row = table.getSelectedRow();
        if (row == -1)
            return null;
        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        return authorBUS.getAll().stream().filter(a -> a.getAuthorId() == id).findFirst().orElse(null);
    }

    private void openDialog(AuthorDTO author) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (!(parent instanceof Frame))
            return;
        AuthorDialog dialog = new AuthorDialog((Frame) parent, author);
        dialog.setVisible(true);
        if (dialog.isSuccess())
            loadDataToTable();
    }
}