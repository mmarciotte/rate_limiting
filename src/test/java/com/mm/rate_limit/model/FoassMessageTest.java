package com.mm.rate_limit.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FoassMessageTest {

    @Test
    void testToJson() throws JsonProcessingException {
        FoassMessage message = FoassMessage.builder().message("the message").subtitle("and subtitle").build();
        assertEquals(message.toJson(), "{\n" +
                "  \"message\" : \"the message\",\n" +
                "  \"subtitle\" : \"and subtitle\"\n" +
                "}");
    }
}
