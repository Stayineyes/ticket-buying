package project;

public class Event implements Manageable{
	private String eventID;
	private String name;
	private String date;
	private String location;
	private int capacity;
	private int ticketsSold;
	private double price;
	private Organizer createdBy;
	
	public Event (String eventID, String name, String date, String location,int capacity,int ticketsSold, double price, Organizer createdBy)
	{
		this.capacity=capacity;
		this.createdBy=createdBy;
		this.date=date;
		this.eventID=eventID;
		this.location=location;
		this.name=name;
		this.price=price;
		this.ticketsSold=0;//initially the sold ticket number is 0
	}
	
	//check if the left quantity is enough for the need 
	public boolean isAvailable(int qty)
	{
		return (ticketsSold +qty<=capacity);
	}
	
	public void sellTicket(int qty)throws TicketSoldOutException
	{
		if(isAvailable(qty))
		{
			ticketsSold+=qty;
		}
		else
		{
			throw new TicketSoldOutException("The stock of the product you have chosen is insufficient.");
		}
	}
	
	//cancel purchasing
	public void cancelTicket(int qty)
	{
		ticketsSold-=qty;
		if(ticketsSold<0) ticketsSold=0;
	}
	
	//get ticket information
	public String getDetails()
	{
		return "Event ID: "+eventID+
				"\nName: "+name+
				"\nDate: "+date+
				"\nLocation: "+location+
				"\nPrice: "+price+
				"\nCapacity: "+capacity+
				"\nTicketsSold: "+ticketsSold;
	}
	
	//implement interface manageable
	@Override
	public void create()
	{
		System.out.println("Event "+name+" created.");
	}
	@Override
	public void update()
	{
		System.out.println("Event "+name+" updated.");
	}
	@Override
	public void delete()
	{
		System.out.println("Event "+name+" delete.");
	}
	
	//getters and setters
	  public String getEventID() { return eventID; }
	  public String getName() { return name; }
	  public String getDate() { return date; }
	  public String getLocation() { return location; }
	  public int getCapacity() { return capacity; }
	  public int getTicketsSold() { return ticketsSold; }
	  public double getPrice() { return price; }
	  public Organizer getCreatedBy() { return createdBy; }

	  public void setName(String name) { this.name = name; }
	  public void setDate(String date) { this.date = date; }
	  public void setLocation(String location) { this.location = location; }
	  public void setCapacity(int capacity) { this.capacity = capacity; }
	  public void setPrice(double price) { this.price = price; }

}
