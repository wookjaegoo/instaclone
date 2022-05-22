package com.example.recentverinstacloneforsuccess.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.recentverinstacloneforsuccess.Adapter.PostAdapter;
import com.example.recentverinstacloneforsuccess.Adapter.StoryAdapter;
import com.example.recentverinstacloneforsuccess.Model.Post;
import com.example.recentverinstacloneforsuccess.Model.Story;
import com.example.recentverinstacloneforsuccess.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private List<String> followinglist;
    ProgressBar progressBar;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.progress_circular);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView_story =view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext()
        ,LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(layoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);

        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        checkFollowing();
        return view;

    }

    private void checkFollowing()
    {
        followinglist = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                followinglist.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren())
                {
                    followinglist.add(snapshot.getKey());
                    //내아이디가 지금 팔ㄹ로우 세명하잖아 걔네 세명아디 키값을 가져와서
                    //스냅샷 으로 한명씩 반복문 돌려서 followinglst에 너허두는거
                }
                readPosts();
                readyStory();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readPosts()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                postLists.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren())
                {
                    Post post = snapshot.getValue(Post.class);
                    //여기서 아디 하나에 해당하는 정보 POST = postid decription,. 2개더 이정보가 담기잖아
                    for(String id : followinglist)
                    {

                        if(post.getPublisher().equals(id))
                        {
                            postLists.add(post);

                        }
                    }
                }



                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readyStory()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                long currenttime= System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));

                for(String id:followinglist)
                {
                    int countStory =0;
                    Story story =null;
                    for(DataSnapshot dataSnapshot1 : datasnapshot.child(id).getChildren())
                    {
                        story = dataSnapshot1.getValue(Story.class);
                        if(currenttime > story.getTimestart() && currenttime <story.getTimeend())
                        {
                            countStory++;
                        }
                    }
                    if(countStory > 0)
                    {
                        storyList.add(story);
                    }

                }
                storyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}