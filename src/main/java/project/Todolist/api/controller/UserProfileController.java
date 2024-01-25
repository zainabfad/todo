package project.Todolist.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import project.Todolist.Repository.UserProfileRepository;
import project.Todolist.api.model.User;
import project.Todolist.api.model.UserProfile;
import project.Todolist.api.user.UserRepository;
import project.Todolist.service.UserProfileService;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController {
    @Autowired
    private final UserProfileService userProfileService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserProfileRepository userProfileRepository;

    public UserProfileController(UserProfileService userProfileService, UserRepository userRepository, UserProfileRepository userProfileRepository) {

        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
        this.userProfileRepository=userProfileRepository;
    }


    @PostMapping("/{user_id}")
    public ResponseEntity<?> createProfile(@PathVariable Long user_id, @RequestBody UserProfile userProfile) {
        try {
            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

            UserProfile createdProfile = userProfileService.createProfile(userProfile, user);
            return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>("An error occurred while creating the user profile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @PutMapping("/{user_id}/{userProfile_id}")
    public ResponseEntity<?> editProfile(@PathVariable Long user_id,
                                              @PathVariable Long userProfile_id,
                                              @RequestBody UserProfile updateProfile){
        try {
            User user=userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

            UserProfile existingProfile = userProfileRepository.findById(userProfile_id)
                    .orElseThrow(() -> new EntityNotFoundException("UserProfile with ID " + userProfile_id + " not found"));

            if (!existingProfile.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UserProfile does not belong to the specified User.");
            }

            if(updateProfile.getFirstname()!=null) {
                existingProfile.setFirstname(updateProfile.getFirstname());
            }else {
                throw new IllegalStateException("please enter your firstname");
            }
            if(updateProfile.getLastname()!=null) {
                existingProfile.setLastname(updateProfile.getLastname());
            }else {
                throw new IllegalStateException("please enter your lastname");
            }
            if(updateProfile.getUsername()!=null){
                existingProfile.setUsername(updateProfile.getUsername());
            }else {
                throw new IllegalStateException("please enter your username");
            }
            if (updateProfile.getEmail()!=null){
                existingProfile.setEmail(updateProfile.getEmail());
            }else{
                throw new IllegalStateException("please enter your email");
            }
//                existingProfile.setProfileImageUrl(updateProfile.getProfileImageUrl());

            userProfileService.updateProfile(existingProfile);
            return ResponseEntity.status(HttpStatus.OK).body(existingProfile);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
        }
    }




}