package com.codewith.sandisuryadi.sampleretrofitrx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codewith.sandisuryadi.sampleretrofitrx.apiservice.BaseApiService;
import com.codewith.sandisuryadi.sampleretrofitrx.apiservice.UtilsApi;
import com.codewith.sandisuryadi.sampleretrofitrx.model.Repo;
import com.codewith.sandisuryadi.sampleretrofitrx.model.ResponseRepos;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    /*
    inisialisasi komponen komponen yang sudah kita buat di layout activity_main.xml
     */
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    @BindView(R.id.rvRepos)
    RecyclerView rvRepos;
    @BindView(R.id.etUserName)
    EditText etUserName;

    /*
    inisialisasi file pembantu yang sudah kita buat.
    BaseApiService : untuk persoalan request API
    ReposAdapter : untuk kebutuhan adapter RecyclerView
     */

    BaseApiService mApiService;
    ReposAdapter mReposAdapter;

    List<Repo> repoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mApiService = UtilsApi.getAPIService();

        /*
        inisialisasi adapter dan recyclerview
         */

        mReposAdapter = new  ReposAdapter (this, repoList, null);
        rvRepos.setLayoutManager(new LinearLayoutManager(this));
        rvRepos.setItemAnimator(new DefaultItemAnimator());
        rvRepos.setHasFixedSize(true);
        rvRepos.setAdapter(mReposAdapter);

        etUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*
                EditorInfo.IME_ACTION_SEARCH ini berfungsi untuk men-set keyboard kamu
                agar enter di keyboard menjadi search.
                 */
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String username = etUserName.getText().toString();
                    requestRepos(username);
                    return true;
                }
                return false;
            }
        });
    }

    /*
    Fungsi untuk berkomunikasi dengan API Server menggunakan library Retrofit dan RxJava.
     */
    private void requestRepos(String username){
        pbLoading.setVisibility(View.VISIBLE);

        mApiService.requestRepos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ResponseRepos>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ResponseRepos> responseRepos) {
                        /*
                        onNext disini ketika data sudah masuk dan biasanya kita memasukan data API
                        ke lokal ataupun sesuai kebutuhan kamu. Di contoh ini data dari API Server dimasukan
                        dalam List repoList.
                         */
                        for (int i = 0; i < responseRepos.size(); i++){
                            String name = responseRepos.get(i).getName();
                            String description = responseRepos.get(i).getDescription();

                            repoList.add(new Repo(name, description));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        pbLoading.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Berhasil Mengambil Data", Toast.LENGTH_SHORT).show();

                        mReposAdapter = new ReposAdapter(MainActivity.this, repoList, null);
                        rvRepos.setAdapter(mReposAdapter);
                        mReposAdapter.notifyDataSetChanged();
                    }
                });

    }
}
