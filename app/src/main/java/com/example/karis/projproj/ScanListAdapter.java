package com.example.karis.projproj;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScanListAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;

    //to store the list of bleNames
    private final String[] nameArray;

    //to store the list of bleAddresses
    private final String[] infoArray;

    //to store image id
    private int imageID;

    public ScanListAdapter (Activity context, String[] nameArrayParam, String[] infoArrayParam, int imageIDParam){
        super(context,R.layout.listview_format , nameArrayParam);

        this.context=context;
        this.nameArray = nameArrayParam;
        this.infoArray = infoArrayParam;
        this.imageID = imageIDParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_format, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = (TextView) rowView.findViewById(R.id.textViewName);
        TextView infoTextField = (TextView) rowView.findViewById(R.id.textViewInfo);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(nameArray[position]);
        infoTextField.setText(infoArray[position]);
        imageView.setImageResource(imageID);

        return rowView;

    };

}
