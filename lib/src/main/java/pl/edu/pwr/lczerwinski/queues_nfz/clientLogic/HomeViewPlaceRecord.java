package pl.edu.pwr.lczerwinski.queues_nfz.clientLogic;

public class HomeViewPlaceRecord {
    public String date;
    public String name;
    public String adress;
    public String phoneNumber;

    public HomeViewPlaceRecord(String date, String name, String adress, String phoneNumber)
    {
        this.date = date;
        this.name = name;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
