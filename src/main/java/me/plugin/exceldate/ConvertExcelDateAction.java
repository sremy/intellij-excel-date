package me.plugin.exceldate;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertExcelDateAction extends AnAction {

    public static final String PLUGIN_GROUP_ID = "me.plugin";

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        event.getPresentation().setEnabledAndVisible(editor != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, implement an action.
        // For example, create and show a dialog.
        System.out.println(event);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        System.out.println("editor: " + editor);
        if (editor == null) {
            return;
        }
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null) {
            return;
        }

        // 19850731
        Pattern patternCompactReversed = Pattern.compile("([12][0-9]{3}[01][0-9][0123][0-9])");
        Matcher matcherCompact = patternCompactReversed.matcher(selectedText);
        if (matcherCompact.matches()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate localDate = LocalDate.parse(selectedText, formatter);
            int excelDate = convertLocalDateToExcel(localDate);
            showNotification(localDate + " => " + excelDate, "Date Plugin");
            return;
        }

        Pattern pattern = Pattern.compile("([0-9]{1,7})(.[0-9]+)?");
        Matcher matcher = pattern.matcher(selectedText);
        if (matcher.matches()) {
            String dateIsoFormat;
            dateIsoFormat = convertExcelDateToISO(Integer.parseInt(matcher.group(1)));
            showNotification(selectedText + " => " + dateIsoFormat, "Date Plugin");
            return;
        }

        showNotification("Please select an integer representing an Excel date", "Date Plugin");
    }

    public static String convertExcelDateToISO(int excelDate) {
        // Excel stores dates as the number of days since January 0, 1900 (including leap years)
        LocalDate baseDate = LocalDate.of(1899, 12, 31);
        LocalDate convertedDate = baseDate.plusDays(excelDate);

        // Adjust for the inconsistency with Excel date system
        convertedDate = convertedDate.minusDays(1);

        // Format the date as ISO 8601
        DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd eee");
        return convertedDate.format(isoFormatter);
    }

    public static int convertLocalDateToExcel(LocalDate date) {
        // Excel stores dates as the number of days since January 0, 1900 (including leap years)
        LocalDate baseDate = LocalDate.of(1899, 12, 30);

        return (int) date.toEpochDay() - (int) baseDate.toEpochDay();
    }

    public static void showNotification(String selectedText, String notificationTitle) {
        Notification notification = new Notification(
                PLUGIN_GROUP_ID,
                notificationTitle,
                selectedText,
                NotificationType.INFORMATION
        );

        // Display the notification
        Notifications.Bus.notify(notification);
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!

}