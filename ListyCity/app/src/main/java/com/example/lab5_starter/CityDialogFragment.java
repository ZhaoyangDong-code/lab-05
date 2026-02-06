package com.example.lab5_starter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class CityDialogFragment extends DialogFragment {
    interface CityDialogListener {
        void updateCity(City city, String title, String year);
        void addCity(City city);
        void deleteCity(City city);
    }
    private CityDialogListener listener;

    public static CityDialogFragment newInstance(City city){
        Bundle args = new Bundle();
        args.putSerializable("City", city);

        CityDialogFragment fragment = new CityDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CityDialogListener){
            listener = (CityDialogListener) context;
        }
        else {
            throw new RuntimeException("Implement listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_city_details, null);
        EditText editCityName = view.findViewById(R.id.edit_city_name);
        EditText editProvince = view.findViewById(R.id.edit_province);

        String tag = getTag();
        Bundle bundle = getArguments();
        City city;

        if (Objects.equals(tag, "City Details") && bundle != null){
            city = (City) bundle.getSerializable("City");
            assert city != null;
            editCityName.setText(city.getName());
            editProvince.setText(city.getProvince());
        }
        else {
            city = null;}

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        // Use dynamic title based on tag
        String dialogTitle = Objects.equals(tag, "City Details") ? "City Details" : "Add City";
        
        builder.setView(view)
                .setTitle(dialogTitle)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Continue", null); // Set listener to null here to prevent automatic dismissal

        if (Objects.equals(tag, "City Details") && city != null) {
            builder.setNeutralButton("Delete", (dialog, which) -> {
                listener.deleteCity(city);
            });
        }

        AlertDialog dialog = builder.create();
        
        // Custom listener to prevent dismissal on invalid input
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = editCityName.getText().toString().trim();
                String year = editProvince.getText().toString().trim();

                if (title.isEmpty()) {
                    editCityName.setError("City name required");
                    return;
                }
                if (year.isEmpty()) {
                    editProvince.setError("Province required");
                    return;
                }

                if (Objects.equals(tag, "City Details")) {
                    listener.updateCity(city, title, year);
                } else {
                    listener.addCity(new City(title, year));
                }
                dialog.dismiss();
            });
        });

        return dialog;
    }
}
