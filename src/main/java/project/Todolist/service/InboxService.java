package project.Todolist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.Todolist.Repository.InboxRepository;
import project.Todolist.api.model.Inbox;
import project.Todolist.api.model.Task;

@Service
@Transactional
@RequiredArgsConstructor
public class InboxService {

    private  final InboxRepository inboxRepository;

    public Inbox createInbox(Inbox inbox) {

        return inboxRepository.save(inbox);

    }

}
