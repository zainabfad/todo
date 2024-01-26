package project.Todolist.api.email.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.Todolist.Repository.TaskRepository;

import org.springframework.scheduling.annotation.Scheduled;
import project.Todolist.api.model.Task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ReminderJob  {
    private final ReminderEmailService reminderEmailService;
    private final TaskRepository taskRepository;

//    @Scheduled(cron = "0 0 9 * * ?")
@Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void execute() {
        log.info("Scheduled job started.");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
        String dateTime = LocalDateTime.now().format(formatter);

        LocalDateTime atm = LocalDateTime.parse(dateTime, formatter);

        log.info("Current time: {}", atm);


        List<Task> tasks = taskRepository.findAllByReminderSent(false);

        log.info("Number of tasks retrieved: {}", tasks.size());

        for (Task task : tasks) {

            if (shouldSendReminder(task, atm)) {
                log.info("Sending reminder email for task: {}", task.getId());
                reminderEmailService.sendReminderEmail(task.getTodoList().getUser().getEmail(), task);

                task.setReminderSent(true);
                taskRepository.save(task);
            }
        }

        log.info("Scheduled job completed.");
    }

        private boolean shouldSendReminder(Task task, LocalDateTime atm) {
            if (taskRepository.existsByReminderSentAndId(true, task.getId())) {
                log.info("Reminder email already sent for task: {}", task.getId());
                return false;
            }
            return (task.getReminder() != null && task.getReminder().isEqual(atm));

        }


    @Scheduled(cron = "0 0 9 * * ?")
    public void executeDueDate(){
        LocalDate today = LocalDate.now();
        List<Task> tasks = taskRepository.findAllByReminderSent(false);


        for (Task task : tasks) {
            log.info("Task ID: {}", task.getId());
            if (shouldSendDueDate(task, today)) {
                reminderEmailService.sendReminderEmailForDueDate(task.getTodoList().getUser().getEmail(), task);

                task.setReminderSent(true);
                taskRepository.save(task);
            }
        }

    }

    private boolean shouldSendDueDate(Task task, LocalDate today) {
        log.info("Checking shouldSendReminder for Task ID: {}", task.getId());

        if (taskRepository.existsByReminderSentAndId(true, task.getId())) {
            log.info("Reminder email already sent for task: {}", task.getId());
            return false;
        }
       return  (task.getDueDate() != null && task.getDueDate().isEqual(today));


    }




}
