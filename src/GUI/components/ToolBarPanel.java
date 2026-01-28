package GUI.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import GUI.model.ButtonModel;

public class ToolBarPanel extends JPanel {

    public ToolBarPanel(ArrayList<ButtonModel> buttonList, java.awt.event.ActionListener listener) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        // Dùng vòng lặp for-each để tạo nút từ mảng
        for (ButtonModel data : buttonList) {
            // Tạo nút ActionButton (đã có ở file trước của bạn)
            ActionButton btn = new ActionButton(data.getTitle(), data.getIconPath());

            // Gán ActionCommand để biết nút nào được nhấn
            btn.setActionCommand(data.getActionCommand());

            // Gán Listener bạn truyền từ ngoài vào (Controller hoặc Header)
            btn.addActionListener(listener);

            // Thêm vào Panel
            add(btn);
        }
    }
}