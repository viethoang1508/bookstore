package org.example.order_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.order_service.client.BookClient;
import org.example.order_service.client.PromotionClient;
import org.example.order_service.dto.request.*;
import org.example.order_service.dto.response.*;
import org.example.order_service.entity.Order;
import org.example.order_service.entity.OrderItem;
import org.example.order_service.entity.OrderStatus;
import org.example.order_service.exception.ApplicationException;
import org.example.order_service.kafka.event.OrderCreatedEvent;
import org.example.order_service.kafka.event.OrderStockDeductedEvent;
import org.example.order_service.kafka.event.OrderStockFailedEvent;
import org.example.order_service.kafka.producer.OrderEventProducer;
import org.example.order_service.repository.OrderItemRepository;
import org.example.order_service.repository.OrderRepository;
import org.example.order_service.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final BookClient bookClient;
    private final PromotionClient promotionClient;
    private final OrderEventProducer orderEventProducer;


    @Override
    public CheckoutPreviewResponse preview(PlaceOrderRequest request, String userId) {
        log.info("Previewing order, userId={}", userId);

        // Validate
        if(request == null){
            throw new ApplicationException("Invalid request");
        }

        if (userId == null){
            throw new ApplicationException("Invalid user id");
        }

        // Items
        List<OrderItemRequest> items = request.getItems();

        if (items == null || items.isEmpty()) {
            throw new ApplicationException("Items cannot be empty");
        }

        // Lấy danh sách book id
        List<String> bookIds = new ArrayList<>();
        for(OrderItemRequest item : items){
            bookIds.add(item.getBookId());
        }

        // Gọi sang Book Service kiểm tra
        List<BookDTO> existingBooks = bookClient.getBooksInfo(bookIds);

        Map<String, BookDTO> existingBooksMap = new HashMap<>();
        for(BookDTO book : existingBooks){
            existingBooksMap.put(book.getBookId(), book);
        }

        // Check thiếu books
        if(existingBooks.size() != bookIds.size()){
            throw new ApplicationException("Book Ids not match");
        }

        // Tính tổng
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Tạo list gửi promotion
        List<PromotionItemRequest> promotionItemRequestList = new ArrayList<>();

        for(OrderItemRequest item : items){

            String bookId = item.getBookId();
            Integer quantity = item.getQuantity();

            if (quantity == null || quantity <= 0) {
                throw new ApplicationException("Invalid quantity");
            }

            BookDTO book = existingBooksMap.get(bookId);

            if (book == null) {
                throw new ApplicationException("Book not found");
            }

            if(quantity > book.getStock()){
                throw new ApplicationException("Quantity exceeded");
            }

            BigDecimal subTotal = book.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            totalPrice = totalPrice.add(subTotal);

            PromotionItemRequest promotionItemRequest = new PromotionItemRequest();
            promotionItemRequest.setBookId(bookId);
            promotionItemRequest.setQuantity(quantity);
            promotionItemRequest.setPrice(book.getPrice());
            promotionItemRequest.setSubTotal(subTotal);

            promotionItemRequestList.add(promotionItemRequest);
        }

        // Áp dụng promotion nếu có
        BigDecimal discountAmount = BigDecimal.ZERO;

        ApplyPromotionResponse promoResponse = null; // ko khởi tạo object

        if (request.getPromotionCode() != null) {
            ApplyPromotionRequest applyPromotionRequest = new ApplyPromotionRequest();
            applyPromotionRequest.setUserId(userId);
            applyPromotionRequest.setCode(request.getPromotionCode());
            applyPromotionRequest.setTotalAmount(totalPrice);
            applyPromotionRequest.setItems(promotionItemRequestList);

            promoResponse = promotionClient.applyPromotion(applyPromotionRequest);
        }

        if (promoResponse != null && promoResponse.getDiscountAmount() != null) {
            discountAmount = promoResponse.getDiscountAmount();
        }

        // Build CheckoutItemDTO
        List<CheckoutItemDTO> checkoutItems = new ArrayList<>();

        // Kiểm tra xem có giảm giá theo từng sách không
        Map<String, BigDecimal> discountDetailMap = new HashMap<>();

        if (promoResponse != null && promoResponse.getDiscountDetais() != null) {
            for (DiscountDetail discountDetail : promoResponse.getDiscountDetais()) {
                discountDetailMap.put(
                        discountDetail.getBookId(),
                        discountDetail.getSubDiscountAmount()
                );
            }
        }

        // Build items
        for (OrderItemRequest item : request.getItems()){
            BookDTO book = existingBooksMap.get(item.getBookId());

            CheckoutItemDTO dto = new CheckoutItemDTO();
            dto.setBookId(book.getBookId());
            dto.setBookName(book.getBookName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(book.getPrice());

            // Sách đó được giảm bao nhiêu (tổng giảm nếu mua nhiều)
            BigDecimal itemDiscount = discountDetailMap.getOrDefault(
                    item.getBookId(),
                    BigDecimal.ZERO
            );

            // chia mỗi sách giảm bao nhiêu
            BigDecimal discountPerUnit = BigDecimal.ZERO;

            if (item.getQuantity() > 0) {
                discountPerUnit = itemDiscount.divide(
                        BigDecimal.valueOf(item.getQuantity()),2, RoundingMode.HALF_UP
                );
            }

            // Tính discountedPrice
            BigDecimal discountedPrice = book.getPrice().subtract(discountPerUnit);
            if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
                discountedPrice = BigDecimal.ZERO;
            }

            // Tính subTotal
            BigDecimal originalSubTotal = book.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal subTotal = originalSubTotal.subtract(itemDiscount);

            if (subTotal.compareTo(BigDecimal.ZERO) < 0) {
                subTotal = BigDecimal.ZERO;
            }

            dto.setSubTotal(subTotal);
            dto.setDiscountedPrice(discountedPrice);

            checkoutItems.add(dto);
        }

        // Tính final price
        BigDecimal finalPrice = totalPrice.subtract(discountAmount);

        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        // Tạo CheckoutPreviewResponse
        CheckoutPreviewResponse response = new CheckoutPreviewResponse();
        response.setItems(checkoutItems);
        response.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
        response.setTotalPrice(totalPrice);
        response.setFinalPrice(finalPrice);
        response.setPromotionCode(request.getPromotionCode());

        log.info("Order preview generated, userId={}, itemsCount={}, total={}, final={}", userId, checkoutItems.size(), totalPrice, finalPrice);
        return response;
    }


    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request, String userId) {
        log.info("Placing order, userId={}", userId);

        // 1. Validate
        if(request == null){
            throw new ApplicationException("Invalid request");
        }


        if (userId == null){
            throw new ApplicationException("Invalid user id");
        }

        List<OrderItemRequest> items = request.getItems();

        if (items == null || items.isEmpty()) {
            throw new ApplicationException("Items cannot be empty");
        }

        // 2. Lấy books
        List<String> bookIds = items.stream()
                .map(OrderItemRequest::getBookId)
                .toList();

        // Gọi sang Book Service kiểm tra
        List<BookDTO> existingBooks = bookClient.getBooksInfo(bookIds);


        Map<String, BookDTO> existingBooksMap = new HashMap<>();
        for(BookDTO book : existingBooks){
            existingBooksMap.put(book.getBookId(), book);
        }


        // Check thiếu books
        if(existingBooks.size() != bookIds.size()){
            throw new ApplicationException("Book Ids not match");
        }


        // 3. Tính tổng
        BigDecimal totalPrice = BigDecimal.ZERO;


        // Tạo list gửi promotion
        List<PromotionItemRequest> promotionItemRequestList = new ArrayList<>();


        for(OrderItemRequest item : items){


            String bookId = item.getBookId();
            Integer quantity = item.getQuantity();


            if (quantity == null || quantity <= 0) {
                throw new ApplicationException("Invalid quantity");
            }


            BookDTO book = existingBooksMap.get(bookId);


            if (book == null) {
                throw new ApplicationException("Book not found");
            }


            if(quantity > book.getStock()){
                throw new ApplicationException("Quantity exceeded");
            }


            BigDecimal subTotal = book.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));


            totalPrice = totalPrice.add(subTotal);


            PromotionItemRequest promotionItemRequest = new PromotionItemRequest();
            promotionItemRequest.setBookId(bookId);
            promotionItemRequest.setQuantity(quantity);
            promotionItemRequest.setPrice(book.getPrice());
            promotionItemRequest.setSubTotal(subTotal);


            promotionItemRequestList.add(promotionItemRequest);
        }


        // 4. Áp dụng promotion nếu có
        BigDecimal discountAmount = BigDecimal.ZERO;

        ApplyPromotionResponse promoResponse = null;

        if (request.getPromotionCode() != null) {
            ApplyPromotionRequest applyPromotionRequest = new ApplyPromotionRequest();
            applyPromotionRequest.setUserId(userId);
            applyPromotionRequest.setCode(request.getPromotionCode());
            applyPromotionRequest.setTotalAmount(totalPrice);
            applyPromotionRequest.setItems(promotionItemRequestList);

            promoResponse = promotionClient.applyPromotion(applyPromotionRequest);
        }

        if (promoResponse != null && promoResponse.getDiscountAmount() != null) {
            discountAmount = promoResponse.getDiscountAmount();
        }

        // Build CheckoutItemDTO
        List<CheckoutItemDTO> checkoutItems = new ArrayList<>();

        // 5. Map discount
        Map<String, BigDecimal> discountDetailMap = new HashMap<>();

        if (promoResponse != null && promoResponse.getDiscountDetais() != null) {
            for (DiscountDetail discountDetail : promoResponse.getDiscountDetais()) {
                discountDetailMap.put(
                        discountDetail.getBookId(),
                        discountDetail.getSubDiscountAmount()
                );
            }
        }

        // 6. Final Price
        BigDecimal finalPrice = totalPrice.subtract(discountAmount);

        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        // 7. TẠO ORDER
        Order order = new Order();

        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalPrice);
        order.setFinalAmount(finalPrice);

        orderRepository.save(order);

        // 8. TẠO ORDER ITEMS
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest orderItem : items) {

            BookDTO book = existingBooksMap.get(orderItem.getBookId());

            BigDecimal originalPrice = book.getPrice();

            // Discount nhiều sách
            BigDecimal itemDiscount = discountDetailMap.getOrDefault(
                    orderItem.getBookId(),
                    BigDecimal.ZERO
            );
            BigDecimal discountPerUnit = BigDecimal.ZERO;

            Integer quantity = orderItem.getQuantity();

            if (quantity > 0) {
                discountPerUnit = itemDiscount.divide(
                        BigDecimal.valueOf(orderItem.getQuantity()),
                        2,
                        RoundingMode.HALF_UP
                );
            }

            BigDecimal discountedPrice = originalPrice.subtract(discountPerUnit);

            if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
                discountedPrice = BigDecimal.ZERO;
            }

            BigDecimal subTotal = originalPrice
                    .multiply(BigDecimal.valueOf(quantity))
                    .subtract(itemDiscount);

            if (subTotal.compareTo(BigDecimal.ZERO) < 0) {
                subTotal = BigDecimal.ZERO;
            }

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setBookId(orderItem.getBookId());
            item.setQuantity(quantity);
            item.setPrice(discountedPrice);
            item.setBookName(book.getBookName());
            item.setTotalPrice(subTotal);

            orderItems.add(item);
        }

        orderItemRepository.saveAll(orderItems);

        // 9. KAFKA EVENT
        List<DeductStockRequest> deductRequests = items.stream()
                .map(i -> {
                    DeductStockRequest deductStockRequest = new DeductStockRequest();
                    deductStockRequest.setBookId(i.getBookId());
                    deductStockRequest.setQuantity(i.getQuantity());
                    return deductStockRequest;
                })
                .toList();

        OrderCreatedEvent  orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderId(order.getId());
        orderCreatedEvent.setDeductRequests(deductRequests);

        orderEventProducer.publishOrderCreated(orderCreatedEvent);
        log.info("Published order created event, orderId={}", order.getId());

        // 10. TẠO RESPONSE
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setTotalAmount(totalPrice);
        orderResponse.setDiscountAmount(discountAmount);
        orderResponse.setStatus(OrderStatus.PENDING.name());

        List<OrderItemDTO> itemDTOS = orderItems.stream()
                .map(oi -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setBookId(oi.getBookId());
                    dto.setBookName(oi.getBookName());
                    dto.setQuantity(oi.getQuantity());
                    dto.setPrice(oi.getPrice());
                    dto.setTotalPrice(oi.getTotalPrice());

                    return dto;
                })
                .toList();
        orderResponse.setItems(itemDTOS);

        log.info("Order placed successfully, orderId={}, userId={}", order.getId(), userId);
        return orderResponse;
    }

    @Override
    @Transactional
    public void handleStockDeductedEvent(OrderStockDeductedEvent event) {
        log.info("Handling stock deducted event, orderId={}", event != null ? event.getOrderId() : null);

        // Kiểm orderId
        String orderId = event.getOrderId();

        if (orderId == null || orderId.isBlank()) {
            log.error("Order ID is null or empty");
            throw new ApplicationException("orderId is null or empty");
            return;
        }

        // Kiểm tra order và status
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            throw new ApplicationException("order not found");
        }

        OrderStatus orderStatus = order.getStatus();
        if (orderStatus != OrderStatus.PENDING) {
            throw new ApplicationException("order status is not PENDING");
        }

        // Update
        order.setStatus(OrderStatus.COMPLETED);

        orderRepository.save(order);

        log.info("Order has been completed successfully");
    }

    @Override
    @Transactional
    public void handleStockFailedEvent(OrderStockFailedEvent event) {
        log.info("Handling stock failed event, orderId={}", event != null ? event.getOrderId() : null);
        // Kiểm orderId
        String orderId = event.getOrderId();

        if (orderId == null || orderId.isBlank()) {
            log.error("Order ID is null or empty");
            throw new ApplicationException("orderId is null or empty");
            return;
        }

        // Kiểm tra order và status
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            throw new ApplicationException("order not found");
        }

        OrderStatus orderStatus = order.getStatus();
        if (orderStatus != OrderStatus.PENDING) {
            throw new ApplicationException("order status is not PENDING");
        }

        // Update
        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        log.info("Order cancelled: {}, reason={}", orderId, event.getReason());
    }

    @Override
    public List<OrderResponse> getAllOrders(String userId) {
        log.info("Fetching all orders for userId={}", userId);
        if (userId == null) {
            throw new ApplicationException("Invalid user id");
        }

        // Lấy danh sách orders
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // Lấy toàn bộ order items
        List<String> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        List<OrderItem> orderItems = orderItemRepository.findByOrderIdIn(orderIds);

        // Nhóm items theo order
        Map<String, List<OrderItem>> itemsMap = new HashMap<>();

        for (OrderItem item : orderItems) {

            String orderId = item.getOrderId();

            if(!itemsMap.containsKey(orderId)) {
                itemsMap.put(orderId, new ArrayList<>());
            }

            itemsMap.get(orderId).add(item);
        }

        // Tạp response
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            OrderResponse response = new OrderResponse();
            response.setOrderId(order.getId());
            response.setDiscountAmount(order.getDiscountAmount());
            response.setTotalAmount(order.getTotalAmount());
            response.setFinalAmount(order.getFinalAmount());
            response.setStatus(order.getStatus());

            // Tạo order item
            List<OrderItem> items = itemsMap.getOrDefault(order.getId(), new ArrayList<>());

            List<OrderItemDTO> itemDTOS = items.stream()
                    .map(i -> {
                        OrderItemDTO dto = new OrderItemDTO();
                        dto.setBookId(i.getBookId());
                        dto.setBookName(i.getBookName());
                        dto.setQuantity(i.getQuantity());
                        dto.setPrice(i.getPrice());
                        dto.setTotalPrice(i.getTotalPrice());

                        return dto;
                    })
                    .toList();

            response.setItems(itemDTOS);
            orderResponses.add(response);
        }

        log.info("Fetched orders for userId={}, totalOrders={}", userId, orderResponses.size());
        return orderResponses;
    }

    @Override
    public OrderResponse getOrderById(String orderId, String userId) {
        log.info("Fetching order by id={}, userId={}", orderId, userId);
        if (orderId == null || userId == null) {
            throw new ApplicationException("Invalid input");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new ApplicationException("Access denied");
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        List<OrderItemDTO> itemDTOs = items.stream().map(i -> {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setBookId(i.getBookId());
            dto.setBookName(i.getBookName());
            dto.setPrice(i.getPrice());
            dto.setQuantity(i.getQuantity());
            dto.setTotalPrice(i.getTotalPrice());
            return dto;
        }).toList();

        OrderResponse res = new OrderResponse();
        res.setOrderId(order.getId());
        res.setStatus(order.getStatus().name());
        res.setTotalAmount(order.getTotalAmount());
        res.setDiscountAmount(order.getDiscountAmount());
        res.setFinalAmount(order.getFinalAmount());
        res.setItems(itemDTOs);

        log.info("Fetched order successfully, orderId={}", orderId);
        return res;
    }
}