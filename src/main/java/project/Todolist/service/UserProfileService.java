package project.Todolist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.Todolist.Repository.UserProfileRepository;

import project.Todolist.api.model.User;
import project.Todolist.api.model.UserProfile;




@Service
@Transactional
public class UserProfileService {
    @Autowired
    private final UserProfileRepository userProfileRepository;


    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository=userProfileRepository;

    }
    public UserProfile createProfile(UserProfile userProfile, User user) {
        userProfile.setUser(user);
        return userProfileRepository.save(userProfile);
    }


    public void updateProfile(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }

}
