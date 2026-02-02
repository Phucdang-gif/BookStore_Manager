package GUI.dialog.invoice;

import javax.swing.*;

public class InvoiceDialog extends JDialog {
    private InvoiceDialogView view;
    private InvoiceDialogController controller;

    public InvoiceDialog(JFrame parent) {
        super(parent, "Tạo Hóa Đơn Bán Hàng", true);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        // 1. Khởi tạo View
        view = new InvoiceDialogView();

        // 2. Khởi tạo Controller (Truyền View và Dialog vào)
        controller = new InvoiceDialogController(view, this);

        // 3. Setup
        setContentPane(view);
        controller.initData();
        controller.initEvents();
        controller.loadBookData("");
    }

    public boolean isSucceeded() {
        return controller.isSuccess();
    }
}