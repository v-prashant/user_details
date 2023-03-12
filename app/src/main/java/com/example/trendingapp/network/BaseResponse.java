package com.example.trendingapp.network;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Prashant Verma
 */

public class BaseResponse implements Serializable {

    public BaseResponse() {
    }

    private ResponseStatus responseStatus;

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public class ResponseStatus implements Serializable {
        private String message;
        private String status;
        private String code;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCode() {
            try {
                return code;
            } catch (Exception e) {
                return "";
            }
        }

        public String getStringCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @NonNull
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
