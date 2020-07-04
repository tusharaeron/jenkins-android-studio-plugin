package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class BuildCustomBranch extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if(UserStatePersistence.getInstance() == null ||
                UserStatePersistence.getInstance().getState() == null ||
                UserStatePersistence.getInstance().getState().getAuthorizationToken() == null ||
                UserStatePersistence.getInstance().getState().getAuthorizationToken().isEmpty() ||
                UserStatePersistence.getInstance().getState().getJenkinsCrumb() == null ||
                UserStatePersistence.getInstance().getState().getJenkinsCrumb().isEmpty()) {
            CredentialForm wrapper = new CredentialForm();
            if (wrapper.showAndGet()) {
                MyState state = UserStatePersistence.getInstance().getState();
                if (state == null || state.getAuthorizationToken().isEmpty()) return;
                startBuild(UserStatePersistence.getInstance().getState(), e);
            }
        } else {
            startBuild(UserStatePersistence.getInstance().getState(), e);
        }
    }

    private void startBuild(MyState state, AnActionEvent e) {
        MyNewForm buildBranch = new MyNewForm(e);
        buildBranch.setVisible(true);
    }
}
