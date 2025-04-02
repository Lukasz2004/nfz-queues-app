package pl.edu.pwr.lczerwinski.queues_nfz.clientLogic;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpOperations {
    private static String paramFormater(String key, String value, boolean isFirst)
    {
        StringBuilder result = new StringBuilder();
        if (!isFirst)
        {
            result.append("&");
        }
        result.append(key).append("=");
        boolean isSplit = false;
        for (String s : value.split(" ")) {
            if(isSplit){
                result.append("%20");
            }
            isSplit = true;
            result.append(s);
        }
        return result.toString();
    }
    private static String paramFormater(String key, int value, boolean isFirst)
    {
        return paramFormater(key, Integer.toString(value), isFirst);
    }
    public static ArrayList<HomeViewPlaceRecord> askForData(String benefitType, int page, String voivodeship, String city, int urgency) throws URISyntaxException {
        ArrayList<HomeViewPlaceRecord> records = new ArrayList<>();
        int limit = 25;
        StringBuilder requestUrl = new StringBuilder();
        requestUrl.append("https://api.nfz.gov.pl/app-itl-api/queues?");
        requestUrl.append(paramFormater("page",page,true));
        requestUrl.append(paramFormater("limit",limit,false));
        requestUrl.append(paramFormater("format","json",false));
        requestUrl.append(paramFormater("case",urgency,false));
        if(!voivodeship.equals("00"))
        {
            requestUrl.append(paramFormater("province",voivodeship,false));
        }
        requestUrl.append(paramFormater("benefit",benefitType,false));
        requestUrl.append(paramFormater("locality",city,false));
        requestUrl.append(paramFormater("api-version","1.3",false));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl.toString()))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Zapytanie");
            System.out.println("Wyslano zapytanie: " + request.toString());
            System.out.println("Otrzymano: " + response.statusCode() + response.body().toString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> entireResponse;
        try {
            entireResponse = mapper.readValue(response.body(), HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Map<String,Object>> dataFromResponse = (ArrayList<Map<String, Object>>) entireResponse.get("data");
        for (Map<String, Object> responseEntry : dataFromResponse) {
            Map<String, Object> responseAttributes = (Map<String, Object>) responseEntry.get("attributes");
            String newName = (String) responseAttributes.get("provider");
            String newAdress = responseAttributes.get("address") + ", " + responseAttributes.get("locality");
            String newPhoneNumber = (String) responseAttributes.get("phone");
            Map<String, Object> responseDates = (Map<String, Object>) responseAttributes.get("dates");
            String newDate = (String) responseDates.get("date");
            HomeViewPlaceRecord newRecord = new HomeViewPlaceRecord(newDate, newName, newAdress, newPhoneNumber);
            records.add(newRecord);
        }

        return records;
    }
    public static ArrayList<String> askForBenefitType(String benefitInput) throws URISyntaxException {
        if(benefitInput.length() < 3)
        {
            return new ArrayList<>();
        }
        int limit = 25;
        StringBuilder requestUrl = new StringBuilder();
        requestUrl.append("https://api.nfz.gov.pl/app-itl-api/benefits?");
        requestUrl.append(paramFormater("page",1,true));
        requestUrl.append(paramFormater("limit",limit,false));
        requestUrl.append(paramFormater("format","json",false));
        requestUrl.append(paramFormater("name",benefitInput,false));
        requestUrl.append(paramFormater("api-version","1.3",false));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl.toString()))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Zapytanie");
            System.out.println("Wyslano zapytanie: " + request.toString());
            System.out.println("Otrzymano: " + response.statusCode() + response.body().toString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> entireResponse;
        try {
            entireResponse = mapper.readValue(response.body(), HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return (ArrayList<String>) entireResponse.get("data");
    }

}
