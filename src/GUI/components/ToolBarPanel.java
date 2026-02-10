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
        setBackground(ThemeColor.bgWhite);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        // Dùng vòng lặp for-each để tạo nút từ mảng
        for (ButtonModel data : buttonList) {
            ActionButton btn = new ActionButton(data.getTitle(), data.getIconPath());
            btn.setActionCommand(data.getActionCommand());

            // Gán Listener bạn truyền từ ngoài vào (Controller hoặc Header)
            listActions.add(btn);

            // Thêm vào Panel
            add(btn);
        }
    }

    public void setButtonVisible(boolean[] configs) {
        // Giả sử bạn lưu các nút trong một ArrayList<JButton> hoặc Component
        // Component[] components = this.getComponents(); // Nếu bạn add trực tiếp

        // Cách an toàn nhất nếu bạn không lưu list nút: Duyệt qua các component
        int index = 0;
        for (Component c : getComponents()) {
            if (c instanceof JButton) { // Hoặc ActionButton tùy class bạn dùng
                if (index < configs.length) {
                    c.setVisible(configs[index]);
                    c.setEnabled(configs[index]); // Có thể dùng setEnabled nếu muốn hiện mờ thay vì ẩn
                }
                index++;
            }
        }
        this.revalidate();
        this.repaint();
    }

    public void initEvent(ActionListener listener) {
        for (ActionButton btn : listActions) {
            btn.addActionListener(listener);
        }
    }
}