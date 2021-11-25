
package be.kuleuven.distributedsystems.cloud;

import be.kuleuven.distributedsystems.cloud.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;
;

@Component
public class Model {

    @Autowired
    private ApplicationContext context;
    private String reliable = "https://reliabletheatrecompany.com";
    private String unreliable = "https://unreliabletheatrecompany.com";
    private String API_KEY = "wCIoTqec6vGJijW2meeqSokanZuqOL";
    private Map<String, List<Booking>> registeredBookings = new HashMap<String, List<Booking>>();


    public List<Show> getShows() {
        try {
            WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");

            var result1 = webClientBuilder.baseUrl(reliable).build().get()
                    .uri(uriBuilder -> uriBuilder.
                            pathSegment("shows").
                            queryParam("key", API_KEY).
                            build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {
                    }).retry(4).block().getContent();

            var result2 = webClientBuilder.baseUrl(unreliable).build().get()
                    .uri(uriBuilder -> uriBuilder.
                            pathSegment("shows").
                            queryParam("key", API_KEY).
                            build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {
                    }).retry(4).block().getContent();

            List<Show> newList = new ArrayList<>(result1);
            newList.addAll(result2);
            return newList;
        }catch (Exception e){
            List<Show> error = new ArrayList<Show>();
            error.add(new Show("Error", UUID.randomUUID(), "Something went wrong, try to refresh the page", "Serverside error", ""));
            return error;
        }
    }

    public Show getShow(String company, UUID showId){
        try{
            WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");
            var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                    .uri(uriBuilder -> uriBuilder.
                            pathSegment("shows", showId.toString()).
                            queryParam("key", API_KEY).
                            build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<Show>() {}).retry(4).block();
            return result;
        }catch (Exception e) {
            return new Show("Error", UUID.randomUUID(), "Something went wrong, try to refresh the page", "Serverside error", "");
        }

    }


    public List<LocalDateTime> getShowTimes(String company, UUID showId) {
        try {
            WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");
            var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                    .uri(uriBuilder -> uriBuilder.
                            pathSegment("shows", showId.toString(), "times").
                            queryParam("key", API_KEY).
                            build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<CollectionModel<LocalDateTime>>() {
                    }).retry(4).block().getContent();
            List<LocalDateTime> newList = new ArrayList<>(result);
            return newList;
        }catch (Exception e){
            return new ArrayList<LocalDateTime>();
        }
    }

    public List<Seat> getAvailableSeats(String company, UUID showId, LocalDateTime time) {
        try{
            WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");

            var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                    .uri(uriBuilder -> uriBuilder.
                            pathSegment("shows", showId.toString(), "seats").
                            queryParam("key", API_KEY).queryParam("time", time).queryParam("available", "true").
                            build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<CollectionModel<Seat>>() {}).retry(4).block().getContent();
            List<Seat> newList = new ArrayList<>(result);
            return newList;
        }catch (Exception e){
            return new ArrayList<Seat>();
        }

    }

    public Seat getSeat(String company, UUID showId, UUID seatId) {
        try{
            WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");

            var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                    .uri(uriBuilder -> uriBuilder.
                            pathSegment("shows/", showId.toString() + "/", "seats/", seatId.toString()).
                            queryParam("key", API_KEY).
                            build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<Seat>() {}).retry(4).block();

            return result;
        }catch(Exception e){
            return new Seat("Error", UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "Something went wrong, try to refresh the page", "Error", 0);
        }

    }

    public Ticket getTicket(String company, UUID showId, UUID seatId) {
        try{
            WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");


            var result = webClientBuilder.baseUrl(String.format("https://%s", company)).build().get()
                    .uri(uriBuilder -> uriBuilder.
                            pathSegment("shows", showId.toString(), "seats", seatId.toString(), "ticket").
                            queryParam("key", API_KEY).
                            build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<Ticket>() {}).retry(4).block();

            return result;
        }catch(Exception e){
            return new Ticket("Error", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Something went wrong, try to refresh the page");
        }
    }

    public List<Booking> getBookings(String customer) {
        if (this.registeredBookings.containsKey(customer)){
            return this.registeredBookings.get(customer);
        }else{
            return new ArrayList<>();
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> result = new ArrayList<Booking>();
        this.registeredBookings.values().forEach(result::addAll);
        return result;
    }

    public Set<String> getBestCustomers() {
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
        System.out.println("in confirmQuotes");
        ArrayList<Ticket> tickets = new ArrayList<>();
        try {
            WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");
            quotes.forEach(q -> {
                var ticket = webClientBuilder.baseUrl(String.format("https://%s", q.getCompany())).build().put()
                        .uri(uriBuilder -> uriBuilder.
                                pathSegment("shows/", q.getShowId().toString(), "/seats/", q.getSeatId().toString(), "/ticket").
                                queryParam("customer", customer).queryParam("key", API_KEY).
                                build())
                        .retrieve().bodyToMono(new ParameterizedTypeReference<Ticket>() {}).retry(4).block();
                tickets.add(ticket);
                System.out.println(String.format("put ticket is %s", ticket));
                System.out.println(String.format("received ticket is %s", this.getTicket(q.getCompany(), q.getShowId(), q.getSeatId())));
            });
        }catch(Exception e){
            //When something goes wrong during the confirming of the quotes into tickets, delete the already booked tickets
            System.out.println("Problem occured during the booking of the tickets");
            deleteTickets(tickets);
        }

        Booking newBooking = new Booking(UUID.randomUUID(), LocalDateTime.now(), tickets, customer);

        List<Booking> newRegisteredBookings = new ArrayList<Booking>();
        newRegisteredBookings.add(newBooking);
        if(registeredBookings.containsKey(customer)) {
            newRegisteredBookings.addAll(registeredBookings.get(customer));
        }
        registeredBookings.put(customer, newRegisteredBookings);
        // TODO: reserve all seats for the given quotes. Moet dit nog? Miguel enzo hebben ook dezelfde functie als die wij nu hebben
    }

    private void deleteTickets(List<Ticket> tickets){
        WebClient.Builder webClientBuilder = (WebClient.Builder) context.getBean("webClientBuilder");
        tickets.forEach(q -> {
            var ticket = webClientBuilder.baseUrl(String.format("https://%s", q.getCompany())).build().delete()
                    .uri(uriBuilder -> uriBuilder.
                            pathSegment("shows/", q.getShowId().toString(), "/seats/", q.getSeatId().toString(), "/ticket", q.getTicketId().toString()).
                            queryParam("key", API_KEY).
                            build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<Ticket>() {}).retry(5).block();
        });
        System.out.println("tickets deleted");
    }
}
