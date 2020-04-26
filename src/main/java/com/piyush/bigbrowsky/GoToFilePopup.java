package com.piyush.bigbrowsky;

import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Used by CustomPopup only.
public class GoToFilePopup extends ChooseByNamePopup {

    protected GoToFilePopup(@Nullable Project project, @NotNull ChooseByNameModel model, @NotNull ChooseByNameItemProvider provider, @Nullable ChooseByNamePopup oldPopup, @Nullable String predefinedText, boolean mayRequestOpenInCurrentWindow, int initialIndex) {
        super(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
    }

    @Override
    public String transformPattern(String pattern) {
        final ChooseByNameModel model = getModel();
        return getTransformedPattern(pattern, model);
    }

    public static String getTransformedPattern(String pattern, ChooseByNameModel model) {
        if (! (model instanceof GoToFileModel) ) {
            return pattern;
        }
        return pattern;
    }

    @Override
    protected boolean isCloseByFocusLost() {
        return false;
    }

    @Nullable
    public String getMemberPattern() {
        final String enteredText = getTrimmedText();
        final int index = enteredText.lastIndexOf('#');
        if (index == -1) {
            return null;
        }

        String name = enteredText.substring(index + 1).trim();
        return StringUtil.isEmptyOrSpaces(name) ? null : name;
    }
}
