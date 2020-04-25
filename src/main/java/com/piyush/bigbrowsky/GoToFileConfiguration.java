package com.piyush.bigbrowsky;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class GoToFileConfiguration extends ChooseByNameFilterConfiguration<String> {

    public GoToFileConfiguration(){}

    public static GoToFileConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, GoToFileConfiguration.class);
    }

    @Override
    protected String nameForElement(String type) {
        return type;
    }
}
