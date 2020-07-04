package actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class ResetCredentialsForm extends JDialog{
    private JTextField inputUsername;
    private JTextField inputPassword;
    private JTextField inputCrumb;
    private JButton changeButton;
    private JButton cancelButton;
    private JPanel contentPane;
    private AnActionEvent anActionEvent;

    ResetCredentialsForm() {}

    ResetCredentialsForm(AnActionEvent actionEvent) {
        anActionEvent=actionEvent;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(changeButton);
        Editor editor = PlatformDataKeys.EDITOR.getData(actionEvent.getDataContext());
        int width=500,height=500;
        if(editor!=null) {
            if (editor.getComponent() != null) {
                width = editor.getComponent().getWidth();
                height = editor.getComponent().getHeight();
            }
        }
        changeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSubmit();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        if(UserStatePersistence.getInstance() != null &&
                UserStatePersistence.getInstance().getState() != null &&
                UserStatePersistence.getInstance().getState().getAuthorizationToken() != null &&
                !UserStatePersistence.getInstance().getState().getAuthorizationToken().isEmpty() ||
                UserStatePersistence.getInstance().getState().getJenkinsCrumb() == null ||
                UserStatePersistence.getInstance().getState().getJenkinsCrumb().isEmpty()) {
            inputUsername.setText(UserStatePersistence.getInstance().getState().getUsername());
            inputPassword.setText(UserStatePersistence.getInstance().getState().getPassword());
            inputCrumb.setText(UserStatePersistence.getInstance().getState().getJenkinsCrumb());
        }

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onSubmit() {
        String username = inputUsername.getText();
        String password = inputPassword.getText();
        String jenkinsCrumb = inputCrumb.getText();
        UserStatePersistence.getInstance().getState().setUsername(username);
        UserStatePersistence.getInstance().getState().setPassword(password);
        UserStatePersistence.getInstance().getState().setJenkinsCrumb(jenkinsCrumb);
        try {
            UserStatePersistence.getInstance().getState().setAuthorizationToken(Base64.getEncoder().encodeToString((username+":"+password).getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        onCancel();
    }
}
