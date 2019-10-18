package com.example.service.notification.test.config;

import com.example.service.notification.config.AppInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppInitializerTest {

    @Autowired
    private AppInitializer appInitializer;

    @Test
    public void testNotificationPreferenceLoader() {
        long count = appInitializer.seedDatabase();
        assertThat(count, greaterThan(0L));
    }
}
