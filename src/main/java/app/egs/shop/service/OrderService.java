package app.egs.shop.service;

import app.egs.shop.domain.OrderEntity;
import app.egs.shop.domain.UserEntity;
import app.egs.shop.exception.NotFoundException;
import app.egs.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "order")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderService {

    @Transactional
    public OrderEntity createOrder(UserEntity user, Long cart) {
        var oldOrder = getOrderByUser(user.getId());
        oldOrder.ifPresent(orderEntity -> changeStatusOrder(orderEntity.getId(), OrderEntity.Status.CANCEL));
        var cartEntity = cartService.loadCart(cart);
        var discount = discountService.applyPromotion(cartEntity);
        var basicOrder = OrderEntity.getBasicOrder(user.getId(), cart, discount.getRate(), cartEntity.calculateTotal());
        var save = orderRepository.save(basicOrder);
        log.debug("The order has been persisted: ({}).", save);
        return save;
    }


    @Transactional(readOnly = true)
    public OrderEntity loadOrder(Long order) {
        return Optional.of(orderRepository.findById(order))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException(String.format("Order id (%s) not found for user.", order)));
    }

    @Transactional(readOnly = true)
    public OrderEntity loadCurrentOrderByUser(Long user) {
        return Optional.of(orderRepository.findByUserAndStatus(user, OrderEntity.Status.OPEN))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException(String.format("Order by user (%s) not found.", user)));
    }

    @Transactional(readOnly = true)
    public Optional<OrderEntity> getOrderByUser(Long user) {
        return orderRepository.findByUserAndStatus(user, OrderEntity.Status.OPEN);
    }


    private void changeStatusOrder(Long order, OrderEntity.Status status) {
        Optional.of(orderRepository.findById(order))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(reply -> {
                var current = reply.getStatus();
                reply.setStatus(status);
                var save = orderRepository.save(reply);
                log.debug("The order status (from:{}) -> (to:{}) has been changed.", current, status);
                return save;
            })
            .orElseThrow(() -> new NotFoundException(String.format("Order id (%s) not found for user.", order)));
    }

    @Transactional
    public void deleteOrder(Long order) {
        orderRepository
            .findById(order)
            .ifPresent(
                entity -> {
                    orderRepository.delete(entity);
                    log.debug("Deleted Order: ({}).", entity);
                }
            );
    }

    private final OrderRepository orderRepository;
    private final DiscountService discountService;
    private final CartService cartService;
}
