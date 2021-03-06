package actions;

import actions.service.MyState;
import actions.service.UserStatePersistence;
import actions.ui.CredentialForm;
import com.intellij.ide.DataManager;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.squareup.okhttp.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class BuildTestRelease extends AnAction {

    OkHttpClient client = new OkHttpClient();
    Process process = null;
    String repoName;
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
        String command  = "git branch";
        String branchPath = runCommand(command);
        String branch = branchPath.split(" ")[1];
        String comment = "testRelease";
        command  = "git rev-parse --show-toplevel";
        repoName = getRepo(command);
        if(repoName.equals("service-market-provider-android")) {
            comment = "production";
        }

        doGetRequest("http://android-jenkins.urbanclap.com:8080/job/" + repoName + "/build", branch, comment, state);

        NotificationGroup noti = new NotificationGroup("testReleaseBuild", NotificationDisplayType.BALLOON, true);
        NotificationAction action = null;
        if(repoName.equals("service-market-customer-android-app") ) {
            action = NotificationAction.createSimple("Slack", () -> {
                Desktop desktop = java.awt.Desktop.getDesktop();
                URI oURL = null;
                try {
                    oURL = new URI("https://app.slack.com/client/T034MTGTM/CFD5QKJE9");
                    desktop.browse(oURL);
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        } else if(repoName.equals("service-market-provider-android")) {
            action = NotificationAction.createSimple("Slack", () -> {
                Desktop desktop = java.awt.Desktop.getDesktop();
                URI oURL = null;
                try {
                    oURL = new URI("https://app.slack.com/client/T034MTGTM/CCMC0Q3GT");
                    desktop.browse(oURL);
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
        noti.createNotification("Build",
                "testRelease build triggered on Jenkins: Check Slack",
                NotificationType.INFORMATION,
                null
        ).addAction(action).notify(e.getProject());
        process.destroy();
    }

    private String doGetRequest(String url, String branch, String comment, MyState state) {
        String body = "{\"parameter\": [{\"name\":\"BRANCH\", \"value\":\"" + branch+ "\"}, {\"name\":\"PROJECT\", \"value\":\"" + repoName + "\"}, {\"name\":\"COMMENT\", \"value\":\"#"+ comment +"\"}]}";
        RequestBody requestBody = new MultipartBuilder().addFormDataPart("json", body).build();
        Request request = new Request.Builder()
                .header("Jenkins-Crumb", state.getJenkinsCrumb())
                .header("Content-type","application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + state.getAuthorizationToken())
                .post(requestBody)
                .url(url)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body().toString();
    }

    private String getRepo(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true);
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        processBuilder.directory(new File(project.getBasePath()));

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String[] temp = (new BufferedReader(new InputStreamReader(process.getInputStream()))).readLine().split("/");
            return temp[temp.length -1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "service-market-customer-android-app";
    }

    private String runCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true);
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        processBuilder.directory(new File(project.getBasePath()));

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getBranch(new BufferedReader(new InputStreamReader(process.getInputStream())));
    }

    private String getBranch(BufferedReader bufferedReader) {
        String line  = "";
        while (true) {
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) {
                return "stage";
            }
            else if (line.startsWith("*"))
                return line;
        }
    }
}
