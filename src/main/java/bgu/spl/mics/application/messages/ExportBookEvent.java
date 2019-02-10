package bgu.spl.mics.application.messages;
//from: selling service
//to: inventory service
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class ExportBookEvent implements Event<OrderResult> {
	private String book;

	public ExportBookEvent(String book) {
		this.book = book;
	}

	public String getBook() {
		return book;
	}

}
