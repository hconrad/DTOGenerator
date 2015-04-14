package com.conrad.jetbrains.plugin.generatedto;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiType;

import static com.intellij.psi.JavaPsiFacade.getElementFactory;

/**
 * PsiType Helper class
 */
public class PsiTypeUtil {

    /**
     * Method determines whether the given type extends the class Collection
     *
     * @param type    the type we are investigating
     * @param project the current project
     * @return true if the type extends Collection, false otherwise
     */
    public static boolean isCollection(PsiType type, Project project) {

        PsiElementFactory elementFactory = getElementFactory
                (project);
        PsiClassType collection = elementFactory.createTypeByFQClassName
                ("java.util.Collection");

        if (type.equalsToText("java.lang.Object")) {
            return false;
        }

        if (type.isAssignableFrom(collection)) {
            return true;
        }

        for (PsiType superType : type.getSuperTypes()) {
            return isCollection(superType, project);
        }

        return false;
    }
}
