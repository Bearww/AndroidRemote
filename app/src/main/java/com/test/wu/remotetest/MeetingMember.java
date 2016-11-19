package com.test.wu.remotetest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Random;

public class MeetingMember extends Fragment implements OnItemSelectedListener {

    private static int NONE = 0;
    private static int MULTICHOICE = 1;
    private static int OPEN_TEXT = 2;
    private static int RATING = 3;

    TextView controllerTextView;
    TextView chairmanTextView;
    TextView presenterTextView;
    TextView memberTextView;

    Button controllerButton;
    Button chairmanButton;
    Button presenterButton;
    Button memberButton;

    Button askButton;
    Button voteButton;
    Button createVoteButton;

    ListView askListView;

    // TODO change predefine user name
    String userName = "bearww";
    String controller = userName;
    String chairman = "";
    String presenter = "";
    String[] members = { "bearww", "member0", "member1", "member2", "member3", "member4", "member5" };
    String[] questions = {};
    String selectedQuestion = "";

    int selectPollType = NONE;
    String[] pollType = { "Multiple Choice", "Open Text", "Rating" };

    // TODO poll list
    ArrayList<Vote> polls = new ArrayList<>();

    int selectedOption = -1;

    AskEvent askEvent = new AskEvent();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.meeting_member, container, false);

        controllerTextView = (TextView) view.findViewById(R.id.controllerTextView);
        chairmanTextView = (TextView) view.findViewById(R.id.chairmanTextView);
        presenterTextView = (TextView) view.findViewById(R.id.presenterTextView);
        memberTextView = (TextView) view.findViewById(R.id.memberTextView);

        // TODO temp setting
        controllerTextView.setText(controller);

        controllerButton = (Button) view.findViewById(R.id.controllerButton);
        chairmanButton = (Button) view.findViewById(R.id.chairmanButton);
        presenterButton = (Button) view.findViewById(R.id.presenterButton);
        memberButton = (Button) view.findViewById(R.id.memberButton);

        askListView = (ListView) view.findViewById(R.id.askListView);
        updateAskList();

        controllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showControllerAlertDialog();
            }
        });

        chairmanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChairmanAlertDialog();
            }
        });

        presenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPresenterAlertDialog();
            }
        });

        memberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMemberAlertDialog();
            }
        });

        askButton = (Button) view.findViewById(R.id.askButton);
        voteButton = (Button) view.findViewById(R.id.voteButton);
        createVoteButton = (Button) view.findViewById(R.id.createVoteButton);

        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAskAlertDialog();
            }
        });

        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVoteAlertDialog();
            }
        });

        createVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateVoteAlertDialog();
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getContext(), "您選擇" + id, Toast.LENGTH_LONG).show();
        if(id == 0)
            selectedQuestion = "";
        else
            selectedQuestion = parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedQuestion = "";
        Toast.makeText(getContext(), "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
    }

    private void showControllerAlertDialog() {
        final String[] memberList = getMemberList(controller);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("選擇");
        alertDialog.setItems(memberList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                controller = memberList[which];
                controllerTextView.setText(controller);
            }
        });
        alertDialog.show();
    }

    private void showChairmanAlertDialog() {
        final String[] memberList = getMemberList(chairman);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("選擇");
        alertDialog.setItems(memberList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chairman = memberList[which];
                chairmanTextView.setText(chairman);
            }
        });
        alertDialog.show();
    }

    private void showPresenterAlertDialog() {
        final String[] memberList = getMemberList(presenter);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("選擇");
        alertDialog.setItems(memberList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter = memberList[which];
                presenterTextView.setText(presenter);
            }
        });
        alertDialog.show();
    }

    private void showMemberAlertDialog() {
        // TODO need to change to display member not in meeting
        String[] memberList = getMemberList();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("選擇");
        alertDialog.setItems(memberList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private void showAskAlertDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.dialog_ask, null);

        questions = getQuestions();
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, questions);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("提問");
        alertDialog.setView(view);
        alertDialog.setPositiveButton("送出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText askText = (EditText) view.findViewById(R.id.askText);
                CheckBox publicCheck = (CheckBox) view.findViewById(R.id.publicCheck);

                if(publicCheck.isChecked())
                    askEvent.addAsk(askText.getText().toString(), userName, selectedQuestion);
                else
                    askEvent.addAsk(askText.getText().toString(), userName, selectedQuestion, false);

                // TODO update ask list
                updateAskList();
            }
        });
        alertDialog.show();
    }

    private void showVoteAlertDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.dialog_vote_list, null);

        TextView noPollText = (TextView) view.findViewById(R.id.noPollTextView);
        if (polls.size() == 0)
            noPollText.setVisibility(View.VISIBLE);

        else
            noPollText.setVisibility(View.INVISIBLE);

        ListView pollListView = (ListView) view.findViewById(R.id.voteListView);
        ArrayAdapter<String> pollAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getPollsTitle());
        pollListView.setAdapter(pollAdapter);
        pollListView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View dialogView = inflater.inflate(R.layout.dialog_vote, null);

                // TODO checked previous vote result and display
                // if(...) then radio = selected;
                selectedOption = -1;    // Suppose no any selected option

                RadioGroup voteOptionGroup = (RadioGroup) dialogView.findViewById(R.id.voteOptionGroup);
                voteOptionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        selectedOption = group.indexOfChild(dialogView.findViewById(checkedId));
                    }
                });

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("投票");
                alertDialog.setView(dialogView);
                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO save vote result
                        if(selectedOption != -1)
                            dialog.dismiss();
                    }
                });
                alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("投票");
        alertDialog.setView(view);
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    private void showCreateVoteAlertDialog() {
        selectPollType = NONE;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.dialog_create_vote, null);
/*
        ImageButton multiChoiceBtn = (ImageButton) view.findViewById(R.id.multiChoiceButton);
        ImageButton openTextBtn = (ImageButton) view.findViewById(R.id.openTextButton);
        ImageButton ratingBtn = (ImageButton) view.findViewById(R.id.ratingButton);

        final ViewFlipper flipper = (ViewFlipper) view.findViewById(R.id.pollViewFlipper);

        ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, pollType);
        OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "您選擇" + id, Toast.LENGTH_LONG).show();
                displayVoteView(flipper, (int) (id + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        };

        setAllSpinner(view, spinnerAdapter, spinnerListener);

        multiChoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayVoteView(flipper, MULTICHOICE);
            }
        });

        openTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayVoteView(flipper, OPEN_TEXT);
            }
        });

        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayVoteView(flipper, RATING);
            }
        });
*/
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Create poll");
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Check question field is not empty
                EditText pollTitle = (EditText) view.findViewById(R.id.pollTitle);
                EditText pollBody = (EditText) view.findViewById(R.id.pollBody);

                if(pollTitle.getText().length() == 0) {
                    pollTitle.requestFocus();
                }
                else {
                    // Add this poll to polls
                    Vote vote = new Vote();
                    vote.setTitle(pollTitle.getText().toString());
                    vote.setContent(pollBody.getText().toString());

                    // Add default options. (yes/no);
                    vote.addOption("yes");
                    vote.addOption("no");

                    polls.add(vote);
                    // TODO send to server

                    // Update vote list
                }

                // Back to main window
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ask for save modified, then load saved data next time

                // Back to main window
            }
        });
        alertDialog.show();
    }

    // TODO request ask list from database after received update message(about 1s)
    private void updateAskList() {
        ArrayAdapter<String> askAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, askEvent.getAsks());
        askListView.setAdapter(askAdapter);
    }

    // Request all questions created from controller or chairman
    private String[] getQuestions() {
        ArrayList<String> list = new ArrayList<String> ();
        String[] questionList = {};
        list.add("直接發問");

        // TODO request all questions created from controller or chairman
        int n = 1;
        try {
            Random random = new Random();
            n = random.nextInt(5) + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 1; i <= n; i++)
            list.add("Test Question " + i);

        return list.toArray(questionList);
    }

    // Get list of member who is not in meeting
    private String[] getMemberList() {
        ArrayList<String> list = new ArrayList<>();
        String[] memberList = {};

        for(String member : members) {
            if(member != controller && member != chairman && member != presenter) {
                list.add(member);
            }
        }

        return list.toArray(memberList);
    }

    // Get list of member who is not ignored
    private String[] getMemberList(String ignore) {
        ArrayList<String> list = new ArrayList<>();
        String[] memberList = {};

        for(String member : members) {
           if(member != ignore) {
               list.add(member);
           }
        }

        return list.toArray(memberList);
    }

    private String[] getPollsTitle() {
        ArrayList<String> list = new ArrayList<>();
        String[] pollList = {};

        for(Vote poll : polls) {
            list.add(poll.getTitle());
        }

        return list.toArray(pollList);
    }

    private void displayVoteView(ViewFlipper flipper, int target) {
        new AnimationUtils();
        flipper.setAnimation(AnimationUtils.makeInAnimation(getContext(), true));

        // Move to target view
        if(target > selectPollType)
            while(target > selectPollType) {
                flipper.showNext();
                selectPollType++;
            }
        else
            while(target < selectPollType) {
                flipper.showPrevious();;
                selectPollType--;
            }
    }

    private void setAllSpinner(View view, ArrayAdapter adapter, OnItemSelectedListener listener) {
/*
        Spinner spinner1 = (Spinner) view.findViewById(R.id.pollSpinner1);
        Spinner spinner2 = (Spinner) view.findViewById(R.id.pollSpinner2);
        Spinner spinner3 = (Spinner) view.findViewById(R.id.pollSpinner3);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(listener);
        spinner2.setOnItemSelectedListener(listener);
        spinner3.setOnItemSelectedListener(listener);

        spinner1.setSelection(adapter.getPosition(pollType[0])); // MultiChoice
        spinner2.setSelection(adapter.getPosition(pollType[1])); // Open Text
        spinner3.setSelection(adapter.getPosition(pollType[2])); // Rating
*/
    }
}
