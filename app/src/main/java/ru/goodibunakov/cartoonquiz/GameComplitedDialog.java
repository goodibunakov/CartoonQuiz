package ru.goodibunakov.cartoonquiz;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameComplitedDialog extends Dialog implements View.OnClickListener {

    @BindView(R.id.dia4)
    ImageView dia4;

    private Context context;

    GameComplitedDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_complited_dialog);
        ButterKnife.bind(this);
        setCancelable(false);
        dia4.setOnClickListener(this);
    }

    @OnClick
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dia4:
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        dismiss();
    }
}
