package com.mysite.irms.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins="http://localhost:3000", allowCredentials = "true")
public class WebErrorController implements ErrorController{
	@GetMapping({"/", "/error"})
	public String index() {
		return "index.html";
	}
}
