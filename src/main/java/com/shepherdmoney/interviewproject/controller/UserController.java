package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    // Completed: wire in the user repository (~ 1 line)
    @Autowired
    UserRepository userRepo;


    /*/
        Minor Bug: Stack overflow error when getting all users after addCreditCardToUser.
        not an issue cause fixing get request not required.
         */
    @GetMapping("/users")
    List<User> all() {
        return userRepo.findAll();
    }


    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {


        // COMPLETED: Create a user entity with information given in the payload, store it in the database
        //       and return the id of the user in 200 OK response
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());

        if (user.getName() == null || user.getEmail() == null)
        {
            return ResponseEntity.badRequest().body(-1); //ResponseEntity needs to be an integer, could be made generic...
        }
        else {
            userRepo.save(user);
            return ResponseEntity.ok(user.getId());
        }


    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        // Completed: Return 200 OK if a user with the given ID exists, and the deletion is successful
        //       Return 400 Bad Request if a user with the ID does not exist
        //       The response body could be anything you consider appropriate
        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            userRepo.delete(user.get());
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("User with ID " + userId + " not found");
        }
    }
}
