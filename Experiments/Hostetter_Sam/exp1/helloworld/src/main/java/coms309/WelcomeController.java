package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }

    /*
    This allows you to after localhost:8080/"EnterNameHere" for your name to show at the end.
     */
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }

    @GetMapping("/welcome/{name}/{age}")
    public String welcomeWithAge(@PathVariable String name, @PathVariable int age) {
        return "Hello and welcome to COMS 309, " + name + ". You are " + age + " years old.";
    }
    @GetMapping("/user/{id}")
    public String welcomeUser(@PathVariable int id){
        if (id==1) {
            return "Hello and welcome to COMS 309, John.";
        }
        if (id==2) {
            return "Hello and welcome to COMS 309, Jane";
        }
        else{
            return "Error no user";
        }
    }

}
