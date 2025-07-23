package project;

public class Ticket {
	private String ticketID;
	private Event event;
	private int quantity;
	private Customer buyer;
	
	public Ticket (String ticketID, Event event,int quantity,Customer buyer)
	{
		this.buyer=buyer;
		this.event=event;
		this.quantity=quantity;
		this.ticketID=ticketID;
	}
	
	//calculate the price
	public double getTotalPrice()
	{
		return this.quantity*event.getPrice();
	}
	
	public String getInfo() {
        return "Ticket ID: " + ticketID +
               "\nEvent: " + event.getName() +
               "\nDate: " + event.getDate() +
               "\nLocation: " + event.getLocation() +
               "\nQuantity: " + quantity +
               "\nTotal Price: " + getTotalPrice();
    }

    // getters and setters
    public String getTicketID() { return ticketID; }
    public Event getEvent() { return event; }
    public int getQuantity() { return quantity; }
    public Customer getBuyer() { return buyer; }

 
    public void setQuantity(int quantity) { this.quantity = quantity; }

}
