package xyz.xoventechdev.appforown;

public class SmsMessageModel implements Comparable<SmsMessageModel> {
    public String phoneNumber;
    public String messageBody;
    public String timestamp;
    public String mobileModel;

    public SmsMessageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(SmsMessageModel.class)
    }

    public SmsMessageModel(String phoneNumber, String messageBody, String timestamp, String mobileModel) {
        this.phoneNumber = phoneNumber;
        this.messageBody = messageBody;
        this.timestamp = timestamp;
        this.mobileModel = mobileModel;
    }

    @Override
    public int compareTo(SmsMessageModel o) {
        return o.timestamp.compareTo(this.timestamp); // For descending order
    }
}


