package sg.edu.nus.iss.thememorygame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import sg.edu.nus.iss.thememorygame.R;

public class HistoryAdapter extends ArrayAdapter<String> {
    protected List<String> timeStamps;


    public HistoryAdapter(Context context, List<String> timeStamps){
        super(context,0, timeStamps);

        this.timeStamps = timeStamps;

    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view == null){
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.row, parent, false);
        }

        //Set the text for TextView
        TextView txtSrNum = view.findViewById(R.id.txtSrNum);
        txtSrNum.setText(String.format("%d",pos));

        TextView txtHistory = view.findViewById(R.id.txtHistory);
        txtHistory.setText(String.format("Time Elapsed: %s",timeStamps.get(pos)));

        return view;
    }
}
