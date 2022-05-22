package com.example.recentverinstacloneforsuccess;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity  {
//    url: http://gogang.co.kr/index<<자원 존재 위치를 가리킴
//    uri: http://gogang.co.kr/user/19 이런식임 <<url 포함하는거지 <<자원위치+고유 식별자

    Uri imageUri;
    String imageUrl;
    StorageTask uploadTask;
    StorageReference storageReference;
    EditText description;
    ImageView close, image_added;
    TextView post ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close = findViewById(R.id.close);
        description = findViewById(R.id.description);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        storageReference = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(view -> {
            startActivity(new Intent(PostActivity.this, MainActivity.class));

            finish();
//            main함수 호출 intent객체 생성시 activity가 Context 상속한 클래스임
//            그래서 contex타입 파라미터에 액티비자신을 넣음음
//            그냥 postactivity 종료하는 함수 그자체 postactivity 종료하고 main으로 복귀인듯
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });




        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(PostActivity.this);


    }



    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
//    contentresolver는 data 공유 통로임 uri 매개변수를 보아 img파일 연결 통로,
//    주소통해서 데이터 가져옴 manifest에 permission READ 추가해준거 봐서 이거 때문임
//    getsingleton으로 객체 생성해서 mime 만듦 저게 파일 확장자 mimetype:ppt 이런식으로 알려주는듯



    private void uploadImage()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.show();
//앱 진행정도 알려주는 팝업이라 생가갛면된다
        if(imageUri !=null)
        {
            StorageReference referencefile = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));
//            현재시간 +.확장자
//            storagereference는 구글 클라우드 스토어에 uploadign download 지원해주는 도구
//            firebase 툴 함수라 이미지 업로드한 정보가 저장되는데에 지원해주는거임
//            파일 업로드,다운로드 등등을 위해 reference를 만드는거 referenece는 클라우드 파일 가리키는 포인터
//            child메소드는 images/speace.jpg 같이 트리에서 하위 위치 가리키는 참조를 만들게해줌

            uploadTask = referencefile.putFile(imageUri);
            //                파일 업로드후 파일 다운로드 url가져오는 과정
            uploadTask.continueWithTask(task -> {

                if(!task.isSuccessful())
                {
                    throw task.getException();
                }
                return referencefile.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {

                if(task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
//파이어베이스에 데이터 업로드 라는데 이거
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                    String postid = reference.push().getKey();
//                    child 위치에 참조 만드는 push ,push로 생긴 키값 반환
                    HashMap <String,Object> hashMap = new HashMap<>();

                    hashMap.put("postid",postid);
                    hashMap.put("postimage",imageUrl);
                    hashMap.put("description",description.getText().toString());
                    hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    reference.child(postid).setValue(hashMap);
                    progressDialog.dismiss();

                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();


                }
                else
                {
                    Toast.makeText(PostActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                }


            }).addOnFailureListener(e -> Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        else
        {
            Toast.makeText(this, "No image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri=result.getUri();
            image_added.setImageURI(imageUri);

            ContentResolver resolver = getContentResolver();
            try {
                InputStream instream = resolver.openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(instream);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                instream.close();   // 스트림 닫아주기
                String datastring = new String(byteArray);
                System.out.println(datastring);

                Toast.makeText(getApplicationContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
            }





        }
        else
        {
            Toast.makeText(this,"Try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }


    }


}