package com.openclassrooms.front.controller;

import com.openclassrooms.front.dto.LoginForm;
import com.openclassrooms.front.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("form", new LoginForm("", ""));
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute("form") LoginForm form, HttpSession session) {
        SessionAuth auth = new SessionAuth();
        auth.setUsername(form.username());
        auth.setPassword(form.password());
        session.setAttribute("auth", auth);
        return "redirect:/patients";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
