package com.thecupboardapp.cupboard.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.adapters.SListItemAdapter;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;
import com.thecupboardapp.cupboard.models.viewmodels.SListEditViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SListEditActivity extends AppCompatActivity {
    private String TAG = "SListEditActivity";

    public static final String EXTRA_ID = "com.thecupboardapp.cupboard.EXTRA_ID";
    private long sListIdExtra;

    private RecyclerView mRecyclerView;
    private SListItemAdapter mAdapter;

    private Disposable disposableSListItems;
    private Disposable disposableSList;
    private CompositeDisposable mDisposables;

    private SList oldSList;
    private List<SListItem> oldSListItems;

    private SListEditViewModel mSListEditViewModel;

    public static boolean mIsEnterPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slist_edit);

        // Find the recycler view and set its layout
        mRecyclerView = findViewById(R.id.shopping_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SListEditActivity.this));

        // init disposable container
        mDisposables = new CompositeDisposable();

        // Get the sListIdExtra to see if this is a new list or not
        sListIdExtra = getIntent().getLongExtra(EXTRA_ID, -1);

        // Initialize the viewmodel
        mSListEditViewModel = ViewModelProviders.of(this).get(SListEditViewModel.class);
        mSListEditViewModel.setSListItems(sListIdExtra);
        mSListEditViewModel.setSList(sListIdExtra);

        // If the list exists, get the items and the slist
        if (sListIdExtra != -1) {
            disposableSListItems = mSListEditViewModel.getListItemsById(sListIdExtra)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sListItems -> {
                        if (mAdapter == null) {
                            Log.d(TAG, "onCreate: null adapter " + sListItems);
                            oldSListItems = new ArrayList<SListItem>(sListItems);
                            mAdapter = new SListItemAdapter(sListItems, sListIdExtra);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "onCreate: not null adapter");
                            mAdapter.updateList(sListItems);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
            disposableSList = mSListEditViewModel.getListById(sListIdExtra)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sList -> {
                        oldSList = sList;
                        setTitle(sList.getName());
                    });

            mDisposables.addAll(disposableSListItems);
        } else {
            oldSList = new SList("New List", 99);
            setTitle("New List");
            oldSListItems = new ArrayList<SListItem>();

            mAdapter = new SListItemAdapter(new ArrayList<SListItem>(), sListIdExtra);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onStop() {
        mDisposables.dispose();
        super.onStop();
    }

    public static Intent newIntent(Context packageContext, long id) {
        Intent intent = new Intent(packageContext, SListEditActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_title: {
                editTitle();
                break;
            }
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mAdapter.getSListItems().size() == 0) {
            super.onBackPressed();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Save Changes?");

        alert.setPositiveButton("Yes", (dialog, whichButton) -> {
            if (sListIdExtra == -1) {
                SList newList = new SList(getTitle().toString());
                Observable.fromCallable(() -> mSListEditViewModel.insertSList(newList))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(primaryKey -> {
                            newList.setIndex(primaryKey.intValue());
                            mSListEditViewModel.updateSList(newList);
                            mAdapter.setParentId(primaryKey);
                            mSListEditViewModel.update(oldSListItems, mAdapter.getSListItems());
                        });
            } else {
                mSListEditViewModel.update(oldSListItems, mAdapter.getSListItems());
                mSListEditViewModel.updateListTitle(sListIdExtra, getTitle().toString());
                mSListEditViewModel.updateLastModified(sListIdExtra);
            }

            super.onBackPressed();
        });

        alert.setNeutralButton("Back", (dialogInterface, i) -> {});
        alert.setNegativeButton("No", (dialog, whichButton) -> super.onBackPressed());
        alert.show();
    }

    private void editTitle(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View v = getLayoutInflater().inflate(R.layout.dialog_slist_edit_title, null);
        EditText text = v.findViewById(R.id.slist_edit_title);

        alert.setTitle("Title");
        alert.setView(v);

        alert.setPositiveButton("Ok", (dialog, whichButton) ->
                SListEditActivity.this.setTitle(text.getText().toString()));

        alert.setNegativeButton("Back", (dialog, whichButton) -> dialog.dismiss());

        alert.show();
    }
}
