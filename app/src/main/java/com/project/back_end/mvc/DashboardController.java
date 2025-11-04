package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.project.back_end.services.Service;

@Controller
public class DashboardController {

    // 2. Autowire the Shared Service
    @Autowired
    private Service service;

    // 3. Admin Dashboard Routing
    @GetMapping("/adminDashboard/{token}")
    public ModelAndView adminDashboard(@PathVariable String token) {
        boolean isValid = service.validateToken(token, "admin");
        if (isValid) {
            return new ModelAndView("admin/adminDashboard");
        } else {
            return new ModelAndView("redirect:/");
        }
    }

    // 4. Doctor Dashboard Routing
    @GetMapping("/doctorDashboard/{token}")
    public ModelAndView doctorDashboard(@PathVariable String token) {
        boolean isValid = service.validateToken(token, "doctor");
        if (isValid) {
            return new ModelAndView("doctor/doctorDashboard");
        } else {
            return new ModelAndView("redirect:/");
        }
    }
}
