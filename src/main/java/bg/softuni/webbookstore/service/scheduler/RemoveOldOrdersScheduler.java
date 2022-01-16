package bg.softuni.webbookstore.service.scheduler;

import bg.softuni.webbookstore.service.OrderService;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class RemoveOldOrdersScheduler {

    private final OrderService orderService;

    public RemoveOldOrdersScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(1);

    ScheduledFuture<?> scheduledFuture =
            scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      orderService.deleteOrdersOlderThanOneYear();
                                                  }
                                              },
                    365,
                    1,
                    TimeUnit.DAYS);
}
