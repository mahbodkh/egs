package app.egs.shop.exception;


public class AccountFrozenException extends BadRequestException {

    public AccountFrozenException(String message) {
        super(message);
    }
}
