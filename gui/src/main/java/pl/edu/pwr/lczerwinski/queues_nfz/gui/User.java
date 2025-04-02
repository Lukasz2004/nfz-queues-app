package pl.edu.pwr.lczerwinski.queues_nfz.gui;

public class User {
    public String chosenBenefitType;
    public Boolean isUrgent;
    public String chosenCity;
    public String chosenVoivodeship;
    User(String chosenBenefitType, boolean urgent, String chosenCity, String chosenVoivodeship)
    {
        this.chosenBenefitType = chosenBenefitType;
        this.isUrgent = urgent;
        this.chosenCity = chosenCity;
        this.chosenVoivodeship = chosenVoivodeship;
    }
}
