package com.quanxiaoha.weblog.common.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface Constants {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    DateTimeFormatter DATE_FORMAT_THREAD_SAFE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    DateTimeFormatter MONTH_FORMAT_THREAD_SAFE = DateTimeFormatter.ofPattern("yyyy-MM");

    DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
}
