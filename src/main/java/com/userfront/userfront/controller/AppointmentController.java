package com.userfront.userfront.controller;

import com.userfront.userfront.domain.Appointment;
import com.userfront.userfront.domain.User;
import com.userfront.userfront.service.AppointmentService;
import com.userfront.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/create")
    public String createAppointment(Model theModel) {
        Appointment appointment = new Appointment();
        theModel.addAttribute("appointment", appointment);
        theModel.addAttribute("dateString", "");

        return "appointment";
    }

    @PostMapping("/create")
    public String createAppointmentPost(@ModelAttribute("appointment")Appointment appointment,
                                        @ModelAttribute("dateString") String date, Model theModel,
                                        Principal principal) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date d1 = format.parse(date);
        appointment.setDate(d1);

        User user = userService.findByUsername(principal.getName());
        appointment.setUser(user);

        appointmentService.createAppointment(appointment);

        return "redirect:/userFront";
    }
}
