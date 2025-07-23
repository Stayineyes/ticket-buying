package project;
import java.util.*;
import java.io.*;

public class TicketingSystem {
	    private ArrayList<User> users;
	    private ArrayList<Event> events;
	    private ArrayList<Ticket> tickets;

	    public TicketingSystem() {
	        users = new ArrayList<>();
	        events = new ArrayList<>();
	        tickets = new ArrayList<>();
	    }

	    // ---------------- File I/O ----------------
	    public void loadDataFromFile() {
	        try {
	            // Load Users
	            Scanner userScanner = new Scanner(new File("users.txt"));
	            while (userScanner.hasNextLine()) {
	                String[] parts = userScanner.nextLine().split(",");
	                String role = parts[0];
	                String id = parts[1];
	                String password = parts[2];

	                if (role.equals("Customer")) {
	                    users.add(new Customer(id, password));
	                } else if (role.equals("Organizer")) {
	                    users.add(new Organizer(id, password));
	                }
	            }
	            userScanner.close();

	            // Load Events
	            Scanner eventScanner = new Scanner(new File("events.txt"));
	            while (eventScanner.hasNextLine()) {
	                String[] parts = eventScanner.nextLine().split(",");
	                String eventID = parts[0];
	                String name = parts[1];
	                String date = parts[2];
	                String location = parts[3];
	                int capacity = Integer.parseInt(parts[4]);
	                int ticketsSold = Integer.parseInt(parts[5]);
	                double price = Double.parseDouble(parts[6]);
	                String organizerID = parts[7];

	                Organizer creator = null;
	                for (User u : users) {
	                    if (u instanceof Organizer && u.getId().equals(organizerID)) {
	                        creator = (Organizer) u;
	                        break;
	                    }
	                }

	                Event event = new Event(eventID, name, date, location, capacity, ticketsSold, price, creator);
	                events.add(event);
	                if (creator != null) {
	                    creator.createEvent(event);
	                }
	            }
	            eventScanner.close();

	            // Load Tickets
	            Scanner ticketScanner = new Scanner(new File("tickets.txt"));
	            while (ticketScanner.hasNextLine()) {
	                String[] parts = ticketScanner.nextLine().split(",");
	                String ticketID = parts[0];
	                String eventID = parts[1];
	                int quantity = Integer.parseInt(parts[2]);
	                String buyerID = parts[3];

	                Event event = null;
	                Customer buyer = null;

	                for (Event e : events) {
	                    if (e.getEventID().equals(eventID)) {
	                        event = e;
	                        break;
	                    }
	                }

	                for (User u : users) {
	                    if (u instanceof Customer && u.getId().equals(buyerID)) {
	                        buyer = (Customer) u;
	                        break;
	                    }
	                }

	                if (event != null && buyer != null) {
	                    Ticket ticket = new Ticket(ticketID, event, quantity, buyer);
	                    tickets.add(ticket);
	                    buyer.addTicket(ticket);
	                    event.sellTicket(quantity); // Update event's ticketsSold
	                }
	            }
	            ticketScanner.close();

	            System.out.println("Data loaded successfully.");

	        } catch (FileNotFoundException e) {
	            System.out.println("Error: Data file not found.");
	        } catch (Exception e) {
	            System.out.println("Error loading data: " + e.getMessage());
	        }
	    }
	    

	    public void saveDataToFile() {
	        try {
	            // Save Users
	            PrintWriter userWriter = new PrintWriter("users.txt");
	            for (User u : users) {
	                userWriter.println(u.getRole() + "," + u.getId() + "," + u.getPassword());
	            }
	            userWriter.close();

	            // Save Events
	            PrintWriter eventWriter = new PrintWriter("events.txt");
	            for (Event e : events) {
	                eventWriter.println(e.getEventID() + "," + e.getName() + "," + e.getDate() + "," + e.getLocation()
	                    + "," + e.getCapacity() + "," + e.getTicketsSold() + "," + e.getPrice() + "," + e.getCreatedBy().getId());
	            }
	            eventWriter.close();

	            // Save Tickets
	            PrintWriter ticketWriter = new PrintWriter("tickets.txt");
	            for (Ticket t : tickets) {
	                ticketWriter.println(t.getTicketID() + "," + t.getEvent().getEventID() + "," + t.getQuantity() + "," + t.getBuyer().getId());
	            }
	            ticketWriter.close();

	            System.out.println("Data saved successfully.");
	        } catch (IOException e) {
	            System.out.println("Error saving data: " + e.getMessage());
	        }
	    }

	    // ---------------- User Management ----------------

	    public void registerUser(User user) {
	        users.add(user);
	        System.out.println("User registered: " + user.getId());
	    }

	    public User login(String id, String password) {
	        for (User u : users) {
	            if (u.getId().equals(id) && u.login(password)) {
	                System.out.println(u.getRole() + " " + u.getId() + " logged in.");
	                return u;
	            }
	        }
	        System.out.println("Invalid credentials.");
	        return null;
	    }

	    public ArrayList<User> getAllUsers() {
	        return users;
	    }

	    // ---------------- Event Management ----------------

	    public void addEvent(Event e) {
	        events.add(e);
	        System.out.println("Event added: " + e.getName());
	    }

	    public ArrayList<Event> getAllEvents() {
	        return events;
	    }

	    // ---------------- Ticket Management ----------------

	    public void buyTicket(Customer c, Event e, int qty) {
	        try {
	            if (!e.isAvailable(qty)) {
	                throw new TicketSoldOutException("Only " + (e.getCapacity() - e.getTicketsSold()) + " tickets left.");
	            }

	            String ticketID = "T" + (tickets.size() + 1);
	            Ticket t = new Ticket(ticketID, e, qty, c);

	            e.sellTicket(qty);
	            c.addTicket(t);
	            tickets.add(t);

	            System.out.println("Ticket purchased: " + ticketID + ", qty: " + qty);
	        } catch (TicketSoldOutException ex) {
	            System.out.println("Error: " + ex.getMessage());
	        }
	    }

	    public void cancelTicket(Customer c, String ticketID) {
	        Ticket toCancel = null;
	        for (Ticket t : tickets) {
	            if (t.getTicketID().equals(ticketID) && t.getBuyer().equals(c)) {
	                toCancel = t;
	                break;
	            }
	        }

	        if (toCancel != null) {
	            c.cancelTicket(ticketID);
	            toCancel.getEvent().cancelTicket(toCancel.getQuantity());
	            tickets.remove(toCancel);
	            System.out.println("Ticket canceled: " + ticketID);
	        } else {
	            System.out.println("Ticket not found or does not belong to customer.");
	        }
	    }

	    public ArrayList<Ticket> getTicketsForUser(String userID) {
	        ArrayList<Ticket> result = new ArrayList<>();
	        for (Ticket t : tickets) {
	            if (t.getBuyer().getId().equals(userID)) {
	                result.add(t);
	            }
	        }
	        return result;
	    }
	}

