package project;
import java.util.*;

public class Customer extends User{
	private ArrayList<Ticket> myTickets = new ArrayList<>();
	
	//a customer constructor
	public Customer (String id, String password)
	{
		super(id,password);
	}
	
	@Override //override the getRole method in the User class
	public String getRole()
	{
		return "Customer";
	}
	
	//add a ticket
	public void  addTicket(Ticket t)
	{
		myTickets.add(t);
	}
	
	//cancel a specific ticket
	public void cancelTicket(String ticketID)
	{
		boolean found=false;
		for(int i=0; i<myTickets.size();i++)
		{
			if(myTickets.get(i).getTicketID().equals(ticketID))
			{
				myTickets.remove(i);
				found=true;
				System.out.println("Ticket canceled successfully.");
				break;
			}
		}
		if(!found)
		{
			System.out.println("Ticket not found.");
		}
	}
	
	//get ticket list
	public ArrayList<Ticket> getMyTickets()
	{
		return myTickets;
	}
	
	
}
