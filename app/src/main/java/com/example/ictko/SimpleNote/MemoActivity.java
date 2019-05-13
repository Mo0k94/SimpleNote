package com.example.ictko.SimpleNote;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MemoActivity extends AppCompatActivity {

    private EditText title_edit;
    private EditText contents_edit;
    private TextView datetxt;
    private long mMemoId = -1;
    private MemoFacade mMemoFacade;
    private MemoRecyclerAdapter mAdapter;
    private ImageView imgView;

    String mImageUri;

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();
    // 현재시간을 date 변수에 저장한다.
    Date nowdate = new Date(now);
    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    // nowDate 변수에 값을 저장한다.
    String formatDate = sdfNow.format(nowdate);

    Intent intent;
    SpeechRecognizer mRecognizer; //음성인식


    private Activity mActivity;
    private Context mContext;

    private ProgressDialog STTDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        intent = getIntent();

        title_edit = (EditText) findViewById(R.id.title_edit);
        contents_edit = (EditText) findViewById(R.id.contents_edit);
        datetxt = (TextView) findViewById(R.id.datetxt);
        datetxt.setText(formatDate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        imgView = (ImageView) findViewById(R.id.imgView);

        if (getIntent() != null) {
            if (getIntent().hasExtra("memo")) {
                //보여주기
                mMemoId = getIntent().getLongExtra("id",-1);
                Log.d("TAG","getIntent : " + mMemoId);


               mImageUri = getIntent().getStringExtra("image2");
                Log.d("IMAGE", "get Intent : " + mImageUri);


               if(mImageUri !=null){
                   Glide.with(this)
                           .load((mImageUri))
                           .into(imgView);
               }

                Memo memo = (Memo) getIntent().getSerializableExtra("memo");
                title_edit.setText(memo.getTitle());
                contents_edit.setText(memo.getContents());
                Log.d("TAG","getIntent" + memo.getTitle() +"/" + memo.getContents());
            }
        }




        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        //mRecognizer.startListening(intent);

        STTDialog = new ProgressDialog(MemoActivity.this);

        AdView mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memomenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.ShareBtn :
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, title_edit.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT, contents_edit.getText().toString());

                Intent chooser = Intent.createChooser(intent, "공유");
                startActivity(chooser);
                return true;
            case R.id.SSTBtn :
                if(ContextCompat.checkSelfPermission(MemoActivity.this, Manifest.permission.RECORD_AUDIO)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MemoActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},1000);
                }else{
                    //권한을 허용한경우
                    try{
                        mRecognizer.startListening(intent);
                        STTDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        STTDialog.setMessage("음성 확인 중 입니다...");
                        STTDialog.show();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return true;
            case R.id.saveBtn :
                save();
                return true;
            case R.id.CancelBtn :
                AlertDialog.Builder dialog = new AlertDialog.Builder(MemoActivity.this);
                dialog.setTitle("종료 알림")

                        .setMessage("작성 중인 메모를 저장하시겠습니까?")

                        .setPositiveButton("예", new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialog, int which) {
                                save();

                                Toast.makeText(MemoActivity.this, "저장하였습니다.", Toast.LENGTH_SHORT).show();
                            }

                        })

                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialog, int which) {
                                cancel();
                                Log.d("TAG","Intent : " + mMemoId);
                                Toast.makeText(MemoActivity.this, "저장하지 않았습니다.", Toast.LENGTH_SHORT).show();

                            }

                        }).create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 1000 :
            {
                if(grantResults.length >0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    //승인됨
                    Toast.makeText(MemoActivity.this,"권한 승인됨",Toast.LENGTH_LONG).show();
                    mRecognizer.startListening(intent);

                }else{

                    //앱을 종료합니다
                    Toast.makeText(MemoActivity.this,"권한 거부됨",Toast.LENGTH_LONG).show();
                    MemoActivity.this.finish();
                }
                return;
            }
        }
    }

    private RecognitionListener listener = new RecognitionListener()
    {


        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            STTDialog.dismiss();
            Toast.makeText(getApplicationContext(), "음성인식 종료", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int i) {
            //Toast.makeText(getApplicationContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            contents_edit.setText(""+rs[0]);
            //mRecognizer.startListening(intent);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };


    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
    private void save(){

        Intent intent = new Intent();
        intent.putExtra("title",title_edit.getText().toString());
        intent.putExtra("contents",contents_edit.getText().toString());
        intent.putExtra("date",datetxt.getText().toString());
        intent.putExtra("id",mMemoId);
        int position = getIntent().getIntExtra("position",-1);
        intent.putExtra("position",position);
        intent.putExtra("image2",mImageUri);
        setResult(RESULT_OK,intent);
        Log.d("TAG","Intent"+ mMemoId + "," + RESULT_OK);
        finish();

    }


    @Override
    public void onBackPressed() {
        // Alert을 이용해 종료시키기

        AlertDialog.Builder dialog = new AlertDialog.Builder(MemoActivity.this);
        dialog.setTitle("종료 알림")

                .setMessage("작성 중인 메모를 저장하시겠습니까?")

                .setPositiveButton("예", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        save();
                        Toast.makeText(MemoActivity.this, "저장하였습니다.", Toast.LENGTH_SHORT).show();
                    }

                })

                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        cancel();
                        Toast.makeText(MemoActivity.this, "저장하지 않았습니다.", Toast.LENGTH_SHORT).show();

                    }

                }).create().show();
    } //뒤로가기 종료버튼


    public void onImageClick(View view) {
        //이미지 요청
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            //이미지가 정상적으로 선택되었을 때

            //이미지 경로
            Uri uri = data.getData();

            //mImageUri = MyUtils.getRealPath(this,uri);

            mImageUri = uri.toString();

            // 이미지를 bitmap으로 얻기
            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);

            // 이미지뷰에 bitmap 설정 (그냥)
            //imgView.setImageBitmap(bitmap);

            // Glide 라이브러리 사용 (좋은방법)
            Glide.with(this).load(uri.toString()).into(imgView);
            //Glide.with(this).load(uri).thumbnail(0.1f).into(imgView);
            //썸네일 효과
            /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            ThumbnailUtils.extractThumbnail(bitmap,100,100);*/
            //Toast.makeText(this, data.getData().toString(), Toast.LENGTH_SHORT).show();
        }
    }



}
