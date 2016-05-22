package com.example.wish;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wish.bean.Festival;
import com.example.wish.bean.FestivalLab;
import com.example.wish.bean.Msg;
import com.example.wish.biz.SmsBiz;
import com.example.wish.view.FlowLayout;

import java.util.HashSet;
import java.util.List;
import java.util.zip.Inflater;

public class SendMsgActivity extends AppCompatActivity {
    public static final String KEY_FESIIVAL_ID="festivalId";
    public static final String KEY_MSG_ID = "msgId";
    private int mFestivalId;
    private int msgId;
    private Festival mFestival;
    private Msg mMsg;
    private EditText mEdMsg;
    private Button mBtnAdd;
    private FlowLayout mFlContacts;
    private FloatingActionButton mFabBtn;
    private View mLayoutLoading;
    public static final  int CODE_REQUEST=1;

    private HashSet<String> mContactNames = new HashSet<>();
    private HashSet<String> mContactNums = new HashSet<>();
    private String number;

    private LayoutInflater mInflater;
    private static final String ACTION_SEND_MSG = "ACTION_SEND_MSG";
    private static final String ACTION_DELIVER_MSG = "ACTION_DELIVER_MSG";

    private PendingIntent mSendPi;
    private PendingIntent mDeliverPi;
    private BroadcastReceiver mSendBroadcastReceiver;
    private BroadcastReceiver mDeliverBroadcastReceiver;

    private SmsBiz smsBiz = new SmsBiz();

    private int mMsgSendCount;
    private int totalCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);

        mInflater = LayoutInflater.from(this);
        initDatas();
        initViews();
        initEvent();
        initReceiver();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initReceiver() {
        Intent sendIntent = new Intent (ACTION_SEND_MSG);
        mSendPi=PendingIntent.getBroadcast(this,0,sendIntent,0);
        Intent deliverIntent = new Intent (ACTION_DELIVER_MSG);
        mDeliverPi = PendingIntent.getBroadcast(this,0,deliverIntent,0);

        registerReceiver(mSendBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mMsgSendCount++;
                if (getResultCode() == RESULT_OK) {
                    Log.e("TAG", "短信发送成功" + (mMsgSendCount + "  / " + totalCount));
                } else {
                    Log.e("TAG", "短信发送失败");
                }
                Toast.makeText(SendMsgActivity.this,(mMsgSendCount + "  / " +totalCount)+"短信发送成功",Toast.LENGTH_SHORT).show();

                if (mMsgSendCount == totalCount) {
                    finish();
                }

            }
        }, new IntentFilter(ACTION_SEND_MSG));
        registerReceiver(mDeliverBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("TAG", "联系人已经接受我们的短信");
            }
        }, new IntentFilter(ACTION_DELIVER_MSG));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSendBroadcastReceiver);
        unregisterReceiver(mDeliverBroadcastReceiver);
    }

    private void initEvent() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CODE_REQUEST);
            }
        });
        mFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContactNums.size() == 0) {
                    Toast.makeText(SendMsgActivity.this, "请先选择联系人", Toast.LENGTH_SHORT).show();
                    return ;
                }
                String msg = mEdMsg.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(SendMsgActivity.this,"短信内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                mLayoutLoading.setVisibility(View.VISIBLE);
                int count = smsBiz.sendMsg(mContactNums, msg, mSendPi, mDeliverPi);
                mMsgSendCount=0;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CODE_REQUEST){
            if(resultCode==RESULT_OK){
                Uri contactUri = data.getData();
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                cursor.moveToFirst();
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String number = getContactNumber(cursor);
                if(!TextUtils.isEmpty(number)){
                    mContactNums.add(number);
                    mContactNames.add(contactName);

                    addTag(contactName);
                }
            }

        }
    }

    private void addTag(String contactName) {
        TextView view = (TextView)mInflater.inflate(R.layout.tag, mFlContacts, false);
        view.setText(contactName);
        mFlContacts.addView(view);
    }

    private String getContactNumber(Cursor cursor) {
        int numberCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        String number = null;
        if(numberCount>0){
            int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId,null ,null);
            phoneCursor.moveToFirst();
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneCursor.close();
        }
        cursor.close();
        return number;
    }

    private void initViews() {
        mEdMsg = (EditText)findViewById(R.id.id_et_content);
        mBtnAdd = (Button)findViewById(R.id.id_btn_send);
        mFlContacts = (FlowLayout)findViewById(R.id.id_fl_content);
        mFabBtn = (FloatingActionButton)findViewById(R.id.id_fab_toSend);
        mLayoutLoading = findViewById(R.id.id_layout_loading);

        mLayoutLoading.setVisibility(View.GONE);
        if(msgId!=-1){
            mMsg=FestivalLab.getInstance().getMsgById(msgId);
            mEdMsg.setText(mMsg.getContent());
        }
    }


    private void initDatas() {
        mFestivalId = getIntent().getIntExtra(KEY_FESIIVAL_ID,-1);
        msgId = getIntent().getIntExtra(KEY_MSG_ID,-1);

        mFestival = FestivalLab.getInstance().getFestivalById(mFestivalId);
        setTitle(mFestival.getName());
    }

    public static void toActivity(Context context,int festivalId ,int msgId){
        Intent intent= new Intent(context,SendMsgActivity.class);
        intent.putExtra(KEY_FESIIVAL_ID,festivalId);
        intent.putExtra(KEY_MSG_ID,msgId);
        context.startActivity(intent);
    }

}
