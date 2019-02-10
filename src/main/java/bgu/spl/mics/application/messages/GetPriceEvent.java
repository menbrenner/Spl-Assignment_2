package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
//from: selling service
//to: inventory service
public class GetPriceEvent implements Event<Integer> {
	private String Book;

	public GetPriceEvent(String book)
	{
		this.Book = book;

	}
	public String getBook() {
		return Book;
	}
}

