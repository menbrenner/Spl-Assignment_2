package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class FutureOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	private String bookTitle;
    private int tick;


    public FutureOrder(int orderTick, String book) {
        this.tick = orderTick;
        this.bookTitle = book;
    }

    public int getOrderTick() {
        return tick;
    }

    public String getBookName() {
        return bookTitle;
    }
}
