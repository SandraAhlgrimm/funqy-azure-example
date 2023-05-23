package com.redhat;

import io.quarkus.funqy.Funq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFunctions {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyFunctions.class);
    @Funq("FunqyHttpTrigger")
    public String fun(FunqyInput input) {

        LOGGER.info("received: {}", input);

        return String.format("Hello %s!", input != null ? input.name : "Funqy");
    }


    public static class FunqyInput {
        public String name;

        @Override
        public String toString() {
            return new StringBuilder("FunqyInput[name=")
                    .append(name != null ? name : "''")
                    .append("]")
                    .toString();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
