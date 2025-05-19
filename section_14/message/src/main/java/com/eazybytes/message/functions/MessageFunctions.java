package com.eazybytes.message.functions;

import com.eazybytes.message.dto.AccountsMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {

    private static final Logger logger = LoggerFactory.getLogger(MessageFunctions.class);

    @Bean
    public Function<AccountsMsgDto, AccountsMsgDto> email() {
        return msg -> {
            logger.info("Sending email with the details: {}", msg.toString());
            return msg;
        };
    }

    @Bean
    public Function<AccountsMsgDto, Long> sms() {
        return msg -> {
            logger.info("Sending sms with the details: {}", msg.toString());
            return msg.accountNumber();
        };
    }
}
