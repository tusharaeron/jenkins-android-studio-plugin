package actions.ui;

import actions.service.UserStatePersistence;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.AbstractLayout;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class CredentialForm extends DialogWrapper {

    JPanel panel = new JPanel(new GridBagLayout());
    JTextField username = new JTextField();
    JTextField password = new JTextField();
    JTextField jenkinsCrumb = new JTextField();

    protected CredentialForm() {
        super(true);
        init();
        setTitle("Enter Jenkins Credentials");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        GridBag gb = new GridBag()
                .setDefaultWeightX(1.0)
                .setDefaultFill(GridBagConstraints.HORIZONTAL)
                .setDefaultInsets(new Insets(0, 0, AbstractLayout.DEFAULT_VGAP, AbstractLayout.DEFAULT_HGAP));
        panel.setPreferredSize(new Dimension(400, 200));
        panel.add(label("Username"), gb.nextLine().next().weightx(0.2));
        panel.add(username, gb.next().weightx(0.8));
        panel.add(label("Password"), gb.nextLine().next().weightx(0.2));
        panel.add(password, gb.next().weightx(0.8));
        panel.add(label("Jenkins Crumb"), gb.nextLine().next().weightx(0.2));
        panel.add(jenkinsCrumb, gb.next().weightx(0.8));

        if(UserStatePersistence.getInstance() != null &&
                UserStatePersistence.getInstance().getState() != null &&
                UserStatePersistence.getInstance().getState().getAuthorizationToken() != null &&
                !UserStatePersistence.getInstance().getState().getAuthorizationToken().isEmpty() ||
                UserStatePersistence.getInstance().getState().getJenkinsCrumb() == null ||
                UserStatePersistence.getInstance().getState().getJenkinsCrumb().isEmpty()) {
            username.setText(UserStatePersistence.getInstance().getState().getUsername());
            password.setText(UserStatePersistence.getInstance().getState().getPassword());
            jenkinsCrumb.setText(UserStatePersistence.getInstance().getState().getJenkinsCrumb());
        }
        return panel;
    }

    private JBLabel label(String text) {
        JBLabel label = new JBLabel(text);
        label.setComponentStyle(UIUtil.ComponentStyle.SMALL);
        label.setFontColor(UIUtil.FontColor.BRIGHTER);
        label.setBorder(JBUI.Borders.empty(0, 5, 2, 0));
        return label;
    }

    @Override
    protected void doOKAction() {
        if(username.getText().isEmpty() || password.getText().isEmpty() || jenkinsCrumb.getText().isEmpty()){
            JOptionPane.showMessageDialog(panel, "Please enter all the fields.");
            return ;
        }
        String username = this.username.getText();
        String password = this.password.getText();
        String jenkinsCrumb = this.jenkinsCrumb.getText();
        UserStatePersistence.getInstance().getState().setUsername(username);
        UserStatePersistence.getInstance().getState().setPassword(password);
        UserStatePersistence.getInstance().getState().setJenkinsCrumb(jenkinsCrumb);
        try {
            UserStatePersistence.getInstance().getState().setAuthorizationToken(Base64.getEncoder().encodeToString((username+":"+password).getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        super.doOKAction();
    }
}
