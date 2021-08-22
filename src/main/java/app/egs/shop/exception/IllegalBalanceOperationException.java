package app.egs.shop.exception;


public class IllegalBalanceOperationException extends BadRequestException {

    public IllegalBalanceOperationException(String message) {
        super(message);
    }
}
