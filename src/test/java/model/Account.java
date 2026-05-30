package model;

public record Account(
        Long id,
        String owner,
        Integer balance
) {
}