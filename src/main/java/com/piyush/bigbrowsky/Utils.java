package com.piyush.bigbrowsky;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

public class Utils {

    private static final long FADEOUT_TIME = 1000;
    private static final Project project = ProjectManager.getInstance().getOpenProjects()[0];
    private static final StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

    public static void createPopupWithMessage(String message, MessageType messageType){
        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(
                message,
                messageType,
                null
        ).setFadeoutTime(FADEOUT_TIME)
                .createBalloon().show(
                RelativePoint.getCenterOf(statusBar.getComponent()),
                Balloon.Position.atRight
        );
    }
}
