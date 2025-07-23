package project;
import java.util.*;

public class Organizer extends User {
	private ArrayList<Event> myEvents = new ArrayList<>();
	
	public Organizer (String id, String password)
	{
		super(id,password);
	}
	
	@Override //override the getRole method in the User class
	public String getRole()
	{
		return "Organizer";
	}
	
	//create an event
	public void createEvent(Event e)
	{
		myEvents.add(e);
		System.out.println("Event "+e.getName()+" created.");
	}
	
	//edit an event
	public void editEvent(String eventID, Event updEvent)
	{
		for(int i=0;i<myEvents.size();i++)
		{
			if(myEvents.get(i).getEventID().equals(eventID))
			{
				myEvents.set(i, updEvent);//use updated event to replace the old one
				System.out.println("Event updated.");
				return ;
			}
		}
		System.out.println("Event ID not found.");
	}
	
	//delete an event
	public void deleteEvent(String eventID)
	{
		for(int i=0;i<myEvents.size();i++)
		{
			if(myEvents.get(i).getEventID().equals(eventID))
			{
				myEvents.remove(i);
				System.out.println("Event deleted.");
				return ;
			}
		}
	}
	
	public ArrayList<Event> getMyEvents()
	{
		return myEvents;
	}
}
