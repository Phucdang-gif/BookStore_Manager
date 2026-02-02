package DAO;

import DTO.InvoiceDetailDTO;
import config.DatabaseConnection;
import java.sql.PreparedStatement;

public class InvoiceDetailDAO {

    public void insert(InvoiceDetailDTO detail) {
        try {
            String sql = "INSERT INTO invoice_details (invoice_id, book_id, quantity, unit_price, subtotal) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);

            ps.setInt(1, detail.getInvoiceId());
            ps.setInt(2, detail.getBookId());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getUnitPrice());
            ps.setDouble(5, detail.getSubtotal());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}