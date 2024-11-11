package com.example.citywatcherfrontend;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        TextView content = view.findViewById(R.id.commentContent);
        Button buttonDeleteComment = view.findViewById(R.id.buttonDeleteComment);
        Button buttonEditComment = view.findViewById(R.id.buttonEditComment);

        // commenter.setText(comment.getCommenter().getUsername());
        date.setText(comment.getDate().toString());
        content.setText(comment.getContent());

        buttonDeleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((IssueDetailsActivity)mContext).makeDeleteCommentReq(position);
            }
        });

        buttonEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((IssueDetailsActivity)mContext).editComment(position);
            }
        });

        return view;
    }
}
