package rest;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class otherEntry {
	@ResponseBody
	@RequestMapping("/helloworld")
	public String Helloworld(){
		Date nowDate = new Date();
		return "hello world "+nowDate.toString();
	}
	
	
	@ResponseBody
	@RequestMapping("/test2")
	public String test2(){
		Date nowDate = new Date();
		return "hello world "+nowDate.toString();
	}
}
