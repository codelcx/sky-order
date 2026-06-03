package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.RedisConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.enumeration.OrderEvent;
import com.sky.enumeration.OrderStatus;
import com.sky.exception.BusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.state.OrderStateContext;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;

    @Autowired
    private OrderStateContext orderStateContext;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 订单号格式
    private static final DateTimeFormatter ORDER_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * 用户下单
     * @param ordersSubmitDTO 下单数据
     * @return 下单结果
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();

        AddressBook address = addressMapper.getAddressById(userId, ordersSubmitDTO.getAddressBookId());
        if (address == null) {
            throw new BusinessException("地址不存在");
        }

        List<ShoppingCart> shoppingCartList = shoppingCartMapper.listShoppingCart(userId);
        if (CollectionUtils.isEmpty(shoppingCartList)) {
            throw new BusinessException("购物车为空，无法下单");
        }

        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(RedisConstant.SHOP_STATUS_KEY);
        if (shopStatus != null && shopStatus == StatusConstant.DISABLE) {
            throw new OrderBusinessException("下单失败，店铺不在营业中");
        }

        // 创建订单对象
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        // 设置收货人姓名
        orders.setConsignee(address.getConsignee());
        // 设置收货人号码
        orders.setPhone(address.getPhone());
        // 设置收货人地址
        orders.setAddress(address.getProvinceName() + address.getCityName() + address.getDistrictName() + address.getDetail());
        // 设置用户ID
        orders.setUserId(userId);
        // 设置订单号
        String orderNumber = createOrderNumber(userId);
        orders.setNumber(orderNumber);
        // 设置下单时间
        LocalDateTime orderTime = LocalDateTime.now();
        orders.setOrderTime(orderTime);
        // 设置订单总金额
        BigDecimal orderAmount = countAmount(shoppingCartList)
                // 商品总价
                .add(BigDecimal.valueOf(ordersSubmitDTO.getPackAmount()))
                // 配送费
                .add(BigDecimal.valueOf(2));
        orders.setAmount(orderAmount);

        // 插入订单表中
        orderMapper.insert(orders);

        // 将购物车数据插入到订单明细表中
        insertOrderDetail(shoppingCartList, orders.getId());

        // 清空购物车
        shoppingCartMapper.clearShoppingCart(userId);

        // 组装OrderSubmitVO返回
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderAmount(orderAmount)
                .orderNumber(orderNumber)
                .orderTime(orderTime)
                .build();
    }

    /**
     * 插入订单明细
     * @param shoppingCartList 购物车列表
     * @param orderId 订单ID
     */
    private void insertOrderDetail(List<ShoppingCart> shoppingCartList, Long orderId) {
        List<OrderDetail> orderDetailList = shoppingCartList.stream()
                .map(shoppingCart -> {
                    OrderDetail orderDetail = new OrderDetail();
                    BeanUtils.copyProperties(shoppingCart, orderDetail);
                    orderDetail.setOrderId(orderId);
                    return orderDetail;
                })
                .toList();
        orderDetailMapper.insertBatch(orderDetailList);
    }

    /**
     * 生成订单号
     * @param userId 用户ID
     * @return 订单号
     */
    private String createOrderNumber(Long userId) {
        String timestamp = LocalDateTime.now().format(ORDER_NUMBER_FORMATTER);
        int randomNum = ThreadLocalRandom.current().nextInt(100, 1000);
        return timestamp + userId + randomNum;
    }

    /**
     * 计算购物车总金额
     * @param shoppingCartList 购物车列表
     * @return 总金额
     */
    private BigDecimal countAmount(List<ShoppingCart> shoppingCartList) {
        if (CollectionUtils.isEmpty(shoppingCartList)) {
            return BigDecimal.ZERO;
        }

        return shoppingCartList.stream()
                // 计算每个购物车项的小计：单价 × 数量
                .map(cart -> cart.getAmount().multiply(BigDecimal.valueOf(cart.getNumber())))
                // 累加所有小计
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO 支付数据
     */
    @Override
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        Orders order = orderMapper.getOrderByOrderNumber(userId, ordersPaymentDTO.getOrderNumber());
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }

        // 查到订单需要判断当前的订单状态是否是待支付订单, 通过状态机进行判断
        StateMachine<OrderStatus, OrderEvent> stateMachine = buildOrderStateMachine(order);

        orderStateContext.init(order, stateMachine);
        orderStateContext.pay();
    }

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO 分页查询条件
     * @return 订单分页结果
     */
    @Override
    public PageResult<OrderVO> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        try (Page<Object> page = PageHelper.startPage(
                ordersPageQueryDTO.getPage(),
                ordersPageQueryDTO.getPageSize())) {

            Page<OrderVO> orderVOPage = page.doSelectPage(() ->
                    orderMapper.getHistoryOrders(ordersPageQueryDTO)
            );

            return new PageResult<>(
                    orderVOPage.getTotal(),
                    orderVOPage.getResult(),
                    ordersPageQueryDTO.getPage(),
                    orderVOPage.getPageNum()
            );
        }
    }

    /**
     * 查询用户订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @Override
    public OrderVO getUserOrderDetail(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders order = orderMapper.getOrderByOrderIdAndUserId(userId, id);
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 再来一单（将历史订单商品重新加入购物车）
     * @param id 订单ID
     */
    @Override
    public void repeatOrder(Long id) {
        List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailByOrderId(id);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new OrderBusinessException("订单不存在");
        }

        // 查找该订单历史的菜品ID和套餐ID
        List<Long> dishIdList = new ArrayList<>();
        List<Long> setmealIdList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            Long dishId = orderDetail.getDishId();
            if (dishId != null) {
                dishIdList.add(dishId);
            }
            else {
                setmealIdList.add(orderDetail.getSetmealId());
            }
        }

        // 查找订单历史中的菜品和套餐是否还在出售
        List<Long> sellingDishIds = new ArrayList<>();
        List<Long> sellingSetMealIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dishIdList)) {
            sellingDishIds = dishMapper.getSellingDishListByIds(dishIdList);
        }
        if (!CollectionUtils.isEmpty(setmealIdList)) {
            sellingSetMealIds = setMealMapper.getSellingSetMealByIds(setmealIdList);
        }

        if (sellingDishIds.size() != dishIdList.size() || setmealIdList.size() != sellingSetMealIds.size()) {
            throw new OrderBusinessException("存在菜品或套餐下架，无法重新创建购物车");
        }

        // 加入购物车
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setId(null);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCartList.add(shoppingCart);
        }
        shoppingCartMapper.saveItemBatch(shoppingCartList);
    }

    /**
     * 用户取消订单
     * @param id 订单ID
     */
    @Override
    public void userCancelOrder(Long id) {
        // 涉及到状态流转，所以需要使用状态机
        Long userId = BaseContext.getCurrentId();
        Orders order = orderMapper.getOrderByOrderIdAndUserId(userId, id);
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }

        // 创建状态机对象
        StateMachine<OrderStatus, OrderEvent> stateMachine = buildOrderStateMachine(order);
        orderStateContext.init(order, stateMachine);
        orderStateContext.userCancel();
    }

    /**
     * 条件查询订单列表（管理端）
     * @param ordersPageQueryDTO 分页查询条件
     * @return 订单分页结果
     */
    @Override
    public PageResult<OrderVO> getOrderListByCondition(OrdersPageQueryDTO ordersPageQueryDTO) {
        Page<OrderVO> orderVOPage;
        try (Page<Object> page = PageHelper.startPage(
                ordersPageQueryDTO.getPage(),
                ordersPageQueryDTO.getPageSize())) {

            orderVOPage = page.doSelectPage(() ->
                    orderMapper.getHistoryOrders(ordersPageQueryDTO)
            );
        }

        // 添加菜品信息(菜品1*1；菜品2*3）
        List<OrderVO> voList = orderVOPage.getResult();
        voList.forEach(orderVO -> {
            List<OrderDetail> orderDetailList = orderVO.getOrderDetailList();
            StringBuilder orderDishes = new StringBuilder();
            for (OrderDetail detail : orderDetailList) {
                orderDishes.append(detail.getName())
                        .append("*")
                        .append(detail.getNumber())
                        .append(";");
            }
            orderVO.setOrderDishes(orderDishes.toString());
        });

        return new PageResult<>(
                orderVOPage.getTotal(),
                orderVOPage.getResult(),
                orderVOPage.getPageSize(),
                orderVOPage.getPageNum()
        );
    }

    /**
     * 查询订单详情（管理端）
     * @param id 订单ID
     * @return 订单详情
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        Orders order = orderMapper.getOrderByOrderId(id);
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 接单（确认订单）
     * @param id 订单ID
     */
    @Override
    public void confirmOrder(Long id) {
        Orders order = orderMapper.getOrderByOrderId(id);
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }
        // 创建状态机对象
        StateMachine<OrderStatus, OrderEvent> stateMachine = buildOrderStateMachine(order);
        orderStateContext.init(order, stateMachine);
        orderStateContext.confirmOrder();
    }

    /**
     * 订单统计
     * @return 统计结果
     */
    @Override
    public OrderStatisticsVO statistics() {
        return orderMapper.statistics();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO 拒单数据
     */
    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = orderMapper.getOrderByOrderId(ordersRejectionDTO.getId());
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }
        // 创建状态机对象
        StateMachine<OrderStatus, OrderEvent> stateMachine = buildOrderStateMachine(order);
        orderStateContext.init(order, stateMachine);
        orderStateContext.adminCancel(ordersRejectionDTO.getRejectionReason());
    }

    /**
     * 管理端取消订单
     * @param ordersCancelDTO 取消订单数据
     */
    @Override
    public void adminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders order = orderMapper.getOrderByOrderId(ordersCancelDTO.getId());
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }
        // 创建状态机对象
        StateMachine<OrderStatus, OrderEvent> stateMachine = buildOrderStateMachine(order);
        orderStateContext.init(order, stateMachine);
        orderStateContext.adminCancel(ordersCancelDTO.getCancelReason());
    }

    /**
     * 配送订单
     * @param id 订单ID
     */
    @Override
    public void deliveryOrder(Long id) {
        Orders order = orderMapper.getOrderByOrderId(id);
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }
        // 创建状态机对象
        StateMachine<OrderStatus, OrderEvent> stateMachine = buildOrderStateMachine(order);
        orderStateContext.init(order, stateMachine);
        orderStateContext.delivery();
    }

    /**
     * 完成订单
     * @param id 订单ID
     */
    @Override
    public void completeOrder(Long id) {
        Orders order = orderMapper.getOrderByOrderId(id);
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }
        // 创建状态机对象
        StateMachine<OrderStatus, OrderEvent> stateMachine = buildOrderStateMachine(order);
        orderStateContext.init(order, stateMachine);
        orderStateContext.complete();
    }

    /**
     * 催单
     * @param id 订单ID
     */
    @Override
    public void remindOrder(Long id) {
        Orders order = orderMapper.getOrderByOrderId(id);
        if (order == null) {
            throw new OrderBusinessException("订单不存在");
        }

        if (!Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException("当前订单状态不支持催单");
        }

        // 客户催单
        Map<String, Object> message = new HashMap<>();
        message.put("type", 2);
        message.put("orderId", order.getId());
        message.put("content", "订单号: " + order.getNumber());

        webSocketServer.sendToAllClient(JSON.toJSONString(message));
    }

    /**
     * 构建订单状态机
     * @param order 订单
     * @return 状态机实例
     */
    private StateMachine<OrderStatus, OrderEvent> buildOrderStateMachine(Orders order) {
        // 获取新的状态机实例，通过订单号作唯一标识
        StateMachine<OrderStatus, OrderEvent> stateMachine = stateMachineFactory.getStateMachine(order.getNumber());
        // 先停止，重置状态为订单当前状态
        stateMachine.stopReactively().block();
        // 状态机访问器
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> access.resetStateMachineReactively(
                        // 以当前订单号的状态作为准，重新设置状态机
                        new DefaultStateMachineContext<>(OrderStatus.fromState(order.getStatus()), null, null, null)
                ).block());
        // 启动状态机
        stateMachine.startReactively().block();
        return stateMachine;
    }

}
