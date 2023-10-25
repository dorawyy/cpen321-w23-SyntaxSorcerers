package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.frontend.apiWrappers.UBCGradesRequest;
import com.example.frontend.models.CourseGradesModel;
import com.example.frontend.models.Deserializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CourseSearchActivity extends AppCompatActivity {
    private final String[] availableYearSessions = {"", "2023S", "2022W", "2022S", "2021W", "2021S"};
    private final String BASE_URI = "/api/v3/";
    private final String CAMPUS = "UBCV";
    private final int NUM_SPINNERS = 4;

    private String[] spinnerURIs = {"subjects", "courses", "sections", "grades"};
    private String[] spinnerItems = new String[4];

    private Spinner[] spinners = new Spinner[NUM_SPINNERS];
    private int[] spinnerIds = {R.id.yearSession, R.id.subject, R.id.course, R.id.section};
    private ArrayAdapter<String>[] adapters = new ArrayAdapter[NUM_SPINNERS];

    private TextView courseName;
    private TextView average;
    private TextView stats;
    private TextView teachers;

    /**
     * Generated by ChatGPT
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);
        BottomNavMenu.createBottomNavMenu(this, findViewById(R.id.bottom_navigation), R.id.action_home);

        courseName = findViewById(R.id.courseName);
        average = findViewById(R.id.average);
        stats = findViewById(R.id.stats);
        teachers = findViewById(R.id.teachers);

        for (int i=0; i < NUM_SPINNERS; i++) {
            spinners[i] = findViewById(spinnerIds[i]);
            if (i == 0) adapters[i] = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableYearSessions);
            else adapters[i] = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
            adapters[i].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinners[i].setAdapter(adapters[i]);
            final int spinnerIndex = i;
            spinners[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedItem = adapters[spinnerIndex].getItem(position);
                    if (!selectedItem.equals("")) {
                        updateOtherSpinnersOnChange(selectedItem, spinnerIndex);
                        updateNextSpinnerWithApiData(spinnerIndex);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing here
                }
            });
        }
    }

    /**
     * Generated by ChatGPT
     */
    private void updateNextSpinnerWithApiData(int currentSpinnerIndex) {
        if (currentSpinnerIndex < 4) {
            String apiEndpoint = constructEndpoint(currentSpinnerIndex);
            if (currentSpinnerIndex < 3) callUBCGradesJsonArray(currentSpinnerIndex, apiEndpoint);
            else callUBCGradesJsonObject(apiEndpoint);
        }
    }

    /**
     * Generated by ChatGPT
     */
    private void updateSpinnerWithData(JsonArray responseData, int spinnerIndex) {
        if (spinnerIndex < 4) {
            adapters[spinnerIndex].clear();

            adapters[spinnerIndex].add("");
            for (int i = 0; i < responseData.size(); i++) {
                if (spinnerIndex == 1) {
                    adapters[spinnerIndex].add(
                            responseData.get(i).getAsJsonObject().get("subject").getAsString());
                } else if (spinnerIndex == 2) {
                    adapters[spinnerIndex].add(
                            responseData.get(i).getAsJsonObject().get("course").getAsString()
                            + responseData.get(i).getAsJsonObject().get("detail").getAsString());
                } else if (spinnerIndex == 3) {
                    adapters[spinnerIndex].add(responseData.get(i).getAsString());
                }
            }

            adapters[spinnerIndex].notifyDataSetChanged();
        }
    }

    /**
     * Used to clear subsequent drop down menus if user changes a selection
     * Also updates chose values
     * @param spinnerIndex index to indicate which menu user changed
     */
    private void updateOtherSpinnersOnChange(String selectedItem,int spinnerIndex) {
        spinnerItems[spinnerIndex] = selectedItem;
        for (int i = spinnerIndex + 1; i < NUM_SPINNERS; i++) {
            spinnerItems[i] = "";
            adapters[i].clear();
        }
    }

    /**
     * Constructs endpoint to call depending on which dropdown menu is clicked and
     * which options were previously chosen
     * @param spinnerIndex current dropdown menu
     */
    private String constructEndpoint(int spinnerIndex) {
        StringBuilder endpoint = new StringBuilder(BASE_URI);
        endpoint.append(spinnerURIs[spinnerIndex]);
        endpoint.append("/" + CAMPUS);
        for (int i = 0; i < spinnerIndex+1; i++) {
            endpoint.append("/" + spinnerItems[i]);
        }
        return endpoint.toString();
    }

    private void callUBCGradesJsonArray(int currentSpinnerIndex, String apiEndpoint) {
        UBCGradesRequest ubcGradesRequest = new UBCGradesRequest();
        UBCGradesRequest.ApiRequestListener apiRequestListener = new UBCGradesRequest.ApiRequestListener<JsonArray>() {
            @Override
            public void onApiRequestComplete(JsonArray response) {
                updateSpinnerWithData(response, currentSpinnerIndex + 1);
            }

            @Override
            public void onApiRequestError(String error) {
                Log.d(UBCGradesRequest.RequestTag, "Failure");
                Log.d(UBCGradesRequest.RequestTag, error);
            }
        };
        ubcGradesRequest.makeGetRequestForJsonArray(apiEndpoint, apiRequestListener);
    }

    private void callUBCGradesJsonObject(String apiEndpoint) {
        UBCGradesRequest ubcGradesRequest = new UBCGradesRequest();
        UBCGradesRequest.ApiRequestListener apiRequestListener = new UBCGradesRequest.ApiRequestListener<JsonObject>() {
            @Override
            public void onApiRequestComplete(JsonObject response) {
                Log.d(UBCGradesRequest.RequestTag, "Course grade request success");
                Deserializer deserializer = new Deserializer();
                CourseGradesModel courseGradesModel = deserializer.courseGradesModelDeserialize(response);
                displaySearchResults(courseGradesModel);
            }
            @Override
            public void onApiRequestError(String error) {
                Log.d(UBCGradesRequest.RequestTag, "Failure");
                Log.d(UBCGradesRequest.RequestTag, error);
            }
        };
        try {
            ubcGradesRequest.makeGetRequestForJsonObject(apiEndpoint, apiRequestListener);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void displaySearchResults(CourseGradesModel data) {
        courseName.setText(String.format("%s %s%s %s %s %s",
                data.getCampus(), data.getYear(), data.getSession(),
                data.getSubject(), data.getCourse(), data.getSection()));
        average.setText(String.format("Average: %s", data.getAverage()));
        stats.setText(String.format("Median: %s, High: %s, Low %s",
                data.getMedian(), data.getHigh(), data.getLow()));
    }
}