package be.kuleuven.distributedsystems.cloud.PubSub;

import be.kuleuven.distributedsystems.cloud.Model;
import be.kuleuven.distributedsystems.cloud.controller.AuthController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class PubSubController {
    private final Model model;

    public PubSubController(Model model){
        this.model = model;
        System.out.println("init pubsubcontroller");
        new Sub();
    }

    @PostMapping("/subscription")
    public void subscription(@RequestBody String body) throws IOException{
//      decode message here
    };
}
