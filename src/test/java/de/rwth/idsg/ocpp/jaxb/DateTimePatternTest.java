package de.rwth.idsg.ocpp.jaxb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import ocpp._2022._02.security.GetLog;
import ocpp._2022._02.security.LogParametersType;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DateTimePatternTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JodaModule());

    @Test
    public void validateStringFormat() throws Exception {
        String oldestStr = "2022-06-30T01:20:52.000Z";
        DateTime oldest = DateTime.parse(oldestStr);

        LogParametersType log = new LogParametersType();
        log.setRemoteLocation("localhost");
        log.setOldestTimestamp(oldest);

        GetLog getLog = new GetLog();
        getLog.setLog(log);

        String s = mapper.writeValueAsString(getLog);
        System.out.println("Written: " + s);

        JsonNode jsonNode = mapper.readTree(s);
        System.out.println("Read: " + s);

        JsonNode oldestNode = jsonNode.get("log").get("oldestTimestamp");
        Assertions.assertFalse(oldestNode.isNumber());

        String oldestStrParsed = oldestNode.asText();
        Assertions.assertEquals(oldestStr, oldestStrParsed);

        DateTime oldestParsed = DateTime.parse(oldestStrParsed);
        Assertions.assertEquals(oldest, oldestParsed);
    }
}
