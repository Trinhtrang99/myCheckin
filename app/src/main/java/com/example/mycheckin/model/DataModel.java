package com.example.mycheckin.model;

import java.util.Objects;

public class DataModel {
    private Information information;

    public DataModel(Information information) {
        this.information = information;
    }

    public class Date {
        private String date;
        private String email;
        private int status;
        private String timeCheckIn;
        private int type;
        private String timeCheckOut;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTimeCheckIn() {
            return timeCheckIn;
        }

        public void setTimeCheckIn(String timeCheckIn) {
            this.timeCheckIn = timeCheckIn;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTimeCheckOut() {
            return timeCheckOut;
        }

        public void setTimeCheckOut(String timeCheckOut) {
            this.timeCheckOut = timeCheckOut;
        }

        public Date(String date, String email, int status, String timeCheckIn, int type, String timeCheckOut) {
            this.date = date;
            this.email = email;
            this.status = status;
            this.timeCheckIn = timeCheckIn;
            this.type = type;
            this.timeCheckOut = timeCheckOut;
        }

        public Date FilterDate(Date date, String date1){
            if(Objects.equals(date.date, date1)){
                return date;
            }
            return null;
        }
    }


    public class CheckIn {
        private Date date;
    }

    public static class Information {
        private CheckIn checkIn;

        public Information(Builder builder) {
            this.checkIn = builder.checkIn;
        }

        public static class Builder {
            private CheckIn checkIn;

            public Builder() {
            }

            public Builder checkIn(CheckIn checkIn) {
                this.checkIn = checkIn;
                return this;
            }

            public Information build() {
                return new Information(this);
            }
        }
    }


}

