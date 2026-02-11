package GUI.model;

import BUS.AuthorBUS;
import DTO.AuthorDTO;
import GUI.dialog.group.AuthorDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.util.ArrayList;

public class AuthorPanel extends JPanel implements FeatureControllerInterface {

    private AuthorBUS authorBUS; // Tái sử dụng lớp xử lý nghiệp vụ
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public AuthorPanel() {
        this.authorBUS = new AuthorBUS(); // Khởi tạo BUS để load dữ liệu từ DB
        initComponents();
        loadDataToTable(); // Nạp dữ liệu vào bảng ngay khi mở panel
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        // --- 2. TABLE DỮ LIỆU ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = { "ID Tác giả", "Tên tác giả" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Chỉ cho xem, không cho sửa trực tiếp trên ô
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35); // Độ cao dòng cho thoáng
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Đổ dữ liệu từ danh sách cache của BUS vào bảng
     */
    public void loadDataToTable() {
        tableModel.setRowCount(0);
        ArrayList<AuthorDTO> list = authorBUS.getAll(); // Lấy list từ BUS
        for (AuthorDTO author : list) {
            tableModel.addRow(new Object[] {
                    author.getAuthorId(),
                    author.getAuthorName()
            });
        }
    }

    @Override
    public void onAdd() {
        openDialog(null); // Null hiểu là thêm mới
    }

    @Override
    public void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả cần sửa!");
            return;
        }
        // Lấy ID từ dòng đã chọn (cột 0)
        int id = Integer.parseInt(table.getValueAt(row, 0).toString());

        // Tìm DTO tương ứng
        AuthorDTO selected = null;
        for (AuthorDTO a : authorBUS.getAll()) {
            if (a.getAuthorId() == id) {
                selected = a;
                break;
            }
        }

        if (selected != null) {
            openDialog(selected);
        }
    }

    @Override
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả để xóa!");
            return;
        }

        JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tác giả này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
    }

    @Override
    public void onDetail() {
        onEdit();
    }

    @Override
    public void onSearch(String text) {
        if (text.trim().isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            // Tìm kiếm không phân biệt hoa thường trên cột Tên (cột 1)
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
        }
    }

    @Override
    public void onRefresh() {
        authorBUS.loadData(); // Load lại từ DB
        loadDataToTable(); // Vẽ lại bảng
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

    private void openDialog(AuthorDTO author) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        // Kiểm tra xem parent có phải là Frame không để tránh lỗi cast
        if (parent instanceof Frame) {
            AuthorDialog dialog = new AuthorDialog((Frame) parent, author);
            dialog.setVisible(true);

            if (dialog.isSuccess()) {
                authorBUS.loadData();
                loadDataToTable();
            }
        }
    }
}