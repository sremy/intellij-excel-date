package me.plugin.exceldate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class InsertExcelDateAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        Project project = event.getData(PlatformDataKeys.PROJECT);
        event.getPresentation().setEnabledAndVisible(editor != null && project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        Project project = event.getData(PlatformDataKeys.PROJECT);
        System.out.println("editor: " + editor);
        if (editor == null || project == null) {
            return;
        }

        int excelDate = ConvertExcelDateAction.convertLocalDateToExcel(LocalDate.now());

        // Insert the text in the editor without replacing the selection
//        @NotNull CaretModel caretModel = editor.getCaretModel();
//        WriteCommandAction.runWriteCommandAction(project, () ->
//                editor.getDocument().insertString(caretModel.getOffset(), excelDate + ".0"));

        // Write the text in the editor and replace the selection if any
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = documentManager.getPsiFile(editor.getDocument());
        Runnable runnableInsertString = () -> EditorModificationUtil.insertStringAtCaret(editor, excelDate + ".0");
        WriteCommandAction.runWriteCommandAction(project, "Insert Excel Date",
                "me.plugin",
                runnableInsertString,
                psiFile);

    }
}
