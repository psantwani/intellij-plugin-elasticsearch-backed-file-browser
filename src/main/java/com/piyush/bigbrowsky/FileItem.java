package com.piyush.bigbrowsky;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FileItem implements NavigationItem {

    private static final Project project = ProjectManager.getInstance().getOpenProjects()[0];

    private final String fileName;
    private final String virtualPath;
    private String realPath;
    private Navigatable navigationElement;

    public FileItem(PsiElement psiElement, String fileName, String virtualPath, String realPath) {
        this.fileName = fileName;
        this.virtualPath = virtualPath;
        this.realPath = realPath;

        if (psiElement instanceof Navigatable) {
            navigationElement = (Navigatable) psiElement;
        }
    }

    @Nullable
    @Override
    public String getName() {
        return this.fileName;
    }

    public String getPath(){
        return this.virtualPath;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new RestServiceItemPresentation();
    }

    @Override
    public void navigate(boolean requestFocus) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(realPath);
        if(virtualFile != null){
            navigationElement = PsiManager.getInstance(project).findFile(virtualFile);
        }

        if(navigationElement != null){
            navigationElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    private class RestServiceItemPresentation implements ItemPresentation {

        @Nullable
        public String getPresentableText() {
            return " " + fileName;
        }

        @Nullable
        public String getLocationString() {
            return "(" + virtualPath + ")";
        }

        @Nullable
        @Override
        public Icon getIcon(boolean unused) {
            return IconLoader.getIcon("/icons/search.png");
        }
    }
}