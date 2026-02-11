package GUI.model;

import BUS.PublisherBUS;
import DTO.PublisherDTO;
import GUI.dialog.group.PublisherDialog;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;

public class PublisherPanel extends JPanel implements FeatureControllerInterface {
    private PublisherBUS publisherBUS;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public PublisherPanel() {
        this.publisherBUS = new PublisherBUS();
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        String[] columns = { "ID", "Tên NXB", "Số điện thoại", "Trạng thái" };
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        ArrayList<PublisherDTO> list = publisherBUS.getAll();
        for (PublisherDTO pub : list) {
            tableModel.addRow(new Object[] { pub.getId(), pub.getName(), pub.getPhone(), pub.getStatus() });
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn NXB cần sửa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        // Giả sử BUS có hàm getById
        PublisherDTO pub = publisherBUS.getById(id);
        // Tạm thời lấy từ danh sách hiện tại hoặc gọi DB
        openDialog(null); // Bạn nên bổ sung hàm getById vào BUS
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

    private void openDialog(PublisherDTO pub) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof Frame) {
            PublisherDialog dialog = new PublisherDialog((Frame) parent, pub);
            dialog.setVisible(true);
            if (dialog.isSuccess())
                loadDataToTable();
        }
    }

    @Override
    public void onSearch(String text) {
        rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
    }

    @Override
    public void onRefresh() {
        publisherBUS.loadData();
        loadDataToTable();
    }

    @Override
    public boolean[] getButtonConfig() {
        return new boolean[] { true, true, true, false, false, false };
    }
}