package es.smartidea.android.legalalerts;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnTextChanged;

public class LegalAlertDialog extends AppCompatDialogFragment {

    private static final long LITERAL_SWAP_DURATION = 150L;
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 100;
    private static final String ALERT_NAME = "alert_name";
    @BindView(R.id.textInputLayoutDialogAlert) TextInputLayout textInputLayout;
    @BindView(R.id.textViewLiteralInfo) TextView textViewLiteralInfo;
    @BindView(R.id.switchLiteralSearch) SwitchCompat switchLiteralSearch;
    @BindView(R.id.editTextDialogAlert) EditText editTextDialogAlert;

    @OnTextChanged(R.id.editTextDialogAlert)
    public void alertNameTextChanged(CharSequence text) {
        if (isValidAlertNameLength(text.length())) {
            textInputLayout.setErrorEnabled(false);
            if (text.length() == MAX_LENGTH){
                textInputLayout.setError(getString(R.string.text_dialog_alert_name_too_long));
                textInputLayout.setErrorEnabled(true);
            }
        } else {
            textInputLayout.setError(getString(R.string.text_dialog_alert_name_invalid));
            textInputLayout.setErrorEnabled(true);
        }
    }

    @OnCheckedChanged(R.id.switchLiteralSearch)
    public void onIsLiteralChanged(boolean isChecked) {
        if (isChecked) {
            YoYo.with(Techniques.SlideOutRight).duration(LITERAL_SWAP_DURATION).withListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            //do nothing
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            textViewLiteralInfo.setText(R.string.text_dialog_info_literal_yes);
                            YoYo.with(Techniques.SlideInLeft).duration(LITERAL_SWAP_DURATION)
                                    .playOn(textViewLiteralInfo);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            //do nothing
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            //do nothing
                        }
                    }).playOn(textViewLiteralInfo);
        } else {
            YoYo.with(Techniques.SlideOutLeft).duration(LITERAL_SWAP_DURATION).withListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            //do nothing
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            textViewLiteralInfo.setText(R.string.text_dialog_info_not_literal);
                            YoYo.with(Techniques.SlideInRight).duration(LITERAL_SWAP_DURATION)
                                    .playOn(textViewLiteralInfo);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            //do nothing
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            //do nothing
                        }
                    }).playOn(textViewLiteralInfo);
        }
    }

    /**
     * Validates length of alert name
     *
     * @param textLength int representing alert name string length
     * @return true if given textLength is valid according to specifications
     */
    private static boolean isValidAlertNameLength(int textLength) {
        // Return true if alert name is in the accepted interval
        return textLength >= MIN_LENGTH && textLength <= MAX_LENGTH;
    }

    /**
     * Static factory method receiving alertName and returning new instance
     * with given parameters set to bundle
     *
     * @param receivedName @NonNull String representing alert name to edit
     * @return new instance of LegalAlertDialog with given parameter as bundle
     */
    public static LegalAlertDialog newInstance(@NonNull String receivedName, boolean isLiteralSearch) {
        LegalAlertDialog fragment = new LegalAlertDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ALERT_NAME, receivedName);
        bundle.putBoolean("is_literal_search", isLiteralSearch);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public android.support.v7.app.AlertDialog onCreateDialog(Bundle savedInstanceState) {

        final android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(this.getContext());
        @SuppressLint("InflateParams")
        final View view =
                LayoutInflater.from(this.getContext()).inflate(R.layout.dialog_legal_alerts, null);
        ButterKnife.bind(this, view);
        int dialogTitleInt = R.string.text_dialog_new_alert;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            editTextDialogAlert.setText(bundle.getString(ALERT_NAME));
            switchLiteralSearch.setChecked(bundle.getBoolean("is_literal_search"));
            dialogTitleInt = R.string.text_dialog_edit_alert;
        }
        // Set View and positive button listener to null (will be overridden in setOnShowListener)
        builder.setView(view)
                .setPositiveButton(R.string.button_dialog_alert_save, null)
                .setNegativeButton(R.string.button_dialog_alert_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LegalAlertDialog.this.getDialog().cancel();
                        }
                    }
                ).setTitle(dialogTitleInt);
        /*
        * Create the LegalAlertDialog object to override
        * positive button´s click listener using setOnShowListener
        * on the already created AlertDialog
        * */
        final android.support.v7.app.AlertDialog alertDialog = builder.create();
        // Avoid NPE warning about get attributes*
        //noinspection ConstantConditions
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isValidAlertNameLength(editTextDialogAlert.length())) {
                                int result;
                                if (bundle != null)
                                    result = MainActivity.updateAlert(getContext(),
                                        bundle.getString(ALERT_NAME),
                                        editTextDialogAlert.getText().toString(),
                                        switchLiteralSearch.isChecked());
                                else result = MainActivity.insertNewAlert(getContext(),
                                        editTextDialogAlert.getText().toString(),
                                        switchLiteralSearch.isChecked());
                                if (result == -1) {
                                    Toast.makeText(getContext(),
                                            getString(R.string.text_dialog_error_inserting_or_updating),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {
                                    Snackbar.make(
                                            getActivity().findViewById(R.id.fragmentMainPlaceholder),
                                            getString(R.string.text_dialog_inserted_or_updated_ok),
                                            Snackbar.LENGTH_SHORT
                                    ).setAction("Action", null).show();
                                    LegalAlertDialog.this.getDialog().dismiss();

                                }
                            } else {
                                YoYo.with(Techniques.Tada).duration(300L).playOn(editTextDialogAlert);
                                alertNameTextChanged(editTextDialogAlert.getText());
                                Toast.makeText(getContext(),
                                    getString(R.string.text_dialog_alert_name_invalid),
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                );
            }
        });
        return alertDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}