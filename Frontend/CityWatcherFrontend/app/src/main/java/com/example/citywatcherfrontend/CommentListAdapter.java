package com.example.citywatcherfrontend;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.media.RouteListingPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommentListAdapter extends ArrayAdapter<CommentData> {
    Context mContext;

    public CommentListAdapter(@NonNull Context context, ArrayList<CommentData> commentArrayList) {
        super(context, R.layout.list_item_comment, commentArrayList);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        CommentData comment = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_comment, parent, false);
        }

        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        TextView commenter = view.findViewById(R.id.commenterLabel);
        TextView date = view.findViewById(R.id.commentDate);
        ImageView menu = view.findViewById(R.id.commentMenu);
        TextView content = view.findViewById(R.id.commentContent);

        commenter.setText(comment.getUser().getUsername());
        date.setText(comment.getDate().toString());
        content.setText(comment.getContent());

        commenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((IssueDetailsActivity)mContext).viewProfileFromComment(position);
            }
        });

        PopupMenu popupMenu = new PopupMenu(getContext().getApplicationContext(), menu);
        popupMenu.inflate(R.menu.menu_popup_comment);

        if (!(CityWatcherController.getInstance().getUserId() == comment.getUser().getId())) {
            popupMenu.getMenu().findItem(R.id.popupEditComment).setEnabled(false);
            if (!(CityWatcherController.getInstance().getRole().equals("ADMIN"))) {
                popupMenu.getMenu().findItem(R.id.popupDeleteComment).setEnabled(false);
            }

            if (!CityWatcherController.getInstance().isLoggedIn() || !(CityWatcherController.getInstance().getRole().equals("CITIZEN"))){
                popupMenu.getMenu().findItem(R.id.popupReportComment).setEnabled(false);
            }
        } else {
            popupMenu.getMenu().findItem(R.id.popupReportComment).setEnabled(false);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.popupEditComment) {
                    ((IssueDetailsActivity)mContext).editComment(position);
                    return true;
                } else if (menuItem.getItemId() == R.id.popupDeleteComment) {
                    ((IssueDetailsActivity)mContext).makeDeleteCommentReq(position);
                    return true;
                } else if (menuItem.getItemId() == R.id.popupReportComment) {
                    ((IssueDetailsActivity)mContext).reportComment(position);
                    return true;
                }
                return true;
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Field popup = PopupMenu.class.getDeclaredField("mPopup");
                    popup.setAccessible(true);
                    Object menu = popup.get(popupMenu);
                    menu.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menu, true);
                } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                } finally {
                    popupMenu.show();
                }
            }
        });

        return view;
    }
}
