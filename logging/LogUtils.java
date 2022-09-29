package br.com.onoma.infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;

@Log4j2
@UtilityClass
public final class LogUtils {

    public static String getStackTrace(Throwable thrown){
        StringWriter sw = new StringWriter();
        thrown.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static void error(Exception ex) {
        log.error(LogUtils.getStackTrace(ex));
    }

    public static void error(Loggable loggable) {
        log.error(toJSON(loggable));
    }

    private static String toJSON(Object object) {

        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return mapper.writeValueAsString(object);
        }catch (Exception ex){

            log.error("Error JSON parsing: ",getStackTrace(ex));
            return object.toString();
        }
    }
}
