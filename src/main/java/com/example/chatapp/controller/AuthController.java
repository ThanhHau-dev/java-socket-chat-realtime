package com.example.chatapp.controller;

import com.example.chatapp.entity.User;
import com.example.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller xử lý authentication (đăng ký, đăng nhập)
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Trang đăng ký
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Xử lý đăng ký user mới
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("fullName") String fullName,
                              Model model) {
        
        try {
            // Kiểm tra username đã tồn tại
            if (userService.existsByUsername(username)) {
                model.addAttribute("error", "Username đã tồn tại!");
                model.addAttribute("username", username);
                model.addAttribute("fullName", fullName);
                return "register";
            }
            
            // Kiểm tra độ dài password
            if (password.length() < 6) {
                model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
                model.addAttribute("username", username);
                model.addAttribute("fullName", fullName);
                return "register";
            }
            
            // Tạo user mới
            User newUser = userService.createUser(username, password, fullName);
            
            model.addAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
            return "index";
            
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi đăng ký: " + e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("fullName", fullName);
            return "register";
        }
    }

    /**
     * Xử lý đăng nhập
     */
    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           Model model) {
        
        try {
            User user = userService.authenticateUser(username, password);
            
            if (user != null) {
                return "redirect:/chat?username=" + username;
            } else {
                model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
                model.addAttribute("username", username);
                return "index";
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi đăng nhập: " + e.getMessage());
            model.addAttribute("username", username);
            return "index";
        }
    }
}
