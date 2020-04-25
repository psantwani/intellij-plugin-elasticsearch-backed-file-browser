package com.piyush.bigbrowsky;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameFilter;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoToFileAction extends GotoActionBase implements DumbAware {

    public GoToFileAction(){}

    @Override
    protected void gotoActionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ChooseByNameContributor[] chooseByNameContributors = {
                new GoToFileContributor(),
        };

        final GoToFileModel model = new GoToFileModel(project, chooseByNameContributors);

        GotoActionCallback<String> callback = new GotoActionCallback<String>() {
            @Override
            protected ChooseByNameFilter<String> createFilter(@NotNull ChooseByNamePopup popup) {
                System.out.println("createFilter");
                return new GoToFileFilter(popup, model, project);
            }

            @Override
            public void elementChosen(ChooseByNamePopup chooseByNamePopup, Object element) {
                System.out.println("elementChosen: " + element);
                if (element instanceof FileItem) {
                    FileItem navigationItem = (FileItem) element;
                    if (navigationItem.canNavigate()) {
                        navigationItem.navigate(true);
                    }
                }
            }
        };

        GoToFileProvider provider = new GoToFileProvider(getPsiContext(e));
        showNavigationPopup(e, model, callback, "File name matching pattern", true, true, (ChooseByNameItemProvider)provider);
    }

    protected <T> void showNavigationPopup(AnActionEvent e,
                                           ChooseByNameModel model,
                                           final GotoActionCallback<T> callback,
                                           @Nullable final String findUsagesTitle,
                                           boolean useSelectionFromEditor,
                                           final boolean allowMultipleSelection,
                                           final ChooseByNameItemProvider itemProvider) {
        final Project project = e.getProject();
        boolean mayRequestOpenInCurrentWindow = model.willOpenEditor() && FileEditorManagerEx.getInstanceEx(project).hasSplitOrUndockedWindows();
        // Pair<String, Integer> start = getInitialText(useSelectionFromEditor, e);
        // String copiedURL = tryFindCopiedURL();
        // String predefinedText = start.first == null ? copiedURL : start.first;
        String predefinedText = "";

        showNavigationPopup(callback, findUsagesTitle,
                GoToFilePopup.createPopup(project, model, itemProvider, predefinedText,
                        mayRequestOpenInCurrentWindow,
                        0), allowMultipleSelection);
    }

    protected static class GoToFileFilter extends ChooseByNameFilter<String> {
        GoToFileFilter(final ChooseByNamePopup popup, GoToFileModel model, final Project project) {
            super(popup, model, new GoToFileConfiguration(), project);
        }

        @Override
        @NotNull
        protected List<String> getAllFilterValues() {
            System.out.println("getAllFilterValues");
            List<String> elements = new ArrayList<>();
            return elements;
        }

        @Override
        protected String textForFilterValue(@NotNull String value) {
            System.out.println("textForFilterValue");
            return value;
        }

        @Override
        protected Icon iconForFilterValue(@NotNull String value) {
            return null;
        }
    }
}
