package GUI.dialog.sale;

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
import java.util.ArrayList;
import java.util.List;

public class SalesDialogController {
    private SalesDialog view;
    private BookBUS bookBUS;
    private InvoiceBUS invoiceBUS;

    // Data Model
    private List<BookDTO> listBooks;
    private List<InvoiceDetailDTO> cartDetails = new ArrayList<>();
    private boolean isSuccess = false;

    public SalesDialogController(SalesDialog view) {
        this.view = view;
        this.bookBUS = new BookBUS();
        this.invoiceBUS = new InvoiceBUS();
    }

    public void initEvents() {
        // 1. Tìm kiếm sách
        view.getTxtSearch().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadBookData(view.getTxtSearch().getText());
            }
        });

        // 2. Click bảng sách -> Thêm vào giỏ
        view.getTblBooks().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = view.getTblBooks().getSelectedRow();
                    if (row != -1) {
                        int bookId = (int) view.getTblBooks().getValueAt(row, 0);
                        addToCart(bookId);
                    }
                }
            }
        });

        // 3. Tính tiền thừa khi nhập tiền khách đưa
        view.getTxtMoneyReceived().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });

        // 4. Nút Thanh toán
        view.getBtnPay().addActionListener(e -> processPayment());

        // 5. Phím tắt F9 để thanh toán
        view.getMainPanel().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F9) {
                    processPayment();
                }
            }
        });
    }

    public void loadBookData(String keyword) {
        listBooks = bookBUS.getAll();
        view.getModelBooks().setRowCount(0);
        String key = UIConstants.removeAccent(keyword);

        for (BookDTO b : listBooks) {
            String name = UIConstants.removeAccent(b.getBookTitle());
            if ((keyword.isEmpty() || name.contains(key) || b.getIsbn().contains(key))
                    && b.getStockQuantity() > 0 && !b.getStatus().equals("SUSPENDED")) {
                view.getModelBooks().addRow(new Object[] {
                        b.getBookId(),
                        b.getBookTitle(),
                        b.getFormattedSellingPrice(),
                        b.getStockQuantity()
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
                    JOptionPane.showMessageDialog(view, "Đã hết hàng trong kho!");
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

    public void removeFromCart() {
        int row = view.getTblCart().getSelectedRow();
        if (row != -1) {
            cartDetails.remove(row);
            refreshCartTable();
        }
    }

    private void refreshCartTable() {
        view.getModelCart().setRowCount(0);
        double total = 0;
        for (InvoiceDetailDTO item : cartDetails) {
            view.getModelCart().addRow(new Object[] {
                    item.getBookId(),
                    item.getBookTitle(),
                    item.getQuantity(),
                    String.format("%,.0f", item.getUnitPrice()),
                    String.format("%,.0f", item.getSubtotal())
            });
            total += item.getSubtotal();
        }
        view.getLblTotalMoney().setText(String.format("%,.0f VNĐ", total));
        calculateChange();
    }

    private void calculateChange() {
        try {
            double total = cartDetails.stream().mapToDouble(InvoiceDetailDTO::getSubtotal).sum();
            String moneyStr = view.getTxtMoneyReceived().getText().replace(",", "").replace(".", "");
            double received = moneyStr.isEmpty() ? 0 : Double.parseDouble(moneyStr);
            double change = received - total;
            view.getLblChangeAmount().setText(String.format("%,.0f VNĐ", change));
        } catch (Exception e) {
            view.getLblChangeAmount().setText("0 VNĐ");
        }
    }

    private void processPayment() {
        if (cartDetails.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Giỏ hàng trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = cartDetails.stream().mapToDouble(InvoiceDetailDTO::getSubtotal).sum();
        String moneyStr = view.getTxtMoneyReceived().getText().replace(",", "").replace(".", "");
        double received = moneyStr.isEmpty() ? 0 : Double.parseDouble(moneyStr);

        if (received < total) {
            JOptionPane.showMessageDialog(view, "Khách đưa thiếu tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setEmployeeId(1); // TODO: Lấy ID từ session
        invoice.setCustomerId(1);
        invoice.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        invoice.setTotalAmount(total);
        invoice.setFinalAmount(total);
        invoice.setStatus("PAID");
        invoice.setPaymentMethod("CASH");

        if (invoiceBUS.createInvoice(invoice, cartDetails)) {
            JOptionPane.showMessageDialog(view, "Thanh toán thành công!");
            this.isSuccess = true;
            view.dispose(); // Đóng View
        } else {
            JOptionPane.showMessageDialog(view, "Thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}