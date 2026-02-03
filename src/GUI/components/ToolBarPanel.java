package GUI.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import GUI.model.ButtonModel;
import GUI.util.ThemeColor;

public class ToolBarPanel extends JPanel {
    private ArrayList<ActionButton> listActions = new ArrayList<>();

    public ToolBarPanel(ArrayList<ButtonModel> buttonList) {
        setOpaque(true);
        setBackground(ThemeColor.bgPanel);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        // Dùng vòng lặp for-each để tạo nút từ mảng
        for (ButtonModel data : buttonList) {
            // Tạo nút ActionButton (đã có ở file trước của bạn)
            ActionButton btn = new ActionButton(data.getTitle(), data.getIconPath());
            btn.setActionCommand(data.getActionCommand());

            // Gán Listener bạn truyền từ ngoài vào (Controller hoặc Header)
            listActions.add(btn);

            // Thêm vào Panel
            add(btn);
        }
    }

    public void initEvent(ActionListener listener) {
        for (ActionButton btn : listActions) {
            btn.addActionListener(listener);
        }
    }
}