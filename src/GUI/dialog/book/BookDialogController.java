package GUI.dialog.book;

import DTO.AuthorDTO;
import DTO.BookDTO;
import DTO.CategoryDTO;
import DTO.PublisherDTO;
import GUI.util.ImageHelper;
import BUS.BookBUS;
import BUS.CategoryBUS;
import BUS.PublisherBUS;
import BUS.AuthorBUS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
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
                // Có thể cho phép sửa ISBN hoặc không tùy nghiệp vụ, ở đây tạm khóa để an toàn
                // view.txtIsbn.setEditable(false);
                break;
            case READ:
                view.lblTitle.setText("Chi Tiết Sách");
                view.btnSave.setVisible(false);
                view.btnCancel.setText("Đóng");
                setFormEditable(false);
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

        view.txtAuthorSearch.setVisible(editable);
        view.btnAuthorAdd.setVisible(editable);
        view.lblAddNewAuthor.setVisible(editable);

        view.btnUpload.setEnabled(editable);
        renderAuthorTags(editable);
    }

    public void fillData() {
        if (bookDTO == null) {
            view.cbStatus.setSelectedItem("Còn hàng");
            view.cbCoverType.setSelectedItem("Bìa mềm");
            return;
        }

        view.txtTitle.setText(bookDTO.getBookTitle());

        // Format ISBN ngay khi hiển thị
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

        if (bookDTO.getImage() != null && !bookDTO.getImage().isEmpty()) {
            String imgName = bookDTO.getImage();
            String finalPath;
            if (imgName.contains(":") || imgName.startsWith("/") || imgName.contains("\\")) {
                finalPath = imgName;
            } else {
                finalPath = "src/image/" + imgName;
            }

            File f = new File(finalPath);
            if (f.exists()) {
                this.selectedImagePath = imgName;
                ImageIcon icon = new ImageIcon(finalPath);
                Image img = icon.getImage().getScaledInstance(250, 360, Image.SCALE_SMOOTH);
                view.lblImagePreview.setIcon(new ImageIcon(img));
                view.lblImagePreview.setText("");
            } else {
                view.lblImagePreview.setIcon(null);
                view.lblImagePreview.setText("Ảnh lỗi");
            }
        } else {
            view.lblImagePreview.setIcon(null);
            view.lblImagePreview.setText("Chưa có ảnh");
        }

        if (bookDTO.getAuthors() != null) {
            this.currentAuthors = new ArrayList<>(bookDTO.getAuthors());
            renderAuthorTags(mode != DialogMode.READ);
        }

        String statusEN = bookDTO.getStatus();
        if (statusEN != null) {
            switch (statusEN) {
                case "out_of_stock":
                    view.cbStatus.setSelectedItem("Hết hàng");
                    break;
                case "discontinued":
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

            if (isEditable) {
                JLabel lblX = new JLabel("x");
                lblX.setForeground(Color.RED);
                lblX.setCursor(new Cursor(Cursor.HAND_CURSOR));
                lblX.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                lblX.addMouseListener(new MouseAdapter() {
                    @Override
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

    public boolean isSucceeded() {
        return isSuccess;
    }

    public void initEvents() {
        view.btnCancel.addActionListener(e -> {
            ((JDialog) SwingUtilities.getWindowAncestor(view)).dispose();
        });

        // --- XỬ LÝ NHẬP LIỆU ISBN (FOCUS LISTENER & KEY LISTENER) ---
        view.txtIsbn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                // Chỉ cho nhập số và các phím điều khiển
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE
                        && c != java.awt.event.KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        // --- SỰ KIỆN TÌM KIẾM TÁC GIẢ ---
        view.txtAuthorSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                showAuthorSuggestions(view.txtAuthorSearch.getText().trim());
            }
        });

        view.listAuthorSuggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AuthorDTO selected = view.listAuthorSuggestions.getSelectedValue();
                if (selected != null) {
                    addAuthorToSelection(selected);
                }
            }
        });

        view.btnAuthorAdd.addActionListener(e -> {
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

        view.lblAddNewAuthor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String name = JOptionPane.showInputDialog(view, "Nhập tên tác giả mới:");
                if (name != null && !name.trim().isEmpty()) {
                    AuthorDTO newAuth = new AuthorDTO(0, name);
                    if (authorBUS.addAuthor(newAuth)) {
                        allAuthors = authorBUS.getAll();
                        AuthorDTO finalAuth = allAuthors.stream()
                                .filter(a -> a.getAuthorName().equals(name))
                                .findFirst().orElse(newAuth);
                        addAuthorToSelection(finalAuth);
                        JOptionPane.showMessageDialog(view, "Thêm tác giả thành công!");
                    }
                }
            }
        });

        // --- SỰ KIỆN LƯU (SAVE) ---
        view.btnSave.addActionListener(e -> {
            if (!getFormInput())
                return;

            boolean result = false;
            if (mode == DialogMode.ADD) {
                result = bookBUS.addBook(this.bookDTO);
                if (result)
                    JOptionPane.showMessageDialog(view, "Thêm thành công!");
            } else if (mode == DialogMode.EDIT) {
                result = bookBUS.updateBook(this.bookDTO);
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

        view.btnUpload.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
            if (fc.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                String newFileName = ImageHelper.saveImageToProject(f);
                if (newFileName != null) {
                    this.selectedImagePath = newFileName;
                    String fullPath = "src/image/" + newFileName;
                    ImageIcon icon = new ImageIcon(fullPath);
                    Image img = icon.getImage().getScaledInstance(250, 360, Image.SCALE_SMOOTH);
                    view.lblImagePreview.setIcon(new ImageIcon(img));
                    view.lblImagePreview.setText("");
                }
            }
        });
    }

    private boolean getFormInput() {
        // BƯỚC 1: Validate dữ liệu
        String errors = validateData();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(view, errors, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // BƯỚC 2: Gán dữ liệu vào DTO
        if (bookDTO == null)
            bookDTO = new BookDTO();

        // XỬ LÝ ISBN: Lưu chuỗi sạch (không gạch) vào DTO
        String rawIsbn = view.txtIsbn.getText().trim();
        String cleanIsbn = rawIsbn.replace("-", "");
        bookDTO.setIsbn(cleanIsbn);

        bookDTO.setBookTitle(toTitleCase(view.txtTitle.getText()));
        bookDTO.setLanguage(toTitleCase(view.txtLanguage.getText()));
        bookDTO.setPublicationYear(parseInt(view.txtYear.getText()));
        bookDTO.setPageCount(parseInt(view.txtPage.getText()));
        bookDTO.setImportPrice(parseDouble(view.txtPriceImport.getText()));
        bookDTO.setSellingPrice(parseDouble(view.txtPriceExport.getText()));
        bookDTO.setStockQuantity(parseInt(view.txtQuantity.getText()));

        String minStock = view.txtMinStock.getText().trim();
        bookDTO.setMinimumStock(minStock.isEmpty() ? 0 : parseInt(minStock));

        bookDTO.setCoverType(view.cbCoverType.getSelectedItem().toString());

        String statusVN = view.cbStatus.getSelectedItem().toString();
        switch (statusVN) {
            case "Hết hàng":
                bookDTO.setStatus("out_of_stock");
                break;
            case "Ngừng kinh doanh":
                bookDTO.setStatus("discontinued");
                break;
            default:
                bookDTO.setStatus("in_stock");
        }

        String selectedCatName = view.cbCategory.getSelectedItem().toString();
        for (CategoryDTO cat : listCategories) {
            if (cat.getName().equals(selectedCatName)) {
                bookDTO.setCategoryId(cat.getId());
                bookDTO.setCategoryName(cat.getName());
                break;
            }
        }

        String selectedPubName = view.cbPublisher.getSelectedItem().toString();
        for (PublisherDTO pub : listPublishers) {
            if (pub.getName().equals(selectedPubName)) {
                bookDTO.setPublisherId(pub.getId());
                bookDTO.setPublisherName(pub.getName());
                break;
            }
        }

        bookDTO.setAuthors(currentAuthors);
        bookDTO.setImage(this.selectedImagePath);

        return true;
    }

    /**
     * Hàm kiểm tra dữ liệu đầu vào.
     */
    private String validateData() {
        StringBuilder sb = new StringBuilder();

        // 1. Tên sách
        if (view.txtTitle.getText().trim().isEmpty()) {
            sb.append("- Tên sách không được để trống.\n");
            view.setInputError(view.txtTitle, true);
        } else {
            view.setInputError(view.txtTitle, false);
        }

        // 2. Mã ISBN
        String rawIsbn = view.txtIsbn.getText().trim();
        String cleanIsbn = rawIsbn.replace("-", "");

        if (rawIsbn.isEmpty()) {
            sb.append("- Mã ISBN không được để trống.\n");
            view.setInputError(view.txtIsbn, true);
        } else if (!cleanIsbn.matches("\\d{10}|\\d{13}")) {
            sb.append("- Mã ISBN không hợp lệ (phải có 10 hoặc 13 số).\n");
            view.setInputError(view.txtIsbn, true);
        } else {
            // Kiểm tra trùng lặp
            BookDTO existingBook = bookBUS.getByIsbn(cleanIsbn);
            if (existingBook != null) {
                boolean isDuplicate = false;
                if (mode == DialogMode.ADD) {
                    isDuplicate = true;
                } else if (mode == DialogMode.EDIT) {
                    if (existingBook.getBookId() != bookDTO.getBookId()) {
                        isDuplicate = true;
                    }
                }

                if (isDuplicate) {
                    sb.append("- Mã ISBN này đã tồn tại (Sách: " + existingBook.getBookTitle() + ").\n");
                    view.setInputError(view.txtIsbn, true);
                } else {
                    view.setInputError(view.txtIsbn, false);
                }
            } else {
                view.setInputError(view.txtIsbn, false);
            }
        }

        // 3. Năm xuất bản
        String yearTxt = view.txtYear.getText().trim();
        if (!yearTxt.matches("\\d{4}")) {
            sb.append("- Năm xuất bản phải là 4 chữ số.\n");
            view.setInputError(view.txtYear, true);
        } else {
            view.setInputError(view.txtYear, false);
        }

        // 4. Giá tiền
        double importPrice = parseDouble(view.txtPriceImport.getText());
        double exportPrice = parseDouble(view.txtPriceExport.getText());

        if (importPrice < 0) {
            sb.append("- Giá nhập không hợp lệ.\n");
            view.setInputError(view.txtPriceImport, true);
        } else {
            view.setInputError(view.txtPriceImport, false);
        }

        if (exportPrice < 0) {
            sb.append("- Giá bán không hợp lệ.\n");
            view.setInputError(view.txtPriceExport, true);
        } else {
            view.setInputError(view.txtPriceExport, false);
        }

        if (importPrice >= 0 && exportPrice >= 0 && exportPrice < importPrice) {
            sb.append("- Cảnh báo: Giá bán thấp hơn giá nhập!\n");
            view.setInputError(view.txtPriceExport, true);
        }

        // 5. Tồn kho
        if (parseInt(view.txtQuantity.getText()) < 0) {
            sb.append("- Tồn kho không được âm.\n");
            view.setInputError(view.txtQuantity, true);
        } else {
            view.setInputError(view.txtQuantity, false);
        }

        if (!view.txtMinStock.getText().trim().isEmpty() && parseInt(view.txtMinStock.getText()) < 0) {
            sb.append("- Tồn kho tối thiểu không được âm.\n");
            view.setInputError(view.txtMinStock, true);
        }

        // 6. ComboBox & Tác giả
        if (view.cbCategory.getSelectedIndex() <= 0) {
            sb.append("- Vui lòng chọn Danh mục.\n");
            view.setInputError(view.cbCategory, true);
        } else {
            view.setInputError(view.cbCategory, false);
        }

        if (view.cbPublisher.getSelectedIndex() <= 0) {
            sb.append("- Vui lòng chọn Nhà xuất bản.\n");
            view.setInputError(view.cbPublisher, true);
        } else {
            view.setInputError(view.cbPublisher, false);
        }

        if (currentAuthors.isEmpty()) {
            sb.append("- Vui lòng chọn ít nhất một tác giả.\n");
            view.setInputError(view.pnlAuthorTags, true);
        } else {
            view.setInputError(view.pnlAuthorTags, false);
        }

        return sb.toString();
    }

    // --- UTILS ---

    private double parseDouble(String text) {
        try {
            String cleanText = text.replace(",", "").replace(".", "").trim();
            return Double.parseDouble(cleanText);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int parseInt(String text) {
        try {
            String cleanText = text.replace(",", "").replace(".", "").trim();
            return Integer.parseInt(cleanText);
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

}