package com.example.ictko.SimpleNote;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_NEW_MEMO = 1000;
    public static final int REQUEST_CODE_UPDATE_MEMO = 1001;
    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Memo> mMemoList;
    //private List<Memo> newMemoList = null;
    private MemoRecyclerAdapter mAdapter;
    private RecyclerView mMemoRecyclerview;

    private MemoFacade mMemoFacade;

    private Button AtoZbtn;

    LinearLayout Linear1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //화면 전환 기능 켜기
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //setTitle("Memo");

        // 롤리팝 이상에서만 동작
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            TransitionSet set = new TransitionSet();
            set.addTransition(new ChangeImageTransform());
            getWindow().setExitTransition(set);
            getWindow().setEnterTransition(set);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //SearchView
        SearchView searchView = (SearchView) findViewById(R.id.Search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //완료누르면
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//변경될때마다
                // 새로운 쿼리의 결과 뿌리기
                mMemoList = mMemoFacade.getMemoList(
                        MemoContract.MemoEntry.COLUMN_NAME_TITLE + " LIKE '%" + newText + "%'",//조건
                        null,
                        null,
                        null,
                        null
                );
                mAdapter.swap(mMemoList);
                Log.d("Test","newMemoList 값 : " + mMemoList.size());
                return true;
            }
        });
        //메모 파사드
        mMemoFacade = new MemoFacade(this);


        mMemoRecyclerview = (RecyclerView) findViewById(R.id.memo_recycler);
        //애니메이션 커스터마이징 참고용 mMemoRecyclerview.setItemAnimator();
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setChangeDuration(1000);
        mMemoRecyclerview.setItemAnimator(animator);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, MemoActivity.class), REQUEST_CODE_NEW_MEMO);
                Log.d("TAG", "Fab" + REQUEST_CODE_NEW_MEMO);
            }
        });


        // 데이터
        mMemoList = mMemoFacade.getMemoList();


        // 어댑터
        mAdapter = new MemoRecyclerAdapter(this,mMemoList);

        mMemoRecyclerview.setAdapter(mAdapter);

        AdView mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.AtoZ:
                Comparator<Memo> textAsc = new Comparator<Memo>() {
                    @Override
                    public int compare(Memo item1, Memo item2) {
                        return item1.getTitle().compareTo(item2.getTitle());
                    }
                };
                Collections.sort(mMemoList,textAsc);   //오름차순으로 정렬
                mAdapter.notifyDataSetChanged();

                return true;
            case R.id.ZtoA:
                Comparator<Memo> textDesc = new Comparator<Memo>() {
                    @Override
                    public int compare(Memo item1, Memo item2) {
                        return item2.getTitle().compareTo(item1.getTitle());
                    }
                };
                Collections.sort(mMemoList,textDesc);   //내림차순으로 정렬
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.oldest:
                Comparator<Memo> dateAsc = new Comparator<Memo>() {
                    @Override
                    public int compare(Memo item1, Memo item2) {
                        return item1.getDate().compareTo(item2.getDate());
                    }
                };
                Collections.sort(mMemoList,dateAsc);  //오래된순으로 정렬
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.newest:
                Comparator<Memo> dateDesc = new Comparator<Memo>() {
                    @Override
                    public int compare(Memo item1, Memo item2) {
                        return item2.getDate().compareTo(item1.getDate());
                    }
                };
                Collections.sort(mMemoList,dateDesc); // 최신순으로 정렬??
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");
            String contents = data.getStringExtra("contents");
            String date = data.getStringExtra("date");
            String imageUri = data.getStringExtra("image2");
            if (requestCode == REQUEST_CODE_NEW_MEMO) {
                //새 메모
                long newRowId = mMemoFacade.insert(title, contents, date,imageUri);
                if (newRowId == -1) {
                    //에러
                    //Toast.makeText(this, "저장이 실패했습니다", Toast.LENGTH_SHORT).show();
                } else {
                    //성공
                    //리스트 갱신
                    mMemoList = mMemoFacade.getMemoList();
                    Log.d("TAG", "insert : " + mMemoList);
                }
                mAdapter.insert(mMemoList);
            } else if (requestCode == REQUEST_CODE_UPDATE_MEMO) {
                long id = data.getLongExtra("id", -1);
                int position = data.getIntExtra("position", -1);
                //수정

                if (mMemoFacade.update(id, title, contents, date,imageUri) > 0) {
                    mMemoList = mMemoFacade.getMemoList();
                    Log.d("TAG", "update" + id + mMemoList);
                }
                mAdapter.update(mMemoList, position);
            }
            Log.d(TAG, "onActivityResult : " + title + " , " + contents + " , " + date + " , " + imageUri);
            Toast.makeText(this, "저장 되었습니다", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "저장 실패했습니다", Toast.LENGTH_SHORT).show();
        }


    }

    // 보낸이 : MemoRecyclerAdapter
    @SuppressLint("RestrictedApi")
    @Subscribe
    public void onItemClick(MemoRecyclerAdapter.ItemClickEvent event) {
        Memo memo = mMemoList.get(event.position);

            //Memo memo2 = newMemoList.get(event.position);
        Intent intent = new Intent(this, MemoActivity.class);
            intent.putExtra("id", event.id);
            intent.putExtra("memo", memo);
            intent.putExtra("position", event.position);
            intent.putExtra("image2",memo.getImg_uri());

       /* Intent intent = new Intent(this, MemoActivity.class);
        intent.putExtra("id", event.id);
        intent.putExtra("memo", memo);
        intent.putExtra("position", event.position);
        intent.putExtra("image2",memo.getImg_uri());
        Log.d("TAG", "put Intent : " + event.id);
        Log.d("IMAGE", "put Intent : " + memo.getImg_uri());*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(intent, REQUEST_CODE_UPDATE_MEMO,
                        ActivityOptions.makeSceneTransitionAnimation(this, event.view, "image").toBundle());
            }
        }
    }

    // 보낸이 : MemoRecyclerAdapter
    @Subscribe
    public void onItemLongClick(final MemoRecyclerAdapter.ItemLongClickEvent event) {


        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("삭제 알림")
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("삭제합니다", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMemo(event.id);
                        Toast.makeText(MainActivity.this, "삭제하였습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, "삭제하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }
    /*@Subscribe
    public void onItemDelClick(final MemoRecyclerAdapter.ItemDelClickEvent event){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("삭제 알림")
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("삭제합니다", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMemo(event.id);
                        Toast.makeText(MainActivity.this, "삭제하였습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, "삭제하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
    }*/
    //데이터 삭제
    private void deleteMemo(long id) {
        int deleted = mMemoFacade.delete(id);
        // if (deleted != 0) {
        mAdapter.swap(mMemoFacade.getMemoList());
        // }
    }

    @Override
    public void onBackPressed() {

        //Admob 네이티브 광고
        NativeAdDialog dialog = new NativeAdDialog(this);
        dialog.show();
        // Alert을 이용해 종료시키기

        /*AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

        dialog.setTitle("종료 알림")

                .setMessage("정말 종료하시겠습니까?")

                .setPositiveButton("종료합니다", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }

                })

                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, "종료하지 않습니다", Toast.LENGTH_SHORT).show();

                    }

                }).create().show();
*/
    } //뒤로가기 종료버튼
}
