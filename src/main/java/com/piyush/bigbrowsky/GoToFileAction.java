package com.piyush.bigbrowsky;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameFilter;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class GoToFileAction extends GotoActionBase implements DumbAware {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    public GoToFileAction(){}

    @Override
    protected void gotoActionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        if (project == null) return;
        ElasticSearchClient.connect();

        ChooseByNameContributor[] chooseByNameContributors = { new GoToFileContributor() };
        final GoToFileModel model = new GoToFileModel(project, chooseByNameContributors);

        GotoActionCallback<String> callback = new GotoActionCallback<String>() {
            @Override
            protected ChooseByNameFilter<String> createFilter(@NotNull ChooseByNamePopup popup) {
                return new GoToFileFilter(popup, model, project);
            }

            @Override
            public void elementChosen(ChooseByNamePopup chooseByNamePopup, Object element) {
                if (element instanceof FileItem) {
                    FileItem navigationItem = (FileItem) element;
                    if (navigationItem.canNavigate()) {
                        logger.info("File selected: " + navigationItem.getPath());
                        navigationItem.navigate(true);
                    }
                }
            }
        };

        /* Custom popup with advanced options.
        GoToFileProvider provider = new GoToFileProvider(getPsiContext(e));
        showNavigationPopup(e, model, callback, "File name matching pattern", true, true, (ChooseByNameItemProvider)provider);
        */

        showNavigationPopup(e, model, callback);
    }

    protected static class GoToFileFilter extends ChooseByNameFilter<String> {

        GoToFileFilter(final ChooseByNamePopup popup, GoToFileModel model, final Project project) {
            super(popup, model, new GoToFileConfiguration(), project);
        }

        @Override
        @NotNull
        protected List<String> getAllFilterValues() {
            return new ArrayList<>();
        }

        @Override
        protected String textForFilterValue(@NotNull String value) {
            return value;
        }

        @Override
        protected Icon iconForFilterValue(@NotNull String value) {
            return null;
        }
    }

    /*
    protected <T> void showNavigationPopup(AnActionEvent e, ChooseByNameModel model, final GotoActionCallback<T> callback,
                                           @Nullable final String findUsagesTitle, boolean useSelectionFromEditor,
                                           final boolean allowMultipleSelection, final ChooseByNameItemProvider itemProvider) {

        final Project project = e.getData(CommonDataKeys.PROJECT);
        boolean mayRequestOpenInCurrentWindow = model.willOpenEditor()
                && FileEditorManagerEx.getInstanceEx(Objects.requireNonNull(project)).hasSplitOrUndockedWindows();

        Pair<String, Integer> start = getInitialText(useSelectionFromEditor, e);
        String copiedURL = tryFindCopiedURL();
        String predefinedText = start.first == null ? copiedURL : start.first;

        showNavigationPopup(callback, findUsagesTitle, GoToFilePopup.createPopup(project, model, itemProvider, predefinedText,
                mayRequestOpenInCurrentWindow, 0), allowMultipleSelection);
    }

    private String tryFindCopiedURL() {
        String contents = CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);
        if (contents == null) {
            return null;
        }
        contents = contents.trim();
        return contents;
    }
    */
}
