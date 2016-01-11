package ics4u.ics4u_final_project;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class IngredientSelectionActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private LinearLayoutManager lLayoutIngredient;
    ArrayList<Ingredient> results = new ArrayList<>();
    String[] ingredientCategories;
    ArrayAdapter<String> adapter;
    Spinner ingredientDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        results.add(new Ingredient(0,"Search for an Ingredient. Use commas to separate keywords."));
        setContentView(R.layout.rv_ingredientselect);

        AdapterView.OnItemSelectedListener onSpinner = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        ingredientDropdown = (Spinner) findViewById(R.id.category_combobox);
        ingredientCategories = new String[]{"Show all"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ingredientCategories);
        ingredientDropdown.setPrompt("Please select a category of ingredient");
        ingredientDropdown.setAdapter(adapter);
        ingredientDropdown.setOnItemSelectedListener(onSpinner);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Create A Recipe");
        setSupportActionBar(mToolbar);
        System.out.println(results.get(0).getName());
        List<Ingredient> rowListItem = getAllItemList();
        lLayoutIngredient = new LinearLayoutManager(IngredientSelectionActivity.this);

        RecyclerView rView = (RecyclerView) findViewById(R.id.recycler_view_ingredient);
        rView.setLayoutManager(lLayoutIngredient);

        IngredientAdapter rcAdapter = new IngredientAdapter(IngredientSelectionActivity.this, rowListItem);
        rView.setAdapter(rcAdapter);
    }

    private List<Ingredient> getAllItemList() {

        return results;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(true);
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.close));

            isSearchOpened = true;
        }
    }

    private void doSearch(boolean fetchCats) {
        String searchText = edtSeach.getText().toString();
        if (searchText.length() < 2){
            Toast.makeText(getBaseContext(), "Search keyword too short, please be more specific", Toast.LENGTH_SHORT).show();
        } else {
            results = Database.search(searchText);
            System.out.println("Search Done");
            //TextView t = (TextView) findViewById(R.id.textView);
            //System.out.println(results.size());
            //t.setText(results.get(0).getName());
            if (results.isEmpty()){
                Toast.makeText(getBaseContext(), "Nothing Found", Toast.LENGTH_SHORT).show();
            } else {
                List<Ingredient> rowListItem = getAllItemList();
                //add the matches to the list model
                ArrayList<String> cats = new ArrayList<>();
                cats.add("Show All");
                for (int i = 0; i < rowListItem.size(); i++) {
                    if (fetchCats) {
                        if (rowListItem.get(i).getName().contains(",")) {
                            cats.add(rowListItem.get(i).getName().substring(0, rowListItem.get(i).getName().indexOf(",")));
                        } else {
                            cats.add(rowListItem.get(i).getName());
                        }
                    }
                }
                for (int i = 0; i < cats.size(); i++){
                    for (int j = i + 1; j < cats.size(); j++) {
                        if (cats.get(i).equals(cats.get(j))) {
                            cats.remove(j);
                            j--;
                        }
                    }
                }
                System.out.println(cats.size());
                String[] categories = new String[cats.size()];
                for (int i = 0; i < cats.size(); i++){
                    categories[i] = cats.get(i);
                    System.out.println(categories[i]);
                }
                ingredientCategories = categories;
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ingredientCategories);
                ingredientDropdown.setAdapter(adapter);
                lLayoutIngredient = new LinearLayoutManager(IngredientSelectionActivity.this);

                RecyclerView rView = (RecyclerView) findViewById(R.id.recycler_view_ingredient);
                rView.setLayoutManager(lLayoutIngredient);

                IngredientAdapter rcAdapter = new IngredientAdapter(IngredientSelectionActivity.this, rowListItem);
                rView.setAdapter(rcAdapter);
            }
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
