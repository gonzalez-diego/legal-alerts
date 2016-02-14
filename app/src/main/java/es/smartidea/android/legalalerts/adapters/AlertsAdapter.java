package es.smartidea.android.legalalerts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.smartidea.android.legalalerts.R;
import es.smartidea.android.legalalerts.database.DBContract;

/**
 * A custom ResourceCursorAdapter {@link ResourceCursorAdapter} subclass.
 * Binds alerts info to corresponding list view item.
 * Also binds data as tag object using a viewHolder class
 * to reuse int id´s and avoid findViewById calls
 */

public class AlertsAdapter extends ResourceCursorAdapter {
    private LayoutInflater inflater;

    /**
     * ViewHolder static class to store associated Views
     */
    static class ViewHolder {
        @Bind(R.id.textViewAlertListItem) TextView textViewAlertListItem;
        @Bind(R.id.imageAlertIsLiteralSearch) ImageView imageViewAlertListItemLiteral;
        @Bind(R.id.textViewIsLiteralSearch) TextView textViewIsLiteralSearch;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public AlertsAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item_alert, parent, false);
        // Bind the ViewHolder with ButterKnife passing the View to ViewHolder constructor
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Get associated ViewHolder
        ViewHolder holder = (ViewHolder) view.getTag();

        // Get alertName from DBCursor
        final String alertName =
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Alerts.COL_ALERT_NAME));

        // Populate the ViewHolder fields
        holder.textViewAlertListItem.setText(alertName);
        // Change the resource image to an open/closed padlock
        // according if its set to not literal search
        switch (cursor.getInt(
                cursor.getColumnIndexOrThrow(DBContract.Alerts.COL_ALERT_SEARCH_NOT_LITERAL))){
            case 0:
                holder.imageViewAlertListItemLiteral
                        .setImageResource(android.R.drawable.ic_secure);
                // Set tag for imageView to TRUE according to literal search setup
                holder.imageViewAlertListItemLiteral.setTag(true);
                holder.textViewIsLiteralSearch.setText(R.string.fragment_alerts_text_literal_search);
                break;
            case 1:
                holder.imageViewAlertListItemLiteral
                        .setImageResource(android.R.drawable.ic_partial_secure);
                // Set tag for imageView to FALSE according to literal search setup
                holder.imageViewAlertListItemLiteral.setTag(false);
                holder.textViewIsLiteralSearch.setText(R.string.fragment_alerts_text_not_literal_search);
                break;
        }
//        holder.imageViewAlertListItemLiteral.setImageResource(
//                (cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Alerts.COL_ALERT_SEARCH_NOT_LITERAL)) == 0)
//                        ? android.R.drawable.ic_secure
//                        : android.R.drawable.ic_partial_secure
//        );
    }
}
