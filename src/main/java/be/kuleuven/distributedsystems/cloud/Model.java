package be.kuleuven.distributedsystems.cloud;

import be.kuleuven.distributedsystems.cloud.controller.AuthController;
import be.kuleuven.distributedsystems.cloud.entities.*;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

import be.kuleuven.distributedsystems.cloud.Application;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

@Component
public class Model {


    private WebClient.Builder webClientBuilder = WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
            .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024));
    private final WebClient webClient = webClientBuilder.build();
    //"https://reliabletheatrecompany.com"

    public Model(WebClient.Builder builder) {
        this.webClientBuilder = builder;
    }
//
//    public MyService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("http://example.org").build();
//    }
//
//    public Mono<Details> someRestCall(String name) {
//        return this.webClient.get().uri("/shows/?key=wCIoTqec6vGJijW2meeqSokanZuqOL", name)
//                .retrieve().bodyToMono(Details.class);
//    }
//    private String createURI(String company, UUID showId){
//        String uri = String.format("My String is: %1$s, %1$s and %2$s", str1, str2);
//        String uri = "https://reliabletheatrecompany.com/shows/?key=wCIoTqec6vGJijW2meeqSokanZuqOL"
//        return uri
//    }

    public List<Show> getShows() {
        // TODO: return all shows
        List<Show> result1 = this.webClient.get().uri("https://reliabletheatrecompany.com/shows/?key=wCIoTqec6vGJijW2meeqSokanZuqOL")
                .retrieve().bodyToFlux(Show.class).collectList().block();
        List<Show> result2 = this.webClient.get().uri("https://unreliabletheatrecompany.com/shows/?key=wCIoTqec6vGJijW2meeqSokanZuqOL")
                .retrieve().bodyToFlux(Show.class).collectList().block();
//        System.out.printf("result is %s", result);
        return result1;
//        return new ArrayList<>();
    }

    public Show getShow(String company, UUID showId) {
        // TODO: return the given show
        String uri = String.format("https://%1$s.com/shows/%2$s/?key=wCIoTqec6vGJijW2meeqSokanZuqOL", company,showId);
        Show result = this.webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Show.class).block();
        return result;
//        return null;
    }

    public List<LocalDateTime> getShowTimes(String company, UUID showId) {
        // TODO: return a list with all possible times for the given show
        String uri = String.format("https://%1$s.com/shows/%2$s/times/?key=wCIoTqec6vGJijW2meeqSokanZuqOL", company,showId);
        List<LocalDateTime> result = this.webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(LocalDateTime.class).collectList().block();
        return result;
        // return new ArrayList<>();
    }

    public List<Seat> getAvailableSeats(String company, UUID showId, LocalDateTime time) {
        // TODO: return all available seats for a given show and time
        //{time} wat met haakjes?
        String uri = String.format("https://%1$s.com/shows/%2$s/seats?time={%3$s}&available=true/?key=wCIoTqec6vGJijW2meeqSokanZuqOL", company,showId, time);
        List<Seat> result = this.webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(Seat.class).collectList().block();
        return result;
        //return new ArrayList<>();
    }

    public Seat getSeat(String company, UUID showId, UUID seatId) {
        // TODO: return the given seat
        return null;
    }

    public Ticket getTicket(String company, UUID showId, UUID seatId) {
        // TODO: return the ticket for the given seat
        return null;
    }

    public List<Booking> getBookings(String customer) {
        // TODO: return all bookings from the customer
        return new ArrayList<>();
    }

    public List<Booking> getAllBookings() {
        // TODO: return all bookings
        return new ArrayList<>();
    }

    public Set<String> getBestCustomers() {
        // TODO: return the best customer (highest number of tickets, return all of them if multiple customers have an equal amount)
        return null;
    }

    public void confirmQuotes(List<Quote> quotes, String customer) {
        // TODO: reserve all seats for the given quotes
    }
}
