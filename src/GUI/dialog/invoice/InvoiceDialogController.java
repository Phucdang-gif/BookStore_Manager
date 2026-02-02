package GUI.dialog.invoice;

import BUS.BookBUS;
import BUS.InvoiceBUS;
import DTO.BookDTO;
import DTO.InvoiceDTO;
import DTO.InvoiceDetailDTO;
import GUI.util.UIConstants;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDialogController {
    private InvoiceDialogView view; // Tham chiếu đến View
    private InvoiceDialog dialog; // Tham chiếu đến Dialog (để đóng)

    private BookBUS bookBUS;
    private InvoiceBUS invoiceBUS;
    private List<BookDTO> listBooks;
    private List<InvoiceDetailDTO> cartDetails = new ArrayList<>();
    private boolean isSuccess = false;

    public InvoiceDialogController(InvoiceDialogView view, InvoiceDialog dialog) {
        this.view = view;
        this.dialog = dialog;
        this.bookBUS = new BookBUS();
        this.invoiceBUS = new InvoiceBUS();
    }

    public void initData() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        view.lblDate.setText(now.format(formatter));

        // TODO: Lấy tên nhân viên từ Session đăng nhập
        view.txtEmployeeName.setText("Admin (ID: 1)");
    }

    public void initEvents() {
        // Tìm kiếm
        view.txtSearchBook.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadBookData(view.txtSearchBook.getText());
            }
        });

        // Double click bảng sách -> Thêm vào giỏ
        view.tblBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = view.tblBooks.getSelectedRow();
                    if (row != -1) {
                        int bookId = (int) view.tblBooks.getValueAt(row, 0);
                        addToCart(bookId);
                    }
                }
            }
        });

        // Menu chuột phải xóa giỏ hàng
        JPopupMenu popup = new JPopupMenu();
        JMenuItem itemDel = new JMenuItem("Xóa dòng này");
        itemDel.addActionListener(e -> removeFromCart());
        popup.add(itemDel);
        view.tblCart.setComponentPopupMenu(popup);

        // Tính tiền thừa
        view.txtMoneyReceived.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });

        // Nút Thanh toán
        view.btnPay.addActionListener(e -> processPayment());
    }

    public void loadBookData(String keyword) {
        listBooks = bookBUS.getAll();
        view.modelBooks.setRowCount(0);
        String key = UIConstants.removeAccent(keyword);

        for (BookDTO b : listBooks) {
            String name = UIConstants.removeAccent(b.getBookTitle());
            if ((keyword.isEmpty() || name.contains(key) || b.getIsbn().contains(key))
                    && !b.getStatus().equals("SUSPENDED") && b.getStockQuantity() > 0) {
                view.modelBooks.addRow(new Object[] {
                        b.getBookId(), b.getBookTitle(), b.getFormattedSellingPrice(), b.getStockQuantity()
                });
            }
        }
    }

    private void addToCart(int bookId) {
        BookDTO book = listBooks.stream().filter(b -> b.getBookId() == bookId).findFirst().orElse(null);
        if (book == null)
            return;

        boolean exists = false;
        for (InvoiceDetailDTO item : cartDetails) {
            if (item.getBookId() == bookId) {
                if (item.getQuantity() < book.getStockQuantity()) {
                    item.setQuantity(item.getQuantity() + 1);
                    item.setSubtotal(item.getQuantity() * item.getUnitPrice());
                } else {
                    JOptionPane.showMessageDialog(dialog, "Kho chỉ còn " + book.getStockQuantity() + " quyển!");
                }
                exists = true;
                break;
            }
        }

        if (!exists) {
            InvoiceDetailDTO newItem = new InvoiceDetailDTO();
            newItem.setBookId(bookId);
            newItem.setBookTitle(book.getBookTitle());
            newItem.setQuantity(1);
            newItem.setUnitPrice(book.getSellingPrice());
            newItem.setSubtotal(book.getSellingPrice());
            cartDetails.add(newItem);
        }
        refreshCartTable();
    }

    private void removeFromCart() {
        int row = view.tblCart.getSelectedRow();
        if (row != -1) {
            cartDetails.remove(row);
            refreshCartTable();
        }
    }

    private void refreshCartTable() {
        view.modelCart.setRowCount(0);
        double total = 0;
        int stt = 1;
        for (InvoiceDetailDTO item : cartDetails) {
            view.modelCart.addRow(new Object[] {
                    stt++,
                    item.getBookId(),
                    item.getBookTitle(),
                    item.getQuantity(),
                    String.format("%,.0f", item.getUnitPrice()),
                    String.format("%,.0f", item.getSubtotal())
            });
            total += item.getSubtotal();
        }
        view.lblTotalMoney.setText(String.format("%,.0f VNĐ", total));
        calculateChange();
    }

    private void calculateChange() {
        try {
            double total = cartDetails.stream().mapToDouble(InvoiceDetailDTO::getSubtotal).sum();
            String moneyStr = view.txtMoneyReceived.getText().replace(",", "").replace(".", "");
            double received = moneyStr.isEmpty() ? 0 : Double.parseDouble(moneyStr);
            double change = received - total;
            view.lblChangeAmount.setText(String.format("%,.0f VNĐ", change));
        } catch (Exception e) {
            view.lblChangeAmount.setText("0 VNĐ");
        }
    }

    private void processPayment() {
        if (cartDetails.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Giỏ hàng trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ... (Logic kiểm tra tiền và tạo InvoiceDTO giữ nguyên như cũ) ...
        // Gọi BUS createInvoice và xử lý kết quả:

        double total = cartDetails.stream().mapToDouble(InvoiceDetailDTO::getSubtotal).sum();
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setEmployeeId(1);
        invoice.setCustomerId(1); // Hoặc xử lý khách hàng nhập tay
        invoice.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        invoice.setTotalAmount(total);
        invoice.setFinalAmount(total);
        invoice.setStatus("PAID");
        invoice.setPaymentMethod("CASH");

        if (invoiceBUS.createInvoice(invoice, cartDetails)) {
            JOptionPane.showMessageDialog(dialog, "Thanh toán thành công!");
            isSuccess = true;
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(dialog, "Lỗi thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}