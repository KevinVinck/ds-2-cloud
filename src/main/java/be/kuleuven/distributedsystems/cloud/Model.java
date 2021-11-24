package be.kuleuven.distributedsystems.cloud;

import be.kuleuven.distributedsystems.cloud.entities.*;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.web.util.NestedServletException;

@Component
public class Model {

    @Autowired
    private ApplicationContext context;
    private String reliable = "https://reliabletheatrecompany.com";
    private String unreliable = "https://unreliabletheatrecompany.com";
    private String API_KEY = "wCIoTqec6vGJijW2meeqSokanZuqOL";
    Map<String, List<Booking>> registeredBookings = new HashMap<String, List<Booking>>();

    public List<Show> getShows() {
        WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");

        var result1 = webClientBuilder.baseUrl(reliable).build().get()
                .uri(uriBuilder -> uriBuilder.
                        pathSegment("shows").
                        queryParam("key", API_KEY).
                        build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {}).block().getContent();

        var result2 = webClientBuilder.baseUrl(unreliable).build().get()
                .uri(uriBuilder -> uriBuilder.
                        pathSegment("shows").
                        queryParam("key", API_KEY).
                        build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {}).block().getContent();

        List<Show> newList = new ArrayList<>(result1);
        newList.addAll(result2);
        return newList;
    }

    public Show getShow(String company, UUID showId){
        System.out.println("in getshow");
//        System.out.println(company);
//        System.out.println(showId);
//        System.out.println(String.format("https://%s", company));
//        System.out.println(String.format("shows/%s", showId.toString()));
        // TODO hier moet je werken met retry's van de webclient???
//        boolean stopperOn = false;
//        while (!stopperOn){
//            try{
                WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");
                var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                        .uri(uriBuilder -> uriBuilder.
                                pathSegment("shows", showId.toString()).
                                queryParam("key", API_KEY).
                                build())
                        .retrieve().bodyToMono(new ParameterizedTypeReference<Show>() {}).block();
                System.out.println(String.format("result is %s ", result));
//                stopperOn = true;
                return result;
//            }catch (NestedServletException e){
//                System.out.println("The server crashed");
//            }
//        }


    }


    public List<LocalDateTime> getShowTimes(String company, UUID showId) {
//        System.out.println(company);
//        System.out.println(showId);
//        System.out.println(String.format("https://%s", company));
//        System.out.println(String.format("shows/%s", showId.toString()));

        WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");
        var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                .uri(uriBuilder -> uriBuilder.
                        pathSegment("shows", showId.toString(), "times").
                        queryParam("key", API_KEY).
                        build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<CollectionModel<LocalDateTime>>() {}).block().getContent();
        List<LocalDateTime> newList = new ArrayList<>(result);
        return newList;
    }

    public List<Seat> getAvailableSeats(String company, UUID showId, LocalDateTime time) {
        System.out.println("1");

        System.out.println(company);
        System.out.println(showId);
        System.out.println(time);
        System.out.println(String.format("https://%s", company));
        System.out.println(String.format("shows/%s", showId.toString()));

        WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");

        var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                .uri(uriBuilder -> uriBuilder.
                        pathSegment("shows", showId.toString(), "seats").
                        queryParam("key", API_KEY).queryParam("time", time).queryParam("available", "true").
                        build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<CollectionModel<Seat>>() {}).block().getContent();
        List<Seat> newList = new ArrayList<>(result);
        return newList;
    }

    public Seat getSeat(String company, UUID showId, UUID seatId) {
        System.out.println("2");
        System.out.println(company);
        System.out.println(showId);
        System.out.println(String.format("https://%s", company));
        System.out.println(String.format("shows/%s", showId.toString()));
        System.out.println(String.format("seatid is /%s", seatId.toString()));


        WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");

        var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                .uri(uriBuilder -> uriBuilder.
                        pathSegment("shows/", showId.toString() + "/", "seats/", seatId.toString()).
                        queryParam("key", API_KEY).
                        build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<Seat>() {}).block();


        System.out.println(String.format("result is %s", result));

        return result;

    }

    public Ticket getTicket(String company, UUID showId, UUID seatId) {
        System.out.println("3");


        WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");


        var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                .uri(uriBuilder -> uriBuilder.
                        pathSegment("shows/", showId.toString() + "/", "seats/", seatId.toString() + "/ticket").
                        queryParam("key", API_KEY).
                        build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<Ticket>() {}).block();
        System.out.println();


        return result;
    }

    public List<Booking> getBookings(String customer) {
        System.out.println(String.format("customer is %s", customer));
        System.out.println("4");
        return this.registeredBookings.get(customer);
    }

    public List<Booking> getAllBookings() {
        System.out.println("5");
        List<Booking> result = new ArrayList<Booking>();
        this.registeredBookings.values().forEach(result::addAll);
        return result;
    }

    public Set<String> getBestCustomers() {
        System.out.println("6");
        Set<String> best_customers = new HashSet<String>();
        int highest_nmb_tickets = 0;

        for( String customer : this.registeredBookings.keySet()) {
            int number = 0;
            List<Booking> customerBookings = registeredBookings.get(customer);
            for(Booking booking: customerBookings){
                number = number + booking.getTickets().size();
            }
            if (number > highest_nmb_tickets){
                best_customers = new HashSet<String>();
                best_customers.add(customer);
                highest_nmb_tickets = number;
            } else if(number == highest_nmb_tickets){
                best_customers.add(customer);
            }
        }
        return best_customers;
    }

    public void confirmQuotes(List<Quote> quotes, String customer) {
//        WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");
//
//        ArrayList<Ticket> tickets = new ArrayList<>();
//        quotes.forEach(q -> {
//            var ticket = webClientBuilder.baseUrl(String.format("https://%s", q.getCompany())).build().put()
//                    .uri(uriBuilder -> uriBuilder.
//                            pathSegment("shows/", q.getShowId().toString(), "/seats/", q.getSeatId().toString(), "/ticket").
//                            queryParam("key", API_KEY).
//                            build())
//                    .retrieve().bodyToMono(new ParameterizedTypeReference<Ticket>() {}).block();
//            tickets.add(ticket);
//        });
        List<Ticket> allTickets = new ArrayList<Ticket>();
        for(Quote q: quotes){
            Ticket ticket = this.getTicket(q.getCompany(), q.getShowId(), q.getSeatId());
            allTickets.add(ticket);
        }
        Booking newBooking = new Booking(UUID.randomUUID(), LocalDateTime.now(), allTickets, customer);

        // TODO: hier moet pubsub tussen van kekke, all or nothing is dat je Bookings hebt
        //  en Puts van de pubsub kan halen


        List<Booking> newRegisteredBookings = new ArrayList<Booking>();
        newRegisteredBookings.add(newBooking);
        newRegisteredBookings.addAll(this.registeredBookings.get(customer));
        this.registeredBookings.put(customer, newRegisteredBookings);

        // TODO: reserve all seats for the given quotes
        //quotes ook meegeven
        ByteString customerByte = ByteString.copyFromUtf8(customer);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(customerByte).build();
    }
}
