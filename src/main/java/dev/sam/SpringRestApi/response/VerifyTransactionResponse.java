package dev.sam.SpringRestApi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyTransactionResponse {

    private boolean status;
    private String message;
    private Data data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private long id;
        private String domain;
        private String status;
        private String reference;
        private String receipt_number;
        private int amount;
        private String message;
        private String gateway_response;
        private String paid_at;
        private String created_at;
        private String channel;
        private String currency;
        private String ip_address;
        private String metadata;
        private Log log;
        private int fees;
        private Object fees_split;
        private Authorization authorization;
        private Customer customer;
        private Object plan;
        private Split split;
        private String order_id;
        private String paidAt;
        private String createdAt;
        private int requested_amount;
        private Object pos_transaction_data;
        private Object source;
        private Object fees_breakdown;
        private Object connect;
        private String transaction_date;
        private Object plan_object;
        private Object subaccount;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Log {
        private long start_time;
        private int time_spent;
        private int attempts;
        private int errors;
        private boolean success;
        private boolean mobile;
        private Object[] input;
        private History[] history;

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class History {
            private String type;
            private String message;
            private int time;
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Authorization {
        private String authorization_code;
        private String bin;
        private String last4;
        private String exp_month;
        private String exp_year;
        private String channel;
        private String card_type;
        private String bank;
        private String country_code;
        private String brand;
        private boolean reusable;
        private String signature;
        private String account_name;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {
        private long id;
        private String first_name;
        private String last_name;
        private String email;
        private String customer_code;
        private String phone;
        private String metadata;
        private String risk_action;
        private String international_format_phone;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Split {
        // Define fields if needed based on Paystack's API documentation
    }
}

