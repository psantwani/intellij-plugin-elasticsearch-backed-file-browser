package com.piyush.bigbrowsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class GoToFileModel extends FilteringGotoByModel<String> implements DumbAware, CustomMatcherModel {

    protected GoToFileModel(@NotNull Project project, @NotNull ChooseByNameContributor[] contributors) {
        super(project, contributors);
    }

    @Nullable
    @Override
    protected String filterValueFor(NavigationItem item) {
        if (item instanceof FileItem) {
            return item.getName();
        }

        return null;
    }

    @Nullable
    @Override
    protected synchronized Collection<String> getFilterItems() {
        return super.getFilterItems();
    }

    @Override
    protected boolean acceptItem(final NavigationItem item){
        return true;
    }

    @Override
    public String getPromptText() {
        return "Enter filename";
    }


    @NotNull
    @Override
    public String getNotInMessage() {
        return "File not found";
    }

    @NotNull
    @Override
    public String getNotFoundMessage() {
        return "File not found";
    }

    @Nullable
    @Override
    public String getCheckBoxName() {
        return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return true;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {
        return;
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

    /* TODO: Write JavaDoc */
    @Override
    public boolean matches(@NotNull String popupItem, @NotNull String userPattern) {
        return true;
    }

    @NotNull
    @Override
    public String removeModelSpecificMarkup(@NotNull String pattern) {
        return super.removeModelSpecificMarkup(pattern);
    }
}
