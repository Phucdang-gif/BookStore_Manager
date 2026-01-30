package GUI.dialog.book;

import DTO.AuthorDTO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;
import BUS.BookBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;
import BUS.AuthorBUS;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
// -------------------------------

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookDialogController {
    private BookDialogView view;
    private BookDTO bookDTO;
    private BookBUS bookBUS;
    private AuthorBUS authorBUS;
    private DialogMode mode;
    private boolean isSuccess = false;
    private String selectedImagePath = "";

    private List<AuthorDTO> currentAuthors = new ArrayList<>();
    private List<AuthorDTO> allAuthors;
    private List<CategoryDTO> listCategories;
    private List<PublisherDTO> listPublishers;

    public BookDialogController(BookDialogView view, BookDTO book, DialogMode mode) {
        this.view = view;
        this.bookDTO = book;
        this.mode = mode;
        this.bookBUS = new BookBUS();
        this.authorBUS = new AuthorBUS();
        this.allAuthors = authorBUS.getAll();
    }

    public void loadComboBoxData() {
        view.cbCategory.removeAllItems();
        view.cbPublisher.removeAllItems();
        view.cbCategory.addItem("-- Chọn danh mục --");
        view.cbPublisher.addItem("-- Chọn NXB --");

        CategoryBUS catBUS = new CategoryBUS();
        this.listCategories = catBUS.getAll(); // Lưu vào biến class
        for (CategoryDTO cat : listCategories) {
            view.cbCategory.addItem(cat.getName());
        }

        PublisherBUS pubBUS = new PublisherBUS();
        this.listPublishers = pubBUS.getAll(); // Lưu vào biến class
        for (PublisherDTO pub : listPublishers) {
            view.cbPublisher.addItem(pub.getName());
        }

        view.cbStatus.setSelectedItem("Còn hàng");
        view.cbCoverType.setSelectedItem("Bìa mềm");
    }

    public void applyModeSettings() {
        setFormEditable(false);
        view.lblTitle.setText("Chi tiết Sách");
        view.btnSave.setVisible(false);
        view.btnCancel.setText("Đóng");
        switch (mode) {
            case ADD:
                view.lblTitle.setText("Thêm Sách Mới");
                view.btnSave.setText("Lưu thông tin");
                setFormEditable(true);
                view.btnSave.setVisible(true);
                view.btnCancel.setText("Hủy bỏ");
                view.btnUpload.setVisible(true);
                break;
            case EDIT:
                view.lblTitle.setText("Chỉnh Sửa Thông Tin Sách");
                view.btnSave.setText("Cập nhật");
                view.btnSave.setVisible(true);
                setFormEditable(true);
                view.txtIsbn.setEditable(false);
                break;
            case READ:
                view.lblTitle.setText("Chi Tiết Sách");
                view.btnSave.setVisible(false);
                view.btnCancel.setText("Đóng");
                setFormEditable(false); // Khóa toàn bộ
                break;
        }
    }

    private void setFormEditable(boolean editable) {
        view.txtTitle.setEditable(editable);
        view.txtIsbn.setEditable(editable);
        view.txtYear.setEditable(editable);
        view.txtPage.setEditable(editable);
        view.txtPriceImport.setEditable(editable);
        view.txtPriceExport.setEditable(editable);
        view.txtQuantity.setEditable(editable);
        view.cbCategory.setEnabled(editable);
        view.cbPublisher.setEnabled(editable);
        view.cbStatus.setEnabled(editable);
        view.cbCoverType.setEnabled(editable);
        view.txtLanguage.setEditable(editable);
        view.txtMinStock.setEditable(editable);

        // --- XỬ LÝ UI TÁC GIẢ ---
        view.txtAuthorSearch.setVisible(editable);
        view.btnAuthorAdd.setVisible(editable);
        view.lblAddNewAuthor.setVisible(editable);

        view.btnUpload.setEnabled(editable);
        // Vẽ lại tags (để hiện hoặc ẩn nút xóa 'x')
        renderAuthorTags(editable);
    }

    public void fillData() {
        if (bookDTO == null) {
            view.cbStatus.setSelectedItem("Còn hàng");
            view.cbCoverType.setSelectedItem("Bìa mềm");
            return;
        }
        // ------------------------------

        // (Đoạn code cũ bên dưới giữ nguyên)
        view.txtTitle.setText(bookDTO.getBookTitle());
        view.txtIsbn.setText(bookDTO.getIsbn());
        // Đổ dữ liệu text
        view.txtTitle.setText(bookDTO.getBookTitle());
        view.txtIsbn.setText(bookDTO.getIsbn());
        view.txtYear.setText(String.valueOf(bookDTO.getPublicationYear()));
        view.txtPage.setText(String.valueOf(bookDTO.getPageCount()));
        view.txtPriceImport.setText(String.valueOf(bookDTO.getImportPrice()));
        view.txtPriceExport.setText(String.valueOf(bookDTO.getSellingPrice()));
        view.txtQuantity.setText(String.valueOf(bookDTO.getStockQuantity()));
        view.txtLanguage.setText(bookDTO.getLanguage()); // Ngôn ngữ
        view.txtMinStock.setText(String.valueOf(bookDTO.getMinimumStock())); // Tồn kho tối thiểu

        // ComboBox (chỉ hiển thị text vì đã disable)
        view.cbCategory.addItem(bookDTO.getCategoryName());
        view.cbPublisher.addItem(bookDTO.getPublisherName());
        view.cbCategory.setSelectedItem(bookDTO.getCategoryName());
        view.cbPublisher.setSelectedItem(bookDTO.getPublisherName());

        view.cbStatus.setSelectedItem(bookDTO.getStatus());
        view.cbCoverType.setSelectedItem(bookDTO.getCoverType());

        // LOAD ẢNH (Nếu có)
        if (bookDTO.getImage() != null && !bookDTO.getImage().isEmpty()) {
            this.selectedImagePath = bookDTO.getImage();
            ImageIcon icon = new ImageIcon(bookDTO.getImage());
            Image img = icon.getImage().getScaledInstance(250, 360, Image.SCALE_SMOOTH);
            view.lblImagePreview.setIcon(new ImageIcon(img));
            view.lblImagePreview.setText("");
        }

        if (bookDTO.getAuthors() != null) {
            this.currentAuthors = new ArrayList<>(bookDTO.getAuthors());
            renderAuthorTags(mode != DialogMode.READ); // Nếu không phải xem thì hiện nút xóa
        }

        String statusEN = bookDTO.getStatus(); // DB trả về: IN_STOCK, OUT_OF_STOCK...
        if (statusEN != null) {
            switch (statusEN) {
                case "IN_STOCK":
                    view.cbStatus.setSelectedItem("Còn hàng");
                    break;
                case "OUT_OF_STOCK":
                    view.cbStatus.setSelectedItem("Hết hàng");
                    break;
                case "SUSPENDED":
                    view.cbStatus.setSelectedItem("Ngừng kinh doanh");
                    break;
                default:
                    view.cbStatus.setSelectedItem("Còn hàng");
            }
        }
    }

    private void renderAuthorTags(boolean isEditable) {
        view.pnlAuthorTags.removeAll();
        for (AuthorDTO author : currentAuthors) {
            JPanel tag = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            tag.setBackground(new Color(225, 225, 225));
            tag.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel lblName = new JLabel(author.getAuthorName());
            tag.add(lblName);

            // Chỉ thêm nút X khi đang ở chế độ sửa/thêm
            if (isEditable) {
                JLabel lblX = new JLabel("x");
                lblX.setForeground(Color.RED);
                lblX.setCursor(new Cursor(Cursor.HAND_CURSOR));
                lblX.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

                // Sự kiện xóa tag
                lblX.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        currentAuthors.remove(author);
                        renderAuthorTags(true); // Vẽ lại
                    }
                });
                tag.add(lblX);
            }
            view.pnlAuthorTags.add(tag);
        }
        view.pnlAuthorTags.revalidate();
        view.pnlAuthorTags.repaint();
    }

    private void showAuthorSuggestions(String keyword) {
        if (keyword.isEmpty()) {
            view.popupAuthorSuggestions.setVisible(false);
            return;
        }

        // Lọc danh sách: Có tên chứa từ khóa VÀ chưa được chọn
        List<AuthorDTO> filtered = allAuthors.stream()
                .filter(a -> a.getAuthorName().toLowerCase().contains(keyword.toLowerCase()))
                .filter(a -> !currentAuthors.contains(a))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            view.popupAuthorSuggestions.setVisible(false);
        } else {
            view.listAuthorSuggestions.setListData(filtered.toArray(new AuthorDTO[0]));
            view.popupAuthorSuggestions.pack();
            view.popupAuthorSuggestions.setPopupSize(view.txtAuthorSearch.getWidth(), 150);
            view.popupAuthorSuggestions.show(view.txtAuthorSearch, 0, view.txtAuthorSearch.getHeight());
            view.txtAuthorSearch.requestFocus();
        }
    }

    private void addAuthorToSelection(AuthorDTO author) {
        if (!currentAuthors.contains(author)) {
            currentAuthors.add(author);
            view.txtAuthorSearch.setText(""); // Xóa ô tìm kiếm
            view.popupAuthorSuggestions.setVisible(false); // Ẩn popup
            renderAuthorTags(true); // Vẽ lại tags
        }
    }

    public boolean isSucceeded() {
        return isSuccess;
    }

    public void initEvents() {
        view.btnCancel.addActionListener(e -> {
            ((JDialog) SwingUtilities.getWindowAncestor(view)).dispose();
        });

        // --- SỰ KIỆN TÌM KIẾM TÁC GIẢ ---
        view.txtAuthorSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                showAuthorSuggestions(view.txtAuthorSearch.getText().trim());
            }
        });

        // --- SỰ KIỆN CHỌN TỪ GỢI Ý ---
        view.listAuthorSuggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AuthorDTO selected = view.listAuthorSuggestions.getSelectedValue();
                if (selected != null) {
                    addAuthorToSelection(selected);
                }
            }
        });

        // --- SỰ KIỆN NÚT ADD (+) ---
        view.btnAuthorAdd.addActionListener(e -> {
            // Logic: Nếu gõ tên đúng 100% với có sẵn thì add, không thì báo lỗi hoặc hỏi
            // thêm mới
            String keyword = view.txtAuthorSearch.getText().trim();
            if (!keyword.isEmpty()) {
                AuthorDTO match = allAuthors.stream()
                        .filter(a -> a.getAuthorName().equalsIgnoreCase(keyword))
                        .findFirst().orElse(null);
                if (match != null) {
                    addAuthorToSelection(match);
                } else {
                    JOptionPane.showMessageDialog(view, "Tác giả chưa có trong hệ thống! Vui lòng thêm mới.");
                }
            }
        });

        // --- SỰ KIỆN LINK "THÊM TÁC GIẢ MỚI" ---
        view.lblAddNewAuthor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String name = JOptionPane.showInputDialog(view, "Nhập tên tác giả mới:");
                if (name != null && !name.trim().isEmpty()) {
                    AuthorDTO newAuth = new AuthorDTO(0, name); // ID 0 vì DB tự tăng
                    if (authorBUS.addAuthor(newAuth)) {
                        // Reload lại danh sách gốc
                        allAuthors = authorBUS.getAll();
                        // Tìm lại ông vừa thêm để lấy ID chuẩn
                        AuthorDTO finalAuth = allAuthors.stream()
                                .filter(a -> a.getAuthorName().equals(name))
                                .findFirst().orElse(newAuth);
                        addAuthorToSelection(finalAuth); // Tự động chọn luôn
                        JOptionPane.showMessageDialog(view, "Thêm tác giả thành công!");
                    }
                }
            }
        });

        // --- SỰ KIỆN NÚT LƯU (SAVE) ---
        view.btnSave.addActionListener(e -> {
            if (!getFormInput())
                return; // Validate

            boolean result = false;
            if (mode == DialogMode.ADD) {
                result = bookBUS.addBook(this.bookDTO);
                if (result)
                    JOptionPane.showMessageDialog(view, "Thêm thành công!");
            } else if (mode == DialogMode.EDIT) {
                result = bookBUS.updateBook(this.bookDTO); // Hàm này chúng ta sẽ làm ở bước sau
                if (result)
                    JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
            }

            if (result) {
                this.isSuccess = true;
                ((JDialog) SwingUtilities.getWindowAncestor(view)).dispose();
            } else {
                JOptionPane.showMessageDialog(view, "Thao tác thất bại (Lỗi Database)!");
            }
        });

        // Sự kiện upload ảnh giữ nguyên
        view.btnUpload.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(250, 360, Image.SCALE_SMOOTH);
                view.lblImagePreview.setIcon(new ImageIcon(img));
                view.lblImagePreview.setText("");
                this.selectedImagePath = f.getAbsolutePath();
            }
        });
    }

    private boolean getFormInput() {
        // ... (đoạn validate tác giả, tên sách giữ nguyên) ...

        if (bookDTO == null)
            bookDTO = new BookDTO();

        try {
            // ... (đoạn set text giữ nguyên) ...
            bookDTO.setBookTitle(view.txtTitle.getText().trim());
            bookDTO.setIsbn(view.txtIsbn.getText().trim());
            bookDTO.setPublicationYear(Integer.parseInt(view.txtYear.getText().trim()));
            bookDTO.setPageCount(Integer.parseInt(view.txtPage.getText().trim()));
            bookDTO.setImportPrice(Double.parseDouble(view.txtPriceImport.getText().trim()));
            bookDTO.setSellingPrice(Double.parseDouble(view.txtPriceExport.getText().trim()));
            bookDTO.setStockQuantity(Integer.parseInt(view.txtQuantity.getText().trim()));
            bookDTO.setLanguage(view.txtLanguage.getText().trim());
            if (view.txtMinStock.getText().trim().isEmpty()) {
                bookDTO.setMinimumStock(0);
            } else {
                bookDTO.setMinimumStock(Integer.parseInt(view.txtMinStock.getText().trim()));
            }
            if (currentAuthors.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn ít nhất một tác giả!");
                return false;
            }
            if (view.txtTitle.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Tên sách không được để trống!");
                return false;
            }
            String statusVN = view.cbStatus.getSelectedItem().toString();
            switch (statusVN) {
                case "Còn hàng":
                    bookDTO.setStatus("IN_STOCK");
                    break;
                case "Hết hàng":
                    bookDTO.setStatus("OUT_OF_STOCK");
                    break;
                case "Ngừng kinh doanh":
                    bookDTO.setStatus("SUSPENDED");
                    break;
                default:
                    bookDTO.setStatus("IN_STOCK"); // Mặc định
            }
            bookDTO.setCoverType(view.cbCoverType.getSelectedItem().toString());
            bookDTO.setAuthors(currentAuthors);

            // --- ĐOẠN QUAN TRỌNG MỚI THÊM: MAP TÊN SANG ID ---

            // Xử lý Category
            if (view.cbCategory.getSelectedIndex() > 0) {
                String selectedCatName = view.cbCategory.getSelectedItem().toString();
                // Tìm trong listCategories xem ông nào có tên trùng thì lấy ID ông đó
                for (CategoryDTO cat : listCategories) {
                    if (cat.getName().equals(selectedCatName)) {
                        bookDTO.setCategoryId(cat.getID()); // Set ID để lưu DB
                        bookDTO.setCategoryName(cat.getName()); // Set Tên để hiển thị
                        break;
                    }
                }
            }

            // Xử lý Publisher
            if (view.cbPublisher.getSelectedIndex() > 0) {
                String selectedPubName = view.cbPublisher.getSelectedItem().toString();
                for (PublisherDTO pub : listPublishers) {
                    if (pub.getName().equals(selectedPubName)) {
                        bookDTO.setPublisherId(pub.getId()); // Set ID để lưu DB
                        bookDTO.setPublisherName(pub.getName()); // Set Tên để hiển thị
                        break;
                    }
                }
            }
            bookDTO.setImage(this.selectedImagePath);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập số hợp lệ!");
            return false;
        }
    }
}
