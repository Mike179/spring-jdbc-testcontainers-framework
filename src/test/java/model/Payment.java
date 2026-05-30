package model;

public record Payment(
        Long id,
        String paymentId,
        Integer amount
) {
}