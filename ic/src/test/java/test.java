import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hello")
public class test {

    @RequestMapping("/start")
    @ResponseBody
    public String myTest(){
        return "hello world";
    }

}
