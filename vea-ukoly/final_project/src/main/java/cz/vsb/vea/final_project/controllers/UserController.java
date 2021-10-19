package cz.vsb.vea.final_project.controllers;

import cz.vsb.vea.final_project.entities.User;
import cz.vsb.vea.final_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/get")
    public User getUser(long id) {
        return userRepository.getUser(id);
    }

    @PutMapping("/add")
    public void addUser(User user) {
        userRepository.save(user);
    }

    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

}
