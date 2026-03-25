package GUI.dialog.book;

import BUS.AuthorBUS;
import BUS.BookBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;
import BUS.SystemParameterBUS;
import DTO.AuthorDTO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;
import DTO.ValidationResult;
import GUI.util.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Locale;
import java.util.TreeSet;

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

    // Map field name (từ ValidationResult) -> component trên view
    private Map<String, JComponent> fieldMap;

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
        this.listCategories = catBUS.getAll();
        for (CategoryDTO cat : listCategories) {
            view.cbCategory.addItem(cat.getName());
        }

        PublisherBUS pubBUS = new PublisherBUS();
        this.listPublishers = pubBUS.getAll();
        for (PublisherDTO pub : listPublishers) {
            view.cbPublisher.addItem(pub.getName());
        }

        view.cbStatus.setSelectedItem("Còn hàng");
        view.cbCoverType.setSelectedItem("Bìa mềm");
        view.cbLanguage.removeAllItems();
        view.cbLanguage.addItem("-- Chọn ngôn ngữ --");

        // Dùng TreeSet để lọc trùng và tự động sắp xếp theo Alphabet
        TreeSet<String> languages = new TreeSet<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            String name = locale.getDisplayLanguage(Locale.of("vi"));
            if (!name.isEmpty()) {
                languages.add(toTitleCase(name));
            }
        }
        for (String lang : languages) {
            view.cbLanguage.addItem(lang);
        }
        // chọn sẵn Tiếng Việt
        view.cbLanguage.setSelectedItem("Tiếng Việt");
    }

    public void applyModeSettings() {
        setFormEditable(false);
        view.btnSave.setVisible(false);
        view.btnCancel.setText("Đóng");

        switch (mode) {
            case ADD:
                view.lblTitle.setText("Thêm Sách Mới");
                view.btnSave.setText("Lưu thông tin");
                view.btnSave.setVisible(true);
                view.btnCancel.setText("Hủy bỏ");
                setFormEditable(true);

                if (view.txtQuantity.getParent() != null) {
                    view.txtQuantity.getParent().setVisible(false);
                }
                if (view.txtPriceExport.getParent() != null) {
                    view.txtPriceExport.getParent().setVisible(false);
                }
                if (view.txtPriceImport.getParent() != null) {
                    view.txtPriceImport.getParent().setVisible(false);
                }
                view.cbStatus.getParent().setVisible(false);

                int defaultMinStock = SystemParameterBUS.getInstance().getInt("SO_LUONG_TOI_THIEU_CANH_BAO", 10);
                view.txtMinStock.setText(String.valueOf(defaultMinStock));
                break;

            case EDIT:
                view.lblTitle.setText("Chỉnh Sửa Thông Tin Sách");
                view.btnSave.setText("Cập nhật");
                view.btnSave.setVisible(true);
                setFormEditable(true);
                // Khóa các field liên quan đến kho — chỉ thay đổi qua Phiếu Nhập
                view.txtQuantity.setEditable(false);
                view.txtQuantity.setBackground(new Color(230, 230, 230));
                view.txtQuantity.setToolTipText("Tồn kho chỉ thay đổi qua Phiếu Nhập");

                view.txtPriceImport.setEditable(false);
                view.txtPriceImport.setBackground(new Color(230, 230, 230));
                view.txtPriceImport.setToolTipText("Giá nhập chỉ thay đổi qua Phiếu Nhập");
                break;

            case READ:
                view.lblTitle.setText("Chi Tiết Sách");
                setFormEditable(false);
                break;
        }
    }

    public void fillData() {
        if (bookDTO == null)
            return;

        view.txtTitle.setText(bookDTO.getBookTitle());
        view.txtIsbn.setText(bookDTO.getIsbn());
        view.txtYear.setText(String.valueOf(bookDTO.getPublicationYear()));
        view.txtPage.setText(String.valueOf(bookDTO.getPageCount()));
        view.txtPriceImport.setText(String.valueOf(bookDTO.getImportPrice()));
        view.txtPriceExport.setText(String.valueOf(bookDTO.getSellingPrice()));
        view.txtQuantity.setText(String.valueOf(bookDTO.getStockQuantity()));
        view.cbLanguage.setSelectedItem(bookDTO.getLanguage());
        view.txtMinStock.setText(String.valueOf(bookDTO.getMinimumStock()));
        view.cbCategory.setSelectedItem(bookDTO.getCategoryName());
        view.cbPublisher.setSelectedItem(bookDTO.getPublisherName());
        view.cbCoverType.setSelectedItem(bookDTO.getCoverType());

        // Trạng thái
        switch (bookDTO.getStatus() != null ? bookDTO.getStatus() : "") {
            case "out_of_stock":
                view.cbStatus.setSelectedItem("Hết hàng");
                break;
            case "discontinued":
                view.cbStatus.setSelectedItem("Ngừng kinh doanh");
                break;
            default:
                view.cbStatus.setSelectedItem("Còn hàng");
        }

        // Ảnh
        loadImage(bookDTO.getImage());

        // Tác giả
        if (bookDTO.getAuthors() != null) {
            this.currentAuthors = new ArrayList<>(bookDTO.getAuthors());
            renderAuthorTags(mode != DialogMode.READ);
        }
    }

    public void initEvents() {
        // Đóng dialog
        view.btnCancel.addActionListener(e -> ((JDialog) SwingUtilities.getWindowAncestor(view)).dispose());

        // Chỉ cho nhập số vào ISBN
        view.txtIsbn.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE)
                    e.consume();
            }
        });

        // Tìm kiếm tác giả
        view.txtAuthorSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                showAuthorSuggestions(view.txtAuthorSearch.getText().trim());
            }
        });

        view.listAuthorSuggestions.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AuthorDTO selected = view.listAuthorSuggestions.getSelectedValue();
                if (selected != null)
                    addAuthorToSelection(selected);
            }
        });

        // Upload ảnh
        view.btnUpload.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
            if (fc.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                String newFileName = ImageHelper.saveImageToProject(fc.getSelectedFile());
                if (newFileName != null) {
                    selectedImagePath = newFileName;
                    loadImage(newFileName);
                }
            }
        });

        // Lưu
        view.btnSave.addActionListener(e -> handleSave());
    }

    // ===================== XỬ LÝ LƯU =====================

    private void handleSave() {
        BookDTO temp = collectFormData();

        if (mode == DialogMode.ADD) {
            handleAdd(temp);
        } else {
            handleEdit(temp);
        }
    }

    private void handleAdd(BookDTO temp) {
        ValidationResult vr = bookBUS.addBook(temp);

        if (!vr.isValid()) {
            applyValidationErrors(vr);
            JOptionPane.showMessageDialog(view, vr.getSummary(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Sách đã lưu thành công với tồn kho mặc định là 0
        JOptionPane.showMessageDialog(view, "Thêm sách mới thành công!");

        this.bookDTO = temp;
        isSuccess = true;
        ((JDialog) SwingUtilities.getWindowAncestor(view)).dispose();
    }

    /**
     * Xử lý cập nhật sách (EDIT).
     * Không cho phép thay đổi tồn kho, giá nhập, trạng thái.
     */
    private void handleEdit(BookDTO temp) {
        ValidationResult vr = bookBUS.updateBook(temp);

        if (vr.isValid()) {
            this.bookDTO = temp;
            isSuccess = true;
            JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
            ((JDialog) SwingUtilities.getWindowAncestor(view)).dispose();
        } else {
            applyValidationErrors(vr);
            JOptionPane.showMessageDialog(view, vr.getSummary(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ===================== THU THẬP DỮ LIỆU TỪ FORM =====================

    private BookDTO collectFormData() {
        BookDTO tempBook = new BookDTO();
        // 1. XỬ LÝ THEO CHẾ ĐỘ (ADD/EDIT)
        if (mode == DialogMode.ADD) {
            // Khi thêm mới: Gán mặc định bằng 0 để vượt qua Validator vì các ô này đã bị ẩn
            // Các thông tin này sẽ được cập nhật chính xác qua Phiếu Nhập sau này
            tempBook.setImportPrice(0.0);
            tempBook.setSellingPrice(0.0);
            tempBook.setStockQuantity(0);
            tempBook.setStatus("out_of_stock");
        } else if (this.bookDTO != null) {
            // Khi chỉnh sửa: Giữ nguyên ID và các thông tin kho/giá nhập từ DB
            tempBook.setBookId(this.bookDTO.getBookId());
            tempBook.setImportPrice(this.bookDTO.getImportPrice());
            tempBook.setStockQuantity(this.bookDTO.getStockQuantity());
            tempBook.setStatus(this.bookDTO.getStatus());

            // Cho phép cập nhật Giá bán từ giao diện ở chế độ EDIT
            tempBook.setSellingPrice(parseDouble(view.txtPriceExport.getText()));
        }

        // 2. THU THẬP THÔNG TIN CƠ BẢN (METADATA)
        tempBook.setBookTitle(toTitleCase(view.txtTitle.getText()));
        tempBook.setIsbn(view.txtIsbn.getText().trim().replace("-", ""));
        tempBook.setPublicationYear(parseInt(view.txtYear.getText()));
        tempBook.setPageCount(parseInt(view.txtPage.getText()));
        tempBook.setMinimumStock(parseInt(view.txtMinStock.getText()));
        tempBook.setCoverType(view.cbCoverType.getSelectedItem().toString());
        tempBook.setImage(selectedImagePath);

        // 3. XỬ LÝ NGÔN NGỮ (Tránh lấy placeholder "-- Chọn ngôn ngữ --")
        Object selectedLang = view.cbLanguage.getSelectedItem();
        if (selectedLang != null && !selectedLang.toString().startsWith("--")) {
            tempBook.setLanguage(selectedLang.toString());
        } else {
            tempBook.setLanguage("Tiếng Việt"); // Mặc định nếu không chọn
        }

        // 4. XỬ LÝ DANH MỤC (Lấy ID từ listCategories)
        String selectedCat = view.cbCategory.getSelectedItem().toString();
        for (CategoryDTO cat : listCategories) {
            if (cat.getName().equals(selectedCat)) {
                tempBook.setCategoryId(cat.getId());
                tempBook.setCategoryName(cat.getName());
                break;
            }
        }

        // 5. XỬ LÝ NHÀ XUẤT BẢN (Lấy ID từ listPublishers)
        String selectedPub = view.cbPublisher.getSelectedItem().toString();
        for (PublisherDTO pub : listPublishers) {
            if (pub.getName().equals(selectedPub)) {
                tempBook.setPublisherId(pub.getId());
                tempBook.setPublisherName(pub.getName());
                break;
            }
        }

        // 6. XỬ LÝ DANH SÁCH TÁC GIẢ
        if (currentAuthors != null && !currentAuthors.isEmpty()) {
            tempBook.setAuthors(new ArrayList<>(currentAuthors));
            List<Integer> authorIds = currentAuthors.stream()
                    .map(AuthorDTO::getAuthorId)
                    .collect(Collectors.toList());
            tempBook.setAuthorIds(authorIds);
        }

        return tempBook;
    }

    // ===================== VALIDATION HIGHLIGHT =====================

    private void applyValidationErrors(ValidationResult vr) {
        if (fieldMap == null) {
            fieldMap = new HashMap<>();
            fieldMap.put("bookTitle", view.txtTitle);
            fieldMap.put("isbn", view.txtIsbn);
            fieldMap.put("sellingPrice", view.txtPriceExport);
            fieldMap.put("minimumStock", view.txtMinStock);
            fieldMap.put("categoryId", view.cbCategory);
            fieldMap.put("publisherId", view.cbPublisher);
            fieldMap.put("authors", view.pnlAuthorTags);

            // Chỉ validate 2 field này khi ADD
            if (mode == DialogMode.ADD) {
                fieldMap.put("importPrice", view.txtPriceImport);
            }
        }

        // Reset tất cả
        fieldMap.values().forEach(c -> GUI.util.ValidationUI.reset(c));

        // Highlight field lỗi
        vr.getErrors().forEach((field, msg) -> {
            JComponent comp = fieldMap.get(field);
            if (comp != null) {
                GUI.util.ValidationUI.setError(comp, msg);
            }
        });
    }

    // ===================== AUTHOR TAGS =====================

    private void renderAuthorTags(boolean isEditable) {
        view.pnlAuthorTags.removeAll();
        for (AuthorDTO author : currentAuthors) {
            JPanel tag = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            tag.setBackground(new Color(225, 225, 225));
            tag.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            tag.add(new JLabel(author.getAuthorName()));

            if (isEditable) {
                JLabel lblX = new JLabel("x");
                lblX.setForeground(Color.RED);
                lblX.setCursor(new Cursor(Cursor.HAND_CURSOR));
                lblX.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                lblX.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        currentAuthors.remove(author);
                        renderAuthorTags(true);
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
            view.txtAuthorSearch.setText("");
            view.popupAuthorSuggestions.setVisible(false);
            renderAuthorTags(true);
        }
    }

    // ===================== UTILS =====================

    private void setFormEditable(boolean editable) {
        view.txtTitle.setEditable(editable);
        view.txtIsbn.setEditable(editable);
        view.txtYear.setEditable(editable);
        view.txtPage.setEditable(editable);
        view.txtPriceImport.setEditable(editable);
        view.txtPriceExport.setEditable(editable);
        view.txtQuantity.setEditable(editable);
        view.cbLanguage.setEnabled(editable);
        view.txtMinStock.setEditable(editable);
        view.cbCategory.setEnabled(editable);
        view.cbPublisher.setEnabled(editable);
        view.cbStatus.setEnabled(editable);
        view.cbCoverType.setEnabled(editable);
        view.btnUpload.setEnabled(editable);
        view.txtAuthorSearch.setVisible(editable);
        renderAuthorTags(editable);
    }

    private void loadImage(String imgName) {
        if (imgName == null || imgName.isEmpty()) {
            view.lblImagePreview.setIcon(null);
            view.lblImagePreview.setText("Chưa có ảnh");
            return;
        }
        String path = (imgName.contains(":") || imgName.startsWith("/") || imgName.contains("\\"))
                ? imgName
                : "src/image/" + imgName;
        File f = new File(path);
        if (f.exists()) {
            this.selectedImagePath = imgName;
            Image img = new ImageIcon(path).getImage().getScaledInstance(250, 360, Image.SCALE_SMOOTH);
            view.lblImagePreview.setIcon(new ImageIcon(img));
            view.lblImagePreview.setText("");
        } else {
            view.lblImagePreview.setIcon(null);
            view.lblImagePreview.setText("Ảnh lỗi");
        }
    }

    private double parseDouble(String text) {
        try {
            return Double.parseDouble(text.replace(",", "").replace(".", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int parseInt(String text) {
        try {
            return Integer.parseInt(text.replace(",", "").replace(".", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty())
            return "";
        String[] words = input.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)));
                if (w.length() > 1)
                    sb.append(w.substring(1).toLowerCase());
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    public boolean isSucceeded() {
        return isSuccess;
    }
}