package org.ecn.clob.model;

public class Trade {
    private final String buyer;
    private final String seller;
    private final int price;
    private final int volume;

    public Trade(String buyer, String seller, int price, int volume) {
        this.buyer = buyer;
        this.seller = seller;
        this.price = price;
        this.volume = volume;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getSeller() {
        return seller;
    }

    public int getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "trade " + buyer + "," + seller + ',' + price + "," + volume;
    }
}
