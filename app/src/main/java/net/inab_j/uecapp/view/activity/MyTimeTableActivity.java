package net.inab_j.uecapp.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.inab_j.uecapp.R;
import net.inab_j.uecapp.view.widget.MyTimeTableView;

public class MyTimeTableActivity extends AppCompatActivity
        implements View.OnLongClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String[] DAY_NAME = {"月曜", "火曜", "水曜", "木曜", "金曜", "土曜"};
    public static final String SHARED_PREF_TAG = "mytimetable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_time_table);
        ((MyTimeTableView) findViewById(R.id.content_mytime)).createView(this);
        getSharedPreferences(SHARED_PREF_TAG, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        ClassDialog dialog = ClassDialog.newInstance(v.getTag().toString());
        dialog.show(getFragmentManager(), "edit");
        Log.d("dbg", v.getTag().toString());
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("dbg", key);
        ((TextView) findViewById(Integer.parseInt(key))).setText(sharedPreferences.getString(key, ""));
    }

    public static class ClassDialog extends DialogFragment {
        String posName;

        public static ClassDialog newInstance(String tablePosition) {
            ClassDialog fragment = new ClassDialog();
            Bundle args = new Bundle();
            args.putString("table_pos", tablePosition);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final SharedPreferences sharedPref = getActivity().getSharedPreferences(SHARED_PREF_TAG, MODE_PRIVATE);
            posName = getArguments().getString("table_pos");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // set view
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View content = inflater.inflate(R.layout.dialog_edit, null);
            ((EditText) content.findViewById(R.id.subject_edit)).setText(sharedPref.getString(posName, ""));
            builder.setView(content);

            // set buttons
            int day = Integer.parseInt(posName.substring(0,1));
            String message = DAY_NAME[day - 1] + posName.charAt(1) + "限";
            builder.setMessage(message)
                    .setPositiveButton("登録", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPref.edit()
                                    .putString(posName,
                                            ((EditText)content.findViewById(R.id.subject_edit)).getText().toString())
                                    .apply();
                            dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .setNeutralButton("削除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPref.edit().remove(posName).apply();
                            dismiss();
                        }
                    });

            return builder.create();
        }
    }
}
