package com.conrad.jetbrains.plugin.generatedto;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.JavaPsiFacade.getElementFactory;

/**
 * GenerateDTO Action. Main class that is responsible for the generation of
 * the DTO class.
 */
public class GenerateDTO extends AnAction {

    /**
     * Method that gets called when the Generate DTO action is triggered.
     *
     * @param e the Action event we are observing
     */
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();

        if (project == null) {
            return;
        }

        Editor editor =
                FileEditorManager.getInstance(project).getSelectedTextEditor();

        if (editor == null) {
            return;
        }

        final Document document = editor.getDocument();

        if (document == null) {
            return;
        }

        final PsiFile file = e.getData(LangDataKeys.PSI_FILE);
        if (file == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = file.findElementAt(offset);
        final PsiClass psiClass =
                PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);

        createDTOClass(project, file, psiClass);
    }

    /**
     * Method will create/save the new file. The Source class is copied. If
     * the destination class already exists the method will exit and do
     * nothing. Once the destination class is created. The method iterates
     * through all source fields and makes copies of them on the destination
     * class. As well as generating the convertFromEntity class method.
     *
     * @param project  the current project
     * @param file     the current file in the editor
     * @param psiClass the source class we are working with
     */
    private void createDTOClass(final Project project, final PsiFile file,
                                final PsiClass psiClass) {
        try {
            JavaDirectoryService.getInstance().checkCreateClass(file
                    .getContainingDirectory(), psiClass.getName() + "DTO");
        } catch (IncorrectOperationException e1) {
            return;
        }

        new WriteCommandAction.Simple(project, file) {
            @Override
            protected void run() throws Throwable {
                PsiClass newClass = JavaDirectoryService.getInstance()
                        .createClass(file.getContainingDirectory(),
                                psiClass.getName() + "DTO");
                StringBuffer convertFromEntity = new StringBuffer();
                convertFromEntity.append("public static ")
                        .append(newClass.getName())
                        .append(" convertFromEntity(")
                        .append(psiClass.getName())
                        .append(" entity) {")
                        .append(newClass.getName())
                        .append(" dto = new ")
                        .append(newClass.getName())
                        .append("();");


                for (PsiField field : psiClass.getFields()) {
                    newClass.add(field.copy());


                    newClass.add(createGetMethod(field));
                    newClass.add(createSetMethod(field));

                    convertFromEntity.append("dto.")
                            .append(field.getName())
                            .append(" = entity.")
                            .append(getPrefix(field))
                            .append(capitalize(field.getName()))
                            .append("();");


                }
                convertFromEntity.append("return dto;}");

                newClass.add(getElementFactory(project)
                        .createMethodFromText(convertFromEntity.toString(),
                                null));
            }
        }.execute();
    }

    /**
     * Creates a getter method for a given field.
     *
     * @param field field are generating the getter for
     * @return the getter method
     */
    private PsiMethod createGetMethod(PsiField field) {
        StringBuilder method = new StringBuilder();

        String getterPrefix;

        getterPrefix = getPrefix(field);

        method.append("public ")
                .append(field.getType().getPresentableText())
                .append(" ")
                .append(getterPrefix)
                .append(capitalize(field.getName()))
                .append("()")
                .append("{ return this.")
                .append(field.getName())
                .append(";")
                .append("}");

        PsiElementFactory elementFactory = getElementFactory
                (field.getProject());

        return elementFactory.createMethodFromText(method.toString(), null);
    }


    /**
     * Generats a Prefix for a field. The method uses 'get' as
     * the prefix unless the field extends a Collection, then it will use the
     * prefix 'list'.
     *
     * @param field the field we are creating the method prefix for
     * @return non-null string
     */
    @NotNull
    private String getPrefix(PsiField field) {
        if (PsiTypeUtil.isCollection(field.getType(), field.getProject())) {
            return "list";
        } else {
            return "get";
        }
    }

    /**
     * Creates a set method for a given field.
     *
     * @param field field we are working with
     * @return the setter method
     */
    private PsiMethod createSetMethod(PsiField field) {
        StringBuilder method = new StringBuilder();

        method.append("public void set")
                .append(capitalize(field.getName()))
                .append("(")
                .append(field.getType().getPresentableText())
                .append(" ")
                .append(field.getName())
                .append(")")
                .append("{ this.")
                .append(field.getName())
                .append(" = ")
                .append(field.getName())
                .append(";")
                .append("}");

        PsiElementFactory elementFactory = getElementFactory
                (field.getProject());

        return elementFactory.createMethodFromText(method.toString(), null);
    }

    /**
     * Capitalizes the first letter of the given string.
     *
     * @param item the word we are capitalizing
     * @return capitalized string
     */
    private String capitalize(String item) {
        return Character.toUpperCase(item.charAt(0)) + item.substring(1);
    }


}
