package app.egs.shop.controller;

import app.egs.shop.exception.NotFoundException;
import app.egs.shop.service.OrderService;
import app.egs.shop.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/order", produces = "application/json")
public class OrderController {
    // ==============================================
    //                     CLIENT
    // ==============================================
    @GetMapping("/{id}/user/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OrderReply> getOrder(@PathVariable("id") Long user) {
        var userEntity = userService.loadUser(user);
        if (ObjectUtils.isEmpty(user))
            throw new NotFoundException("Your account has not found.");
        var order = orderService.loadCurrentOrderByUser(userEntity.getId());
        return ResponseEntity.ok(
            new OrderReply(order.getId(),
                order.getUser(),
                order.getCart(),
                order.getPrice().doubleValue(),
                order.getTotal().doubleValue(),
                order.getDiscount().doubleValue(),
                order.getStatus().name(),
                order.getCreated(),
                order.getChanged()
            ));
    }

    @PostMapping("/create/{id}/user/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@PathVariable("id") Long user, @Valid @RequestBody OrderRequest request) {
        var userEntity = userService.loadUser(user);
        if (ObjectUtils.isEmpty(user))
            throw new NotFoundException("Your account has not found.");
        orderService.createOrder(userEntity, request.getCart());
    }


    // ==============================================
    //                     ADMIN
    // ==============================================
    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@PathVariable("id") Long order) {
        orderService.deleteOrder(order);
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class OrderRequest {
        @NotNull
        private Long cart;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class OrderReply {
        private Long id;
        private Long user;
        private Long cart;
        private Double price;
        private Double total;
        private Double discount;
        private String status;
        private Date created;
        private Date changed;
    }

    private final OrderService orderService;
    private final UserService userService;
}
