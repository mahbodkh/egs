package app.egs.shop.exception;



public class InsufficientFundsException extends BadRequestException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
