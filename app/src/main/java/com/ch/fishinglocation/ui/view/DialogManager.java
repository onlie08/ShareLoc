package com.ch.fishinglocation.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ch.fishinglocation.R;
import com.ch.fishinglocation.bean.FishingSpot;

public class DialogManager {

    private Context context;

    public DialogManager(Context context) {
        this.context = context;
    }

    public void showFishingSpotDetailDialog(FishingSpot spot) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_spot_info, null);

        // Set the title and description in the dialog
        TextView titleTextView = dialogView.findViewById(R.id.tv_name);
        TextView descriptionTextView = dialogView.findViewById(R.id.tv_info);
        titleTextView.setText(spot.getName());
        descriptionTextView.setText(spot.getDescription());

        // Build and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle the positive button click
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle the negative button click
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // You can add more methods to handle different dialogs
}
