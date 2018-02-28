package org.socratic.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;

import org.socratic.android.R;
import org.socratic.android.api.model.ApiError;
import org.socratic.android.util.DialogDismissListener;

/**
 * @date 2017-03-07
 */
public class SearchErrorDialog extends DialogFragment {

    private static final String KEY_ERROR = "KEY_ERROR";

    public SearchErrorDialog() {
    }

    public static SearchErrorDialog newInstance(ApiError apiError) {
        Gson gson = new Gson();

        String json = gson.toJson(apiError);

        SearchErrorDialog frag = new SearchErrorDialog();
        Bundle args = new Bundle();
        args.putString(KEY_ERROR, json);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_search_error, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // For rounded corners.
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        Gson gson = new Gson();

        String json = getArguments().getString(KEY_ERROR);
        ApiError error = gson.fromJson(json, ApiError.class);

        TextView tvTitle = (TextView)view.findViewById(R.id.title);
        TextView tvMessage = (TextView)view.findViewById(R.id.message);

        tvTitle.setText(error.getTitle());
        tvMessage.setText(error.getMessage());

        View btnSearchAgain = view.findViewById(R.id.btn_search_again);
        btnSearchAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof DialogDismissListener) {
            DialogDismissListener listener =
                    (DialogDismissListener) getActivity();
            listener.onDialogDismissed(this);
        }
    }
}