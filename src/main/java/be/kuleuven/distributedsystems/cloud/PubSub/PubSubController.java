package be.kuleuven.distributedsystems.cloud.PubSub;

import be.kuleuven.distributedsystems.cloud.Model;
import be.kuleuven.distributedsystems.cloud.PubSub.TransferEntities.PubSubTransfer;
import be.kuleuven.distributedsystems.cloud.controller.Cart;
import be.kuleuven.distributedsystems.cloud.entities.Quote;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/pubsub")
public class PubSubController {
    private final Model model;

    public PubSubController(Model model){
        this.model = model;
        System.out.println("init pubsubcontroller");
    }

    @PostMapping("/subscription")
    public void subscription(@RequestBody String body) throws IOException{
        //System.out.println("BODY" + body);
        ObjectMapper mapper = new ObjectMapper();
        PubSubTransfer transfer = mapper.readValue(body, PubSubTransfer.class);
        List<Quote> cart = Cart.fromCookie(new String(Base64.getDecoder().decode(transfer.message.data)));
        this.model.confirmQuotes(new ArrayList<>(cart), transfer.message.attributes.user);
    };
}
