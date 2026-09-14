package com.rx.controller;

public class Config {
    public static final String BOT_TOKEN       = "8519984636:AAEjhGbpWkpTUbMJL_n7CvFddyuBB6pQnnA";
    public static final String CONTROL_CHAT_ID = "6169099703";
    public static final String API_BASE        = "https://api.telegram.org/bot" + BOT_TOKEN;
    public static final int    POLL_TIMEOUT    = 25;
    public static final int    PING_INTERVAL   = 10000;  // 10 sec
    public static final int    OFFLINE_TIMEOUT = 20000;  // 20 sec = offline
}
