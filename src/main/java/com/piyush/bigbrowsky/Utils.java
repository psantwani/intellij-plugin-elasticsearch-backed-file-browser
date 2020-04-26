package com.piyush.bigbrowsky;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

public class Utils {


    public static void createPopupWithMessage(Project project, String message, MessageType messageType){
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        long FADEOUT_TIME = 1000;

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
