package GUI.dialog.book;

import BUS.AuthorBUS;
import BUS.BookBUS;
import BUS.CategoryBUS;
import BUS.ImportReceiptBUS;
import BUS.ImportReceiptDetailBUS;
import BUS.PublisherBUS;
import BUS.SupplierBUS;
import BUS.SystemParameterBUS;
import DTO.AuthorDTO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.ImportReceiptDTO;
import DTO.ImportReceiptDetailDTO;
import DTO.PublisherDTO;
import DTO.SupplierDTO;
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
                // Khóa các field liên quan đến kho — chỉ thay đổi qua Phiếu Nhập
                view.txtQuantity.setEditable(false);
                view.txtQuantity.setBackground(new Color(230, 230, 230));
                view.txtQuantity.setToolTipText("Tồn kho chỉ thay đổi qua Phiếu Nhập");

                // Ẩn cbStatus khi ADD vì hệ thống tự tính
                view.cbStatus.setEnabled(false);
                view.cbStatus.setToolTipText("Trạng thái tự động tính theo tồn kho ban đầu");
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
        view.txtLanguage.setText(bookDTO.getLanguage());
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

        // Nút thêm tác giả từ ô tìm kiếm
        view.btnAuthorAdd.addActionListener(e -> {
            String keyword = view.txtAuthorSearch.getText().trim();
            if (!keyword.isEmpty()) {
                AuthorDTO match = allAuthors.stream()
                        .filter(a -> a.getAuthorName().equalsIgnoreCase(keyword))
                        .findFirst().orElse(null);
                if (match != null)
                    addAuthorToSelection(match);
                else
                    JOptionPane.showMessageDialog(view, "Tác giả chưa có trong hệ thống! Vui lòng thêm mới.");
            }
        });

        // Label "Thêm tác giả mới"
        view.lblAddNewAuthor.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String name = JOptionPane.showInputDialog(view, "Nhập tên tác giả mới:");
                if (name != null && !name.trim().isEmpty()) {
                    AuthorDTO newAuth = new AuthorDTO(0, name.trim());
                    ValidationResult vr = authorBUS.addAuthor(newAuth);
                    if (vr.isValid()) {
                        allAuthors = authorBUS.getAll();
                        AuthorDTO saved = allAuthors.stream()
                                .filter(a -> a.getAuthorName().equalsIgnoreCase(name.trim()))
                                .findFirst().orElse(newAuth);
                        addAuthorToSelection(saved);
                        JOptionPane.showMessageDialog(view, "Thêm tác giả thành công!");
                    } else {
                        JOptionPane.showMessageDialog(view, vr.getSummary(), "Lỗi", JOptionPane.WARNING_MESSAGE);
                    }
                }
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

    /**
     * Xử lý thêm mới sách.
     * Nếu tồn kho ban đầu > 0 → yêu cầu chọn NCC → tạo phiếu nhập tự động.
     * Nếu tồn kho = 0 → lưu thẳng, không cần phiếu nhập.
     */
    private void handleAdd(BookDTO temp) {
        ValidationResult vr = bookBUS.addBook(temp);

        if (!vr.isValid()) {
            applyValidationErrors(vr);
            JOptionPane.showMessageDialog(view, vr.getSummary(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Sách đã lưu thành công, temp.getBookId() đã được BookBUS gán ID mới
        if (temp.getStockQuantity() > 0) {
            // Có tồn kho ban đầu → tạo phiếu nhập tự động để truy xuất nguồn gốc
            boolean receiptCreated = createInitialImportReceipt(temp);
            if (receiptCreated) {

                JOptionPane.showMessageDialog(view,
                        "Thêm sách thành công!\nĐã tạo Phiếu Nhập ban đầu để lưu nguồn gốc hàng hóa.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // Sách vẫn đã lưu, chỉ phiếu nhập thất bại → cảnh báo nhưng không rollback
                JOptionPane.showMessageDialog(view,
                        "Thêm sách thành công!\nTuy nhiên không thể tạo Phiếu Nhập ban đầu.\n"
                                + "Vui lòng tạo thủ công trong mục Quản lý Phiếu Nhập.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // Tồn kho = 0, không cần phiếu nhập
            JOptionPane.showMessageDialog(view, "Thêm sách thành công!");
        }

        this.bookDTO = temp;
        isSuccess = true;
        ((JDialog) SwingUtilities.getWindowAncestor(view)).dispose();
    }

    /**
     * Tạo phiếu nhập tự động cho tồn kho ban đầu của sách mới.
     * Yêu cầu người dùng chọn Nhà Cung Cấp.
     * 
     * @return true nếu tạo phiếu thành công, false nếu người dùng hủy hoặc lỗi.
     */
    private boolean createInitialImportReceipt(BookDTO book) {
        // Lấy danh sách NCC
        SupplierBUS supplierBUS = new SupplierBUS();
        List<SupplierDTO> suppliers = supplierBUS.getAll();

        if (suppliers == null || suppliers.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Không có Nhà Cung Cấp nào trong hệ thống!\nKhông thể tạo Phiếu Nhập ban đầu.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        SupplierDTO[] arr = suppliers.toArray(new SupplierDTO[0]);

        // Hỏi người dùng chọn NCC
        SupplierDTO chosen = (SupplierDTO) JOptionPane.showInputDialog(
                view,
                "Sách có tồn kho ban đầu: " + book.getStockQuantity() + " cuốn\n"
                        + "Vui lòng chọn Nhà Cung Cấp để lưu Phiếu Nhập nguồn gốc:",
                "Chọn Nhà Cung Cấp",
                JOptionPane.QUESTION_MESSAGE,
                null,
                arr,
                arr[0]);

        // Người dùng bấm Cancel
        if (chosen == null)
            return false;

        // Lấy employee ID từ session
        int employeeId = 1; // mặc định
        if (config.SessionManager.getCurrentAccount() != null) {
            employeeId = config.SessionManager.getCurrentAccount().getEmployeeId();
        }

        double totalAmount = book.getImportPrice() * book.getStockQuantity();

        // Tạo ImportReceiptDTO
        ImportReceiptDTO receipt = new ImportReceiptDTO();
        receipt.setSupplierId(chosen.getSupplierId());
        receipt.setEmployeeId(employeeId);
        receipt.setTotalAmount(totalAmount);
        receipt.setStatus("Completed");

        // Lưu phiếu nhập
        ImportReceiptBUS importBUS = new ImportReceiptBUS();
        int receiptId = importBUS.addReceipt(receipt);

        if (receiptId <= 0)
            return false;

        // Tạo chi tiết phiếu nhập
        ImportReceiptDetailDTO detail = new ImportReceiptDetailDTO(
                receiptId,
                book.getBookId(),
                book.getStockQuantity(),
                book.getImportPrice(),
                totalAmount);

        ArrayList<ImportReceiptDetailDTO> details = new ArrayList<>();
        details.add(detail);

        ImportReceiptDetailBUS detailBUS = new ImportReceiptDetailBUS();
        return detailBUS.saveAllDetails(details);
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

        if (this.bookDTO != null && mode == DialogMode.EDIT) {
            // EDIT: giữ nguyên ID và các field kho từ DB, không đọc từ GUI
            tempBook.setBookId(this.bookDTO.getBookId());
            tempBook.setStockQuantity(this.bookDTO.getStockQuantity());
            tempBook.setImportPrice(this.bookDTO.getImportPrice());
            tempBook.setStatus(this.bookDTO.getStatus());
        }

        // Các field metadata — luôn đọc từ GUI
        tempBook.setBookTitle(toTitleCase(view.txtTitle.getText()));
        tempBook.setIsbn(view.txtIsbn.getText().trim().replace("-", ""));
        tempBook.setLanguage(toTitleCase(view.txtLanguage.getText()));
        tempBook.setPublicationYear(parseInt(view.txtYear.getText()));
        tempBook.setPageCount(parseInt(view.txtPage.getText()));
        tempBook.setSellingPrice(parseDouble(view.txtPriceExport.getText()));
        tempBook.setMinimumStock(parseInt(view.txtMinStock.getText()));
        tempBook.setCoverType(view.cbCoverType.getSelectedItem().toString());
        tempBook.setAuthors(currentAuthors);
        tempBook.setImage(selectedImagePath);

        if (mode == DialogMode.ADD) {
            // ADD: đọc tồn kho và giá nhập từ GUI
            tempBook.setImportPrice(parseDouble(view.txtPriceImport.getText()));
            int stock = parseInt(view.txtQuantity.getText());

            // Tự tính status theo stock — không dùng cbStatus
            if (stock <= 0) {
                tempBook.setStockQuantity(0);
                tempBook.setStatus("out_of_stock");
            } else {
                tempBook.setStockQuantity(stock);
                tempBook.setStatus("in_stock");
            }
        }
        if (currentAuthors != null && !currentAuthors.isEmpty()) {
            List<Integer> authorIds = currentAuthors.stream()
                    .map(AuthorDTO::getAuthorId)
                    .collect(Collectors.toList());

            tempBook.setAuthorIds(authorIds);
        }

        // Category
        String selectedCat = view.cbCategory.getSelectedItem().toString();
        for (CategoryDTO cat : listCategories) {
            if (cat.getName().equals(selectedCat)) {
                tempBook.setCategoryId(cat.getId());
                tempBook.setCategoryName(cat.getName());
                break;
            }
        }

        // Publisher
        String selectedPub = view.cbPublisher.getSelectedItem().toString();
        for (PublisherDTO pub : listPublishers) {
            if (pub.getName().equals(selectedPub)) {
                tempBook.setPublisherId(pub.getId());
                tempBook.setPublisherName(pub.getName());
                break;
            }
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
                fieldMap.put("stockQuantity", view.txtQuantity);
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
        view.txtLanguage.setEditable(editable);
        view.txtMinStock.setEditable(editable);
        view.cbCategory.setEnabled(editable);
        view.cbPublisher.setEnabled(editable);
        view.cbStatus.setEnabled(editable);
        view.cbCoverType.setEnabled(editable);
        view.btnUpload.setEnabled(editable);
        view.txtAuthorSearch.setVisible(editable);
        view.btnAuthorAdd.setVisible(editable);
        boolean canAddAuthor = editable && config.SessionManager.hasPermission(451, "Thêm");
        view.lblAddNewAuthor.setVisible(canAddAuthor);
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