package app.egs.shop.exception;

/**
 * Created by Ebrahim Kh.
 */
public class NotFoundException extends BadRequestException {
    public NotFoundException(String message) {
        super(message);
    }
}