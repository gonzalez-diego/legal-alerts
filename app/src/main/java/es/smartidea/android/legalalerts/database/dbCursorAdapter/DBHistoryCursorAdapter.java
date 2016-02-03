package es.smartidea.android.legalalerts.database.dbCursorAdapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.smartidea.android.legalalerts.R;
import es.smartidea.android.legalalerts.boeHandler.BoeXMLHandler;
import es.smartidea.android.legalalerts.database.dbHelper.DBContract;

/**
 * A custom ResourceCursorAdapter {@link ResourceCursorAdapter} subclass.
 * Binds history records info to corresponding list view item.
 * Also sets on click event listeners for view pdf buttons
 */

public class DBHistoryCursorAdapter extends ResourceCursorAdapter {
    private LayoutInflater inflater;

    // ViewHolder static class to store associated Views
    static class ViewHolder{
        @Bind(R.id.textViewHistoryListItemRelatedAlert) TextView textViewHistoryListItemRelatedAlert;
        @Bind(R.id.textViewHistoryListItemDocumentName) TextView textViewHistoryListItemDocumentName;
        @Bind(R.id.buttonViewHistoryListItem) ImageButton buttonViewHistoryListItem;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public DBHistoryCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item_history, parent, false);
        // Bind the ViewHolder with ButterKnife passing the View to ViewHolder constructor
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Get associated ViewHolder
        ViewHolder holder = (ViewHolder) view.getTag();

        // Get data from DBCursor
        final String relatedAlertName =
                cursor.getString(cursor.getColumnIndexOrThrow(
                        DBContract.History.COL_HISTORY_RELATED_ALERT_NAME)
                );
        final String relatedDocumentName =
                cursor.getString(cursor.getColumnIndexOrThrow(
                        DBContract.History.COL_HISTORY_DOCUMENT_NAME)
                );
        final String relatedPdfDocumentURL =
                cursor.getString(cursor.getColumnIndexOrThrow(
                        DBContract.History.COL_HISTORY_DOCUMENT_URL)
                );
        // Populate the ViewHolder fields
        holder.textViewHistoryListItemRelatedAlert.setText(relatedAlertName);
        holder.textViewHistoryListItemDocumentName.setText(relatedDocumentName);
        // Set onClick() methods fot buttons TODO: Check viewing PDF approach
        holder.buttonViewHistoryListItem.setOnClickListener(
                new OnViewPdfClickListener(context, relatedPdfDocumentURL)
        );
    }

    // Custom static click listener to attach on viewPdf buttons
    private static class OnViewPdfClickListener implements View.OnClickListener{
        Context context;
        String relatedPdfDocumentURL;
        public OnViewPdfClickListener(Context context, String relatedPdfDocumentURL) {
            this.context = context;
            this.relatedPdfDocumentURL = relatedPdfDocumentURL;
        }

        @Override
        public void onClick(View v) {
            context.startActivity(
                    new Intent(Intent.ACTION_VIEW).setData(
                            Uri.parse(BoeXMLHandler.BOE_BASE_URL + relatedPdfDocumentURL))
            );
        }
    }
}