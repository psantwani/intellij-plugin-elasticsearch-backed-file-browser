package com.piyush.bigbrowsky;

import com.fasterxml.jackson.databind.Module;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.IconLoader;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import javax.swing.*;

public class FileItem implements NavigationItem {

    private PsiMethod psiMethod;
    private PsiElement psiElement;
    private Module module;

    private String fileName;
    private String filePath;

    private Navigatable navigationElement;

    @Nullable
    @Override
    public String getName() {
        return this.fileName;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new RestServiceItemPresentation();
    }

    @Override
    public void navigate(boolean requestFocus) {

    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    private class RestServiceItemPresentation implements ItemPresentation {

        @Nullable
        public String getPresentableText() {
            return fileName;
        }

        @Nullable
        public String getLocationString() {
            String fileName = psiElement.getContainingFile().getName();
            String location = null;

            if (psiElement instanceof PsiMethod) {
                PsiMethod psiMethod = ((PsiMethod) psiElement);
                ;
                location = psiMethod.getContainingClass().getName().concat("#").concat(psiMethod.getName());
            } else if (psiElement instanceof KtNamedFunction) {
                KtNamedFunction ktNamedFunction = (KtNamedFunction) FileItem.this.psiElement;
                String className = ((KtClass) psiElement.getParent().getParent()).getName();
                location = className.concat("#").concat(ktNamedFunction.getName());
            }

            return "(" + location + ")";
        }

        @Nullable
        @Override
        public Icon getIcon(boolean unused) {
//            System.out.println(unused + "  " + this.getPresentableText());
            return IconLoader.getIcon("/icons/service.png");
        }
    }
}