package project.Todolist.api.email.reminder;

import project.Todolist.api.model.Task;

public interface ReminderEmailSender {
    void sendReminderEmail(String to, Task task);

    void  sendReminderEmailForDueDate(String to, Task task);
}
