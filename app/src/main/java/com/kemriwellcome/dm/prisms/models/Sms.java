package com.kemriwellcome.dm.prisms.models;

public class Sms {

    private int id;
    private String timestamp;
    private String source;
    private String text;
    private int short_code;
    private int status;
    private String latency;
    private Outbox outbox;

    public Sms(int id, String timestamp, String source, String text, int short_code, int status, String latency, Outbox outbox) {
        this.id = id;
        this.timestamp = timestamp;
        this.source = source;
        this.text = text;
        this.short_code = short_code;
        this.status = status;
        this.latency = latency;
        this.outbox = outbox;
    }

    public Sms(int id, String timestamp, String source, String text, int short_code, int status, String latency) {
        this.id = id;
        this.timestamp = timestamp;
        this.source = source;
        this.text = text;
        this.short_code = short_code;
        this.status = status;
        this.latency = latency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getShort_code() {
        return short_code;
    }

    public void setShort_code(int short_code) {
        this.short_code = short_code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLatency() {
        return latency;
    }

    public void setLatency(String latency) {
        this.latency = latency;
    }

    public Outbox getOutbox() {
        return outbox;
    }

    public void setOutbox(Outbox outbox) {
        this.outbox = outbox;
    }









    public static class Outbox{
        int id;
        String message_id;
        String timestamp;
        String destination;
        String text;
        String status;
        String delivery_time;
        String created_at;

        public Outbox(int id, String message_id, String timestamp, String destination, String text, String status, String delivery_time, String created_at) {
            this.id = id;
            this.message_id = message_id;
            this.timestamp = timestamp;
            this.destination = destination;
            this.text = text;
            this.status = status;
            this.delivery_time = delivery_time;
            this.created_at = created_at;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMessage_id() {
            return message_id;
        }

        public void setMessage_id(String message_id) {
            this.message_id = message_id;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDelivery_time() {
            return delivery_time;
        }

        public void setDelivery_time(String delivery_time) {
            this.delivery_time = delivery_time;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
