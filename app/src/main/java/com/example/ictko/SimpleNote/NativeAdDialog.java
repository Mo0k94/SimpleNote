package com.example.ictko.SimpleNote;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import org.greenrobot.eventbus.Subscribe;

import static android.provider.Settings.System.getString;


public class NativeAdDialog extends Dialog {
    private MemoRecyclerAdapter mAdapter;
    private MemoFacade mMemoFacade;
    final String ADMOB_APP_ID = "ca-app-pub-3145363349418895~4285613266";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_native_ad);

        //NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adView);
        TextView quit = (TextView) findViewById(R.id.dialog_btn_quit);
        TextView back = (TextView) findViewById(R.id.dialog_btn_back);



        //AdRequest request = new AdRequest.Builder().build();
        //adView.loadAd(request);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public NativeAdDialog(Context context){
        super(context);
    }
}
