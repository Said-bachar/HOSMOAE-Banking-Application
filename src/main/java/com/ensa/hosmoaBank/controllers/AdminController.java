package com.ensa.hosmoaBank.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class AdminController {
	@GetMapping
	public String test() {
		System.out.println("Tested!!");
		return "Hello";
	}

}
