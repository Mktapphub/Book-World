package mkt.stw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import org.json.*;
import java.util.HashMap;
import java.util.ArrayList;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.SharedPreferences;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;
import android.content.Intent;
import android.net.Uri;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import com.bumptech.glide.Glide;
import smith.lib.showmoreview.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;


public class BookListActivity extends  AppCompatActivity  { 
	
	private Timer _timer = new Timer();
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private HashMap<String, Object> nn = new HashMap<>();
	private double r = 0;
	private double map_length = 0;
	private boolean darkModeEnabled = false;
	
	private ArrayList<HashMap<String, Object>> map = new ArrayList<>();
	private ArrayList<String> strlist = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> maplist_view_in = new ArrayList<>();
	
	private ScrollView vscroll1;
	private LinearLayout main_bg;
	private LinearLayout search_bar;
	private GridView gridview1;
	private LinearLayout No;
	private EditText edittext2;
	private TextView empty;
	
	private TimerTask timer;
	private SharedPreferences sp;
	private DatabaseReference fdb = _firebase.getReference("book");
	private ChildEventListener _fdb_child_listener;
	private Intent in = new Intent();
	private AlertDialog.Builder d;
	private RequestNetwork net;
	private RequestNetwork.RequestListener _net_request_listener;
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.book_list);
		initialize(_savedInstanceState);
		com.google.firebase.FirebaseApp.initializeApp(this);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		
		vscroll1 = (ScrollView) findViewById(R.id.vscroll1);
		main_bg = (LinearLayout) findViewById(R.id.main_bg);
		search_bar = (LinearLayout) findViewById(R.id.search_bar);
		gridview1 = (GridView) findViewById(R.id.gridview1);
		No = (LinearLayout) findViewById(R.id.No);
		edittext2 = (EditText) findViewById(R.id.edittext2);
		empty = (TextView) findViewById(R.id.empty);
		sp = getSharedPreferences("sp", Activity.MODE_PRIVATE);
		d = new AlertDialog.Builder(this);
		net = new RequestNetwork(this);
		
		gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				try{
					in.setClass(getApplicationContext(), BookInfoActivity.class);
					in.putExtra("name", map.get((int)_position).get("name").toString());
					in.putExtra("poet", map.get((int)_position).get("poet").toString());
					in.putExtra("cover", map.get((int)_position).get("cover").toString());
					in.putExtra("des", map.get((int)_position).get("des").toString());
					in.putExtra("link", map.get((int)_position).get("url").toString());
					in.putExtra("pimg", map.get((int)_position).get("pimg").toString());
					startActivity(in);
				} catch(Exception e){
				}
			}
		});
		
		edittext2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				final String _charSeq = _param1.toString();
				if (_charSeq.equals("")) {
					gridview1.setAlpha((float)(0));
				}
				else {
					gridview1.setAlpha((float)(1));
				}
				fdb.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot _dataSnapshot) {
						map = new ArrayList<>();
						try {
							GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
							for (DataSnapshot _data : _dataSnapshot.getChildren()) {
								HashMap<String, Object> _map = _data.getValue(_ind);
								map.add(_map);
							}
						}
						catch (Exception _e) {
							_e.printStackTrace();
						}
						if (_charSeq.length() > 0) {
							r = map.size() - 1;
							map_length = map.size();
							for(int _repeat25 = 0; _repeat25 < (int)(map_length); _repeat25++) {
								if (map.get((int)r).get("name").toString().toLowerCase().contains(_charSeq.toLowerCase())) {
									
								}
								else {
									map.remove((int)(r));
								}
								r--;
							}
						}
						gridview1.setAdapter(new Gridview1Adapter(map));
					}
					@Override
					public void onCancelled(DatabaseError _databaseError) {
					}
				});
			}
			
			@Override
			public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				
			}
			
			@Override
			public void afterTextChanged(Editable _param1) {
				
			}
		});
		
		_fdb_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				fdb.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot _dataSnapshot) {
						map = new ArrayList<>();
						try {
							GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
							for (DataSnapshot _data : _dataSnapshot.getChildren()) {
								HashMap<String, Object> _map = _data.getValue(_ind);
								map.add(_map);
							}
						}
						catch (Exception _e) {
							_e.printStackTrace();
						}
						gridview1.setAdapter(new Gridview1Adapter(map));
						((BaseAdapter)gridview1.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onCancelled(DatabaseError _databaseError) {
					}
				});
				if (map.size() > 0) {
					timer = new TimerTask() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									empty.setVisibility(View.GONE);
									No.setVisibility(View.VISIBLE);
									gridview1.setVisibility(View.GONE);
								}
							});
						}
					};
					_timer.schedule(timer, (int)(1000));
				}
				else {
					timer = new TimerTask() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									No.setVisibility(View.GONE);
									gridview1.setVisibility(View.VISIBLE);
									empty.setVisibility(View.GONE);
								}
							});
						}
					};
					_timer.schedule(timer, (int)(1000));
				}
			}
			
			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				fdb.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot _dataSnapshot) {
						map = new ArrayList<>();
						try {
							GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
							for (DataSnapshot _data : _dataSnapshot.getChildren()) {
								HashMap<String, Object> _map = _data.getValue(_ind);
								map.add(_map);
							}
						}
						catch (Exception _e) {
							_e.printStackTrace();
						}
						gridview1.setAdapter(new Gridview1Adapter(map));
						((BaseAdapter)gridview1.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onCancelled(DatabaseError _databaseError) {
					}
				});
			}
			
			@Override
			public void onChildMoved(DataSnapshot _param1, String _param2) {
				
			}
			
			@Override
			public void onChildRemoved(DataSnapshot _param1) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onCancelled(DatabaseError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();
				
			}
		};
		fdb.addChildEventListener(_fdb_child_listener);
		
		_net_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		try{
			net.startRequestNetwork(RequestNetworkController.GET, "https://www.google.com", "A", _net_request_listener);
			fdb.addChildEventListener(_fdb_child_listener);
			fdb.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot _dataSnapshot) {
					map = new ArrayList<>();
					try {
						GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
						for (DataSnapshot _data : _dataSnapshot.getChildren()) {
							HashMap<String, Object> _map = _data.getValue(_ind);
							map.add(_map);
						}
					}
					catch (Exception _e) {
						_e.printStackTrace();
					}
					gridview1.setAdapter(new Gridview1Adapter(map));
					((BaseAdapter)gridview1.getAdapter()).notifyDataSetChanged();
				}
				@Override
				public void onCancelled(DatabaseError _databaseError) {
				}
			});
		} catch(Exception e){
		}
		_Check_Dark_Mode();
		if (darkModeEnabled) {
			main_bg.setBackgroundColor(0xFF000000);
			edittext2.setTextColor(0xFFFFFFFF);
		}
		else {
			main_bg.setBackgroundColor(0xFFFFFFFF);
			edittext2.setTextColor(0xFF000000);
		}
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			
			default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		moveTaskToBack (true);
	}
	public void _Check_Dark_Mode () {
		int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
		darkModeEnabled = false;
		darkModeEnabled = (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES);
	}
	
	
	public class Gridview1Adapter extends BaseAdapter {
		ArrayList<HashMap<String, Object>> _data;
		public Gridview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.cus, null);
			}
			
			final LinearLayout linear1 = (LinearLayout) _view.findViewById(R.id.linear1);
			final ImageView cover = (ImageView) _view.findViewById(R.id.cover);
			final TextView name = (TextView) _view.findViewById(R.id.name);
			final TextView poet_name = (TextView) _view.findViewById(R.id.poet_name);
			
			try{
				name.setText(map.get((int)_position).get("name").toString());
				poet_name.setText(map.get((int)_position).get("poet").toString());
				Glide.with(getApplicationContext()).load(Uri.parse(map.get((int)_position).get("cover").toString())).into(cover);
				cover.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						try{
							in.setClass(getApplicationContext(), BookInfoActivity.class);
							in.putExtra("name", map.get((int)_position).get("name").toString());
							in.putExtra("poet", map.get((int)_position).get("poet").toString());
							in.putExtra("cover", map.get((int)_position).get("cover").toString());
							in.putExtra("des", map.get((int)_position).get("des").toString());
							in.putExtra("pimg", map.get((int)_position).get("pimg").toString());
							in.putExtra("link", map.get((int)_position).get("url").toString());
							startActivity(in);
						} catch(Exception e){
						}
					}
				});
			} catch(Exception e){
			}
			_Check_Dark_Mode();
			if (darkModeEnabled) {
				name.setTextColor(0xFFFFFFFF);
				linear1.setBackgroundColor(0xFF000000);
				cover.setBackgroundColor(0xFF000000);
			}
			else {
				cover.setBackgroundColor(Color.TRANSPARENT);
				linear1.setBackgroundColor(Color.TRANSPARENT);
				name.setTextColor(0xFF000000);
			}
			
			return _view;
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels(){
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels(){
		return getResources().getDisplayMetrics().heightPixels;
	}
	
}
