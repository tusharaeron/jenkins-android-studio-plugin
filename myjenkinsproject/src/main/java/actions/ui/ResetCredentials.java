package actions.ui;

import actions.ui.ResetCredentialsForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ResetCredentials extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ResetCredentialsForm resetCredentialsForm = new ResetCredentialsForm(e);
        resetCredentialsForm.setVisible(true);
    }
}