package com.openclassrooms.front.controller;

import com.openclassrooms.front.dto.LoginForm;
import com.openclassrooms.front.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * MVC controller responsible for authentication views.
 *
 * <p>
 * This controller handles:
 * <ul>
 *     <li>Login page display</li>
 *     <li>User authentication via session</li>
 *     <li>Logout and session invalidation</li>
 * </ul>
 *
 * <p>
 * Authentication information is stored in the HTTP session
 * and reused for API calls through the Gateway.
 */
@Controller
public class LoginController {

    /**
     * Root entry point of the application.
     * Redirects the user depending on authentication state.
     *
     * @param session HTTP session
     * @return redirect to login or patients list
     */
    @GetMapping("/")
    public String root(HttpSession session) {
        SessionAuth auth = (SessionAuth) session.getAttribute("auth");
        return (auth == null) ? "redirect:/login" : "redirect:/patients";
    }

    /**
     * Displays the login page.
     *
     * @param model Spring MVC model
     * @return login view
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("form", new LoginForm("", ""));
        return "login";
    }

    /**
     * Handles user login and stores credentials in session.
     *
     * @param form    login form
     * @param session HTTP session
     * @return redirect to patients list
     */
    @PostMapping("/login")
    public String doLogin(@ModelAttribute("form") LoginForm form, HttpSession session) {
        SessionAuth auth = new SessionAuth();
        auth.setUsername(form.username());
        auth.setPassword(form.password());
        session.setAttribute("auth", auth);
        return "redirect:/patients";
    }

    /**
     * Logs out the current user by invalidating the session.
     *
     * @param session HTTP session
     * @return redirect to login page
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
