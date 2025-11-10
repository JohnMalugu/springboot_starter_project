/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.malugu.springboot_starter_project.utils;

public final class Constants {
    // ---------------------------- CONFIG NAMES -----------------------
    public static final String NO_OF_RECORDS = "NO_OF_RECORDS";
    public static final String MAX_LOGIN_ATTEMPTS = "MAX_LOGIN_ATTEMPTS";
    public static final String LOCKOUT_TIME_MINUTES = "LOCKOUT_TIME_MINUTES";
    public static final String SUPPORT_EMAIL = "SUPPORT_EMAIL";
    public static final String INTEGRITY_PLEDGE_SUBMISSION_DAYS = "INTEGRITY_PLEDGE_SUBMISSION_DAYS";
    public static final String PASSWORD_RESET_TOKEN_MINUTES = "PASSWORD_RESET_TOKEN_MINUTES";
    public static final String ENABLE_NGAO_2FA = "ENABLE_NGAO_2FA";

    // ---------------------------- PATTERNS -----------------------
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static String NIDA_RQ_ID = "RQ";
    public static String NIDA_QFD_ID = "QFD";
    public static String NIDA_OTP_ID = "OTP";
    public static String NIDA_BIO_ID = "BIO";
}

