package ca.etsmtl.log792.pdavid.sketch.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.ui.activity.FullscreenActivity;

/**
 * Created by Phil on 18/11/13.
 */
public class SaveDialog extends DialogFragment {
    public static final int TYPE_PNG = 0;
    private int mNum;
    private TextView tv;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static SaveDialog newInstance(int type) {
        SaveDialog f = new SaveDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("type");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Save to :");
        View view = inflater.inflate(R.layout.dialog_save_sketch, container, false);
        tv = (TextView) view.findViewById(R.id.editText);
        view.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                if(mNum == TYPE_PNG){
                    ((FullscreenActivity) getActivity()).savePNG(tv.getText().toString());
                }
            }
        });
        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
