package com.example.recentverinstacloneforsuccess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText fullname,email,password,username;
    TextView txt_login;
    Button register;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        txt_login = findViewById(R.id.txt_login);
        register = findViewById(R.id.register);
        //레이아웃 아이디로 객체 생성

        auth = FirebaseAuth.getInstance();//firebase 사용자 정보 가저오기 getcurrent user 랑 붙혀서 같이 구현
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            //로그인 액티비티 구현 메소드
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please Wait....");
                pd.show();
                //프로그램 진행 상태 알려주는 도구 progress dialog 등록 시도시 뺑뺑이 나옴 저문구랑

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                //gettext로 정보 입력받은거 string으로 변환

                if(TextUtils.isEmpty(str_username)|TextUtils.isEmpty(str_fullname)|TextUtils.isEmpty(str_email)|TextUtils.isEmpty(str_username))
                {
                    Toast.makeText(RegisterActivity.this, "ALL Fields are Required", Toast.LENGTH_SHORT).show();
                    //입력칸 중에 하나라도 null이면 팝업 뜨게해줌 저문구랑 pd랑 비슷한느낌
                }else if (str_password.length()<0)
                {
                    Toast.makeText(RegisterActivity.this, "Password must have 6 character!", Toast.LENGTH_SHORT).show();
                }else
                    {
                        register(str_username,str_fullname,str_email,str_password);
                        //계정 정보 register 메소드에 파라미터로 전달
                    }


            }
        });
    }
    //계정 등록 메소드 파이어베이스랑 연동하는 부분같다.
    public void register(String username,String fullname, String email, String password)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener
                //계정생성 메소드이용
                (RegisterActivity.this, new OnCompleteListener<AuthResult>()
                {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) //task 완료시 oncompletelistner를 부르는거임
            {
                if(task.isSuccessful()) //task 가 false라면 무슨경우인지를 모르겠다 이메일이 잘못된건가
                {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid(); //사용자 프로필정보 가저오기
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    //자식 데이터 접근 child로

                    HashMap<String, Object>hashMap=new HashMap<>(); //key=string value=object 로 접근
                    hashMap.put("id",userid);
                    hashMap.put("username",username.toLowerCase()); //소문자로 바꿔주는 메소드
                    hashMap.put("fullname",fullname);
                    hashMap.put("bio","");
                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/recentverinstacloneforsuccess.appspot.com/o/323232.png?alt=media&token=ff1096fa-d09e-499e-b154-35d972a7233c");
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                pd.dismiss(); //pd창 안띄우는거 register 성공임 근데 안넣으면 어떻게될까?
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                //intent 객체 생성 얘는 중개자 역할같은거임 register랑 main 중개해주는듯
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK); //실행 액티비티 외 다제거 ,실행 액티비티 새로운 테스크로 생성
                                startActivity(intent);

                            }

                        }
                    });

                }else
                {
                    Toast.makeText(RegisterActivity.this, "You are not register with this email and password!", Toast.LENGTH_SHORT).show();
                }//사용자 정보 이메일 패스워드 둘다 틀렸을때 뜨는 팝업 생성

            }
        });
    }
}