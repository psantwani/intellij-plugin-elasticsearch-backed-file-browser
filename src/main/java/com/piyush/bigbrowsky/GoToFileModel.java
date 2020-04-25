package com.piyush.bigbrowsky;

import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class GoToFileModel extends FilteringGotoByModel<String> implements DumbAware, CustomMatcherModel {

    protected GoToFileModel(@NotNull Project project, @NotNull ChooseByNameContributor[] contributors) {
        super(project, contributors);
    }

    @Nullable
    @Override
    protected String filterValueFor(NavigationItem item) {
        System.out.println("filterValueFor: " + item.toString());
        return null;
    }

    @Nullable
    @Override
    protected synchronized Collection<String> getFilterItems() {
        return super.getFilterItems();
    }

    @Override
    public String getPromptText() {
        return "Enter file name :";
    }

    @NotNull
    @Override
    public String getNotInMessage() {
        return "Match not found";
    }

    @NotNull
    @Override
    public String getNotFoundMessage() {
        return "File not found";
    }

    @Nullable
    @Override
    public String getCheckBoxName() {
        return "Only This Module";
        // return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {

    }

    @NotNull
    @Override
    public String[] getSeparators() {
        return new String[0];
    }

    @Nullable
    @Override
    public String getFullName(@NotNull Object element) {
        return getElementName(element);
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @Override
    public boolean matches(@NotNull String popupItem, @NotNull String userPattern) {
        System.out.println(popupItem);
        return false;
    }

}
