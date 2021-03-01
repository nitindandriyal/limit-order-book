package org.ecn.clob;

import org.ecn.clob.model.Order;

import java.util.Optional;

public class OrderParser {
    public Optional<Order> validateAndParseOrder(String[] tokens) {
        if (tokens.length != 4 && tokens.length !=5) {
            return Optional.empty();
        }
        if (tokens[0].trim().length() == 0) {
            return Optional.empty();
        }
        char side = tokens[1].charAt(0);
        int price;
        int volume;
        try {
            price = Integer.parseInt(tokens[2]);
            volume = Integer.parseInt(tokens[3]);
        } catch (NumberFormatException numberFormatException) {
            return Optional.empty();
        }
        boolean isValidSide = side == 'B' || side == 'S';
        if (!isValidSide || price < 0 || volume < 0) {
            return Optional.empty();
        }
        return Optional.of(new Order(tokens[0], tokens[1].charAt(0), price, volume));
    }
}
