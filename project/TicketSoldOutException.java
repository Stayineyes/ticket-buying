package project;

public class TicketSoldOutException extends RuntimeException{
	public TicketSoldOutException(String message)
	{
		super(message);
	}
}
