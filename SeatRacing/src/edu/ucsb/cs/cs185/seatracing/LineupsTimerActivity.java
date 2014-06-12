package edu.ucsb.cs.cs185.seatracing;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.ucsb.cs.cs185.seatracing.model.Boat;
import edu.ucsb.cs.cs185.seatracing.model.RacingSet;
import edu.ucsb.cs.cs185.seatracing.model.Round;

public class LineupsTimerActivity extends FragmentActivity implements AddNewSetListener, OnClickListener {

	private enum LineupTimerState{
		INIT,
		LINEUPS, //only creating lineups
		RACING,
		RESULT,
		SWITCHING, //reviewing lineups
		DONE
	}

	private Handler mHandler = new Handler();
	private Button timerButton;
	private TextView stateView;


	private int numPairs=-1;
	private LineupTimerState state;
	private List<RacingSet>sets;
	private Round mCurrentRound;

	private LineupsPagerContainerFragment lineupsFrag;
	private RunningTimersFragment timersFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lineups_timer);

		if(mCurrentRound==null){
			mCurrentRound = new Round(System.currentTimeMillis());
		}

		timerButton = (Button)findViewById(R.id.button_main_timer);
		timerButton.setOnClickListener(this);
		
		stateView = (TextView)findViewById(R.id.state_label_view);

		if(savedInstanceState==null){
			emplaceLineupsPagerContainerFragment(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lineups_timer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.button_main_timer:
			switch(state){
			case LINEUPS:
				emplaceRunningTimerContainerFragment();
				setState(LineupTimerState.RACING);
				timerButton.setText(R.string.timer_split_button);
				break;
			case RACING:
				if (timersFrag.numTimersRemaining()>1){
					timersFrag.splitOne();
				}
				else{
					//go to finished state
					timersFrag.splitOne();
					//TODO: enable editing
					timerButton.setText(R.string.timer_done_button);
					setState(LineupTimerState.RESULT);
				}
				break;
			case RESULT:
				//TODO: save results somewhere
				writeResults(mCurrentRound);
				emplaceLineupsPagerContainerFragment(false);


				//placeholder to skip switches for now
				performSwitches(mCurrentRound);
				System.out.println("Finished round for race "+(mCurrentRound.getCurrentRace()+1)+" of "+mCurrentRound.getNumRaces());
				if(mCurrentRound.getCurrentRace()+1 == mCurrentRound.getNumRaces()){
					setState(LineupTimerState.DONE);
					timerButton.setText(R.string.timer_finish_button);
				}
				else{
					mCurrentRound.setCurrentRace(mCurrentRound.getCurrentRace()+1);
					setState(LineupTimerState.LINEUPS);
					timerButton.setText(R.string.timer_start_button);
				}
				//TODO: figure out switches, display dialog
				break;
			case SWITCHING:
				//should not happen because this state is only during a modal dialog?
				//is this state even needed?
				//we go to done after switches are performed
				break;
			case DONE:
				switchToResultsActivity(mCurrentRound);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	private void writeResults(Round round) {
		// TODO get times from timers frag, write to results

	}
	
	private void setState(LineupTimerState newState){
		state = newState;
		switch (newState) {
		case INIT:
			stateView.setText(R.string.state_label_init);
			break;
		case LINEUPS:
			if(mCurrentRound.getCurrentRace()>0){
				stateView.setText(R.string.state_label_switching);
			}
			else{
				stateView.setText(R.string.state_label_lineup);
			}
			break;
		case RACING:
			stateView.setText(getResources().getString(R.string.state_label_racing)+(mCurrentRound.getCurrentRace()+1));
			break;
		case SWITCHING:
			stateView.setText(R.string.state_label_switching);
			break;
		case RESULT:
			stateView.setText(getResources().getString(R.string.state_label_result)+(mCurrentRound.getCurrentRace()+1));
			break;
		case DONE:
			stateView.setText(R.string.state_label_done);
			break;
		}
	}

	private void emplaceRunningTimerContainerFragment(){
		if(state != LineupTimerState.RACING){
			if(timersFrag==null){
				timersFrag = new RunningTimersFragment();
			}

			Bundle args = new Bundle();
			sets = lineupsFrag.getAdapter().getRacingSets();
			RacingSet.writeSetsToBundle(args, sets);

			timersFrag.setArguments(args);

			getSupportFragmentManager().beginTransaction()
			.replace(R.id.lineups_timer_container,timersFrag)
			.commit();
		}
	}

	private void emplaceLineupsPagerContainerFragment(boolean editable){
		if(state != LineupTimerState.LINEUPS){
			lineupsFrag = new LineupsPagerContainerFragment();
			if(sets!=null){
				Bundle bndl = RacingSet.writeSetsToBundle(new Bundle(), sets);
				bndl.putBoolean("editable", editable);
				lineupsFrag.setArguments(bndl);
			}
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.lineups_timer_container, lineupsFrag)
			.commit();

			setState(LineupTimerState.LINEUPS);
		}
	}

	private void switchToResultsActivity(Round round){
		//TODO: bundle info here and start activity
	    Intent intent = new Intent(this, ResultsActivity.class);
	    startActivity(intent);
	}

	private void performSwitches(Round round){
		//TODO: display switch dialog frags, maybe here?
		
		int switchToMake = Round.getSwitchIndex(round.getCurrentRace(), round.switchingLast());
		
		System.out.println("Switching rowers at "+switchToMake);

		
		for(RacingSet rs : round.getRacingSets()){
			Boat.switchRowers(rs.getBoat1(), rs.getBoat2(), switchToMake);
		}
		Bundle args = lineupsFrag.getArguments();
		if(args!=null){
			args.putInt("highlightedSeat", switchToMake);
		}

		lineupsFrag.setArguments(args);
	}

	@Override
	public void addNewRacingSet() {
		Intent newRacingSetIntent = new Intent(this,BoatsetCreateActivity.class);
		if(numPairs>0){
			newRacingSetIntent.putExtra("numPairs", numPairs);
		}
		startActivityForResult(newRacingSetIntent, BoatsetCreateActivity.NEW_LINEUP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			if(requestCode == BoatsetCreateActivity.NEW_LINEUP){
				if(state==LineupTimerState.LINEUPS){
					if(! data.hasExtra("racingset")){
						throw new IllegalStateException("Got lineups result with no lineup.");
					}
					RacingSet rs = new RacingSet(data.getBundleExtra("racingset"));

					if(numPairs<0){
						numPairs=rs.getBoat1().size();
					}

					lineupsFrag.getAdapter().addNewSet(rs);
					lineupsFrag.getPager().setCurrentItem(lineupsFrag.getAdapter().getCount()-1, false);

					if(data.hasExtra("switchLast")){
						mCurrentRound.setSwitchingLast(data.getBooleanExtra("switchLast", false));
					}
					mCurrentRound.setRacingSets(lineupsFrag.getAdapter().getRacingSets());

					//lineupsFrag.getPager().setCurrentItem(lineupsFrag.getAdapter().getCount(),true);
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							lineupsFrag.getPager().setCurrentItem(lineupsFrag.getAdapter().getCount()-2, true);
						}
					}, 500);
				}
			}
		}
	}


}
