package GUI.dialog.book;

import DTO.AuthorDTO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorSelectionDialog extends JDialog {
    private JList<AuthorDTO> listAuthors;
    private List<AuthorDTO> selectedAuthors;
    private boolean isConfirmed = false;

    public AuthorSelectionDialog(JFrame parent, List<AuthorDTO> allAuthors, List<AuthorDTO> preSelected) {
        super(parent, "Chọn Tác Giả", true);
        this.selectedAuthors = new ArrayList<>();

        initComponents(allAuthors, preSelected);
        setSize(500, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents(List<AuthorDTO> allAuthors, List<AuthorDTO> preSelected) {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 248, 250));

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Main content - List of authors
        add(createMainPanel(allAuthors, preSelected), BorderLayout.CENTER);

        // Footer - Buttons
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 108, 189));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Chọn Tác Giả");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Chọn một hoặc nhiều tác giả từ danh sách");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(230, 230, 230));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblSub, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMainPanel(List<AuthorDTO> allAuthors, List<AuthorDTO> preSelected) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Search box
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180)),
                new EmptyBorder(5, 8, 5, 8)));

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // Authors list
        DefaultListModel<AuthorDTO> model = new DefaultListModel<>();
        for (AuthorDTO author : allAuthors) {
            model.addElement(author);
        }

        listAuthors = new JList<>(model);
        listAuthors.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAuthors.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listAuthors.setCellRenderer(new AuthorCellRenderer());
        listAuthors.setFixedCellHeight(40);

        // Pre-select authors if any
        if (preSelected != null && !preSelected.isEmpty()) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < model.getSize(); i++) {
                AuthorDTO author = model.getElementAt(i);
                for (AuthorDTO selected : preSelected) {
                    if (author.getAuthorId() == selected.getAuthorId()) {
                        indices.add(i);
                        break;
                    }
                }
            }
            int[] selectedIndices = indices.stream().mapToInt(Integer::intValue).toArray();
            listAuthors.setSelectedIndices(selectedIndices);
        }

        JScrollPane scrollPane = new JScrollPane(listAuthors);
        scrollPane.setBorder(new LineBorder(new Color(180, 180, 180)));

        // Search functionality
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String keyword = txtSearch.getText().toLowerCase().trim();
                DefaultListModel<AuthorDTO> filteredModel = new DefaultListModel<>();
                for (AuthorDTO author : allAuthors) {
                    if (author.getAuthorName().toLowerCase().contains(keyword)) {
                        filteredModel.addElement(author);
                    }
                }
                listAuthors.setModel(filteredModel);
            }
        });

        // Info label
        JLabel lblInfo = new JLabel("💡 Nhấn Ctrl để chọn nhiều tác giả");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(100, 100, 100));
        lblInfo.setBorder(new EmptyBorder(5, 0, 0, 0));

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(lblInfo, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(new Color(245, 248, 250));
        panel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(180, 180, 180)));

        JButton btnCancel = new JButton("Hủy");
        JButton btnConfirm = new JButton("Xác nhận");

        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);

        btnConfirm.setPreferredSize(new Dimension(120, 35));
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBackground(new Color(15, 108, 189));
        btnConfirm.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> {
            isConfirmed = false;
            dispose();
        });

        btnConfirm.addActionListener(e -> {
            selectedAuthors = listAuthors.getSelectedValuesList();
            if (selectedAuthors.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn ít nhất một tác giả!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            isConfirmed = true;
            dispose();
        });

        panel.add(btnCancel);
        panel.add(btnConfirm);
        return panel;
    }

    public List<AuthorDTO> getSelectedAuthors() {
        return selectedAuthors;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    // Custom cell renderer for better UI
    private static class AuthorCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (value instanceof AuthorDTO) {
                AuthorDTO author = (AuthorDTO) value;
                label.setText("  " + author.getAuthorName());
                label.setBorder(new EmptyBorder(8, 10, 8, 10));
            }

            if (isSelected) {
                label.setBackground(new Color(15, 108, 189));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }

            return label;
        }
    }
}