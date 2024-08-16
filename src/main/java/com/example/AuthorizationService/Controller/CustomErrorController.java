package com.example.AuthorizationService.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError() {
        // Return a custom error view
        return "error"; // Ensure you have an error.html template
    }

    public String getErrorPath() {
        return "/error";
    }
}
