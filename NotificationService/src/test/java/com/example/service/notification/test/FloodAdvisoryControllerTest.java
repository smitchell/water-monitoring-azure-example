package com.example.service.notification.test;

import com.example.service.notification.config.AppInitializer;
import com.example.service.notification.controller.FloodAdvisoryController;
import com.example.service.notification.event.ApplicationEvent;
import net.bytebuddy.asm.Advice;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FloodAdvisoryControllerTest {

    @Autowired
    private FloodAdvisoryController floodAdvisoryController;

    @Autowired
    private AppInitializer appInitializer;

    @Before
    public void runBefore() {
        appInitializer.seedDatabase();
    }

    @Test
    public void testProcessFloodAdvisoryEvent() throws IOException {
        ApplicationEvent event = TestHelper.generateFloodAdvisoryController();
        long count = floodAdvisoryController.processFloodAdvisoryEvent(event);
        assertThat(count, equalTo(AppInitializer.SEED_COUNT));
    }
}
