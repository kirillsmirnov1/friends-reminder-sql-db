package com.trulden;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static int daysPassed(Date date) {
        return (int) TimeUnit.DAYS.convert(new Date().getTime() - date.getTime(), TimeUnit.MILLISECONDS);
    }
}
