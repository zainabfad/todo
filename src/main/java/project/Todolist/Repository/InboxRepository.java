package project.Todolist.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.Todolist.api.model.Inbox;

public interface InboxRepository extends JpaRepository <Inbox, Long> {

}
