package com.kaizensundays.eta.jgroups

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource

/**
 * Created: Sunday 9/22/2024, 1:45 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Configuration
@EnableAutoConfiguration
@ImportResource("classpath:service-config.xml", "classpath:service.xml")
open class ServiceContext {
}