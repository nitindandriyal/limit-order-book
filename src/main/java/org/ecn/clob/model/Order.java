package org.ecn.clob.model;

import java.util.Objects;

public class Order {
    private final String id;
    private final char side;
    private final int price;
    private int volume;

    public Order(String id, char side, int price, int volume) {
        this.id = id;
        this.side = side;
        this.price = price;
        this.volume = volume;
    }

    public String getId() {
        return id;
    }

    public char getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return side == order.side &&
                price == order.price &&
                volume == order.volume &&
                Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, side, price, volume);
    }

    @Override
    public String toString() {
        return id + "," + side + "," + price + "," + volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
