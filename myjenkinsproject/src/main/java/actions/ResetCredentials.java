package actions;

import com.intellij.openapi.actionSystem.ActionManager;
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