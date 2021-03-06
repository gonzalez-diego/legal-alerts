package es.smartidea.android.legalalerts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
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
        @BindView(R.id.textViewAlertListItem) TextView textViewAlertListItem;
        @BindView(R.id.imageAlertIsLiteralSearch) ImageView imageViewAlertListItemLiteral;
        @BindView(R.id.textViewIsLiteralSearch) TextView textViewIsLiteralSearch;
        ViewHolder(View view) {
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
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        // Populate the ViewHolder fields according to Cursor data
        holder.textViewAlertListItem.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Alerts.COL_ALERT_NAME))
        );
        if (0 == cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Alerts.COL_ALERT_SEARCH_NOT_LITERAL))){
            holder.imageViewAlertListItemLiteral.setImageResource(R.drawable.ic_lock_closed_black);
            holder.textViewIsLiteralSearch.setText(R.string.fragment_alerts_text_literal_search);
        } else {
            holder.imageViewAlertListItemLiteral.setImageResource(R.drawable.ic_lock_open_black);
            holder.textViewIsLiteralSearch.setText(R.string.fragment_alerts_text_not_literal_search);
        }
    }
}
