package ca.etsmtl.log792.pdavid.sketch.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import ca.etsmtl.log792.pdavid.sketch.R;

/**
 * Created by Phil on 18/11/13.
 */
public class TextCreatorView extends RelativeLayout implements View.OnClickListener {

    private OnClickListener listener;
    private EditText editText;

    public TextCreatorView(Context context) {
        super(context);
        init();
    }

    public TextCreatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.text_creation_view, this, true);

        if (!isInEditMode()) {
            String[] strings = getContext().getResources().getStringArray(R.array.country_arrays);
            findViewById(R.id.text_creation_btn_insert).setOnClickListener(this);
            editText = (EditText) findViewById(R.id.text_creation_text);
            ((Spinner) findViewById(R.id.text_creation_spinner)).setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, strings));
        }

    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(this);
            editText.setText("");
            setVisibility(GONE);
        }
    }

    public void setOnInsertListener(OnClickListener listener) {
        this.listener = listener;
    }
}
