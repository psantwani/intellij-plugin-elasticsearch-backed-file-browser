package com.piyush.bigbrowsky;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameFilter;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GoToFileAction extends GotoActionBase implements DumbAware {

    private static final Logger logger = Logger.getInstance(GoToFileAction.class.getName());
    private static DataSource dataSource = null;

    public GoToFileAction(){}

    @Override
    protected void gotoActionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        if (project == null) return;

        boolean isElasticSearchMode = Utils.checkIfElasticSearchModeIsEnabled();
        dataSource = isElasticSearchMode ? new ElasticSearchClient(project) : new FFClient(project);
        dataSource.connect();

        ChooseByNameContributor[] chooseByNameContributors = { new GoToFileContributor(dataSource) };
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
}
