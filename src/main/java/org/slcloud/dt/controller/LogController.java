package org.slcloud.dt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value="/log")
@Controller
public class LogController {
	
	@RequestMapping(value="/")
	public String index(@RequestParam("id")Integer id){
		return "index";
	}
}
