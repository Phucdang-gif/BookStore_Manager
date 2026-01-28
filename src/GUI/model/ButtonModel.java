package GUI.model;

public class ButtonModel {
    private String title;
    private String iconPath;
    private String actionCommand;

    public ButtonModel(String title, String iconPath, String actionCommand) {
        this.title = title;
        this.iconPath = iconPath;
        this.actionCommand = actionCommand;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getActionCommand() {
        return actionCommand;
    }
}