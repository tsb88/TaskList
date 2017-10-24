package com.example.tejasbhoir.tasklist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText taskTitle;
    EditText taskDesc;
    ListView tasks;
    SimpleAdapter dualViewAdapter;
    List<HashMap<String, String>> keys;
    HashMap<String, String> taskMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keys = new ArrayList<HashMap<String, String>>();
        taskTitle = (EditText) findViewById(R.id.taskTitle);
        taskDesc = (EditText) findViewById(R.id.taskDesc);
        tasks = (ListView) findViewById(R.id.tasks);

        dualViewAdapter = new SimpleAdapter(this, keys,
                R.layout.two_line_list_item,
                new String[] {"title", "desc"},
                new int[] {android.R.id.text1, android.R.id.text2});

        tasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                keys.remove(position);
                tasks.setAdapter(dualViewAdapter);
                dualViewAdapter.notifyDataSetChanged();

                deleteTask(position, getApplicationContext(), view);

                Toast toast = Toast.makeText(getBaseContext(), "Item " + (position+1) + " Deleted", Toast.LENGTH_SHORT);
                toast.show();

                return true;
            }

        });

        taskDesc.setOnKeyListener(new AdapterView.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                taskDesc.setFocusableInTouchMode(true);
                taskDesc.requestFocus();

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    addTask(view);

                    return true;
                }

                return false;
            }

        });

        readFromFile(this);
    }

    public void writeToFile(String data, Context context) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("tasksData.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast toast = Toast.makeText(this, "Saved to tasksData.txt", Toast.LENGTH_LONG);
        toast.show();
    }

    public void clearList(View view) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("tasksData.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        keys.clear();
        tasks.setAdapter(dualViewAdapter);
        dualViewAdapter.notifyDataSetChanged();
    }

    public void deleteTask (int position, Context context, View view) {
        ArrayList<String> newStrings = new ArrayList<>();
        String newReceivedString = "";
        int y = 0; int z = 0;

        try {
            InputStream inputStream = context.openFileInput("tasksData.txt");

            if(inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                while ((newReceivedString = bufferedReader.readLine()) != null) {
                    newStrings.add(newReceivedString);
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"File Not Found",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        newStrings.remove((position*2)+1);
        newStrings.remove(position*2);

        clearList(view);

        if (newStrings.size() > 0) {
            while (z < newStrings.size()) {
                try {
                    String str = newStrings.get(z);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("tasksData.txt", Context.MODE_APPEND));
                    outputStreamWriter.write(str + "\n");
                    outputStreamWriter.close();
                    z++;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (newStrings.size() > 0) {
            while (y < newStrings.size()) {
                taskMap = new HashMap<String, String>();
                taskMap.put("title", newStrings.get(y));
                taskMap.put("desc", newStrings.get(y + 1));
                keys.add(taskMap);
                tasks.setAdapter(dualViewAdapter);
                dualViewAdapter.notifyDataSetChanged();
                y += 2;
            }
        }
    }

    public void readFromFile(Context context) {

        ArrayList<String> readStrings = new ArrayList<>();
        String receivedString = "";
        int x = 0;

        try {
            InputStream inputStream = context.openFileInput("tasksData.txt");

            if(inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                while ((receivedString = bufferedReader.readLine()) != null) {
                    readStrings.add(receivedString);
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"File Not Found",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (readStrings.size() >= 2) {
            while (x < readStrings.size()) {
                taskMap = new HashMap<String, String>();
                taskMap.put("title", readStrings.get(x));
                taskMap.put("desc", readStrings.get(x + 1));
                keys.add(taskMap);
                tasks.setAdapter(dualViewAdapter);
                dualViewAdapter.notifyDataSetChanged();
                x += 2;
            }
        }
    }

    public void addTask(View view) {

        String titleString = taskTitle.getText().toString();
        String descString = taskDesc.getText().toString();

        if(titleString.isEmpty() || descString.isEmpty()) {
            Toast toast = Toast.makeText(this, "Empty Task. Enter a Title", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            taskMap = new HashMap<String, String>();
            taskMap.put("title", titleString);
            taskMap.put("desc", descString);
            keys.add(taskMap);
            tasks.setAdapter(dualViewAdapter);
            dualViewAdapter.notifyDataSetChanged();

            String data = titleString + "\n" + descString + "\n";

            writeToFile(data, getApplicationContext());

            taskTitle.getText().clear();
            taskDesc.getText().clear();
        }

        taskTitle.setSelectAllOnFocus(true);
        taskTitle.requestFocus();
    }
}