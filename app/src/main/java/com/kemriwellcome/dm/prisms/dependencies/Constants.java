package com.kemriwellcome.dm.prisms.dependencies;

public class Constants {
    /*Stash Variables*/
    public static String USER = "LOGGED_IN_USER";
    public static String END_POINT = "END_POINT_URL";

    public static String VERIFICATION_SENT = "VERIFICATION_SENT";
    public static String CODE_VERIFIED = "CODE_VERIFIED";
    public static String MSISDN = "MSISDN";



    /*API Variables*/

    public static String API_VERSION = "prisms_api_v0";

    public static String LOGIN = "auth/login";
    public static String SEND_TEMP_OTP = "auth/send_temp_otp";
    public static String VERIFY_TEMP_OTP = "auth/verify_temp_otp";
    public static String REGISTER = "auth/signup";

    public static String GET_USE_PROFILE = "auth/user";
    public static String ACTIVE_LOAN = "current_active_loan";

    public static String CHECK_APPROVAL = "loans/check_approval";
    public static String CALCULATE_DUE = "loans/calculate_due";
    public static String APPLY_LOAN = "loans/apply_loan";
    public static String CANCEL_LOAN = "loans/cancel_loan";
    public static String WALLET_TRANSACTIONS = "wallet/transactions";
    public static String LOANS = "loans/history";
    public static String WITHDRAW = "wallet/withdraw";
    public static String PAY_LOAN = "loans/pay_loan";

    public static String SET_PIN = "set_pin";
    public static String GET_WHITE_LISTED_BIZ = "get_whitelisted_businesses";
    public static String APPLY_BUSINESS_LOAN = "apply_business_loan";
    public static String GET_PROFILE = "get_profile";
    public static String CHECK_BUSINESS_LOAN = "check_business_loan";
    public static String B2C = "b2c";
    public static String PAYBILL = "823100";
    public static String GET_LOAN_HISTORY = "get_loan_history";
    public static String VERIFY_MPESA_NUMBER = "verify_mpesa_number";
    public static String SEND_FEEDBACK = "feedback";
    public static String SEND_RESET_OTP = "send_reset_otp";
    public static String VERIFY_RESET_OTP = "verify_reset_otp";
    public static String CERTIFICATE = "certificate";
    public static String REPORT = "report";
    public static String CRB_VAI = "crb_vai";


    public static String FETCH_BILLERS = "fetch_billers";
    public static String DIRECT_BILL_PAYMENT = "direct_billpayment";
    public static String APPLY_BILL_PAYMENT_LOAN = "apply_billpayment_loan";
    public static String FETCH_MY_BILLER_ACCOUNTS = "fetch_biller_accounts";
    public static String ADD_BILLER = "add_biller";
    public static String WALLET_BALANCE = "wallet_balance";
    public static String WALLET_TOP_UP = "wallet_topup";
    public static String WALLET_WITHDRAW = "wallet_withdraw";
    public static String GET_TRANSACTIONS = "get_transactions";

    public static String FETCH_PRODUCTS = "fetch_products";
    public static String FETCH_FEATURED_PRODUCTS = "get_featured_products";
    public static String BUY_PRODUCT = "buy_product";

    public static String UPDATE_BUSINESS = "update_whitlelisted_business";


    public static String DIRECT_PRODUCT_PAYMENT = "direct_product_payment";

    //firenase topics
    public static String GENERAL_TOPIC = "general_topic";


    public static String PLACES_API_KEY = "AIzaSyCCUpJ8wonHAppetH-RJ1RaJDCHZjQfvX4";




    //merchant
    public static String MERCHANT_API_AUTHENTICATE = "https://marketplace.sawazisha.com/api/v2/authenticate";
    public static String MERCHANT_GET_BRANDS = "https://marketplace.sawazisha.com/api/v2/products/vendors";
    public static String MERCHANT_GET_CYLINDERS = "https://marketplace.sawazisha.com/api/v2/products/list";
    public static String MERCHANT_GET_NEARBY_VENDORS = "https://marketplace.sawazisha.com/api/v2/merchants/list";
    public static String MERCHANT_CREATE_LOAN_ORDER = "https://marketplace.sawazisha.com/api/v2/transactions/order/create";
    public static String GET_CUSTOMER_ORDERS = "https://marketplace.sawazisha.com/api/v2/transactions/customer/";
    public static String MERCHANT_UPDATE_ORDER = "https://marketplace.sawazisha.com/api/v2/transactions/order/update/";


}
