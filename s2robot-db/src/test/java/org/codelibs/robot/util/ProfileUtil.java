package org.codelibs.robot.util;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.io.ResourceUtil;

public class ProfileUtil {
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    public static void setup() {
        List<String> list = new ArrayList<String>();

        String values = System.getProperty(SPRING_PROFILES_ACTIVE);
        if (values != null) {
            for (String value : values.split(",")) {
                list.add(value);
            }
        }

        if (ResourceUtil.getResourceNoException("org/h2/Driver.class") != null) {
            list.add("h2");
        }

        if (ResourceUtil.getResourceNoException("com/mysql/jdbc/Driver.class") != null) {
            list.add("mysql");
        }

        if (ResourceUtil
                .getResourceNoException("oracle/jdbc/driver/OracleDriver.class") != null) {
            list.add("oracle");
        }

        if (!list.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            for (String value : list) {
                if (buf.length() > 0) {
                    buf.append(',');
                }
                buf.append(value);
            }
            System.setProperty(SPRING_PROFILES_ACTIVE, buf.toString());
        }
    }
}
